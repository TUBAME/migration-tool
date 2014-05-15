/*
 * AbstractSearchToolWithProgress.java
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
package tubame.portability.plugin.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import tubame.common.util.CmnFileUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.logic.JbmAccessFactory;
import tubame.portability.util.CsvUtil;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Run the (python program) search process, and writes jbm file (search result
 * file) search results.<br/>
 */
public abstract class AbstractSearchToolWithProgress implements
        IRunnableWithProgress {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractSearchToolWithProgress.class);

    /**
     * Comparison message of external module failure
     */
    private static final String PYTHON_ERROR = "Traceback (";

    /**
     * Character set of error data
     */
    private static final String CHAR_SET = "UTF-8";

    /**
     * Search keyword file path
     */
    private final String inputKeywordFilePath;

    /**
     * Path of the search target folder
     */
    private final String inputDirectory;

    /**
     * Search result output destination file path
     */
    private final String outputFilePath;

    /**
     * The decision whether the output file
     */
    private boolean isFileOut;

    /**
     * Constructor. Specify search directory, Search keyword file, the output
     * destination file.<br/>
     * 
     * @param target
     *            Search directory
     * @param keywordFilePath
     *            Search files
     * @param outFilePath
     *            Search result output destination file path
     * 
     */
    public AbstractSearchToolWithProgress(String target,
            String keywordFilePath, String outFilePath) {
        inputDirectory = target;
        inputKeywordFilePath = keywordFilePath;
        outputFilePath = outFilePath;
    }

    /**
     * Get the path of PythonExe.<br/>
     * 
     * @return Path of PythonExe
     * @throws IOException
     *             Plugin directory acquisition failure
     */
    public abstract String getPythonExePath() throws IOException;

    /**
     * Get the path to the Python logic.<br/>
     * 
     * @return Python logic path
     * @throws IOException
     *             Plugin directory acquisition failure
     */
    public abstract String getPythonModulePath() throws IOException;

    /**
     * Get the title of the progress status screen.<br/>
     * 
     * @return Title
     */
    public abstract String getProgressTitle();

    /**
     * Get a string of paths in the workspace, and unified "\" the file
     * delimiter.<br/>
     * 
     * @return Workspace path
     */
    private String getWorkspacePath() {
        String root = ResourcesPlugin.getWorkspace().getRoot().getLocation()
                .toString();
        return root.replace(StringUtil.SLASH, FileUtil.FILE_SEPARATOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        isFileOut = false;
        Process process = null;
        InputStream inputStream = null;
        BufferedReader reader = null;

        if (!CmnFileUtil.isFileSize(inputKeywordFilePath)) {
            // View the log to return if the search keyword file is empty.
            JbmException.outputExceptionLog(null, LOGGER, ERROR_LEVEL.ERROR,
                    MessageUtil.ERR_NULL_SEARCHKEYWORD_FILE);
            return;
        }

        try {
            // Title Settings
            monitor.beginTask(getProgressTitle(), IProgressMonitor.UNKNOWN);
            // Since the run Python modules and run PythonExe are doing system
            // administration,
            // determined that there is no vulnerability of user input.
            // Third and fourth parameter is the user input value, but because
            // Validation is done,
            // I will that there is no vulnerability also this item.
            ProcessBuilder builder = createProcessBuilder(getPythonExePath(),
                    getPythonModulePath(), inputKeywordFilePath, inputDirectory);
            builder.redirectErrorStream(true);

            // The run python
            process = builder.start();
            inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    PythonUtil.PYTHON_CHARACTOR_CODE));
            // Get a string of paths in the workspace, and unified "\" the file
            // delimiter
            List<String> lineList = readList(monitor, reader,
                    getWorkspacePath());
            process.waitFor();
            process.destroy();
            // Writes the results
            if (!lineList.isEmpty()) {
                JbmAccessFactory.getJbmWriteFacade().save(outputFilePath,
                        lineList);
                isFileOut = true;
            }
        } catch (IOException e) {
            // Read IO failure
            JbmException je = new JbmException(e, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.ERR_JBM_IO });
            throw new InvocationTargetException(je, je.getViewMessage());
        } catch (JbmException e) {
            // Failure in writing or external module failure results
            throw new InvocationTargetException(e, e.getViewMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                // The only log output to the file write failure
                JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.ERROR,
                        new String[] {});
            }
        }
    }

    /**
     * Generate ProcessBuilder.<br/>
     * 
     * @param pythonRuntimePath
     *            Full path of PythonRuntime
     * @param pythonModulePath
     *            Full path to the Python executable module
     * @param inputKeywordFilePath
     *            Keyword file full path
     * @param inputDirectory
     *            Input directory
     * @return ProcessBuilder
     */
    protected ProcessBuilder createProcessBuilder(String pythonRuntimePath,
            String pythonModulePath, String inputKeywordFilePath,
            String inputDirectory) {
        return new ProcessBuilder(pythonRuntimePath, pythonModulePath,
                inputKeywordFilePath, inputDirectory);
    }

    /**
     * It reads all the results from the Reader, and return the results in the
     * Collection.<br/>
     * 
     * @param monitor
     *            Monitor progress
     * @param reader
     *            Reader
     * @param root
     *            Root path
     * @return List of execution result
     * @throws JbmException
     *             Error in external module
     * @throws IOException
     *             Read failure
     * @throws InterruptedException
     *             User click Cancel at
     */
    protected List<String> readList(IProgressMonitor monitor,
            BufferedReader reader, String root) throws JbmException,
            IOException, InterruptedException {
        List<String> lineList = new ArrayList<String>();
        String line = null;
		CheckListInformationFactory.getCheckListInformationFacade()
				.initCheckListInformationReader();
        // Read one line search results
        while ((line = reader.readLine()) != null) {
            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
            // Converted into a relative path an absolute path, to write the
            // results to file jbm
            line = FileUtil.getRelativePath(root + File.separator, line);
            isNormal(reader, line);
            if (isTargetFilePath(line)) {
                continue;
            } else if (isLineOfFileName(line)) {
                StringBuffer addColums = createAddColums(line);
                lineList.add(line + addColums.toString());
            } else {
            	String message = ResourceUtil.SEARCH_PROGRESS + line;
                monitor.beginTask(message, IProgressMonitor.UNKNOWN);
            }
        }
        return lineList;
    }

    /**
     * Generates a string to add to the return value from Python.<br/>
     * 
     * @param line
     *            Return value from Python
     * @return Add string
     */
    protected StringBuffer createAddColums(String line) {
        String factor = CheckListInformationFactory
                .getCheckListInformationFacade().getFactorDescription(
                        CsvUtil.getSpecificColumn(line, 0)); // Porting factor
        String detail = CheckListInformationFactory
                .getCheckListInformationFacade().getDegreeDescription(
                        CsvUtil.getSpecificColumn(line, 0)); // Difficulty
                                                             // details
        String lineNum = CheckListInformationFactory
                .getCheckListInformationFacade().getLineNumberDescription(
                        CsvUtil.getSpecificColumn(line, 0)); // Number of lines
        String lineNumContents = CheckListInformationFactory
                .getCheckListInformationFacade()
                .getLineNumberContentsDescription(
                        CsvUtil.getSpecificColumn(line, 0)); // Line number
                                                             // basis
        // Be escaped if it contains a double quote
        lineNumContents = lineNumContents.replaceAll("\"", "\"\"");
        // Enclosed in double quotes if it contains a comma, newline, double
        // Fort
        if (lineNumContents.contains(StringUtil.CSV_DELIMITER)
                || lineNumContents.contains(StringUtil.LINE_SEPARATOR)
                || lineNumContents.contains("\"")) {
            lineNumContents = "\"" + lineNumContents + "\"";
        }
        String totalLine = getTotalLine(lineNum,
                CsvUtil.getSpecificColumn(line, 2)); // Total line

        // Add the initial value data of the check list for the Python execution
        // result,
        // to set the null at read the CSV
        StringBuffer addColums = new StringBuffer();
        addColums.append(StringUtil.CSV_DELIMITER);
        addColums.append(factor);
        addColums.append(StringUtil.CSV_DELIMITER);
        addColums.append(detail);
        addColums.append(StringUtil.CSV_DELIMITER);
        addColums.append(lineNum);
        addColums.append(StringUtil.CSV_DELIMITER);
        addColums.append(lineNumContents);
        addColums.append(StringUtil.CSV_DELIMITER);
        addColums.append(totalLine);
        return addColums;
    }

    /**
     * Return the total number of lines.<br/>
     * 
     * @param lineNumber
     *            Number of lines
     * @param hitNumber
     *            Hits
     * @return Total number of lines
     */
    private String getTotalLine(String lineNumber, String hitNumber) {
        String totalLine = "0";
        try {
            Integer lineNum = Integer.valueOf(lineNumber);
            Integer hitNum = Integer.valueOf(hitNumber);
            long sum = lineNum.longValue() * hitNum.longValue();
            totalLine = String.valueOf(sum);
        } catch (NumberFormatException e) {
            // no operation
        }
        return totalLine;
    }

    /**
     * Decision target file exists.<br/>
     * 
     * @param line
     *            Return value from Python
     * @return Return false (the default) usually search.
     */
    protected boolean isTargetFilePath(String line) {
        return false;
    }

    /**
     * The judgment of the return string from Python is, or if it is being
     * output file name search.<br/>
     * 
     * @param line
     *            Return value from Python
     * @return true:Search result row false:Search for file name
     */
    protected boolean isLineOfFileName(String line) {
        if (line.split(StringUtil.CSV_DELIMITER).length > 1) {
            return true;
        }
        return false;
    }

    /**
     * Check the return value from the external module.<br/>
     * If a particular string is present, throw an exception Error.<br/>
     * 
     * @param reader
     *            Read Reader
     * @param line
     *            Line data
     * @throws IOException
     *             Read failure
     * @throws JbmException
     *             When an exception occurs in the external module
     */
    protected void isNormal(BufferedReader reader, String line)
            throws JbmException, IOException {
        // If not in line with the pattern, it is regarded as Error
        if (line.indexOf(AbstractSearchToolWithProgress.PYTHON_ERROR) >= 0) {
            // Error information is provided to the next line transition
            // File failed file
            // Last line causes the most
            String errorLine;
            StringBuilder pyErrTrace = new StringBuilder();
            while ((errorLine = reader.readLine()) != null) {
            	pyErrTrace.append(errorLine+"\n");
            }
            throw new JbmException(new RuntimeException(MessageUtil.ERR_SEARCH_FAILED), LOGGER, ERROR_LEVEL.ERROR,
            		pyErrTrace.toString());
        }
    }

    /**
     * Get whether the output file.<br/>
     * 
     * @return true:The output filefalse:Not file output
     */
    public boolean isFileOut() {
        return isFileOut;
    }
}
