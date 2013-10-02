/*
 * LibraryModel.java
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

import java.io.File;

/**
 * It is a class that contains the basic information of the library.<br/>
 */
public class LibraryModel {

    /**
     * The type library that defines whether the Maven library.<br/>
     */
    public enum LIBRARY_TYPE {
        MAVEN, NOT_MAVEN
    };

    /**
     * It is a type that defines whether a portable source libraries whether the
     * porting.<br/>
     */
    public enum TARGET_TYPE {
        SRC, DEST
    };

    /**
     * Porting library name
     */
    private String libName;

    /**
     * The directory or path, of ZIP files that make up the library
     */
    private String path;

    /**
     * Library that is located is whether maven format
     */
    private LIBRARY_TYPE type;

    /**
     * The file currently being processed
     */
    private File currentTarget;

    /**
     * The directory where the file currently being processed there is a
     * (relative). You can use it to set the core
     */
    private String targetDirectory;

    /**
     * This is the POM file currently being processed
     */
    private File currentPom;

    /**
     * True if extracted to a temporary directory in original ZIP file
     */
    private boolean isUnziped;

    /**
     * Type of whether the original transplant whether the porting
     */
    private TARGET_TYPE targetType;

    /**
     * Whether cache of analysis has already been
     */
    private boolean isCache;

    /**
     * Get the library name.<br/>
     * 
     * @return libName Library name
     */
    public String getLibName() {
        return libName;
    }

    /**
     * Get the directory or path, of ZIP files that make up the library.<br/>
     * 
     * @return path Directory or path, of ZIP files that make up the library
     */
    public String getPath() {
        return path;
    }

    /**
     * Library that is located gets whether maven format.<br/>
     * 
     * @return type Library that is located whether maven format
     */
    public LIBRARY_TYPE getType() {
        return type;
    }

    /**
     * Get the file currently being processed.<br/>
     * 
     * @return currentTarget File currently being processed
     */
    public File getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Get the (relative) directory where the file currently being processed
     * there.<br/>
     * 
     * @return targetDirectory The directory where the file currently being
     *         processed is present (relative)
     */
    public String getTargetDirectory() {
        return targetDirectory;
    }

    /**
     * Get the POM file currently being processed.<br/>
     * 
     * @return currentPom POM file currently being processed
     */
    public File getCurrentPom() {
        return currentPom;
    }

    /**
     * Get true if you extracted to a temporary directory in original ZIP file.<br/>
     * 
     * @return isUnziped True if you extracted to a temporary directory in
     *         original ZIP file
     */
    public boolean isUnziped() {
        return isUnziped;
    }

    /**
     * Get the type of what a portable based whether the porting.<br/>
     * 
     * @return targetType Type of whether the original transplant whether the
     *         porting
     */
    public TARGET_TYPE getTargetType() {
        return targetType;
    }

    /**
     * Set the library name.
     * 
     * @param libName
     *            Library name
     */
    public void setLibName(String libName) {
        this.libName = libName;
    }

    /**
     * Set the directory or path, of ZIP files that make up the library.<br/>
     * 
     * @param path
     *            Directory or path, of ZIP files that make up the library
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Library, which is located to set whether or not the maven format.<br/>
     * 
     * @param type
     *            Library that is located whether maven format
     */
    public void setType(LIBRARY_TYPE type) {
        this.type = type;
    }

    /**
     * Set the file currently being processed.<br/>
     * 
     * @param currentTarget
     *            File currently being processed
     */
    public void setCurrentTarget(File currentTarget) {
        this.currentTarget = currentTarget;
    }

    /**
     * Set the (relative) directory where the file currently being processed
     * there.<br/>
     * 
     * @param targetDirectory
     *            The directory where the file currently being processed is
     *            present (relative)
     */
    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**
     * Set the POM file currently being processed.<br/>
     * 
     * @param currentPom
     *            POM file currently being processed
     */
    public void setCurrentPom(File currentPom) {
        this.currentPom = currentPom;
    }

    /**
     * Set true if you extracted to a temporary directory in original ZIP file.<br/>
     * 
     * @param isUnziped
     *            True if you extracted to a temporary directory in original ZIP
     *            file
     */
    public void setUnziped(boolean isUnziped) {
        this.isUnziped = isUnziped;
    }

    /**
     * Set the type of what a portable based on whether the porting.
     * 
     * @param targetType
     *            Type of whether the original transplant whether the porting
     */
    public void setTargetType(TARGET_TYPE targetType) {
        this.targetType = targetType;
    }

    /**
     * Check whether the cache of parsed.<br/>
     * 
     * @return True if the cache the parsed
     */
    public boolean isCache() {
        return isCache;
    }

    /**
     * Set whether cache the parsed.<br/>
     * 
     * @param isCache
     *            Whether cache of analysis has already been
     */
    public void setCache(boolean isCache) {
        this.isCache = isCache;
    }
}
