/*
 * GjbmSearchResultLabelProvider.java
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
package org.tubame.wsearch.ui.view;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.tubame.wsearch.models.MigrationEditorRow;

/**
 * It is a general-purpose search results for label provider class.</p>
 */
public class GjbmSearchResultLabelProvider extends ColumnLabelProvider
        implements ITableLabelProvider, ITableColorProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;
        if (element instanceof MigrationEditorRow) {
            MigrationEditorRow row = (MigrationEditorRow) element;
            image = row.getColumnImage(columnIndex);
        }
        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        String text = CmnStringUtil.EMPTY;
        if (element instanceof MigrationEditorRow) {
            MigrationEditorRow row = (MigrationEditorRow) element;
            text = row.getColumnText(columnIndex);
        }
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ILabelProviderListener listener) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLabelProperty(Object element, String property) {
        // no operation
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ILabelProviderListener listener) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Font getFont(Object element) {
        if (element instanceof MigrationEditorRow) {
            MigrationEditorRow row = (MigrationEditorRow) element;
            if (row.getFont() != null) {
                return row.getFont();
            }
        }
        return super.getFont(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getForeground(Object element, int columnIndex) {
        if (element instanceof MigrationEditorRow) {
            MigrationEditorRow row = (MigrationEditorRow) element;
            return row.getForeground(columnIndex);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackground(Object element, int columnIndex) {
        return null;
    }
}