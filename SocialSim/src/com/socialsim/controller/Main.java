package com.socialsim.controller;

import com.socialsim.controller.controls.UniversityScreenController;
import com.socialsim.controller.controls.ScreenController;
import com.socialsim.controller.controls.WelcomeScreenController;
import com.socialsim.model.simulator.Simulator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    public static Simulator simulator = null;
    public static boolean hasMadeChoice = false;
    public static UniversityScreenController mainScreenController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeSimulator();

        FXMLLoader welcomeInterfaceLoader = ScreenController.getLoader(getClass(), "/com/socialsim/view/WelcomeScreen.fxml");
        Parent welcomeRoot = welcomeInterfaceLoader.load();
        WelcomeScreenController welcomeScreenController = welcomeInterfaceLoader.getController();

        FXMLLoader mainInterfaceLoader = null;
        Parent mainRoot = null;
        UniversityScreenController mainController = null;

        while (true) {
            welcomeScreenController.setClosedWithAction(false);

            Main.hasMadeChoice = false;

            welcomeScreenController.showWindow(welcomeRoot, "SocialSim", true, false);

            if (welcomeScreenController.isClosedWithAction()) {
                Main.hasMadeChoice = true;

                if (WelcomeScreenController.environment.equals("Grocery")) {
                    break;
                }
                else if (WelcomeScreenController.environment.equals("Mall")) {
                    break;
                }
                else if (WelcomeScreenController.environment.equals("Office")) {
                    break;
                }
                else if (WelcomeScreenController.environment.equals("University")) {
                    mainInterfaceLoader = ScreenController.getLoader(getClass(), "/com/socialsim/view/UniversityScreen.fxml");
                }
            }
            else if (!welcomeScreenController.isClosedWithAction()) {
                break;
            }

            if (!Main.hasMadeChoice) {
                break;
            }

            mainRoot = mainInterfaceLoader.load();
            mainController = mainInterfaceLoader.getController();
            Main.mainScreenController = mainController;
            mainController.showWindow(mainRoot, "University SocialSim", true, false);
        }
    }

    private void initializeSimulator() {
        simulator = new Simulator();
    }

}