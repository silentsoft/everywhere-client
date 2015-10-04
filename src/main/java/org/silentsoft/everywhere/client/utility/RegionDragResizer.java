package org.silentsoft.everywhere.client.utility;

import javafx.scene.layout.Region;

public class RegionDragResizer extends DragResizer {

    private RegionDragResizer(Region region) {
    	super(region);
    }

    public static void makeResizable(Region region) {
    	new RegionDragResizer(region);
    }
}