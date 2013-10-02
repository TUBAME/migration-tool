/*
 * ColorUtil.java
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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class to color (Color) management.<br/>
 */
public class ColorUtil {

    /**
     * Display
     */
    private static final Display DISPLAY = PluginUtil.getStandardDisplay();

    /**
     * Color (red)
     */
    private static final Color RED = new Color(DISPLAY, 255, 0, 0);;
    /**
     * Color (blue)
     */
    private static final Color BLUE = new Color(DISPLAY, 0, 0, 255);;
    /**
     * Color (yellow-green)
     */
    private static final Color YELLOW_GREEN = new Color(DISPLAY, 187, 241, 186);;
    /**
     * Color (white)
     */
    private static final Color WHITE = new Color(DISPLAY, 255, 255, 255);
    /**
     * Color (green)
     */
    private static final Color GREEN = new Color(DISPLAY, 68, 155, 4);
    /**
     * Color (green)
     */
    private static final Color POP_GREEN = new Color(DISPLAY, 100, 225, 6);
    /**
     * Check items OKColor
     */
    private static final Color OK_STATUS = new Color(DISPLAY, 255, 102, 152);
    /**
     * Check items NGColor
     */
    private static final Color NG_STATUS = new Color(DISPLAY, 180, 177, 179);

    /**
     * Constructor.<br/>
     * Another class can not be instantiated.<br/>
     * 
     */
    private ColorUtil() {
        // no operation
    }

    /**
     * Get the red.<br/>
     * 
     * @return Red
     */
    public static Color getRed() {
        return ColorUtil.RED;
    }

    /**
     * Get the blue.<br/>
     * 
     * @return Blue
     */
    public static Color getBule() {
        return ColorUtil.BLUE;
    }

    /**
     * Get yellow-green.<br/>
     * 
     * @return Yellow-green
     */
    public static Color getYellowGreen() {
        return ColorUtil.YELLOW_GREEN;
    }

    /**
     * Get white.<br/>
     * 
     * @return White
     */
    public static Color getWhite() {
        return ColorUtil.WHITE;
    }

    /**
     * Get the green.<br/>
     * 
     * @return Green
     */
    public static Color getGreen() {
        return ColorUtil.GREEN;
    }

    /**
     * Get the green.<br/>
     * 
     * @return Green
     */
    public static Color getPopGreen() {
        return ColorUtil.POP_GREEN;
    }

    /**
     * Get the color of the OK status of confirmation item.<br/>
     * 
     * @return Color OK status
     */
    public static Color getConfirmItemStatusOkColor() {
        return ColorUtil.OK_STATUS;
    }

    /**
     * Get the color of the NG status of confirmation item.<br/>
     * 
     * @return Color NG status
     */
    public static Color getConfirmItemStatusNgColor() {
        return ColorUtil.NG_STATUS;
    }

    /**
     * Get the color of the unconfirmed status of confirmation item.<br/>
     * 
     * @return Color unconfirmed status
     */
    public static Color getConfirmItemStatusNonColor() {
        return ColorUtil.WHITE;
    }
}
