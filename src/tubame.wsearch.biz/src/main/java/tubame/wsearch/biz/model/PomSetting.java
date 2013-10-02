/*
 * PomSetting.java
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
 * It is a class that contains the basic settings of the POM file.<br/>
 */
public class PomSetting {

    /**
     * Group ID
     */
    private String groupId;
    /**
     * Artifact ID
     */
    private String artifactId;
    /**
     * Version
     */
    private String version;
    /**
     * Description
     */
    private String description;

    /**
     * Get the description.<br/>
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
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
     * Get the group ID.<br/>
     * 
     * @return group ID
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Set the group ID.<br/>
     * 
     * @param groupId
     *            Group ID
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Get the artifact ID.
     * 
     * @return Artifact ID
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Set the artifact ID.<br/>
     * 
     * @param artifactId
     *            Artifact ID
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Get the version.<br/>
     * 
     * @return Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version.<br/>
     * 
     * @param version
     *            Version
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
