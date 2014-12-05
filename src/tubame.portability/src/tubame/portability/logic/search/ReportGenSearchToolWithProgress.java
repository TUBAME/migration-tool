package tubame.portability.logic.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.util.FileUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ResourceUtil;

public class ReportGenSearchToolWithProgress extends SearchToolWithProgress {

	public ReportGenSearchToolWithProgress(String target,
			String keywordFilePath, String outFilePath) {
		super(target, keywordFilePath, outFilePath);
	}
	

	public ReportGenSearchToolWithProgress(String target, String keywordFilePath, String outFilePath, IProject iProject) {
		super(target, keywordFilePath, outFilePath, iProject);
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
            System.out.println("line:"+line);
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
	
	@Override
	protected String getWorkspacePath() {
		//WorkSpaceのパス配下に検索対象があるとは限らないので、iproject情報から検索情報対象のデータを取得する必要がある
		if(this.selectedProject!=null){
			 String root = this.selectedProject.getLocation().toFile().getParent();
			 return root.replace(StringUtil.SLASH, FileUtil.FILE_SEPARATOR);
		}
		return null;
	}
	
	
	
	

}
