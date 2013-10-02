/*
 * DifficultyEnum.java
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
package tubame.portability.model;

import java.io.IOException;

import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.util.DifficultyImageUtil;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Enumerated type that represents the degree of difficulty.<br/>
 * Type are as follows.<br/>
 * 
 * High Low Middle Unkown NOT_TRN
 * 
 */
public enum DifficultyEnum {

    /**
     * High
     */
    HIGH("High"), //$NON-NLS-1$
    /**
     * Low
     */
    LOW("Low"), //$NON-NLS-1$
    /**
     * Middle
     */
    MIDDLE("Middle"), //$NON-NLS-1$
    /**
     * Unknown
     */
    UNKNOWN("Unknown"), //$NON-NLS-1$
    /**
     * NOT_TRN
     */
    NOT_TRN("NOT_TRN"), //$NON-NLS-1$
    /**
     * NO_PORTABILITY
     */
    NO_PORTABILITY("-"); //$NON-NLS-1$

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DifficultyEnum.class);

    /**
     * Path of icon
     */
    private static final String ICON_PATH = "resources/icons"; //$NON-NLS-1$
    /**
     * File name of the icon
     */
    private static final String DIFFICULLTY = "Difficulty"; //$NON-NLS-1$
    /**
     * File extension icon
     */
    private static final String EXTENSION = ".png"; //$NON-NLS-1$
    /**
     * Index
     */
    private String name;

    /**
     * Constructor.<br/>
     * Generate an enum.<br/>
     * 
     * @param code
     *            Column index
     */
    private DifficultyEnum(String code) {
        name = code;
    }

    /**
     * Get the index.
     * 
     * @return Index
     */
    public String getName() {
        return name;
    }

    /**
     * Get DifficultyEnum the specified instance.<br/>
     * If the code does not exist, it returns a NULL.<br/>
     * 
     * @param code
     *            Code
     * @return DifficultyEnum
     */
    public static DifficultyEnum get(String code) {
        for (DifficultyEnum temp : DifficultyEnum.values()) {
            if (temp.getName().equals(code)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Get the icon that was in each image.<br/>
     * 
     * @return image
     */
    public Image getImage() {
        try {
            if (!"-".equals(getName())) {
                return DifficultyImageUtil.getImage(getPath()
                        + DifficultyEnum.DIFFICULLTY + getName()
                        + DifficultyEnum.EXTENSION);
            }
        } catch (IOException e) {
            // Processing Continue
            JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.ERR_GET_DIFFICULTY_ICON });
        }
        return null;
    }

    /**
     * Get the path that contains the icon.<br/>
     * 
     * @return Icon path
     * @throws IOException
     *             Plug-in folder acquisition failure
     */
    private String getPath() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(PluginUtil.getPluginDir());
        sb.append(FileUtil.FILE_SEPARATOR);
        sb.append(DifficultyEnum.ICON_PATH);
        sb.append(FileUtil.FILE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Get weight.<br/>
     * Get the weight of - difficulty degree (NOT_TRN > Low > Middle > High >
     * Unknown >).<br/>
     * 
     * @return NOT_TRN >1 Low >2 Middle >3 High >4 Unknown >5 ->6
     */
    public int getSortWeight() {
        switch (this) {
        case NOT_TRN:
            return 1;
        case LOW:
            return 2;
        case MIDDLE:
            return 3;
        case HIGH:
            return 4;
        case UNKNOWN:
            return 5;
        case NO_PORTABILITY:
            return 6;
        }
        return 0;
    }
}
