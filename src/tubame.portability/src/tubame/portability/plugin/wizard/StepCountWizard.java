package tubame.portability.plugin.wizard;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

import tubame.portability.Activator;
import tubame.portability.logic.search.SearchToolWithProgress;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

public class StepCountWizard extends Wizard implements INewWizard {

	private StepCountWizardPage stepCountWizardPage;

	/**
	 * Constructor.<br/>
	 * 
	 * @param window
	 *            Window
	 * @param resource
	 *            Target resource
	 */
	public StepCountWizard(IWorkbenchWindow window, IResource resource) {
		super();
		stepCountWizardPage = new StepCountWizardPage(resource);
		super.setWindowTitle(MessageUtil.STEPCOUNT_WIZARDPAGE_TITLE);

	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

	@Override
	public void addPages() {
		super.addPage(this.stepCountWizardPage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		String target = stepCountWizardPage.getSearchTargetFullPath();

		// Search process
		try {

			JbmSearchWizard.createProgressStatusMapForFile(
					PythonUtil.getSearchKeywordFilePath(ApplicationPropertyUtil.SEARCH_STEPCOUNTER_KEYWORD_FILE));
			final IFile csvFile = stepCountWizardPage.getResource().getProject()
					.getFile(stepCountWizardPage.getOutputPathExcludeProjectName());
			final StepCountSearchWithProgress progress = new StepCountSearchWithProgress(target,
					PythonUtil.getSearchKeywordFilePath(ApplicationPropertyUtil.SEARCH_STEPCOUNTER_KEYWORD_FILE),
					stepCountWizardPage.getOutputFullPath());

			Job job = new Job(MessageUtil.STEPCOUNT_WIZARD_SEARCH_START_MSG) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						progress.run(monitor);
					} catch (InterruptedException e) {
						// Cancellation
						return Status.CANCEL_STATUS;
					} catch (InvocationTargetException e) {
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								MessageUtil.STEPCOUNT_WIZARD_ERR_MSG + StringUtil.LINE_SEPARATOR + e);
					}
					return Status.OK_STATUS;
				}
			};

			job.addJobChangeListener(new IJobChangeListener() {

				@Override
				public void sleeping(IJobChangeEvent arg0) {
				}

				@Override
				public void scheduled(IJobChangeEvent arg0) {
				}

				@Override
				public void running(IJobChangeEvent arg0) {
				}

				@Override
				public void done(IJobChangeEvent arg0) {
					File file = csvFile.getLocation().toFile();
					File parent = file.getParentFile();
					File resultFile = new File(parent, file.getName() + ".result");
					resultFile.deleteOnExit();
					try {
						calcCsv(file.getAbsolutePath(), resultFile.getAbsolutePath());
					} catch (final Exception e) {
						
						Display.getDefault().asyncExec(new Runnable() {
							/**
							 * {@inheritDoc}
							 */
							@Override
							public void run() {
								PluginUtil.errorDialogWithStackTrace(PluginUtil.getActiveWorkbenchShell(),MessageUtil.STEPCOUNT_WIZARD_ERR_MSG, e);
							}
						});
						return;
					}
					IStatus result = arg0.getResult();

					showResults(result, progress, csvFile);

				}

				private void calcCsv(String csvFile, String to) throws Exception {
					Map<String, Long[]> calcResultMap = new HashMap();
					calcResultMap.put("java", calc(new File(csvFile), "java"));
					calcResultMap.put("jsp", calc(new File(csvFile), "jsp"));
					calcResultMap.put("xml", calc(new File(csvFile), "xml"));
					calcResultMap.put("properties", calc(new File(csvFile), "properties"));
					calcResultMap.put("sql", calc(new File(csvFile), "sql"));
					calcResultMap.put("ddl", calc(new File(csvFile), "ddl"));
					calcResultMap.put("c", calc(new File(csvFile), "c"));
					calcResultMap.put("h", calc(new File(csvFile), "h"));
					calcResultMap.put("pc", calc(new File(csvFile), "pc"));
					writeCalcResult(calcResultMap, to);
				}

				private void writeCalcResult(Map<String, Long[]> calcResultMap, String to) throws IOException {
					String[] targetLangs = { "java", "jsp", "xml", "properties", "sql", "ddl", "c", "h", "pc" };
					BufferedOutputStream bufferedOutputStream = null;
					try {
						bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(to));
						for (int i = 0; i < targetLangs.length; i++) {
							String lang = targetLangs[i];
							String csvLine = getPrintStringFromCalcResultMap(calcResultMap, lang);
							bufferedOutputStream.write((csvLine).getBytes("UTF-8"));
						}
					} catch (IOException e) {
						throw e;
					} finally {
						if (bufferedOutputStream != null) {
							bufferedOutputStream.flush();
							bufferedOutputStream.close();
						}
					}

				}

				private String getPrintStringFromCalcResultMap(Map<String, Long[]> calcResultMap, String key) {
					Long[] longs = calcResultMap.get(key);
					Long fileCnt = longs[0];
					Long stepCnt = longs[1];
					return key + "," + fileCnt + "," + stepCnt + "\n";
				}

				public Long[] calc(File in, String extName) throws IOException {
					BufferedReader bufferedReader = null;
					long fileCnt = 0;
					long totalStep = 0;

					try {
						bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
						String s = null;
						while ((s = bufferedReader.readLine()) != null) {
							String[] token = s.split(",");

							if (token[4].equalsIgnoreCase(extName)) {
								totalStep += Long.parseLong(token[3]);
								fileCnt++;
							}
						}
					} catch (IOException e) {
						throw e;
					} finally {
						if (bufferedReader != null) {
							bufferedReader.close();
						}
					}
					Long longResult[] = { fileCnt, totalStep };
					return longResult;
				}

				@Override
				public void awake(IJobChangeEvent arg0) {
				}

				@Override
				public void aboutToRun(IJobChangeEvent arg0) {
				}

			});
			job.setUser(true);
			job.schedule();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void showResults(final IStatus result, final SearchToolWithProgress searchToolWithProgress,
			final IFile csvFile) {
		try {
			csvFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			;
		}
		Display.getDefault().asyncExec(new Runnable() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				if (result.equals(Status.CANCEL_STATUS)) {
					PluginUtil.viewInfoDialog(MessageUtil.STEPCOUNT_WIZARD_SUCCESS_DIALOG_TITLE,
							MessageUtil.STEPCOUNT_WIZARD_CANCEL_MSG);
				} else if (result.equals(Status.OK_STATUS) && searchToolWithProgress.isFileOut()) {
					PluginUtil.viewInfoDialog(MessageUtil.STEPCOUNT_WIZARD_SUCCESS_DIALOG_TITLE,
							MessageUtil.STEPCOUNT_WIZARD_SUCCESS_MSG);
				}
			}
		});

	}

	class StepCountWizardPage extends WizardPage implements FileBrowseButton {

		private static final String OUTPUT_FILE_NAME = "steps.csv";

		private Text outputDirPathText;
		private IResource resource;

		protected StepCountWizardPage(IResource resource) {
			super("");
			this.resource = resource;
			setTitle(MessageUtil.STEPCOUNT_WIZARDPAGE_INFO);
		}

		public IResource getResource() {
			return resource;
		}

		@Override
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			container.setLayout(layout);

			// setting output area
			setOutputArea(container);

			super.setControl(container);
		}

		private void setOutputArea(Composite container) {
			Group group = new Group(container, SWT.SHADOW_NONE);
			group.setText(MessageUtil.STEPCOUNT_WIZARDPAGE_OUTPUT);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(new GridLayout(3, false));

			// output dir
			GridData gridDataDirectory = new GridData(GridData.FILL_HORIZONTAL);
			outputDirPathText = new Text(group, SWT.BORDER);
			outputDirPathText.setLayoutData(gridDataDirectory);
			outputDirPathText.setText(resource.getProject().getName() + FileUtil.FILE_SEPARATOR + OUTPUT_FILE_NAME);
			outputDirPathText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					setPageComplete(textValidate());
				}

				private boolean textValidate() {
					return false;
				}
			});

			// Search target folder selection button
			Button Button = new Button(group, SWT.NULL);
			Button.setText(MessageUtil.STEPCOUNT_WIZARDPAGE_BROWSE);
			// Set the operation when the button is pressed
			Button.addSelectionListener(new BrowseFileButtonSelectionListener(this, outputDirPathText, "csv"));
		}

		@Override
		public String getOutFilePath() {
			// TODO Auto-generated method stub
			return StringUtil.getText(outputDirPathText);
		}

		public String getSearchTargetFullPath() {
			return this.resource.getLocation().toOSString();
		}

		public String getOutputFullPath() {
			String outputPathExcludeProjectName = getOutputPathExcludeProjectName();
			if (outputPathExcludeProjectName != null) {
				return resource.getProject().getFile(outputPathExcludeProjectName).getLocation().toOSString();
			}
			return resource.getLocation().toOSString();
		}

		private String getOutputPathExcludeProjectName() {
			if (outputDirPathText.getText().equals(this.resource.getProject().getName())) {
				return null;
			}
			return outputDirPathText.getText().substring(this.resource.getProject().getName().length());
		}

	}

}
