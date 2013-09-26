/*
 * ConvertDocBookFilePath.java
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
package org.tubame.knowhow.biz.logic.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Converts the URL to use the File the file path.<br/>
 */
public class ConvertDocBookFilePath extends DocBookXMLReader {

    /**
     * {@inheritDoc}
     */
    @Override
    protected URL getUrl(String filePath) {
        try {
            return new File(filePath).toURI().toURL();
        } catch (MalformedURLException e) {
            new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.EMPTY_MESSAGE));
        }
        return null;
    }
}
