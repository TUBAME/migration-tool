/*
 * Convertor.java
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
package tubame.portability.logic.convert.command;

import tubame.portability.model.generated.model.ConvertType;
import tubame.portability.model.generated.model.WebLogic;

/**
 * Conversion process interface to JBoss from WebLogic.<br/>
 * Processing to convert The implements this interface.<br/>
 */
public interface Convertor {

    /**
     * Run the conversion process.<br/>
     * Conversion process to JBoss from WebLogic.<br/>
     * 
     * @param srcFilePath
     *            Source file path
     * @param destFilePath
     *            Convert file path
     * @param pluginDir
     *            Plug-in path
     * @throws Exception
     *             Exception
     */
    public void doConvert(String srcFilePath, String destFilePath,
            String pluginDir) throws Exception;

    /**
     * Get the {@link ConvertType} converter type.<br/>
     * 
     * @return Converter
     */
    public ConvertType getConverType();

    /**
     * Get the weblogic version.<br/>
     * Override if the converter process is dependent on the WebLogic version
     * {@link WebLogic}.<br/>
     * 
     * @return Weblogic version
     */
    public WebLogic getWebLogicVersion();

    /**
     * Get the name of the file you want to convert.<br/>
     * If null, the transformation in the input file name.<br/>
     * 
     * @return File name
     */
    public String getWriteFileName();

    /**
     * Initialization process.<br/>
     * Pre processing before calling the convert method.<br/>
     * 
     */
    public void preConvert();

}
