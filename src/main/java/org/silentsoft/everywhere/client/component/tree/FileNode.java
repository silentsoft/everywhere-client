package org.silentsoft.everywhere.client.component.tree;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

public class FileNode extends TreeItem<FileNode> {

	public enum NodeType {
		FILE,
		DIRECTORY,
		RECYCLE_BIN
	}
	
	private NodeType nodeType;
	
	private String name;
	
	private String size;
	
	public FileNode(NodeType nodeType, String name) {
		this(nodeType, name, null);
	}
	
	public FileNode(NodeType nodeType, String name, String size) {
		super();
		
		setNodeType(nodeType);
		setName(name);
		setSize(size);
		
		if (getName() != null) {
			Label lblName = new Label(getName());
//			getChildren().add(lblName);
		}
		
		switch (nodeType) {
		case RECYCLE_BIN :
//			getChildren().add(new Label("Not yet.."));
			break;
		case FILE :
			if (getSize() != null) {
				Label lblSize = new Label(" - ".concat(getSize()));
//				getChildren().add(lblSize);
			}
			break;
		case DIRECTORY :
			
			break;
		}
	}
	
	public NodeType getNodeType() {
		return nodeType;
	}
	
	private void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	private void setSize(String size) {
		this.size = size;
	}
	
}
