/*
 * DifficultyImageUtil.java
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
package tubame.portability.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

/**
 * Class that manages the difficulty Image.<br/>
 * Held in static area Image read once and return the Image that does not
 * generate an Image instance, <br/>
 * holds in the case of the Image.<br/>
 */
public class DifficultyImageUtil {

    /**
     * Image retention Map <Key:Fairupasu value:Image>
     */
    private static Map<String, Image> imageMap = new HashMap<String, Image>();

    /**
     * Get the difficulty icon.<br/>
     * Get Image of the specified file path (the icon).<br/>
     * 
     * @param filePath
     *            Image file path
     * @return image
     */
    public static Image getImage(String filePath) {
        if (!DifficultyImageUtil.imageMap.containsKey(filePath)) {
            Image image = new Image(null, filePath);
            DifficultyImageUtil.imageMap.put(filePath, image);
        }
        return DifficultyImageUtil.imageMap.get(filePath);
    }
}
