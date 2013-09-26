/*
 * LibraryRepository.java
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
package org.tubame.wsearch.models;

/**
 * Class to store the repository information in the library.<br/>
 */
public class LibraryRepository {

    /**
     * Library name
     */
    private String name;
    /**
     * URL library
     */
    private String url;
    /**
     * Description
     */
    private String description;

    /**
     * Whether MAVEN library
     */
    private boolean mavenType;

    /**
     * Whether embedded plug-in
     */
    private boolean pluginEmbedded;

    /**
     * Whether cash is ZIP compression
     */
    private boolean zipCache;

    /**
     * Display order
     */
    private int order;

    /**
     * Get the order.<br/>
     * 
     * @return Display order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Set the display order.<br/>
     * 
     * @param order
     *            Display order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Determine if the cache is ZIP compression.<br/>
     * 
     * @return Determine the cache that is ZIP compression
     */
    public boolean isZipCache() {
        return zipCache;
    }

    /**
     * Set whether the cache was a ZIP archive.<br/>
     * 
     * @param zipCache
     *            Determine the cache that is ZIP compression
     */
    public void setZipCache(boolean zipCache) {
        this.zipCache = zipCache;
    }

    /**
     * Determine whether an embedded plug-in.<br/>
     * 
     * @return Determine if embedded plug-in
     */
    public boolean isPluginEmbedded() {
        return pluginEmbedded;
    }

    /**
     * Set whether embedded plug-in.<br/>
     * 
     * @param pluginEmbedded
     *            Determine if embedded plug-in
     */
    public void setPluginEmbedded(boolean pluginEmbedded) {
        this.pluginEmbedded = pluginEmbedded;
    }

    /**
     * Cache whether it is held
     */
    private boolean cache;

    /**
     * Determine whether you have the cash.<br/>
     * 
     * @return Determine if it has a cache
     */
    public boolean hasCache() {
        return cache;
    }

    /**
     * Set whether the cache is being held.<br/>
     * 
     * @param cache
     *            Determine if it has a cache
     */
    public void setCache(boolean cache) {
        this.cache = cache;
    }

    /**
     * Enabled or disabled
     */
    private boolean enabled;

    /**
     * Constructor.<br/>
     * 
     * @param name
     *            Library name
     * @param url
     *            URL library
     * @param description
     *            Description
     * @param enabled
     *            Enabled or disabled
     * @param mavenType
     *            Whether MAVEN library
     * @param pluginEmbedded
     *            Whether embedded plug-in
     */
    public LibraryRepository(String name, String url, String description,
            boolean enabled, boolean mavenType, boolean pluginEmbedded) {
        super();
        this.name = name;
        this.url = url;
        this.description = description;
        this.enabled = enabled;
        this.mavenType = mavenType;
        this.pluginEmbedded = pluginEmbedded;
    }

    /**
     * Set the library name.<br/>
     * 
     * @param name
     *            Library name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the library URL.<br/>
     * 
     * @param url
     *            Library URL.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Set the description.<br/>
     * 
     * @param description
     *            Description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Determine whether MAVEN library.<br/>
     * 
     * @return Determine if MAVEN library
     */
    public boolean isMavenType() {
        return mavenType;
    }

    /**
     * Set whether MAVEN library.<br/>
     * 
     * @param mavenType
     *            Determine if MAVEN library
     */
    public void setMavenType(boolean mavenType) {
        this.mavenType = mavenType;
    }

    /**
     * Determine whether valid.<br/>
     * 
     * @return Determine the validity of
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get description.<br/>
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set valid or not.<br/>
     * 
     * @param enabled
     *            Determine the validity of
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the library name.<br/>
     * 
     * @return Library name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the library URL.<br/>
     * 
     * @return URL library
     */
    public String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Repository [name=" + name + ", url=" + url + ", enabled="
                + enabled + "]";
    }
}
