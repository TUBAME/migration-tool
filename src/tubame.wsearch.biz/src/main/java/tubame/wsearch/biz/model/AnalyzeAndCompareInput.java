/*
 * AnalyzeAndCompareInput.java
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
package tubame.wsearch.biz.model;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * It is a class that contains the input information for the analysis and
 * comparison process of porting target.<br/>
 */
public class AnalyzeAndCompareInput {

    /**
     * The path to the output target
     */
    private String outputPath;

    /**
     * List of extensions analyze target
     */
    private List<String> extensions;

    /**
     * Map that holds the file analyze target list(Key search extension)
     */
    private Map<String, List<String>> targetsMap;

    /**
     * Map that holds the filter information(Key search extension)
     */
    private Map<String, List<Pattern>> filtersMap;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param outputPath
     *            The path to the output target
     */
    public AnalyzeAndCompareInput(String outputPath) {
        super();
        this.outputPath = outputPath;
    }

    /**
     * Get a list of extensions analyze target.<br/>
     * 
     * @return List of extensions analyze target
     */
    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * Set the list of extensions analyze target.<br/>
     * 
     * @param extensions
     *            List of extensions analyze target
     */
    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * Get a map that holds the Analyze object file list.<br/>
     * 
     * @return Map that holds the file analyze target list
     */
    public Map<String, List<String>> getTargetsMap() {
        return targetsMap;
    }

    /**
     * set the map that holds the Analyze object file list.<br/>
     * 
     * @param targetsMap
     *            Map that holds the file analyze target list
     */
    public void setTargetsMap(Map<String, List<String>> targetsMap) {
        this.targetsMap = targetsMap;
    }

    /**
     * Get a map that holds the filter information.<br/>
     * 
     * @return Map that holds the filter information
     */
    public Map<String, List<Pattern>> getFiltersMap() {
        return filtersMap;
    }

    /**
     * Set the map that holds the filter information.<br/>
     * 
     * @param filtersMap
     *            Map that holds the filter information
     */
    public void setFiltersMap(Map<String, List<Pattern>> filtersMap) {
        this.filtersMap = filtersMap;
    }

    /**
     * Get the path to the output target.<br/>
     * 
     * @return The path to the output target
     */
    public String getOutputPath() {
        return outputPath;
    }
}
