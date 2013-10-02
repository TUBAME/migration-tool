/*
 * AutoResizeTableLayout.java
 * Created on 2013/06/28
 *
 * Copyright (C) 2011-2013 Nippon Telegraph and Telephone Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tubame.wsearch.ui.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import tubame.wsearch.Activator;

/**
 * It is the auto-size layout class.<br/>
 */
public class AutoResizeTableLayout extends TableLayout implements
        ControlListener {

    /**
     * Table
     */
    private final Table table;

    /**
     * Column list
     */
    private List<ColumnLayoutData> columns = new ArrayList<ColumnLayoutData>();

    /**
     * Whether to the automatic adjustment
     */
    private boolean autosizing = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addColumnData(ColumnLayoutData data) {
        columns.add(data);
        super.addColumnData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void controlMoved(ControlEvent e) {
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param table
     *            Table
     */
    public AutoResizeTableLayout(Table table) {
        this.table = table;
        table.addControlListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void controlResized(ControlEvent e) {
        if (autosizing)
            return;
        autosizing = true;
        try {
            autoSizeColumns();
        } finally {
            autosizing = false;
        }
    }

    /**
     * Make the automatic adjustment of column size.<br/>
     */
    private void autoSizeColumns() {
        int width = table.getClientArea().width;

        if (width <= 1)
            return;
        TableColumn[] tableColumns = table.getColumns();
        int size = Math.min(columns.size(), tableColumns.length);
        int[] widths = new int[size];
        int fixedWidth = 0;
        int numberOfWeightColumns = 0;
        int totalWeight = 0;

        for (int i = 0; i < size; i++) {
            ColumnLayoutData col = columns.get(i);
            if (col instanceof ColumnPixelData) {
                int pixels = ((ColumnPixelData) col).width;
                widths[i] = pixels;
                fixedWidth += pixels;
            } else if (col instanceof ColumnWeightData) {
                ColumnWeightData cw = (ColumnWeightData) col;
                numberOfWeightColumns++;
                int weight = cw.weight;
                totalWeight += weight;
            } else {
                throw new IllegalStateException(
                        Activator.getResourceString(AutoResizeTableLayout.class
                                .getName() + ".err.msg.UnknownColumnErr"));
            }
        }
        if (numberOfWeightColumns > 0) {
            if (width < fixedWidth + totalWeight)
                width = fixedWidth + totalWeight;
            int rest = width - fixedWidth;
            int totalDistributed = 0;
            for (int i = 0; i < size; i++) {
                ColumnLayoutData col = columns.get(i);
                if (col instanceof ColumnWeightData) {
                    ColumnWeightData cw = (ColumnWeightData) col;
                    int weight = cw.weight;
                    int pixels = totalWeight == 0 ? 0 : weight * rest
                            / totalWeight;
                    if (pixels < cw.minimumWidth)
                        pixels = cw.minimumWidth;
                    totalDistributed += pixels;
                    widths[i] = pixels;
                }
            }
            int diff = rest - totalDistributed;
            for (int i = 0; diff > 0; i++) {
                if (i == size)
                    i = 0;
                ColumnLayoutData col = columns.get(i);
                if (col instanceof ColumnWeightData) {
                    ++widths[i];
                    --diff;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            if (tableColumns[i].getWidth() != widths[i])
                tableColumns[i].setWidth(widths[i]);
        }
    }
}