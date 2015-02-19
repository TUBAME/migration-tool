package tubame.portability.plugin.action;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import javax.xml.transform.Source;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.plugin.view.SourceCodeView;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

public class ShowCodeViewerAction extends Action {

	@Override
	public void run() {
		MigrationEditorOperation editor = (MigrationEditorOperation) PluginUtil.getActiveEditor();
		TreeViewer treeViewer = editor.getTreeViewer();

		TreeItem[] selectedItems = treeViewer.getTree().getSelection();

		for (TreeItem selectedItem : selectedItems) {
			JbmEditorMigrationRow row = (JbmEditorMigrationRow) selectedItem.getData();

			if (row.getLevel() == JbmEditorMigrationRow.LEVEL_THIRD) {
				String projectName = PluginUtil.getProjectName(row.getFileName());
				IProject project = PluginUtil.getProject(projectName);
				String pathExcludeProjectName = row.getFileName().substring(
						projectName.length() + File.separator.length());

				IFile file = project.getFile(pathExcludeProjectName);
				try {
					CodeViewHtmlGenerator.gen(file, row.getRowNo());

					String codeViewHtmlFilePath = PluginUtil.getResolvedPluginDir()
							+ ApplicationPropertyUtil.CODE_VIEWER_HTML;
					File codeViewHtmlFile = new File(codeViewHtmlFilePath);
					SourceCodeView.view(codeViewHtmlFile.toURL().toString());
				} catch (Exception e) {
					PluginUtil.viewErrorDialog(MessageUtil.ERR_OPEN_CODE_VIEWER,e.getMessage(),e);
				}

			}
		}

	}

	public ShowCodeViewerAction() {
		this.setText(MessageUtil.MSG_OPEN_CODE_VIEWER);
	}

	enum ExtensionJsModuleBinding {

		JAVA("codeview_java.tpl"), JSP("codeview_jsp.tpl"), XML("codeview_xml.tpl"), HTML("codeview_xml.tpl"), XHTML(
				"codeview_xml.tpl"), JS("codeview_js.tpl"), PY("codeview_py.tpl"), RB("codeview_rb.tpl"), SH(
				"codeview_sh.tpl"), SQL("codeview_sql.tpl");

		private String selectTplFile;

		private ExtensionJsModuleBinding(String selectTplFile) {
			this.selectTplFile = selectTplFile;
		}

	}

	static class CodeViewHtmlGenerator {

		private static final String DEFAULT_TPL = "codeview_plain.tpl";

		public static void gen(IFile viewTargetSourceFile, String rowNumberStr) throws IOException, CoreException {
			int rowNumber = Integer.valueOf(rowNumberStr);
			int startLinePos = getStartLinePos(rowNumber);
			String selectTplFileName = selectTpl(viewTargetSourceFile.getFileExtension());
			String tplString = FileUtil.getContent(new File(PluginUtil.getResolvedPluginDir()
					+ ApplicationPropertyUtil.CODE_VIEWER_TPL_DIR, selectTplFileName));
			String buffer = MessageFormat.format(tplString, viewTargetSourceFile.getLocation().toOSString(),
					viewTargetSourceFile.getName(), rowNumberStr, String.valueOf(startLinePos));

			String codeViewHtmlFilePath = PluginUtil.getResolvedPluginDir() + ApplicationPropertyUtil.CODE_VIEWER_HTML;
			File codeViewHtmlFile = new File(codeViewHtmlFilePath);

			// codeViewHtmlFile create
			createViewHtmlFile(codeViewHtmlFile, buffer);

			// SouceCodeFile data copy
			appendSourceCode(viewTargetSourceFile.getLocation().toFile(), codeViewHtmlFile,
					viewTargetSourceFile.getFileExtension(), viewTargetSourceFile.getCharset(), rowNumber, startLinePos);

		}

		private static void createViewHtmlFile(File codeViewHtmlFile, String buffer) throws IOException {
			FileWriter fileWriter = new FileWriter(codeViewHtmlFile);
			fileWriter.write(buffer);
			fileWriter.flush();
			fileWriter.close();
		}

		private static String selectTpl(String fileExtension) {
			ExtensionJsModuleBinding[] values = ExtensionJsModuleBinding.values();
			for (int i = 0; i < values.length; i++) {
				ExtensionJsModuleBinding extensionJsModuleBinding = values[i];
				if (extensionJsModuleBinding.toString().equalsIgnoreCase(fileExtension)) {
					return extensionJsModuleBinding.selectTplFile;
				}
			}
			return DEFAULT_TPL;
		}

		private static void appendSourceCode(File sourceFile, File codeViewHtmlFile, String ext, String readCharSet,
				int lowNumber, int startLinePos) throws IOException {
			BufferedReader bufferedReader = null;
			BufferedOutputStream bufferedOutputStream = null;
			if (readCharSet == null) {
				readCharSet = ApplicationPropertyUtil.CODE_VIEWER_READ_CHARSET;
			}
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), readCharSet));

				String s = null;
				bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(codeViewHtmlFile, true));
				int currentPos = 0;
				boolean recordON = false;
				int recordCnt = 0;
				while ((s = bufferedReader.readLine()) != null) {
					if (currentPos == startLinePos - 1) {
						recordON = true;
					}
					if (recordON) {
						if (ext.equals("xml") || ext.equals("jsp") || ext.equals("html")) {
							String replaceAll = s.replaceAll("<", "&lt;");
							s = replaceAll.replaceAll(">", "&gt;");
						}
						// SyntaxHighlighterでpreタグの直後に改行があっても、無視する仕様のため、空白を詰めて対処(CodeViewer的には問題はない)。
						if (s.equals("")) {
							s = "&nbsp;" + s;
						}
						bufferedOutputStream.write((s + "\n")
								.getBytes(ApplicationPropertyUtil.CODE_VIEWER_READ_CHARSET));
						recordCnt++;
					}
					if (recordCnt > (Integer.valueOf(ApplicationPropertyUtil.CODE_VIEWER_REC_BASE_LENGTH) * 2)) {
						break;
					}
					currentPos++;
				}
			} catch (IOException e) {
				throw e;
			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (bufferedOutputStream != null) {
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}
			}
		}

		private static int getStartLinePos(int lowNumber) {
			if (lowNumber - Integer.valueOf(ApplicationPropertyUtil.CODE_VIEWER_REC_BASE_LENGTH) > 0) {
				int num = lowNumber - Integer.valueOf(ApplicationPropertyUtil.CODE_VIEWER_REC_BASE_LENGTH) - 1;
				if (num == 0) {
					return 1;
				} else {
					return num;
				}
			}
			return 1;
		}


	}

}
