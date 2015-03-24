package org.silentsoft.everywhere.client.button;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ImageButton extends Button {
    
	/**
	 * -fx-background-insets: 0px -fx-background-radius: 0px;
	 * -fx-background-color: transparent;
	 * -fx-padding: 0;
	 * 
	 * -fx-background-color: linear-gradient(to bottom, #f2f2f2, #d4d4d4);
	 * 
	 */
    private final String STYLE_NORMAL = "-fx-background-color: transparent;";
    private final String STYLE_ENTERED = "-fx-background-color: rgba(235,235,235,0.05);";
    private final String STYLE_PRESSED = "-fx-background-color: rgba(180,180,180,0.05);";
    
    public ImageButton() {
        setStyle(STYLE_NORMAL);
        
        setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				setStyle(STYLE_ENTERED);
			}
        });
        
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setStyle(STYLE_NORMAL);
            }            
        });
        
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setStyle(STYLE_PRESSED);
            }            
        });
        
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               setStyle(STYLE_NORMAL);
            }            
        });
    }
}

