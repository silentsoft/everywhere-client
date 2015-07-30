package org.silentsoft.everywhere.client.model;

import java.io.File;

public class FileModel {
	
	private File file;
	
	private String path;
	
	private long length;
	
	private String size;
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
		
		setSize(this.length);
	}

	public String getSize() {
		return size;
	}

	private void setSize(long bytes) {
		size = bytes + " byte";
		
		double sizeKB = ((double)bytes/1024);
		if (sizeKB >= 1) {
			size = String.format("%.2f", sizeKB) + " KB";
		
			double sizeMB = ((double)bytes/1024/1024);
			if (sizeMB >= 1) {
				size = String.format("%.2f", sizeMB) + " MB";
				
				double sizeGB = ((double)bytes/1024/1024/1024);
				if (sizeGB >= 1) {
					size = String.format("%.2f", sizeGB) + " GB";
				}
			}
		}
	}
}
