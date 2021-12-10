package com.socialsim.controller;

import com.socialsim.controller.university.controls.UniversityScreenController;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.generic.controls.WelcomeScreenController;
import com.socialsim.model.simulator.university.UniversitySimulator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    public static UniversitySimulator universitySimulator = null;

    public static boolean hasMadeChoice = false;
    public static FXMLLoader mainScreenLoader;
    public static Parent mainRoot;
    public static ScreenController mainScreenController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeSimulator();

        FXMLLoader welcomeInterfaceLoader = ScreenController.getLoader(getClass(), "/com/socialsim/view/WelcomeScreen.fxml");
        Parent welcomeRoot = welcomeInterfaceLoader.load();
        WelcomeScreenController welcomeScreenController = welcomeInterfaceLoader.getController();

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
                    mainScreenLoader = ScreenController.getLoader(getClass(), "/com/socialsim/view/UniversityScreen.fxml");
                    mainRoot = mainScreenLoader.load();
                    mainScreenController = (UniversityScreenController) mainScreenLoader.getController();
                }
            }
            else if (!welcomeScreenController.isClosedWithAction()) {
                break;
            }

            if (!Main.hasMadeChoice) {
                break;
            }

            mainScreenController.showWindow(mainRoot, "University SocialSim", true, false);
        }
    }

    private void initializeSimulator() {
        universitySimulator = new UniversitySimulator();
    }

}