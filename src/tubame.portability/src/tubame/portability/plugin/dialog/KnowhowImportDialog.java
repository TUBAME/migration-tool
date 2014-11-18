package tubame.portability.plugin.dialog;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tubame.portability.Activator;
import tubame.portability.logic.GuideViewFacade;
import tubame.portability.plugin.preferences.MigrationGuidePreferencePage;
import tubame.portability.util.FileUtil;
import tubame.portability.util.FileVisitor;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.ResourceUtil;

public class KnowhowImportDialog extends Dialog {

	public Text getImportPathText() {
		return importPathText;
	}

	private Text importPathText;
	
	private IProject project;

	private Map<String, File> knowledgeNameMap;

	private Combo knowledgeSelectCombo;

	private File importedFile;

	protected File guideFilePath;


	public KnowhowImportDialog(Shell parentShell,IProject project) {
		super(parentShell);
		this.project= project;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(ResourceUtil.KNOWHOW_IMPORT_TITLE);
		//
		Composite area = (Composite) super.createDialogArea(parent);
		Composite contents = new Composite(area, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 400;
		contents.setLayoutData(gd);
		contents.setLayout(new GridLayout(3, false));
		applyDialogFont(contents);
		initializeDialogUnits(area);

		// ナレッジ選択label
		Label nameLabel = new Label(contents, SWT.NONE);
		nameLabel.setText(ResourceUtil.KNOWHOW_SELECT_LABEL);

		// ナレッジ選択combo
		knowledgeSelectCombo = new Combo(contents, SWT.DROP_DOWN);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		knowledgeSelectCombo.setLayoutData(gd);
		knowledgeNameMap = PluginUtil.getKnowledgeNamesFromPluginResource();
		Set<String> knowledgeNames = knowledgeNameMap.keySet();
		for (String knowledgeName : knowledgeNames) {
			knowledgeSelectCombo.add(knowledgeName);
		}

		return area;
	}

	@Override
	protected void okPressed() {
		
		if(this.knowledgeSelectCombo==null || this.knowledgeSelectCombo.getSelectionIndex() == -1){
			return;
		}
		importedFile = knowledgeNameMap.get(this.knowledgeSelectCombo.getItem(this.knowledgeSelectCombo.getSelectionIndex()));
		if(importedFile== null){
			return;
		}
		final File parentFile = importedFile.getParentFile();
		try {
			FileVisitor.walkFileTree(parentFile, new FileVisitor() {
			    @Override
			    public FileVisitResult visitFile(File file) throws IOException {
			    	String relative = getRelative(PluginUtil.getKnowledgeDir(), file);
			    	String name = file.getName();
			    	if(name.matches(".*\\.html$")){
			    		guideFilePath = file;
			    	}
			    	File dest = new File(project.getLocation().toOSString()+File.separator+relative);
			    	FileUtil.copyFile(file, dest);
			        return FileVisitResult.CONTINUE;
			    }

			    @Override
			    public FileVisitResult preVisitDirectory(File dir)
			            throws IOException {
			    	String relative = getRelative(PluginUtil.getKnowledgeDir(), dir);
			    	File file = new File(project.getLocation().toOSString()+File.separator+relative);
			    	file.mkdir();
			        return FileVisitResult.CONTINUE;
			    }
			    
			    private String getRelative(String basePath, File file){
			    	int length = basePath.length();
			    	return file.getAbsolutePath().substring(length);
			    }
			});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
		if( guideFilePath!=null){
			String path = this.guideFilePath.getPath();
	    	Activator.savePreferences(MigrationGuidePreferencePage.PREF_KEY_GUIDE_INDEX_PATH,path);
	    	GuideViewFacade.INDEX_GUIDE_FILE_PATH = path;
		}

		super.okPressed();
	}

	public File getImportedFile() {
		return importedFile;
	}

	public void setImportedFile(File importedFile) {
		this.importedFile = importedFile;
	}

	
}
