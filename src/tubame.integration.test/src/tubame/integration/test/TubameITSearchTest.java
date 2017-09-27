package tubame.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(SWTBotJunit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TubameITSearchTest {

	private static final String TEST_PROJECT = "tag-html-struts1";
	
	private SWTWorkbenchBot bot;

	private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

	@Before
	public void before() {
		SWTBotPreferences.TIMEOUT = 30000;
		bot = new SWTWorkbenchBot();
		if (!SwtBotUtil.doesProjectExist(bot, TEST_PROJECT)) {
			copyClassPathAndProject();
			copySettins();
			importProject();
		}
	}

	@Test
	public void tubameDependenceSearch() throws Exception {
		SwtBotUtil.selectProject(bot, TEST_PROJECT);

		selectMenu("tubame", "依存性検索");
		execDependenceSearch();
		// TODO: このスリープがないとテストに失敗する...
		Thread.sleep(15 * 1000);

		File outputJbmFile = getOutputFile("gjbm");
		assertTrue(outputJbmFile.exists());
	}

	@Test
	public void tubameKnowledgeSearch() throws Exception {
		SwtBotUtil.selectProject(bot, TEST_PROJECT);
		selectMenu("tubame", "ナレッジベース検索");
		execKnowledgeSearch();

		File outputJbmFile = getOutputFile("jbm");
		assertTrue(outputJbmFile.exists());
		File expectedJbmFile = null;
		if (isWindowsPlatform()) {
			expectedJbmFile = getExpectedFileFromknowledgeDir("jbm_win");
		} else if (isMacPlatform()) {
			expectedJbmFile = getExpectedFileFromknowledgeDir("jbm_mac");
		} else {
			expectedJbmFile = getExpectedFileFromknowledgeDir("jbm_linux");
		}
		assertEquals(readFileToString(expectedJbmFile, "UTF-8"), readFileToString(outputJbmFile, "UTF-8"));
	}

	@Test
	public void tubameReportGenerate() throws Exception {
		SwtBotUtil.selectProject(bot, TEST_PROJECT);
		selectMenu("tubame", "その他機能","レポート生成");
		execReportGenerate();
		confirmOK();
		noShowReport();

		File dependenceResultFile = getOutputDependenceResultFileFromReportDir();
		assertTrue(dependenceResultFile.exists());
		File knowledgeResultFile = getOutputKnowledgeResultFileFromReportDir();
		assertTrue(knowledgeResultFile.exists());

		File expectedDependenceResultFile = getExpectedDependenceResultFileFromReportDir();
		assertEquals(readFileToString(expectedDependenceResultFile, "UTF-8"),
				readFileToString(dependenceResultFile, "UTF-8"));
		File expectedKnowledgeResultFile = getExpectedKnowledgeResultFileFromReportDir();
		assertEquals(readFileToString(expectedKnowledgeResultFile, "UTF-8"),
				readFileToString(knowledgeResultFile, "UTF-8"));
	}

	private void copyClassPathAndProject() {
		File parent1 = getImportJavaTargetDir(TEST_PROJECT);
		File classPathfrom = new File(parent1, "_classpath");
		File classPathDest = new File(parent1, ".classpath");
		copyFile(classPathfrom.getAbsolutePath(), classPathDest.getAbsolutePath());
		File projectfrom = new File(parent1, "_project");
		File projectDest = new File(parent1, ".project");
		copyFile(projectfrom.getAbsolutePath(), projectDest.getAbsolutePath());
	}
	
	private void copySettins() {
		File parent1 = getImportJavaTargetDir(TEST_PROJECT);
		File settings = new File(parent1, "_settings");
		File settingsDirFrom = new File(settings, "org.eclipse.jdt.core.prefs");
		File settingsDirDest = new File(parent1, ".settings");
		if (!settingsDirDest.exists()) {
			settingsDirDest.mkdir();
			File jdtPrefs = new File(settingsDirDest, "org.eclipse.jdt.core.prefs");
			copyFile(settingsDirFrom.getAbsolutePath(), jdtPrefs.getAbsolutePath());
		}

	}

	private void confirmOK() {
		SwtBotUtil.clickButtonAndWaitForWindowChange(bot, bot.button("OK"));
	}

	private void noShowReport() {
		SwtBotUtil.clickButtonAndWaitForWindowChange(bot, bot.button("No"));
	}

	private void execReportGenerate() {
		SWTBotShell activeShell = bot.shell("レポート生成実行");
		SWTBot wizard = activeShell.bot();
		wizard.comboBox().setSelection("MVCフレームワーク用テンプレート");
		SwtBotUtil.clickButtonAndWaitForWindowChange(wizard, wizard.button("Finish"));
	}

	private void execDependenceSearch() {
		SWTBotShell activeShell = bot.shell("依存性検索確認");
		SWTBot wizard = activeShell.bot();
		SwtBotUtil.clickButtonAndWaitForWindowChange(wizard, wizard.button("Finish"));
	}

	private File getOutputFile(String ext) {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);
		return new File(importJavaTargetDir, "result." + ext);
	}

	private File getExpectedFileFromknowledgeDir(String ext) {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);
		File parent = new File(importJavaTargetDir.getAbsolutePath() + "/testCompareData/knowledge");
		return new File(parent, "_result." + ext);
	}

	private File getOutputDependenceResultFileFromReportDir() {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);
		File parent = new File(
				importJavaTargetDir.getAbsolutePath() + "/tubame-report/js/DependsPackagePicGrapthSumCalclator");
		return new File(parent, "getresult.js");
	}

	private File getOutputKnowledgeResultFileFromReportDir() {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);
		File parent = new File(importJavaTargetDir.getAbsolutePath()
				+ "/tubame-report/js/KnowhowDegreeOfDifficultyHitCountSumCalclator_toFw");
		return new File(parent, "getresult.js");
	}

	private File getExpectedDependenceResultFileFromReportDir() {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);
		File parent = new File(
				importJavaTargetDir.getAbsolutePath() + "/testCompareData/report/DependsPackagePicGrapthSumCalclator");
		return new File(parent, "getresult.js");
	}

	private File getExpectedKnowledgeResultFileFromReportDir() {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);
		File parent = new File(importJavaTargetDir.getAbsolutePath()
				+ "/testCompareData/report/KnowhowDegreeOfDifficultyHitCountSumCalclator_toFw");
		return new File(parent, "getresult.js");
	}

	private void execKnowledgeSearch() throws Exception {
		SWTBot wizard = bot.activeShell().bot();
		SwtBotUtil.clickButtonAndWaitForWindowChange(wizard, wizard.button("Finish"));
		SwtBotUtil.clickButtonAndWaitForWindowChange(bot, bot.button("OK"));
	}

	private void selectMenu(String... menu) {
		MenuItem menuItem = ContextMenuHelper.contextMenu(SwtBotUtil.getProjectRootTree(bot), menu);
		SWTBotMenu swtBotMenu = new SWTBotMenu(menuItem);
		swtBotMenu.click();
	}

	private void importProject() {
		File importJavaTargetDir = getImportJavaTargetDir(TEST_PROJECT);

		bot.activeShell().setFocus();
		bot.menu("File").menu("Import...").click();

		SWTBotTreeItem item = selectTreeItem(new Path("General"), bot.tree()).doubleClick();
		item.select("Existing Projects into Workspace");
		bot.button("Next >").click();

		SWTBotShell shell = bot.shell("Import");
		SWTBot wizardBot = shell.bot();

		wizardBot.comboBox().setText(importJavaTargetDir.getAbsolutePath());
		wizardBot.button("Select All").click();

		bot.activeShell().setFocus();
		bot.button("Finish").click();
	}

	private File getImportJavaTargetDir(String testProject) {
		String workspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		return new File(workspaceRoot + "/../../javaProjects/" + testProject);
	}

	private SWTBotTreeItem selectTreeItem(IPath path, SWTBotTree tree) {
		SWTBotTreeItem item = null;
		for (SWTBotTreeItem treeItem : tree.getAllItems()) {
			System.out.println("itemText:" + treeItem.getText());
			if (treeItem.getText().contains(path.segment(0))) {
				item = treeItem;
				break;
			}
		}
		if (item != null) {
			try {
				item.expand();
				path = path.removeFirstSegments(1);
				if (path.segmentCount() > 0) {
					String lastSegment = path.lastSegment();
					path = path.removeLastSegments(1);
					for (String segment : path.segments()) {
						item = item.expandNode(segment);
					}
					item = item.getNode(lastSegment);
				}
				return item;
			} catch (RuntimeException e) {
				throw e;
			}
		} else {
			throw new WidgetNotFoundException("Tree item '" + path.segment(0) + "' not found.");
		}
	}

	private String readFileToString(File file, String charCode) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader br = null;
		FileReader fileReader = new FileReader(file);
		try {
			br = new BufferedReader(fileReader);
			for (String line; (line = br.readLine()) != null;) {
				stringBuffer.append(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (fileReader != null) {
				fileReader.close();
			}
		}
		if (stringBuffer.length() == 0) {
			new IllegalStateException(file.getAbsolutePath() + " size 0");
		}
		return stringBuffer.toString();
	}

	public static boolean isLinuxPlatform() {
		return OS_NAME.startsWith("linux");
	}

	public static boolean isMacPlatform() {
		return OS_NAME.startsWith("mac");
	}

	public static boolean isWindowsPlatform() {
		return OS_NAME.startsWith("windows");
	}

	public static boolean copyFile(String fromPath, String toPath) {
		FileChannel fromChannel = null;
		FileChannel toChannel = null;
		try {
			fromChannel = new FileInputStream(fromPath).getChannel();
			toChannel = new FileOutputStream(toPath).getChannel();
			fromChannel.transferTo(0, fromChannel.size(), toChannel);
		} catch (IOException e) {
			return false;
		} finally {
			if (fromChannel != null) {
				try {
					fromChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (toChannel != null) {
				try {
					toChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
