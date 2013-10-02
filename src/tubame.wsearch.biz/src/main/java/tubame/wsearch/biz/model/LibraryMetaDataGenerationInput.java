/*
 * LibraryMetaDataGenerationInput.java
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
import java.util.Set;

/**
 * It is a class that contains the input information for the library information
 * generation process.<br/>
 */
public class LibraryMetaDataGenerationInput {

    /**
     * Get a list of models that contains basic information about the library.<br/>
     * 
     * @return List of models that contains basic information about the library
     */
    public List<LibraryModel> getLibraryModels() {
        return libraryModels;
    }

    /**
     * Get the path of the import file information cache Migrated source.<br/>
     * 
     * @return Path of import information cache file Migrated source
     */
    public String getAnalyzerImportCachePath() {
        return analyzerImportCachePath;
    }

    /**
     * List of models that contains basic information about the library
     */
    private List<LibraryModel> libraryModels;

    /**
     * Path of import information cache file Migrated source
     */
    private String analyzerImportCachePath;

    /**
     * Path the underlying of the directory in which you want to write a library
     * information cache
     */
    private String libraryCacheRootPath;

    /**
     * Temp files deployment path to create a library information cache
     */
    private String libraryCacheTempPath;

    /**
     * True if you want to overwrite the existing cache information
     */
    private boolean overwrite = false;

    /**
     * Get temporary file deployment path for creating a library information
     * cache.<br/>
     * 
     * @return Temp files deployment path to create a library information cache
     */
    public String getLibraryCacheTempPath() {
        return libraryCacheTempPath;
    }

    /**
     * Set the temp files for deployment path for creating a library information
     * cache.<br/>
     * 
     * @param libraryCacheTempPath
     *            Temp files deployment path to create a library information
     *            cache
     */
    public void setLibraryCacheTempPath(String libraryCacheTempPath) {
        this.libraryCacheTempPath = libraryCacheTempPath;
    }

    /**
     * Destination directory
     */
    private String outputDir;

    /**
     * Get the destination directory.<br/>
     * 
     * @return Destination directory
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Library information currently being processed
     */
    private LibraryModel currentLibrary;

    /**
     * Set the destination directory.<br/>
     * 
     * @param outputDir
     *            Destination directory
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Filter information of library information searches
     */
    private Set<SearchFilter> librarySearchFilters;

    /**
     * Get the filter information for library information searches.<br/>
     * 
     * @return Filter information of library information searches
     */
    public Set<SearchFilter> getLibrarySearchFilters() {
        return this.librarySearchFilters;
    }

    /**
     * Set the filter information for library information searches.<br/>
     * 
     * @param librarySearchFilters
     *            Filter information of library information searches
     */
    public void setLibrarySearchFilters(Set<SearchFilter> librarySearchFilters) {
        this.librarySearchFilters = librarySearchFilters;
    }

    /**
     * Get the library information is currently being processed.<br/>
     * 
     * @return Library information currently being processed
     */
    public LibraryModel getCurrentLibrary() {
        return this.currentLibrary;
    }

    /**
     * Set the library information is currently being processed.<br/>
     * 
     * @param library
     *            Library information currently being processed
     */
    public void setCurrentLibrary(LibraryModel library) {
        this.currentLibrary = library;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param libraryModels
     *            List of models that contains basic information about the
     *            library
     * @param analyzerImportCachePath
     *            Path of import information cache file Migrated source
     * @param libraryCacheRootPath
     *            Path the underlying of the directory in which you want to
     *            write a library information cache
     * @param overwrite
     *            True if you want to overwrite the existing cache information
     */
    public LibraryMetaDataGenerationInput(List<LibraryModel> libraryModels,
            String analyzerImportCachePath, String libraryCacheRootPath,
            boolean overwrite) {
        super();
        this.libraryModels = libraryModels;
        this.analyzerImportCachePath = analyzerImportCachePath;
        this.libraryCacheRootPath = libraryCacheRootPath;
        this.overwrite = overwrite;
    }

    /**
     * Get true if you want to overwrite the existing cache information.<br/>
     * 
     * @return True if you want to overwrite the existing cache information
     */
    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Set true if you want to overwrite the existing cache information.<br/>
     * 
     * @param overwrite
     *            True if you want to overwrite the existing cache information
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * Get the path to the underlying of the directory in which you want to
     * write a library cache information.<br/>
     * 
     * @return Path the underlying of the directory in which you want to write a
     *         library information cache
     */
    public String getLibraryCacheRootPath() {
        return libraryCacheRootPath;
    }

    /**
     * Get the path to the underlying of the directory in which you want to
     * write a library cache information.<br/>
     * 
     * @param libraryCacheRootPath
     *            Path the underlying of the directory in which you want to
     *            write a library information cache
     */
    public void setLibraryCacheRootPath(String libraryCacheRootPath) {
        this.libraryCacheRootPath = libraryCacheRootPath;
    }

    /**
     * Set the model list that contains basic information about the library.<br/>
     * 
     * @param libraryModels
     *            List of models that contains basic information about the
     *            library
     */
    public void setLibraryModels(List<LibraryModel> libraryModels) {
        this.libraryModels = libraryModels;
    }
}
