package org.silentsoft.everywhere.client.utility;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import org.silentsoft.everywhere.client.model.Delta;

public abstract class DragResizer {
	
	public enum DragMode {
		WIDTH,
		HEIGHT
	}
	
	private static final int RESIZE_MARGIN = 5;

    private Region region;

    private Delta delta;
    
    private boolean dragging;
    
    private boolean dragForWidth;
    
    private boolean dragForHeight;
    
	protected DragResizer(Region region) {
		this.region = region;
		this.delta = new Delta();
		
		region.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePressed(event);
            }
        });
        region.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseDragged(event);
            }
        });
        region.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseOver(event);
            }
        });
        region.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseReleased(event);
            }
        });
	}
	
	protected void drag(DragMode dragMode, double newSize) {
		switch (dragMode) {
		case WIDTH:
			region.setPrefWidth(newSize);
			break;
		case HEIGHT:
			region.setPrefHeight(newSize);
			break;
		}
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
    	} else {
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
	        if (newWidth > region.getMinWidth()) {
	        	delta.setX(event.getX());
	        	drag(DragMode.WIDTH, newWidth);
	        }
        }
        
        if (dragForHeight) {
	        double newHeight = region.getPrefHeight() + (event.getY() - delta.getY());
	        if (newHeight > region.getMinHeight()) {
	        	delta.setY(event.getY());
	        	drag(DragMode.HEIGHT, newHeight);
	        }
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