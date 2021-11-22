package com.socialsim.controller;

import com.socialsim.controller.controls.MainScreenController;
import com.socialsim.model.simulator.Simulator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Simulator simulator = null;
    public static MainScreenController mainScreenController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeSimulator();

        Parent root = FXMLLoader.load(getClass().getResource("/com/socialsim/view/MainScreen.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("University SocialSim");
        primaryStage.show();
    }

    private void initializeSimulator() {
        simulator = new Simulator();
    }

}