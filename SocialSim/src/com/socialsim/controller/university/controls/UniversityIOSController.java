package com.socialsim.controller.university.controls;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.university.University;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UniversityIOSController {

    @FXML VBox container;
    @FXML GridPane gridPane;
    @FXML Button btnCancel;
    @FXML Button btnHelp;
    @FXML Button btnResetToDefault;
    @FXML Button btnSave;

    public UniversityIOSController() {
    }

    @FXML
    private void initialize() {
        University university = Main.universitySimulator.getUniversity();
        for (int i = 0; i < UniversityAgent.Persona.values().length + 1; i++) {
            for (int j = 0; j < UniversityAgent.Persona.values().length + 1; j++) {
                if (i == 0 || j == 0) {
                    if (i == 0 && j == 0) {
                        gridPane.add(new Label(""), j, i);
                    }
                    else {
                        if (i == 0) {
                            Label l = new Label(UniversityAgent.Persona.values()[j - 1].name());
                            l.setAlignment(Pos.CENTER);
                            gridPane.add(l, j, i);
                            GridPane.setHalignment(l, HPos.CENTER);
                        }
                        else {
                            Label l = new Label(UniversityAgent.Persona.values()[i - 1].name());
                            l.setAlignment(Pos.CENTER);
                            gridPane.add(l, j, i);
                            GridPane.setHalignment(l, HPos.CENTER);
                        }
                    }
                }
                else {
                    TextField tf = new TextField(university.getIOSScales().get(i - 1).get(j - 1).toString().replace("]", "").replace("[", ""));
                    tf.setAlignment(Pos.CENTER);
                    gridPane.add(tf, j, i);
                    GridPane.setHalignment(tf, HPos.CENTER);
                }
            }
        }
    }

    public void cancelChanges() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void resetToDefault() {
        for (Node node: gridPane.getChildren()) {
            if (node.getClass() == TextField.class) {
                int row = GridPane.getRowIndex(node), column = GridPane.getColumnIndex(node);
                if (row > 0 && column > 0) {
                    ((TextField) node).setText(University.defaultIOS.get(row - 1).get(column - 1).toString().replace("]", "").replace("[", ""));
                }
            }
        }
    }

    public void saveChanges() {
        boolean validIOS = false;

        for (Node node: gridPane.getChildren()) {
            if (node.getClass() == TextField.class) {
                validIOS = this.checkValidIOS(((TextField) node).getText());
                if (!validIOS) {
                    break;
                }
            }
        }
        if (!validIOS) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            Label label = new Label("Failed to parse. Please make sure IOS levels are from 1-7 only, and separate them with commas (,). Also ensure there are no duplicates in a field.");
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }
        else {
            University university = Main.universitySimulator.getUniversity();
            for (Node node: gridPane.getChildren()) {
                if (node.getClass() == TextField.class) {
                    int row = GridPane.getRowIndex(node), column = GridPane.getColumnIndex(node);
                    String s = ((TextField) node).getText();
                    Integer[] IOSArr = Arrays.stream(s.replace(" ", "").split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
                    if (row > 0 && column > 0) {
                        university.getIOSScales().get(column - 1).set(row - 1, new CopyOnWriteArrayList<>(List.of(IOSArr)));
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            Label label = new Label("IOS Levels successfully parsed.");
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }
    }

    public boolean checkValidIOS(String s) {
        s = s.replace(" ", "");
        if (s.matches("^[1-7](,[1-7])*$")) {
            Integer[] IOSArr = Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
            HashSet<Integer> IOSSet = new HashSet<>(List.of((IOSArr)));
            return IOSSet.size() == IOSArr.length;
        }
        else {
            return false;
        }
    }

    public void openHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        ImageView img = new ImageView();
        img.setImage(new Image(getClass().getResource("../../../view/image/IOS_help.png").toExternalForm()));
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().setContent(img);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

}