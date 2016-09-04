package org.silentsoft.everywhere.client.application;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.model.Delta;
import org.silentsoft.ui.model.MaximizeProperty;
import org.silentsoft.ui.util.StageDragResizer;


public class AppController implements EventListener {
	
	@FXML
	private AnchorPane root;
	
	@FXML
	private AnchorPane head;
	
	@FXML
	private AnchorPane body;
	
	@FXML
	private AnchorPane toast;
	
	@FXML
	private Button appMenuBtn;
	
	@FXML
	private Button appMinimizeBtn;
	
	@FXML
	private Button appMaximizeBtn;
	
	@FXML
	private Button appCloseBtn;
	
	private MaximizeProperty maximizeProperty;
	
	private NotificationPane notificationPane;
	
	protected void initialize() {
		EventHandler.addListener(this);
		
		maximizeProperty = new MaximizeProperty(App.getStage());
		notificationPane = createNotificationPane(toast);
		
		makeDraggable(App.getStage(), head);
		makeNormalizable(App.getStage(), head);
		
		makeTransportable(appMenuBtn);
		
		makeMinimizable(App.getStage(), appMinimizeBtn);
		makeMaximizable(App.getStage(), appMaximizeBtn);
		makeClosable(App.getStage(), appCloseBtn);
		
		StageDragResizer.makeResizable(App.getStage(), root);
	}
	
	protected Pane getHead() {
		return head;
	}
	
	protected Pane getBody() {
		return body;
	}
	
	private NotificationPane getNotificationPane() {
		return notificationPane;
	}
	
	private NotificationPane createNotificationPane(Pane installTarget) {
		final NotificationPane notificationPane = new NotificationPane();
		notificationPane.setShowFromTop(true);
		notificationPane.setOnHidden(event -> {
			installTarget.setVisible(false);
		});
		AnchorPane.setLeftAnchor(notificationPane, 0.0);
		AnchorPane.setRightAnchor(notificationPane, 0.0);
		AnchorPane.setTopAnchor(notificationPane, 0.0);
		AnchorPane.setBottomAnchor(notificationPane, 0.0);
		installTarget.getChildren().add(notificationPane);
		
		return notificationPane;
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
                
                byNode.setOpacity(0.8);
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
    			maximizeProperty.setMaximized(stage, !maximizeProperty.isMaximized());
    			if (maximizeProperty.isMaximized()) {
    				// This option is recommended when maximized.
    				AnchorPane.setLeftAnchor(root, 0.0);
    				AnchorPane.setRightAnchor(root, 0.0);
    				AnchorPane.setTopAnchor(root, 0.0);
    				AnchorPane.setBottomAnchor(root, 0.0);
    			} else {
    				// Showing shadow when normalized.
    				AnchorPane.setLeftAnchor(root, 5.0);
    				AnchorPane.setRightAnchor(root, 5.0);
    				AnchorPane.setTopAnchor(root, 5.0);
    				AnchorPane.setBottomAnchor(root, 5.0);
    			}
    		}
    	});
    }
    
    /**
     * makes a transportable to specific menu using a given node.
     * @param stage
     * @param byNode
     */
    private void makeTransportable(final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			boolean appLoginStatus = ObjectUtil.toBoolean(SharedMemory.getDataMap().get(BizConst.KEY_APP_LOGIN_STATUS), false);
    			if (appLoginStatus) {
    				EventHandler.callEvent(AppController.class, BizConst.EVENT_VIEW_INDEX);
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
    	byNode.setOnMouseClicked(mouseEvent -> {
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
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			maximizeProperty.setMaximized(stage, !maximizeProperty.isMaximized());
    		}
    	});
    }
    
    /**
     * makes a stage closable using a given node.
     * @param stage
     * @param byNode
     */
    private void makeClosable(final Stage stage, final Node byNode) {
    	byNode.setOnMouseClicked(mouseEvent -> {
    		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
    			Transition animation = AnimationUtils.createTransition(App.getParent(), AnimationType.BOUNCE_OUT_DOWN);
    			animation.setOnFinished(actionEvent -> {
    				stage.hide();
    			});
    			animation.play();
    		}
    	});
    }

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_NOTI_FAKE:
			onEventNotiFake();
			break;
		}
	}
	
	private void onEventNotiFake() {
		if (notificationPane == null) {
			return;
		}
		
		Platform.runLater(() -> {
			toast.setVisible(true);
			notificationPane.setText("Hello, World !");
			notificationPane.getActions().clear();
			notificationPane.getActions().add(new Action("Done", eventHandler -> {
				getNotificationPane().hide();
				toast.setVisible(false);
			}));
			notificationPane.show();
		});
	}
}
