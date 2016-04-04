package org.silentsoft.everywhere.client.view.main.download;

import javafx.scene.Parent;

import org.silentsoft.ui.viewer.AbstractViewerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadViewerController extends AbstractViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadViewerController.class);
	
	private Parent downloadViewer;
	
	@Override
	protected void initialize(Parent viewer, Object... parameters) {
		this.downloadViewer = viewer;
	}

}
