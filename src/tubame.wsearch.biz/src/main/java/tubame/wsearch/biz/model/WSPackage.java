/*
 * WSPackage.java
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

/**
 * It is a class that contains the package information.<br/>
 * Class names as a package, but with the exclusion of the results from the
 * analysis.<br/>
 * Used as a class that contains the regular expression pattern.<br/>
 */
public class WSPackage {

    /**
     * Package Name
     */
    private String name;

    /**
     * Package Type
     */
    private String type;

    /**
     * Whether or not valid
     */
    private boolean enabled;

    /**
     * Whether a user-defined package
     */
    private boolean userDefined;

    /**
     * Determine whether a user-defined package.<br/>
     * 
     * @return Whether a user-defined package
     */
    public boolean isUserDefined() {
        return userDefined;
    }

    /**
     * Set whether a user-defined package.<br/>
     * 
     * @param userDefined
     *            Whether a user-defined package
     */
    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }

    /**
     * Get the package name.<br/>
     * 
     * @return Package name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the package type.<br/>
     * 
     * @return Package type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the package name.<br/>
     * 
     * @param name
     *            Package Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Check whether or not valid.<br/>
     * 
     * @return The true if it is valid
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the whether valid.<br/>
     * 
     * @param enabled
     *            The true if it is valid
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Package Type
     * @param name
     *            Package Name
     * @param enabled
     *            The true if it is valid
     */
    public WSPackage(String type, String name, boolean enabled) {
        super();
        this.type = type;
        this.name = name;
        this.enabled = enabled;
    }

}
