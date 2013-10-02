/*
 * ViewUtil.java
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
package tubame.knowhow.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.model.generated.knowhow.Category;
import tubame.knowhow.biz.model.generated.knowhow.KnowhowInfomation;
import tubame.knowhow.biz.model.generated.knowhow.SearchInfomation;
import tubame.knowhow.biz.exception.JbmException;

import tubame.knowhow.plugin.logic.EntryItemManagement;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewer;
import tubame.knowhow.plugin.ui.wizard.register.AbstractCreateKnowhowPage;

/**
 * Utility class know-how of entry view.<br/>
 */
public final class ViewUtil {

    /** Category registration ID */
    private static final String REGISTER_CATEGORY_VIEW_MENU_ID = "tubame.knowhow.maintenance.portability.ui.actions.CreateCategory";
    /** Know-how registration ID */
    private static final String REGISTER_KNOWHOW_VIEW_MENU_ID = "tubame.knowhow.maintenance.portability.ui.actions.CreateKnowhow";
    /** Check items registration ID. */
    private static final String REGISTER_CHECKITEM_VIEW_MENU_ID = "tubame.knowhow.maintenance.portability.ui.actions.CreateCheckItem";

    /**
     * Constructor.<br/>
     * 
     */
    private ViewUtil() {
        super();
    }

    /**
     * Get the category registration ID.<br/>
     * 
     * @return Category registration ID
     */
    public static String getRegisterCategoryViewMenuId() {
        return ViewUtil.REGISTER_CATEGORY_VIEW_MENU_ID;
    }

    /**
     * Get the know-how registration ID.<br/>
     * 
     * @return Know-how registration ID
     */
    public static String getRegisterKnowhowViewMenuId() {
        return ViewUtil.REGISTER_KNOWHOW_VIEW_MENU_ID;
    }

    /**
     * Get items check registration ID.<br/>
     * 
     * @return Check item registration ID
     */
    public static String getRegisterCheckItemViewMenuId() {
        return ViewUtil.REGISTER_CHECKITEM_VIEW_MENU_ID;
    }

    /**
     * Register an item to the know-how entry view.<br/>
     * 
     * @param portabilityKnowhowListViewData
     *            Know-how entry view
     * @param knowhowPage
     *            Know-how entry view
     * @param keyValue
     *            Register key
     * @param level
     *            Level
     * @return Know-how entry view
     */
    public static PortabilityKnowhowListViewOperation registerViewData(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewData,
            AbstractCreateKnowhowPage knowhowPage, String keyValue, int level) {
        PortabilityKnowhowListViewData child = new PortabilityKnowhowListViewData(
                portabilityKnowhowListViewData, level,
                KnowhowManagement
                        .registerPageToEntryView(knowhowPage, keyValue));
        return ViewUtil.insertChildKnowhowEntryViewData(
                portabilityKnowhowListViewData, child);
    }

    /**
     * Register a new item to the know-how entry view.<br/>
     * 
     * @param knowhowPage
     *            Registration information
     * @param keyValue
     *            Register key
     * @param level
     *            Level
     * @throws JbmException
     *             JBM exception
     */
    public static void registerNewViewData(
            AbstractCreateKnowhowPage knowhowPage, String keyValue, int level)
            throws JbmException {
        List<PortabilityKnowhowListViewOperation> operationListDataList = ViewUtil
                .getPortabilityKnowhowListViewOperationList();
        PortabilityKnowhowListViewData portabilityKnowhowListViewData = new PortabilityKnowhowListViewData(
                null, level, KnowhowManagement.registerPageToEntryView(
                        knowhowPage, keyValue));
        operationListDataList.add(portabilityKnowhowListViewData);
        ViewUtil.insertKnowhowEntryViewData(operationListDataList);
    }

    /**
     * Category if it is not already selected, <br/>
     * and return the list of new PortabilityKnowhowListViewOperation.<br/>
     * 
     * @return PortabilityKnowhowListViewOperation list
     */
    @SuppressWarnings("unchecked")
    public static List<PortabilityKnowhowListViewOperation> getPortabilityKnowhowListViewOperationList() {
        List<PortabilityKnowhowListViewOperation> portabilityKnowhowListViewOperations = (List<PortabilityKnowhowListViewOperation>) PluginUtil
                .getKnowhowEntryViewTreeViewer().getInput();
        if (portabilityKnowhowListViewOperations != null) {
            return portabilityKnowhowListViewOperations;
        }
        return new LinkedList<PortabilityKnowhowListViewOperation>();
    }

    /**
     * Set data entry view to know-how.<br/>
     * 
     * @param knowhowListViewOperations
     *            List<PortabilityKnowhowListViewOperation>
     */
    public static void insertKnowhowEntryViewData(
            List<PortabilityKnowhowListViewOperation> knowhowListViewOperations) {
        KnowhowEntryTreeViewer entryTreeViewer = PluginUtil
                .getKnowhowEntryViewTreeViewer();
        if (entryTreeViewer != null && !entryTreeViewer.getTree().isDisposed()) {
            entryTreeViewer.setEntryListData(knowhowListViewOperations);
        }
    }

    /**
     * Set the child hierarchical data entry view to know-how.<br/>
     * 
     * @param addParentEntryParent
     *            entry
     * @param addEntry
     *            Child entry
     * @return Entry
     */
    public static PortabilityKnowhowListViewOperation insertChildKnowhowEntryViewData(
            PortabilityKnowhowListViewOperation addParentEntry,
            PortabilityKnowhowListViewOperation addEntry) {
        KnowhowEntryTreeViewer entryTreeViewer = PluginUtil
                .getKnowhowEntryViewTreeViewer();
        if (entryTreeViewer != null && !entryTreeViewer.getTree().isDisposed()) {
            entryTreeViewer.addRegisterItem(addParentEntry, addEntry);
        }
        return addEntry;
    }

    /**
     * Refresh the know-how entry view.<br/>
     * 
     */
    public static void knowhowEntryViewRefresh() {
        if (!PluginUtil.isTreeViewerDisposed()) {
            PluginUtil.getKnowhowEntryViewTreeViewer().refresh();
        }
    }

    /**
     * Get the know-how category information from XML data.<br/>
     * 
     * @param entryCategoryRefKey
     *            Category reference key
     * @param categoryMap
     *            Category Map data
     * @return Category
     */
    public static Category getCategory(String entryCategoryRefKey,
            Map<String, Category> categoryMap) {
        for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
            if (entryCategoryRefKey.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get the know-how know-how information from XML data.<br/>
     * 
     * @param knowhowRefKey
     *            Reference key know-how
     * @param knowhowMap
     *            Map data know-how
     * @return KnowhowInfomation
     */
    public static KnowhowInfomation getKnowhow(String knowhowRefKey,
            Map<String, KnowhowInfomation> knowhowMap) {
        for (Map.Entry<String, KnowhowInfomation> entry : knowhowMap.entrySet()) {
            if (knowhowRefKey.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get search information from the know-how XML data.<br/>
     * 
     * @param searchRefKey
     *            Search key information reference
     * @param searchInfoMap
     *            Search Info Map data
     * @return SearchInfomation
     */
    public static SearchInfomation getSearchInfo(String searchRefKey,
            Map<String, SearchInfomation> searchInfoMap) {
        for (Map.Entry<String, SearchInfomation> entry : searchInfoMap
                .entrySet()) {
            if (searchRefKey.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get the know-how information from XML, to reflect the contents of the
     * know-how entry view.<br/>
     * 
     * @param targetFile
     *            Target file
     * @throws JbmException
     *             JBM exception
     */
    public static void refrectionEntryViewData(String targetFile)
            throws JbmException {
        // Clear View and editor
        initializationKnowhowData();
        // Initialization of file management data
        FileManagement.setPortabilityKnowhowFilePath(CmnStringUtil.EMPTY);
        // Transplant know-how file settings
        FileManagement.setPortabilityKnowhowFilePath(targetFile);
        if (targetFile != null && CmnFileUtil.isFileSize(targetFile)) {
            // Copy the new file data in the file original
            KnowhowManagement.refresh();
            ViewUtil.insertKnowhowEntryViewData(KnowhowManagement
                    .getEntryViewConvetData());
        }
    }

    /**
     * Initialize the know-how and know-how editor data entry view.<br/>
     * 
     */
    public static void initializationKnowhowData() {
        if (!PluginUtil.isTreeViewerDisposed()) {
            PluginUtil.getKnowhowEntryViewTreeViewer().setEntryListData(null);
        }
        KnowhowManagement.initializationConvetData();
        EntryItemManagement.initializationItemData();
    }
}
