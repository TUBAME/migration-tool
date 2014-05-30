package tubame.portability.logic.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.util.FileUtil;
import tubame.portability.util.resource.ResourceUtil;

public class ReportGenSearchToolWithProgress extends SearchToolWithProgress {

	public ReportGenSearchToolWithProgress(String target,
			String keywordFilePath, String outFilePath) {
		super(target, keywordFilePath, outFilePath);
	}

	@Override
	protected List<String> readList(IProgressMonitor monitor,
			BufferedReader reader, String root) throws JbmException,
			IOException, InterruptedException {
		
        List<String> lineList = new ArrayList<String>();
        String line = null;
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
            	this.isFileOut = true;
                //no add
            	continue;
            } else {
            	String message = ResourceUtil.REPORTGEN_PROGRESS + line;
                monitor.beginTask(message, IProgressMonitor.UNKNOWN);
            }
        }
        return lineList;
	}
	
	

}
