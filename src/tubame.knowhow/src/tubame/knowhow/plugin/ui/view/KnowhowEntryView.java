/*
 * KnowhowEntryView.java
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
package tubame.knowhow.plugin.ui.view;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.CheckItemViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;

/**
 * Know-how entry view class.<br/>
 */
public class KnowhowEntryView extends ViewPart implements ViewRefresh {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(KnowhowEntryView.class);

	/** Tree view */
	private KnowhowEntryTreeViewer knowhowEntryTreeViewer;

	/** open view action */
	private Action openViewAction;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOGGER.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_CREATE_KNOWHOW_ENTRY_VIEW));

		knowhowEntryTreeViewer = new KnowhowEntryTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.FULL_SELECTION);

		makeActions();
		contributeToActionBars();
		// Setting the display data
		knowhowEntryTreeViewer.addDoubleClickListener(new KnowhowEntryViewClickListener());
		setData();
	}

	/**
	 * Set the item information on entry view.<br/>
	 * The display reads, in TreeViewer know-how XML file.<br/>
	 * Also performs together reflecting to know the editor.<br/>
	 * 
	 */
	private void setData() {
		KnowhowManagement.setKnowhowEntryTreeViewer(knowhowEntryTreeViewer);
		if (isReflectionKnowhow()) {
			MaintenanceKnowhowMultiPageEditor knowhowMultiEditor = KnowhowManagement.getKnowhowEditor();
			try {
				ViewUtil.refrectionEntryViewData(FileManagement.getPortabilityKnowhowFilePath());
				knowhowMultiEditor.updateDocumentationFormPage();
				knowhowMultiEditor.setDirty(false);
			} catch (JbmException e) {
				JbmException.outputExceptionLog(LOGGER, e,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_INITIAL_READING));
			}
		}
	}

	/**
	 * Determine whether it is possible to reflect to heading the list. <br/>
	 * It returns false if not reflected.<br/>
	 * 
	 * @return true:Reflect possible false:Reflect not
	 */
	private boolean isReflectionKnowhow() {
		if (FileManagement.getPortabilityKnowhowFilePath().equals(CmnStringUtil.EMPTY)) {
			return false;
		}
		if (KnowhowManagement.getKnowhowEditor() == null) {
			return false;
		}
		if (KnowhowManagement.getKnowhowEditor().getTreeViewerOperator().getTree().isDisposed()) {
			return false;
		}
		return true;
	}

	/**
	 * Refresh the view by setting the data entry view to know-how.<br/>
	 * 
	 * @param knowhowListViewOperations
	 *            List<PortabilityKnowhowListViewOperation>
	 */
	public void setPortabilityKnowhowListViewData(List<PortabilityKnowhowListViewOperation> knowhowListViewOperations) {
		knowhowEntryTreeViewer.setInput(knowhowListViewOperations);
		knowhowEntryTreeViewer.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// no operation
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		setData();
		IViewPart opendView = PluginUtil.isOpendView(KnowhowEntryCheckItemView.ID);
		if (opendView != null) {
			KnowhowEntryCheckItemView checkItemEntryView = (KnowhowEntryCheckItemView) opendView;
			checkItemEntryView.refresh();
		}
	}

	/**
	 * Get treeViewer.<br/>
	 * 
	 * @return treeViewer
	 */
	public KnowhowEntryTreeViewer getTreeViewer() {
		return knowhowEntryTreeViewer;
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void makeActions() {
		openViewAction = new Action() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				try {
					// open view
					KnowhowEntryCheckItemView checkItemView = (KnowhowEntryCheckItemView) PluginUtil.getActivePage()
							.showView(KnowhowEntryCheckItemView.ID);
					checkItemView.refresh();
					KnowhowEntryTreeViewer knowhowEntryViewTreeViewer = PluginUtil.getKnowhowEntryViewTreeViewer();

					PortabilityKnowhowListViewOperation oneSelection = knowhowEntryViewTreeViewer.getOneSelection();
					if (oneSelection != null) {
						AbstractViewType knowhowViewType = oneSelection.getKnowhowViewType();
						if (knowhowViewType != null) {
							if (knowhowViewType instanceof CheckItemViewType) {
								checkItemView.selectElement(oneSelection);
							}
						}
					}

				} catch (PartInitException e) {
					JbmException.outputExceptionLog(LOGGER, e,
							MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_OPEN_VIEW));
				}
			}
		};
		openViewAction.setText(ApplicationPropertiesUtil
				.getProperty(ApplicationPropertiesUtil.OPEN_CHECKITEM_ENTRY_VIEW));

	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(openViewAction);
	}

}
