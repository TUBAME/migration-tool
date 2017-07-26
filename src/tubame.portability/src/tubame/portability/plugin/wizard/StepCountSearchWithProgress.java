package tubame.portability.plugin.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.search.SearchToolWithProgress;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.resource.ResourceUtil;

public class StepCountSearchWithProgress extends SearchToolWithProgress {

	public StepCountSearchWithProgress(String target, String keywordFilePath, String outFilePath) {
		super(target, keywordFilePath, outFilePath);
	}

	@Override
	protected List<String> readList(IProgressMonitor monitor, BufferedReader reader, String root)
			throws JbmException, IOException, InterruptedException {
		
		 List<String> lineList = new ArrayList<String>();
	        String line = null;
			int totalLineCnt = PythonUtil.PY_SEARCH_PROGRESS_STATUS_MAP.keySet().size();
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
	                lineList.add(line);
	            } else {
	            	
	            	String statusLine = this.getStatusLine(line,totalLineCnt);
	            	if(statusLine!= null){
	            		String message = ResourceUtil.SEARCH_PROGRESS + statusLine;
	            		monitor.beginTask(message, IProgressMonitor.UNKNOWN);
	            	}
	            	
	                
	            }
	        }
	        return lineList;
	}
	
    private String getStatusLine(String line,int totalLineCnt) {
		String orginalNumber = line.split("/")[0];
		if(PythonUtil.PY_SEARCH_PROGRESS_STATUS_MAP.containsKey(orginalNumber)){
			Integer integer = PythonUtil.PY_SEARCH_PROGRESS_STATUS_MAP.get(orginalNumber);
			return String.valueOf(integer) + "/" + String.valueOf(totalLineCnt);
		}
		return null;
	}

}
