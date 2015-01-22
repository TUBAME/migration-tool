package tubame.portability.plugin.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.MessageUtil;

public class SelectionItemExpandAndContractAction extends Action {

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		if(expand){
			this.setText(MessageUtil.ITEM_COLLAPSE);
		}else{
			this.setText(MessageUtil.ITEM_EXPAND);
		}
		this.expand = expand;
	}

	private boolean expand;

	public SelectionItemExpandAndContractAction(boolean expand) {
		setExpand(expand);
	}

	@Override
	public void run() {
		MigrationEditorOperation editor = (MigrationEditorOperation) PluginUtil.getActiveEditor();
		TreeViewer treeViewer = editor.getTreeViewer();

		TreeItem[] selectedItems = treeViewer.getTree().getSelection();

		for (TreeItem selectedItem : selectedItems) {
			JbmEditorMigrationRow row = (JbmEditorMigrationRow) selectedItem.getData();
			if (row.getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
				if (expand) {
					//TODO:サブエレメントを強制的にexpandしたかったが、方法が不明.
					selectedItem.setExpanded(false);
				} else {
					//TODO:サブエレメントを強制的にexpandしたかったが、方法が不明.
					treeViewer.setAutoExpandLevel(4);
				}
			}
		}

	}
	
	
}
