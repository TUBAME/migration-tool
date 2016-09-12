package tubame.integration.test;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SwtBotUtil {

	public static SWTBotView getPackageExplorer(final SWTWorkbenchBot bot) {
		SWTBotView explorer = null;
		for (SWTBotView view : bot.views()) {
			if (view.getTitle().equals("Package Explorer") || view.getTitle().equals("Project Explorer")) {
				explorer = view;
				break;
			}
		}
		return explorer;
	}

	/**
	 * Returns the project root tree in Package Explorer.
	 */
	public static SWTBotTree getProjectRootTree(SWTWorkbenchBot bot) {
		SWTBotView explorer = getPackageExplorer(bot);

		if (explorer == null) {
			throw new WidgetNotFoundException("Cannot find Package Explorer or Project Explorer");
		}

		Tree tree = bot.widget(widgetOfType(Tree.class), explorer.getWidget());
		return new SWTBotTree(tree);
	}

	public static SWTBotTreeItem selectProject(final SWTWorkbenchBot bot, String projectName) {
	    /*
	     * Choose either the Package Explorer View or the Project Explorer view. Eclipse 3.3 and 3.4
	     * start with the Java Perspective, which has the Package Explorer View open by default, whereas
	     * Eclipse 3.5 starts with the Resource Perspective, which has the Project Explorer View open.
	     */
	    SWTBotView explorer = getPackageExplorer(bot);
	    for (SWTBotView view : bot.views()) {
	      if (view.getTitle().equals("Package Explorer") || view.getTitle().equals("Project Explorer")) {
	        explorer = view;
	        break;
	      }
	    }

	    if (explorer == null) {
	      throw new WidgetNotFoundException(
	          "Could not find the 'Package Explorer' or 'Project Explorer' view.");
	    }

	    // Select the root of the project tree in the explorer view
	    Widget explorerWidget = explorer.getWidget();
	    Tree explorerTree = bot.widget(widgetOfType(Tree.class), explorerWidget);
	    return new SWTBotTree(explorerTree).getTreeItem(projectName).select();
	  }
	
	public static SWTBotTreeItem selectProjectItem(SWTBotTreeItem item, String... folderPath) {
	    for (String folder : folderPath) {
	      if (item == null) {
	        return null;
	      }
	      item.doubleClick();
	      item = item.getNode(folder);
	    }
	    return item;
	  }
	
	 public static void clickButtonAndWaitForWindowChange(SWTBot bot, final SWTBotButton button) {
		    performAndWaitForWindowChange(bot, new Runnable() {
		      public void run() {
		        button.click();
		      }
		    });
		  }
	 
	 /**
	   * Simple wrapper to block for actions that either open or close a window.
	   */
	  public static void performAndWaitForWindowChange(SWTBot bot, Runnable runnable) {
	    SWTBotShell shell = bot.activeShell();
	    runnable.run();
	    waitUntilShellIsNotActive(bot, shell);
	  }
	  
	  /**
	   * Blocks the caller until the given shell is no longer active.
	   */
	  public static void waitUntilShellIsNotActive(SWTBot bot, final SWTBotShell shell) {
	    bot.waitUntil(new DefaultCondition() {
	      public String getFailureMessage() {
	        return "Shell " + shell.getText() + " did not close"; //$NON-NLS-1$
	      }

	      public boolean test() throws Exception {
	        return !shell.isActive();
	      }
	    });
	  }
	  
	  public static boolean doesProjectExist(final SWTWorkbenchBot bot, String projectName) {
		    SWTBotView explorer = getPackageExplorer(bot);
		    if (explorer == null) {
		      throw new WidgetNotFoundException(
		          "Could not find the 'Package Explorer' or 'Project Explorer' view.");
		    }

		    // Select the root of the project tree in the explorer view
		    Widget explorerWidget = explorer.getWidget();
		    Tree explorerTree = bot.widget(widgetOfType(Tree.class), explorerWidget);
		    SWTBotTreeItem[] allItems = new SWTBotTree(explorerTree).getAllItems();
		    for (int i = 0; i < allItems.length; i++) {
		      if (allItems[i].getText().equals(projectName)) {
		        return true;
		      }
		    }
		    return false;
		  }

}
