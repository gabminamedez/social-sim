package com.socialsim.university.controller.controls;

import com.socialsim.university.controller.Controller;
import javafx.fxml.FXMLLoader;

public class ScreenController extends Controller {

    public ScreenController() {

    }

    public static FXMLLoader getLoader(Class<?> classType, String interfaceLocation) {
        return new FXMLLoader(classType.getResource(interfaceLocation));
    }

}
