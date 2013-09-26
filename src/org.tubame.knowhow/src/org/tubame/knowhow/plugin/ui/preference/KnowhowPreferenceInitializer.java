/*
 * KnowhowPreferenceInitializer.java
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
package org.tubame.knowhow.plugin.ui.preference;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.Activator;

/**
 * Class to manage the know-how creating XML preference.<br/>
 */
public class KnowhowPreferenceInitializer extends AbstractPreferenceInitializer {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowPreferenceInitializer.class);
    /** Know-how XML path */
    public static final String KNOWHOW_XML = "KNOWHOW_XML";
    /** Transplant know-how subject name */
    public static final String KNOWHOW_SUBJECT_NAME = "KNOWHOW_SUBJECT_NAME";

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        // no operation
    }

    /**
     * Get the know-how XML file path.<br/>
     * 
     * @return Know-how XML Path
     */
    public static String getKnowhowXml() {
        KnowhowPreferenceInitializer.LOGGER.debug(CmnStringUtil.EMPTY);

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String temp = store.getString(KNOWHOW_XML);
        if (CmnStringUtil.isEmpty(temp)) {
            return CmnStringUtil.EMPTY;
        }
        return temp;
    }

    /**
     * Get the know-how transplant subject name.<br/>
     * 
     * @return Transplant know-how subject name
     */
    public static String getKnowhowSubjectName() {
        KnowhowPreferenceInitializer.LOGGER.debug(CmnStringUtil.EMPTY);

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String temp = store.getString(KNOWHOW_SUBJECT_NAME);
        if (CmnStringUtil.isEmpty(temp)) {
            return CmnStringUtil.EMPTY;
        }
        return temp;
    }

    /**
     * Get the know-how XML file path.<br/>
     * 
     * @param filePath
     *            Know-how XML Path
     */
    public static void setKnowhowXml(String filePath) {
        KnowhowPreferenceInitializer.LOGGER.debug("[filePath]" + filePath);

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(KNOWHOW_XML, filePath);
    }

    /**
     * Set the transplant know-how subject name.<br/>
     * 
     * @param subjectName
     *            Transplant know-how subject name
     */
    public static void setKnowhowSubjectName(String subjectName) {
        KnowhowPreferenceInitializer.LOGGER
                .debug("[subjectName]" + subjectName);

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(KNOWHOW_SUBJECT_NAME, subjectName);
    }

}
