package tubame.portability.plugin.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

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
        if(expand){
        	treeViewer.expandAll();
        }else{
        	treeViewer.collapseAll();
        }
	}
	

}
