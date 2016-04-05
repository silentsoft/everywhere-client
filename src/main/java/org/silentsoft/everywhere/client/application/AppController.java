package org.silentsoft.everywhere.client.application;

import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.component.button.ImageButton;
import org.silentsoft.everywhere.client.model.Delta;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.model.MaximizeProperty;
import org.silentsoft.ui.util.StageDragResizer;


public class AppController {
	
	@FXML
	private AnchorPane main;
	
	@FXML
	private HBox head;
	
	@FXML
	private AnchorPane body;
	
	@FXML
	private ImageButton appMenuBtn;
	
	@FXML
	private ImageButton appMinimizeBtn;
	
	@FXML
	private ImageButton appMaximizeBtn;
	
	@FXML
	private ImageButton appCloseBtn;
	
	private MaximizeProperty maximizeProperty;
	
	protected void initialize() {
		maximizeProperty = new MaximizeProperty(App.getStage());
		
		makeDraggable(App.getStage(), head);
		makeNormalizable(App.getStage(), head);
		
		makeTransportable(appMenuBtn);
		
		makeMinimizable(App.getStage(), appMinimizeBtn);
		makeMaximizable(App.getStage(), appMaximizeBtn);
		makeClosable(App.getStage(), appCloseBtn);
		
		StageDragResizer.makeResizable(App.getStage(), main);
	}
	
//	public Pane getHead() {
//		return head;
//	}
//	
//	public Pane getBody() {
//		return body;
//	}
//	
//	public void setBody(Pane body) {
//		this.body = body;
//	}

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
    		if (mouseEvent.getClickCount() >= CommonConst.MOUSE_DOUBLE_CLICK) {
//    			stage.setMaximized(!stage.isMaximized());
    			maximizeProperty.setMaximized(stage, !maximizeProperty.getMaximized());
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
    			boolean appLoginStatus = ObjectUtil.toBoolean(SharedMemory.getDataMap().get(BizConst.KEY_APP_LOGIN_STATUS), false);
    			if (appLoginStatus) {
    				EventHandler.callEvent(AppController.class, BizConst.EVENT_VIEW_MAIN);
    			}
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
//    			stage.setMaximized(!stage.isMaximized());
    			maximizeProperty.setMaximized(stage, !maximizeProperty.getMaximized());
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
    			Transition animation = AnimationUtils.createTransition(App.getParent(), AnimationType.BOUNCE_OUT_DOWN);
    			animation.setOnFinished(actionEvent -> {
    				stage.hide();
    			});
    			animation.play();
    		}
    	});
    }
}
