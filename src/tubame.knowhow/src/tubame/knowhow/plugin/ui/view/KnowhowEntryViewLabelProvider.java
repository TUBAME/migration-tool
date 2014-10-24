/*
 * KnowhowEntryViewLabelProvider.java
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

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;
import tubame.knowhow.util.ImageUtil;

/**
 * Label provider of know-how entry view.<br/>
 * Show the data that was in the (hierarchical) each level.<br/>
 * Class of the data and {@link PortabilityKnowhowListViewOperation} reference.<br/>
 */
public class KnowhowEntryViewLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(KnowhowEntryViewLabelProvider.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		KnowhowEntryViewLabelProvider.LOGGER.debug("[element]" + element);
		String text = CmnStringUtil.EMPTY;
		if (element instanceof PortabilityKnowhowListViewOperation) {
			PortabilityKnowhowListViewData row = (PortabilityKnowhowListViewData) element;
			text = row.getName();
		}
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object element) {
		KnowhowEntryViewLabelProvider.LOGGER.debug("[element]" + element);
		if (element instanceof PortabilityKnowhowListViewOperation) {
			PortabilityKnowhowListViewData row = (PortabilityKnowhowListViewData) element;
			// Category
			if (PortabilityKnowhowListViewData.LEVEL_FIRST == row.getLevel()) {
				Image categryImage = grantImageType(row);
				if (categryImage != null) {
					return categryImage;
				}
				return ImageUtil.getCategoryImage();
				// Know-how
			} else if (PortabilityKnowhowListViewData.LEVEL_SECOND == row.getLevel()) {
				return ImageUtil.getKnowhowImage();
				// Check items
			} else if (PortabilityKnowhowListViewData.LEVEL_THIRD == row.getLevel()) {
				return ImageUtil.getCheckItemImage();
				// Search information
			} else if (PortabilityKnowhowListViewData.LEVEL_FOURTH == row.getLevel()) {
				return getSearchInfoImage(row);
			}
		}
		return super.getImage(element);
	}

	/**
	 * Grant the image of the search information.<br/>
	 * 
	 * @param row
	 *            Line data
	 * @return Image
	 */
	private Image getSearchInfoImage(PortabilityKnowhowListViewData row) {
		SearchInfoViewType searchInfoType = (SearchInfoViewType) row.getKnowhowViewType();
		if (!searchInfoType.isAppropriateLine()) {
			return ImageUtil.getSearchInfoAppropriateImage();
		}
		return ImageUtil.getSearchInfoImage();
	}

	/**
	 * Grant of the image category.<br/>
	 * 
	 * @param row
	 *            Line data
	 * @return Image
	 */
	private Image grantImageType(PortabilityKnowhowListViewOperation row) {
		CategoryViewType categoryPage = (CategoryViewType) row.getKnowhowViewType();
		if (!categoryPage.isAppropriateFlag()) {
			return ImageUtil.getCategoryAppropriateImage();
		} else if (row.getParent() == null) {
			return null;
		} else {
			return grantImageType((PortabilityKnowhowListViewData) row.getParent());
		}
	}

	@Override
	public Image getColumnImage(Object element, int index) {
		
		Image image = null;
		switch (index) {
		case 0:
			image = this.getImage(element);
			break;
		default:
//			if(isElementLevelFouth(element)){
//				
//			}
			break;
		}
		return image;
	}

	@Override
	public String getColumnText(Object element, int index) {
		String text = "";
		switch (index) {
		case 0:
			text = this.getText(element);
			break;
		default:
			if(isElementLevelFouth(element)){
				text = getSearchDetailText(element,index);
			}
			break;
		}
		return text;
	}

	private String getSearchDetailText(Object element, int index) {
		String text = "";
		PortabilityKnowhowListViewData row = (PortabilityKnowhowListViewData) element;
		switch (index) {
		case 1:
			AbstractViewType knowhowViewType = row.getKnowhowViewType();
			if (knowhowViewType instanceof SearchInfoViewType) {
				SearchInfoViewType searchInfoViewType = (SearchInfoViewType) knowhowViewType;
				text = searchInfoViewType.getFileType();
			}
			break;
		default:
			break;
		}
		return text;
	}

	private boolean isElementLevelFouth(Object element) {
		PortabilityKnowhowListViewData row = (PortabilityKnowhowListViewData) element;
		if (PortabilityKnowhowListViewData.LEVEL_FOURTH == row.getLevel()) {
			return true;
		}
		return false;
	}

}
