package com.socialsim.controller.generic.controls;

import com.socialsim.controller.generic.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;

public abstract class ScreenController extends Controller {

    private final HashMap<String, Object> windowInput;
    private final HashMap<String, Object> windowOutput;
    private boolean closedWithAction;
    protected Stage stage;

    public ScreenController() {
        this.closedWithAction = false;
        this.windowInput = new HashMap<>();
        this.windowOutput = new HashMap<>();
        this.stage = null;
    }

    public Stage getStage() {
        return this.stage;
    }

    public static FXMLLoader getLoader(Class<?> classType, String interfaceLocation) {
        return new FXMLLoader(classType.getResource(interfaceLocation));
    }

    public void showWindow(Parent loadedRoot, String title, boolean showAndWait, boolean isAlwaysOnTop) {
        Scene scene = loadedRoot.getScene();

        if (scene == null) {
            scene = new Scene(loadedRoot);
        }
        else {
            scene.setRoot(loadedRoot);
        }

        scene.getRoot().setStyle("-fx-font-family: 'serif'");
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);
        this.stage = stage;

        stage.setOnCloseRequest(event -> {
            closeAction();
        });

        this.stage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                closeAction();
            }
        });

        if (showAndWait) {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        else {
            stage.setAlwaysOnTop(isAlwaysOnTop);
            stage.show();
        }
    }

    public void closeWindow() {
        this.stage.close();
    }

    public boolean isClosedWithAction() {
        return this.closedWithAction;
    }

    public void setClosedWithAction(boolean closedWithAction) {
        this.closedWithAction = closedWithAction;
    }

    public HashMap<String, Object> getWindowInput() {
        return windowInput;
    }

    public HashMap<String, Object> getWindowOutput() {
        return windowOutput;
    }

    protected abstract void closeAction();

}