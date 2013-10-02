/*
 * Extension.java
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
 * It is a class that contains the extension information.<br/>
 */
public class Extension {

    /**
     * Extension name
     */
    private String name;

    /**
     * Whether to enable the extraction result of this extension
     */
    private boolean enabled;

    /**
     * Whether extension of user-defined
     */
    private boolean userDefined;

    /**
     * Determine whether the extension of the user-defined.<br/>
     * 
     * @return True if the extension of the user-defined
     */
    public boolean isUserDefined() {
        return userDefined;
    }

    /**
     * Set whether the extension of the user-defined.<br/>
     * 
     * @param userDefined
     *            True if the extension of the user-defined
     */
    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }

    /**
     * Reference information for the child element
     */
    private String childRef;

    /**
     * Get the reference information for the child element.<br/>
     * 
     * @return Reference information for the child element
     */
    public String getChildRef() {
        return childRef;
    }

    /**
     * Set the reference information for the child element.<br/>
     * 
     * @param childRef
     *            Reference information for the child element
     */
    public void setChildRef(String childRef) {
        this.childRef = childRef;
    }

    /**
     * Get the extension name.<br/>
     * 
     * @return Extension name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the extension name.<br/>
     * 
     * @param name
     *            Extension name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Determine whether to enable the extraction result of this extension.<br/>
     * 
     * @return True if you want to enable the extraction result of the extension
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the Whether or not to allow the extraction result of extension.<br/>
     * 
     * @param enabled
     *            True if you want to enable the extraction result of the
     *            extension
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param name
     *            Extension name
     * @param enabled
     *            True if you want to enable the extraction result of the
     *            extension
     */
    public Extension(String name, boolean enabled) {
        super();
        this.name = name;
        this.enabled = enabled;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param name
     *            Extension name
     * @param enabled
     *            True if you want to enable the extraction result of the
     *            extension
     * @param userDefinePossible
     *            True if the extension of the user-defined
     */
    public Extension(String name, boolean enabled, boolean userDefinePossible) {
        super();
        this.name = name;
        this.enabled = enabled;
        this.userDefined = userDefinePossible;
    }

}
