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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.CheckItemViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Know-how entry view class.<br/>
 */
public class KnowhowEntryCheckItemView extends ViewPart implements ViewRefresh {

	/**
	 * View ID
	 */
	public static final String ID = "tubame.knowhow.maintenance.portability.ui.view.KnowhowEntryCheckItemView";

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(KnowhowEntryCheckItemView.class);

	/** viewer */
	private TableViewer viewer;
	
	/** comparator */
	private CheckItemViewComparator comparator;

	/** viewr inpudata */
	List<PortabilityKnowhowListViewOperation> checkItemData = null;

	/** chapter mapping data*/
	Map<String, String> categoryChapterMap = null;

	
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOGGER.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_CREATE_KNOWHOW_ENTRY_VIEW));

		viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		this.comparator = new CheckItemViewComparator();
		viewer.setComparator(this.comparator);
		
		// カラム設定
		for (SearchInfoViewEnum searchInfoViewEnum : SearchInfoViewEnum.values()) {
			TableColumn genColumn = new TableColumn(viewer.getTable(), SWT.LEFT);
			genColumn.setText(searchInfoViewEnum.getTitle());
			genColumn.setWidth(searchInfoViewEnum.getWidth());
			genColumn.addSelectionListener(getSelectionAdapter(genColumn, searchInfoViewEnum.getIndex()));
		}

		//コンテンツプロバイダ設定
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List) inputElement).toArray();
			}

		});

		//ラベルプロバイダ設定
		viewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener arg0) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener arg0) {
			}

			@Override
			public Image getColumnImage(Object arg0, int arg1) {
				return null;
			}

			@Override
			public String getColumnText(Object arg0, int index) {
				SearchInfoViewEnum searchInfoViewEnum = SearchInfoViewEnum.get(index);
				return getColumnTextBySearchInfoViewEnum(searchInfoViewEnum, arg0);
			}
		});

		viewer.addDoubleClickListener(new KnowhowEntryViewClickListener());
		
		//Tooltipを有効にする設定
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE); 
		
		IViewPart opendView = PluginUtil.isOpendView(PluginUtil.getKnowhowEntryViewId());
		if(opendView!=null){
			this.refresh();
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void refresh() {
		try {
			this.categoryChapterMap = createCategoryChapterMap();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		loadElement();
	}

	/**
	 * ノウハウエントリのデータをチェックアイテムエントリビュに設定する.
	 * @param entryViewConvetData
	 */
	public void insertKnowhowEntryViewData(List<PortabilityKnowhowListViewOperation> entryViewConvetData) {
		try {
			this.categoryChapterMap = createCategoryChapterMap();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		this.checkItemData = new LinkedList<PortabilityKnowhowListViewOperation>();
		searchCheckItemAddElement(entryViewConvetData, this.checkItemData);
		this.viewer.setInput(this.checkItemData);
	}
	
	/**
	 * チェックアイテムを選択.
	 * @param element
	 */
	public void selectElement(PortabilityKnowhowListViewOperation element) {
		CheckItemViewType checkItemViewType = (CheckItemViewType) element.getKnowhowViewType();
		TableItem[] items = viewer.getTable().getItems();
		int index = -1;
		for (int i = 0; i < items.length; i++) {
			TableItem tableItem = items[i];
			PortabilityKnowhowListViewData data = (PortabilityKnowhowListViewData) tableItem.getData();
			CheckItemViewType viewType = (CheckItemViewType) data.getKnowhowViewType();
			if (checkItemViewType.getRegisterKey().equals(viewType.getRegisterKey())) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			this.viewer.getTable().select(index);
		}
	}
	
	/**
	 * Set the item information on entry view.<br/>
	 * The display reads, in TreeViewer know-how XML file.<br/>
	 * Also performs together reflecting to know the editor.<br/>
	 * 
	 */
	private void loadElement() {
		this.checkItemData = new LinkedList<PortabilityKnowhowListViewOperation>();
		
		List<PortabilityKnowhowListViewOperation> inputEntry = PluginUtil.getKnowhowEntryViewTreeViewer()
				.getInputEntry();

		if(inputEntry != null){
			searchCheckItemAddElement(inputEntry, this.checkItemData);
			this.viewer.setInput(this.checkItemData);
		}
	}

	private Map<String, String> createCategoryChapterMap() throws Exception {
		MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil.getKnowhowEditor();
		if (knowhowMultiPageEditor == null) {
			return null;
		}
		String fileLocationFullPath = knowhowMultiPageEditor.getFileLocationFullPath();
		Map<String, String> targetMap = new HashMap();
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = null;
		XMLStreamReader reader = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(fileLocationFullPath));
			reader = factory.createXMLStreamReader(stream);
			String chapName = null;
			String categoryId = null;
			while (reader.hasNext()) {
				reader.next();
				switch (reader.getEventType()) {

				case XMLStreamConstants.START_ELEMENT:

					if (reader.getLocalName().equals("ChapterName")) {
						reader.next();
						chapName = reader.getText();
					} else if (reader.getLocalName().equals("ChapterCategoryRefKey")) {
						// KnowhowRefKey
						reader.next();
						categoryId = reader.getText();
						LOGGER.debug("KnowhowRefKey:" + categoryId + "," + chapName);
						targetMap.put(categoryId, chapName);
					}
					break;
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {

			}
		}
		return targetMap;
	}

	private void searchCheckItemAddElement(List<PortabilityKnowhowListViewOperation> inputEntry,
			List<PortabilityKnowhowListViewOperation> addTargets) {
		if (inputEntry != null) {
			for (PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation : inputEntry) {
				if (portabilityKnowhowListViewOperation.isCheckItem()) {
					addTargets.add(portabilityKnowhowListViewOperation);
					LOGGER.debug("checkItem find!!!"
							+ ((CheckItemViewType) portabilityKnowhowListViewOperation.getKnowhowViewType())
									.getRegisterName());
				}
				if (portabilityKnowhowListViewOperation.getChildList().size() != 0) {
					searchCheckItemAddElement(portabilityKnowhowListViewOperation.getChildList(), addTargets);
				}
			}
		}
	}

	private String getColumnTextBySearchInfoViewEnum(SearchInfoViewEnum searchInfoViewEnum, Object arg0) {
		CategoryViewType categoryViewType = null;
		CheckItemViewType checkItemViewType = null;
		SearchInfoViewType searchInfoViewType = null;
		String text = "";
		switch (searchInfoViewEnum) {
		// カテゴリ名
		case COLUMN0:
			categoryViewType = getCategoryViewType(arg0);
			text = categoryViewType.getRegisterName();
			break;

		// チェックアイテム名
		case COLUMN1:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getRegisterName();
			break;

		// 検索手順
		case COLUMN2:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getSearchProcess();
			break;

		// 検索実施
		case COLUMN3:
			checkItemViewType = getCheckItemViewType(arg0);
			text = Boolean.toString(checkItemViewType.isSearchExistance());
			break;

		// 要因
		case COLUMN4:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getPortabilityFactor();
			break;

		// 難易度
		case COLUMN5:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getProtabilityDegree();
			break;

		// 難易度詳細
		case COLUMN6:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getDegreeDetail();
			break;

		// 目視確認
		case COLUMN7:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getVisualConfirm();
			break;

		// ヒアリング確認
		case COLUMN8:
			checkItemViewType = getCheckItemViewType(arg0);
			text = checkItemViewType.getHearingConfirm();
			break;

		// 検索対象ファイル
		case COLUMN9:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getFileType();
			break;

		// 検索キーワード１
		case COLUMN10:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getSearchKeyword1();
			break;

		// 検索キーワード2
		case COLUMN11:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getSearchKeyword2();
			break;

		// pyモジュール
		case COLUMN12:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getPythonModule();
			break;

		// ライン数算出
		case COLUMN13:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : Boolean.toString(searchInfoViewType.isAppropriateLine());
			break;

		// 非算出理由
		case COLUMN14:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getAppropriateContext();
			break;

		// 不明/TODO
		case COLUMN15:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getUnKnownLine();
			break;

		// ライン数
		case COLUMN16:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getLineNumber();
			break;

		// ライン数根拠
		case COLUMN17:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getLineNumberContents();
			break;

		// 調査内容
		case COLUMN18:
			searchInfoViewType = getSearchInfoViewType(arg0);
			text = searchInfoViewType == null ? "" : searchInfoViewType.getInvestigation();
			break;

		// Rootチャプター
		case COLUMN19:
			try {
				text = getChapterName(arg0);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			break;
		}
		return text;
	}

	private String getChapterName(Object arg0) throws Exception {
		String chapName = "";
		if (arg0 instanceof PortabilityKnowhowListViewOperation) {
			PortabilityKnowhowListViewOperation viewOperation = (PortabilityKnowhowListViewOperation) arg0;
			if (viewOperation.isCheckItem()) {
				PortabilityKnowhowListViewOperation categoryParentEntry = viewOperation.getCategoryParentEntry();
				if (categoryParentEntry != null) {
					chapName = getChapterNameFromCategoryChapterMap(categoryParentEntry);
					if (chapName == null) {
						getChapterName(categoryParentEntry);
					}
				}
			}
		}
		return chapName;
	}

	private String getChapterNameFromCategoryChapterMap(PortabilityKnowhowListViewOperation categoryParentEntry) {
		CategoryViewType category = (CategoryViewType) categoryParentEntry.getKnowhowViewType();
		LOGGER.debug("category key:" + category.getRegisterKey());
		if (this.categoryChapterMap.keySet().contains(category.getRegisterKey())) {

			return this.categoryChapterMap.get(category.getRegisterKey());
		}
		return null;
	}

	private CheckItemViewType getCheckItemViewType(Object arg0) {
		if (arg0 instanceof PortabilityKnowhowListViewOperation) {
			PortabilityKnowhowListViewOperation viewOperation = (PortabilityKnowhowListViewOperation) arg0;
			if (viewOperation.isCheckItem()) {
				PortabilityKnowhowListViewOperation categoryParentEntry = viewOperation.getCategoryParentEntry();
				return (CheckItemViewType) viewOperation.getKnowhowViewType();
			}
		}
		return null;
	}
	
	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	private CategoryViewType getCategoryViewType(Object arg0) {
		if (arg0 instanceof PortabilityKnowhowListViewOperation) {
			PortabilityKnowhowListViewOperation viewOperation = (PortabilityKnowhowListViewOperation) arg0;
			if (viewOperation.isCheckItem()) {
				PortabilityKnowhowListViewOperation categoryParentEntry = viewOperation.getCategoryParentEntry();

				return (CategoryViewType) categoryParentEntry.getKnowhowViewType();
			}
		}
		return null;
	}

	private SearchInfoViewType getSearchInfoViewType(Object arg0) {
		if (arg0 instanceof PortabilityKnowhowListViewOperation) {
			PortabilityKnowhowListViewOperation viewOperation = (PortabilityKnowhowListViewOperation) arg0;
			if (viewOperation.isCheckItem()) {
				List<PortabilityKnowhowListViewOperation> childList = viewOperation.getChildList();
				if (childList.size() == 1) {
					PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation = childList.get(0);
					return (SearchInfoViewType) portabilityKnowhowListViewOperation.getKnowhowViewType();
				}
			}
		}
		return null;
	}
	
	enum SearchInfoViewEnum {

		COLUMN0(0, ResourceUtil.currentCategory, 100, 0),

		COLUMN1(1, ResourceUtil.checkItemInfoLabel, 125, 0),

		COLUMN2(2, ResourceUtil.searchProcess, 100, 0),

		COLUMN3(3, ResourceUtil.searchExistance, 75, 0),

		COLUMN4(4, ResourceUtil.protabilityFactor, 100, 0),

		COLUMN5(5, ResourceUtil.portabilityDegree, 75, 0),

		COLUMN6(6, ResourceUtil.degreeDetail, 100, 0),

		COLUMN7(7, ResourceUtil.visualConfirm, 125, 0),

		COLUMN8(8, ResourceUtil.hearingConfirm, 125, 0),

		COLUMN9(9, ResourceUtil.fileTypeLabel, 125, 0),

		COLUMN10(10, ResourceUtil.searchKeyword1Label, 125, 0),

		COLUMN11(11, ResourceUtil.searchKeyword2Label, 125, 0),

		COLUMN12(12, ResourceUtil.pythonModuleLabel, 125, 0),

		COLUMN13(13, ResourceUtil.appropriateLineLabel, 100, 0),

		COLUMN14(14, ResourceUtil.appropriateContents, 100, 0),

		COLUMN15(15, ResourceUtil.unKnownLineLabel, 100, 0),

		COLUMN16(16, ResourceUtil.lineNumber, 100, 0),

		COLUMN17(17, ResourceUtil.lineNumberContents, 100, 0),

		COLUMN18(18, ResourceUtil.investigationLabel, 100, 0),

		COLUMN19(19, ResourceUtil.rootChapter, 125, 0);

		/**
		 * Index column value
		 */
		private int index;

		/**
		 * Column title
		 */
		private String title;

		/**
		 * Column width
		 */
		private int width;

		/**
		 * Display level
		 */
		private int level;

		/**
		 * Constructor.<br/>
		 * Do not do anything.<br/>
		 * 
		 * @param index
		 *            CSV column Index value
		 */
		SearchInfoViewEnum(int index, String title, int width, int level) {
			this.index = index;
			this.title = title;
			this.width = width;
			this.level = level;
		}

		/**
		 * Get the index.<br/>
		 * 
		 * @return index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Get the column title.<br/>
		 * 
		 * @return Column title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Get the column width.<br/>
		 * 
		 * @return Column width
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * Get the display level.<br/>
		 * 
		 * @return Display level
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Get WSearchEditorEnum the specified instance.<br/>
		 * If the code does not exist, return NULL.<br/>
		 * 
		 * @param code
		 *            Code
		 * @return WSearchEditorEnum
		 */
		public static SearchInfoViewEnum get(int code) {
			for (SearchInfoViewEnum temp : SearchInfoViewEnum.values()) {
				if (temp.getIndex() == code) {
					return temp;
				}
			}
			return null;
		}

	}

	class CheckItemViewComparator extends ViewerComparator {

		private int columnIndex;
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (column == this.columnIndex) {
				direction = 1 - direction;
			} else {
				this.columnIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			TableViewer tableViewer = (TableViewer) viewer;
			ITableLabelProvider iTableLabelProvider = (ITableLabelProvider) tableViewer.getLabelProvider();
			String comp1 = iTableLabelProvider.getColumnText(e1, columnIndex);

			if (comp1 != null) {
				String comp2 = iTableLabelProvider.getColumnText(e2, columnIndex);
				int comp = comp1.compareTo(comp2);
				if (direction == DESCENDING) {
					comp = -comp;
				}
				return comp;
			}
			return 0;
		}

	}

}
