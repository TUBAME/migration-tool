/*
 * ImageUtil.java
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
package org.tubame.knowhow.util;

import java.util.HashMap;
import java.util.Map;

import org.tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;

/**
 * Utility classes for image Image etc.<br/>
 */
public final class ImageUtil {
    /** ImageMap <file name, Image> */
    private static Map<String, Image> imageMap = new HashMap<String, Image>();

    /**
     * Constructor.<br/>
     * 
     */
    private ImageUtil() {
        // no operation
    }

    /**
     * Get the category icon.<br/>
     * 
     * @return Category icon image
     */
    public static Image getCategoryImage() {
        return ImageUtil.getImage(ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.PATH_CATEGORY_ICON));
    }

    /**
     * Get the category non-calculation icon.<br/>
     * 
     * @return Category non-calculation icon image
     */
    public static Image getCategoryAppropriateImage() {
        return ImageUtil
                .getImage(ApplicationPropertiesUtil
                        .getProperty(ApplicationPropertiesUtil.PATH_CATEGORY_APPROPRIATE_ICON));
    }

    /**
     * Get the know-how icon.<br/>
     * 
     * @return Know-how icon image
     */
    public static Image getKnowhowImage() {
        return ImageUtil.getImage(ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.PATH_KNOWHOW_ICON));
    }

    /**
     * Get search information icon.<br/>
     * 
     * @return Search information icon image
     */
    public static Image getSearchInfoImage() {
        return ImageUtil.getImage(ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.PATH_SEARCH_ICON));
    }

    /**
     * Get search information non-recognized icon.<br/>
     * 
     * @return Search Non-recognized icon image
     */
    public static Image getSearchInfoAppropriateImage() {
        return ImageUtil
                .getImage(ApplicationPropertiesUtil
                        .getProperty(ApplicationPropertiesUtil.PATH_SEARCH_APPROPRIATE_ICON));
    }

    /**
     * Get heading icon.<br/>
     * 
     * @return Heading icon image
     */
    public static Image getChapterImage() {
        return ImageUtil.getImage(ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.PATH_CHAPTER_ICON));
    }

    /**
     * Get a check item icon.<br/>
     * 
     * @return Check item icon image
     */
    public static Image getCheckItemImage() {
        return ImageUtil.getImage(ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.PATH_CHECKITEM_ICON));
    }

    /**
     * Get icon.<br/>
     * 
     * @param key
     *            Icon file name
     * @return Get icon
     */
    private static Image getImage(String key) {
        if (!ImageUtil.imageMap.containsKey(key)) {
            ImageUtil.imageMap.put(key, ImageUtil.getImageInProject(key));
        }
        Image image = ImageUtil.imageMap.get(key);

        return image;
    }

    /**
     * Get the image in the project.<br/>
     * Please use this method when generating an image.<br/>
     * 
     * @param targetPath
     *            Path from the project
     * @return Image
     */
    private static Image getImageInProject(String targetPath) {
        try {
            return new Image(null, PluginUtil.getPluginDir() + targetPath);
        } catch (SWTException e) {
            return null;
        }
    }
}
