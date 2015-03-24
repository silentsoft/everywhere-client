/**
 * Copyright 2012 - 2013 Andy Till
 * 
 * This file is part of EstiMate.
 * 
 * EstiMate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EstiMate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EstiMate.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.silentsoft.everywhere.client.utility;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import org.silentsoft.everywhere.client.model.Delta;

/**
 * {@link DragResizer} can be used to add mouse listeners to a {@link Region}
 * and make it resizable by the user by clicking and dragging the border in the
 * same way as a window.
 * <p>
 * Only height resizing is currently implemented. Usage: <pre>DragResizer.makeResizable(myAnchorPane);</pre>
 */
public class DragResizer {
    
    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 5;

    private final Region region;

    private Delta delta;
    
    private boolean dragging;
    
    private boolean dragForWidth;
    
    private boolean dragForHeight;
    
    private DragResizer(Region aRegion) {
        region = aRegion;
        delta = new Delta();
    }

    public static void makeResizable(Region region) {
        final DragResizer resizer = new DragResizer(region);
        
        region.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mousePressed(event);
            }});
        region.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseDragged(event);
            }});
        region.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseOver(event);
            }});
        region.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseReleased(event);
            }});
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event) {
    	if (isInDraggableZoneForWidth(event) && isInDraggableZoneForHeight(event)) {
    		region.setCursor(Cursor.SE_RESIZE);
    	} else if (isInDraggableZoneForWidth(event)) {
    		region.setCursor(Cursor.E_RESIZE);
    	} else if (isInDraggableZoneForHeight(event)) {
    		region.setCursor(Cursor.S_RESIZE);
    	}
        else {
            region.setCursor(Cursor.DEFAULT);
        }
    }
    
    protected boolean isInDraggableZoneForWidth(MouseEvent event) {
    	return (event.getX() > (region.getWidth() - RESIZE_MARGIN));
    }
    
    protected boolean isInDraggableZoneForHeight(MouseEvent event) {
    	return (event.getY() > (region.getHeight() - RESIZE_MARGIN));
    }

    protected boolean isInDraggableZone(MouseEvent event) {
         return isInDraggableZoneForWidth(event) || isInDraggableZoneForHeight(event);
    }

    protected void mouseDragged(MouseEvent event) {
        if(!dragging) {
            return;
        }
        
        if (dragForWidth) {
	        double newWidth = region.getPrefWidth() + (event.getX() - delta.getX());
	        region.setPrefWidth(newWidth);
	        delta.setX(event.getX());
        }
        
        if (dragForHeight) {
	        double newHeight = region.getPrefHeight() + (event.getY() - delta.getY());
	        region.setPrefHeight(newHeight);
	        delta.setY(event.getY());
        }
    }

    protected void mousePressed(MouseEvent event) {
        // ignore clicks outside of the draggable margin
        if(!isInDraggableZone(event)) {
            return;
        } else {
        	dragging = true;
        	dragForWidth = false;
        	dragForHeight = false;
        }
        
        if (isInDraggableZoneForWidth(event)) {
        	dragForWidth = true;
        	delta.setX(event.getX());
        }
        
        if (isInDraggableZoneForHeight(event)) {
        	dragForHeight = true;
        	delta.setY(event.getY());
        }
    }
}