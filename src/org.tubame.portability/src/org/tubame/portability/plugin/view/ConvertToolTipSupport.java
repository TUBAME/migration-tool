/*
 * ConvertToolTipSupport.java
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
package org.tubame.portability.plugin.view;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

/**
 * Change the class name after the conversion tool tip before conversion class
 * to display a tool tip.<br/>
 */
public class ConvertToolTipSupport extends ColumnViewerToolTipSupport {
    /**
     * Column viewer
     */
    private final ColumnViewer viewer;

    /**
     * Own instance
     */
    private static ConvertToolTipSupport mySelf;

    /**
     * Constructor.<br/>
     * The class held in the variable viewer you want to use.<br/>
     * 
     * @param viewer
     *            Column viewer @see {@link ColumnViewerToolTipSupport}
     * @param style
     *            Style @see {@link ColumnViewerToolTipSupport}
     * @param manualActivation
     *            Flag @see {@link ColumnViewerToolTipSupport}
     */
    private ConvertToolTipSupport(ColumnViewer viewer, int style,
            boolean manualActivation) {
        super(viewer, style, manualActivation);
        this.viewer = viewer;
    }

    /**
     * Get instance.<br/>
     * If there is no instance, create, and get an instance.<br/>
     * 
     * @param viewer
     *            Column viewer @see {@link ColumnViewerToolTipSupport}
     * @return Instance
     */
    public static ConvertToolTipSupport getInstance(ColumnViewer viewer) {
        if (ConvertToolTipSupport.mySelf == null) {
            ConvertToolTipSupport.mySelf = new ConvertToolTipSupport(viewer,
                    ToolTip.NO_RECREATE, false);
        }
        return ConvertToolTipSupport.mySelf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideOnMouseDown() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldCreateToolTip(Event event) {
        ViewerCell cell = viewer.getCell(new Point(event.x, event.y));
        if (cell != null) {
            int index = cell.getColumnIndex();
            ConvertLabelProvider cellLabelProvider = (ConvertLabelProvider) viewer
                    .getLabelProvider(index);
            cellLabelProvider.setToolTipIndex(index);
        }
        return super.shouldCreateToolTip(event);
    }
}