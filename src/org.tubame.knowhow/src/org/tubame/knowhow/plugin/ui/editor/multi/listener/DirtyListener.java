/*
 * DirtyListener.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.listener;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.forms.editor.FormEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.ui.editor.multi.EditorDirty;
import org.tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;

/**
 * Class when editing is done in expertise editor performs unsaved settings.<br/>
 */
public class DirtyListener implements KeyListener, SelectionListener,
        IDocumentPartitioningListener, IDocumentListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DirtyListener.class);
    /** Editor preserved object */
    private EditorDirty editor;

    /**
     * Constructor.<br/>
     * 
     * @param editor
     *            Editor preserved object
     */
    public DirtyListener(FormEditor editor) {
        DirtyListener.LOGGER.debug("[editor]" + editor);
        this.editor = (EditorDirty) editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void documentPartitioningChanged(IDocument document) {
        DirtyListener.LOGGER.debug("[document]" + document);
        editor.setDirty(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent e) {
        DirtyListener.LOGGER.debug(CmnStringUtil.EMPTY);
        editor.setDirty(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        DirtyListener.LOGGER.debug(CmnStringUtil.EMPTY);
        editor.setDirty(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void documentChanged(DocumentEvent event) {
        DirtyListener.LOGGER.debug(CmnStringUtil.EMPTY);
        // Listener for only Know how page
        if (editor instanceof MaintenanceKnowhowMultiPageEditor) {
            MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = (MaintenanceKnowhowMultiPageEditor) editor;
            if (MaintenanceKnowhowMultiPageEditor.KNOWHOWDETAIL_PAGE == knowhowMultiPageEditor
                    .getActivePage()) {
                editor.setDirty(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void documentAboutToBeChanged(DocumentEvent event) {
        // no operation
    }

}
