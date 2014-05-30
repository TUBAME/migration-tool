/*
 * AbstractDoubleClickListener.java
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
package tubame.portability.plugin.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Editing screen double click listener class.<br/>
 * And an editor with the specified file. Further, setting various markers.<br/>
 */
@SuppressWarnings("restriction")
public abstract class AbstractDoubleClickListener implements
		IDoubleClickListener {

	/**
	 * Layout size to be used when editor display
	 */
	private static final float SIZE = 0.5f;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractDoubleClickListener.class);

	/**
	 * Editor
	 */
	private final MigrationEditorOperation editor;

	/**
	 * Set all markers of the target file.<br/>
	 * Set marker from data that is displayed on the screen.<br/>
	 * 
	 * @param file
	 *            Display the target file in the Editor
	 * @param targetFile
	 *            Display target file path name of the editor
	 * @throws CoreException
	 *             Add marker failure
	 */
	public abstract void createMarker(IFile file, String targetFile)
			throws CoreException;

	/**
	 * Constructor.<br/>
	 * 
	 * @param menuOperation
	 *            Editor
	 */
	public AbstractDoubleClickListener(MigrationEditorOperation menuOperation) {
		editor = menuOperation;
	}

	/**
	 * Open the file in the editor.<br/>
	 * 
	 * @param targetFilePath
	 *            Open the target file path
	 * @param initLine
	 *            Rows to display
	 */
	public void editorOpen(String targetFilePath, int initLine) {
		LOGGER.info("hoge");
		// Specify the file
		IFile file = PluginUtil.createIFile(ResourcesPlugin.getWorkspace()
				.getRoot(), targetFilePath);
		if (file == null) {
			String message = String.format(MessageUtil.ERR_OPEN_FILE,
					targetFilePath);
			JbmException.outputExceptionLog(null, LOGGER, ERROR_LEVEL.ERROR,
					new String[] { message });
			PluginUtil.viewErrorDialog(ResourceUtil.FILE_OPEN_ERROR_TITLE,
					message, null);
		} else {

			try {
				// Marker set
				createMarker(file, targetFilePath);
				// Open at the specified line
				IMarker marker = addMarker(file, IMarker.TEXT, initLine);

				// Because it is not available package of Internal For XXX
				// Eclipse4.2,
				
				//splitEditorArea(marker);
				
				//splitEditorArea method was modified so that it does not use the internal package of Eclipse3 system
				IDE.openEditor(PluginUtil.getActivePage(), marker);
				file.deleteMarkers(IMarker.TEXT, false, IResource.DEPTH_ZERO);
			} catch (CoreException e) {
				JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.ERROR,
						new String[] { MessageUtil.ERR_ADD_MARKER });
			}
		}
	}

	/**
	 * Add marker.<br/>
	 * 
	 * @param file
	 *            Target file
	 * @param markerId
	 *            Marker ID
	 * @param line
	 *            Marker line to be set
	 * @return Marker
	 * @throws CoreException
	 *             Add marker failure
	 */
	public IMarker addMarker(IFile file, String markerId, int line)
			throws CoreException {
		IMarker marker = file.createMarker(markerId);
		marker.setAttribute(IMarker.LINE_NUMBER, line);
		return marker;
	}

	// /**
	// * Open the editor the contents of the file / line that you specify.<br/>
	// *
	// * @param marker
	// * Marker
	// * @throws PartInitException
	// * Editor when open exception
	// *
	// */
	// private void splitEditorArea(IMarker marker) throws PartInitException {
	//
	// PartSite partSite = (PartSite) PluginUtil.getActivePage()
	// .getActivePart().getSite();
	// ILayoutContainer rootLayoutContainer = partSite.getPane().getPart()
	// .getContainer();
	//
	// if (!(rootLayoutContainer instanceof LayoutPart)) {
	// return;
	// }
	// // Is not performed the division processing editor have not been opened
	// // and one
	// if (PluginUtil.getOpenEditors().length < 1) {
	// return;
	// }
	//
	// ILayoutContainer editorSashLayoutContainer = ((LayoutPart)
	// rootLayoutContainer)
	// .getContainer();
	// if (editorSashLayoutContainer != null
	// && editorSashLayoutContainer instanceof EditorSashContainer) {
	// EditorSashContainer editorSashContainer = (EditorSashContainer)
	// editorSashLayoutContainer;
	//
	// // Editor workbook other than the default if it has already made, is
	// // not divided
	// if (editorSashLayoutContainer.getChildren().length > 1) {
	// openEditor(editorSashLayoutContainer, marker);
	// return;
	// }
	// // it is not designed to create an editor workbooks newly open an
	// // editor to the Part
	// openSplitedEditor(rootLayoutContainer, editorSashContainer, marker);
	// }
	// }

	// /**
	// * The editor area that has already been created and opened by the new
	// * editor,<br/>
	// * the contents of the file / line that you specify.<br/>
	// *
	// * @param editorSashLayoutContainer
	// * Sash editor layout container
	// * @param marker
	// * Marker
	// * @throws PartInitException
	// * Editor when open exception
	// */
	// private void openEditor(ILayoutContainer editorSashLayoutContainer,
	// IMarker marker) throws PartInitException {
	// List<IEditorReference> editorRefList =
	// getAnotherJbmEditor(editorSashLayoutContainer
	// .getChildren());
	//
	// for (IEditorReference editorReference : editorRefList) {
	// IEditorPart editorPart = editorReference.getEditor(true);
	// // Because Focus is not set Editor other than Active, the total
	// // Editor,
	// // editor # setForcus is performed
	// editorPart.setFocus();
	// }
	// // Open the editor in the area is hitting the focus
	// IDE.openEditor(PluginUtil.getActivePage(), marker);
	// }

	// /**
	// * Get EditorRefrence in JBM editor tab.<br/>
	// *
	// * @param layoutList
	// * Layout list
	// * @return EditorRefrence
	// */
	// private List<IEditorReference> getAnotherJbmEditor(LayoutPart[]
	// layoutList) {
	// for (LayoutPart layoutPart : layoutList) {
	// if (!"DefaultEditorWorkbook".equals(layoutPart.getPlaceHolderId())) {
	// Control con = ((EditorStack) layoutPart).getTabList()[0];
	// CTabFolder folder = (CTabFolder) con;
	// CTabItem[] items = folder.getItems();
	// int size = items.length - 1;
	// List<IEditorReference> editorRefList = new ArrayList<IEditorReference>();
	// for (int count = 0; count <= size; count++) {
	// if (items[count].getToolTipText().indexOf(".jbm") == -1) {
	// editorRefList.add(PluginUtil
	// .getEditorReference(items[count]
	// .getToolTipText()));
	// }
	// }
	// return editorRefList;
	// }
	// }
	// return null;
	// }

	// /**
	// * Open a new editor the contents of the file / line that split the editor
	// * area, <br/>
	// * as specified.<br/>
	// *
	// * @param rootLayoutContainer
	// * Layout container
	// * @param editorSashContainer
	// * Editor container sash
	// * @param marker
	// * Marker
	// * @throws PartInitException
	// * Editor when open exception
	// */
	// private void openSplitedEditor(ILayoutContainer rootLayoutContainer,
	// EditorSashContainer editorSashContainer, IMarker marker)
	// throws PartInitException {
	// // Open the editor
	// IDE.openEditor(PluginUtil.getActivePage(), marker);
	// PartPane editorPartPane = ((PartSite) PluginUtil.getActivePage()
	// .getActiveEditor().getSite()).getPane();
	// // Create a new editor area
	// PartStack partStack = createStack(editorSashContainer);
	// editorSashContainer.stack(editorPartPane, partStack);
	// if (rootLayoutContainer != null
	// && rootLayoutContainer instanceof LayoutPart) {
	// ILayoutContainer cont = ((LayoutPart) rootLayoutContainer)
	// .getContainer();
	// if (cont instanceof PartSashContainer) {
	// ((PartSashContainer) cont).add(partStack, IPageLayout.TOP,
	// AbstractDoubleClickListener.SIZE,
	// ((PartSashContainer) cont).findBottomRight());
	// }
	// }
	// }

	// /**
	// * Generate a new workbook.<br/>
	// *
	// * @param editorSashContainer
	// * Editor container
	// * @return New workbook
	// */
	// private PartStack createStack(EditorSashContainer editorSashContainer) {
	// EditorStack newWorkbook = EditorStack
	// .newEditorWorkbook(editorSashContainer,
	// (WorkbenchPage) PluginUtil.getActivePage());
	// return newWorkbook;
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		// no operation
	}

	/**
	 * Get editor.<br/>
	 * 
	 * @return editor Editing screen
	 */
	public MigrationEditorOperation getEditor() {
		return editor;
	}
}
