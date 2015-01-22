package tubame.portability.plugin.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.util.PluginUtil;

public class AllItemExpandAndCollapseAction extends Action {

	
	private boolean expand;
	
	
	public AllItemExpandAndCollapseAction(String labelName,boolean expand) {
		super(labelName);
		this.expand = expand;
	}
	
	@Override
	public void run() {
	    MigrationEditorOperation editor = (MigrationEditorOperation) PluginUtil
                .getActiveEditor();
        // Get the selected element
        TreeViewer treeViewer = editor.getTreeViewer();
        boolean flg = false;
        if(expand){
        	treeViewer.expandAll();
        	flg = true;
        }else{
        	treeViewer.collapseAll();
        }
        TreeItem[] items = treeViewer.getTree().getItems();
		for (TreeItem item : items) {
			JbmEditorMigrationRow row = (JbmEditorMigrationRow) item.getData();
			if (row.getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
				row.setExpand(flg);
			}
		}
	}
	

}
