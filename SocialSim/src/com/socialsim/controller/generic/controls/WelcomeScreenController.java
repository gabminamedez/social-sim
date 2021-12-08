package com.socialsim.controller.generic.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class WelcomeScreenController extends ScreenController {

    @FXML ChoiceBox<String> environmentChoice;
    @FXML Button startButton;

    public static String environment = null;

    public WelcomeScreenController() {
    }

    @FXML
    public void gotoEnvironment() {
        environment = environmentChoice.getValue();
        this.setClosedWithAction(true);
        stage.close();
    }

    @Override
    protected void closeAction() {
    }

}