package org.silentsoft.everywhere.client.component.tree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

public class CloudTreeView extends TreeView<CloudTreeItem>{

	private Map<String, TreeItem<CloudTreeItem>> cloudMap;
	
	public CloudTreeView() {
		/**
		 * if exists path (key), then get treeItem and put file.
		 * or just put to root.
		 * 
		 * how to know the path is root ?
		 *  => indexOf(File.separator) == -1
		 */
		super();
		
		cloudMap = new HashMap<String, TreeItem<CloudTreeItem>>();
		cloudMap.put(File.separator, new TreeItem<CloudTreeItem>(new CloudTreeItem(File.separator, true, File.separator, "", false)));
	}
	
	public void add(CloudTreeItem cloudTreeItem) {
		if (cloudTreeItem.getFilePath().indexOf(File.separator) == -1) {
			cloudMap.get(File.separator).getChildren().add(new TreeItem<CloudTreeItem>(cloudTreeItem));
		} else {
			TreeItem<CloudTreeItem> item = new TreeItem<CloudTreeItem>(cloudTreeItem);
			
			if (cloudMap.containsKey(cloudTreeItem.getFilePath())) {
				TreeItem<CloudTreeItem> value = cloudMap.get(cloudTreeItem.getFilePath());
				value.getChildren().add(item);
			} else {
				cloudMap.put(cloudTreeItem.getFilePath(), item);
			}
		}
	}
	
	public void synchronization() {
		TreeItem<CloudTreeItem> root = cloudMap.get(File.separator);
		setRoot(root);
		
		for (TreeItem<CloudTreeItem> cloudTreeItem : cloudMap.values()) {
			if (cloudTreeItem != root) {
				getRoot().getChildren().add(cloudTreeItem);
			}
		}
	}
}
