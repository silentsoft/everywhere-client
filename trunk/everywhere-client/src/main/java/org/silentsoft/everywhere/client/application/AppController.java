package org.silentsoft.everywhere.client.application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.everywhere.client.button.ImageButton;
import org.silentsoft.everywhere.client.model.Delta;
import org.silentsoft.everywhere.client.utility.DragResizer;
import org.silentsoft.everywhere.context.BizConst;

public class AppController {
	
	private final int MOUSE_DOUBLE_CLICK = 2;
	
	@FXML
	private VBox main;
	
	@FXML
	private HBox head;
	
	@FXML
	private Pane body;
	
	@FXML
	private ImageButton appMenuBtn;
	
	@FXML
	private ImageButton appMinimizeBtn;
	
	@FXML
	private ImageButton appMaximizeBtn;
	
	@FXML
	private ImageButton appCloseBtn;
	
	protected void initialize() {
		makeDraggable(App.getStage(), head);
		makeNormalizable(App.getStage(), head);
		
		makeTransportable(appMenuBtn);
		
		makeMinimizable(App.getStage(), appMinimizeBtn);
		makeMaximizable(App.getStage(), appMaximizeBtn);
		makeClosable(App.getStage(), appCloseBtn);
		
		DragResizer.makeResizable(main);
	}
	
	public Pane getHead() {
		return head;
	}
	
	public Pane getBody() {
		return body;
	}
	
	public void setBody(Pane body) {
		this.body = body;
	}

	/**
	 * makes a stage draggable using a given node.
	 * @param stage
	 * @param byNode
	 */
    private void makeDraggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        
        byNode.setOnMousePressed(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		dragDelta.setX(stage.getX() - mouseEvent.getScreenX());
                dragDelta.setY(stage.getY() - mouseEvent.getScreenY());
                
                byNode.setOpacity(0.9);
        	}
        });
        
        byNode.setOnMouseDragged(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		stage.setX(mouseEvent.getScreenX() + dragDelta.getX());
                stage.setY(mouseEvent.getScreenY() + dragDelta.getY());
    		}
        });
        
        byNode.setOnMouseReleased(mouseEvent -> {
        	if (mouseEvent.getButton() == MouseButton.PRIMARY) {
        		byNode.setOpacity(1.0);
        	}			
		});
    }
    
    /**
     * makes a stage normalizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeNormalizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getClickCount() >= MOUSE_DOUBLE_CLICK) {
    			stage.setMaximized(!stage.isMaximized());
    		}
    	});
    }
    
    /**
     * makes a transportable to specific menu using a given node.
     * @param stage
     * @param byNode
     */
    private void makeTransportable(final Node byNode) {
    	byNode.setOnMouseReleased(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			EventHandler.callEvent(AppController.class, BizConst.EVENT_VIEW_MAIN);
    		}
    	});
    }
    
    /**
     * makes a stage minimizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeMinimizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseReleased(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			stage.setIconified(true);
    		}
    	});
    }
    
    /**
     * makes a stage maximizable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeMaximizable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseReleased(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			stage.setMaximized(!stage.isMaximized());
    		}
    	});
    }
    
    /**
     * makes a stage closable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeClosable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseReleased(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			stage.close();
    		}
    	});
    }
}
