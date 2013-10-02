/*
 * SearchFilter.java
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

/**
 * This is a class to store the filter information.<br/>
 */
public class SearchFilter {

    /**
     * Filter name
     */
    private String name;

    /**
     * List of extensions
     */
    private List<Extension> targets;

    /**
     * List of packages
     */
    private List<WSPackage> packages;

    /**
     * True if the filter is enabled
     */
    private boolean enabled;

    /**
     * True if the parent element
     */
    private boolean parent;

    /**
     * Determine whether the parent element.
     * 
     * @return True if the parent element
     */
    public boolean isParent() {
        return parent;
    }

    /**
     * Set whether the parent element.<br/>
     * 
     * @param parent
     *            True if the parent element
     */
    public void setParent(boolean parent) {
        this.parent = parent;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param name
     *            Filter name
     * @param targets
     *            List of extensions
     * @param packages
     *            List of packages
     * @param b
     *            Valid if true
     */
    public SearchFilter(String name, List<Extension> targets,
            List<WSPackage> packages, boolean b) {
        super();
        this.name = name;
        this.targets = targets;
        this.packages = packages;
        this.enabled = b;
    }

    /**
     * Get a list of packages.<br/>
     * 
     * @return List of packages
     */
    public List<WSPackage> getPackages() {
        return packages;
    }

    /**
     * Set the list of packages.<br/>
     * 
     * @param packages
     *            List of packages
     */
    public void setPackages(List<WSPackage> packages) {
        this.packages = packages;
    }

    /**
     * Determine whether this filter enabled.<br/>
     * 
     * @return True if the filter is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set this filter is valid.<br/>
     * 
     * @param enabled
     *            True if the filter is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get a list of extensions.<br/>
     * 
     * @return List of extensions
     */
    public List<Extension> getTargets() {
        return targets;
    }

    /**
     * Set the list of extensions.<br/>
     * 
     * @param targets
     *            List of extensions
     */
    public void setTargets(List<Extension> targets) {
        this.targets = targets;
    }

    /**
     * Get the filter name.<br/>
     * 
     * @return Filter name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the filter name.<br/>
     * 
     * @param name
     *            Filter name
     */
    public void setName(String name) {
        this.name = name;
    }
}
