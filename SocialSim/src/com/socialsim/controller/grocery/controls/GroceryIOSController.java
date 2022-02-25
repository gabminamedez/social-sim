package com.socialsim.controller.grocery.controls;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.grocery.Grocery;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroceryIOSController {

    @FXML VBox container;
    @FXML GridPane gridPane;
    @FXML Button btnHelp;
    @FXML Button btnCancel;
    @FXML Button btnResetToDefault;
    @FXML Button btnSave;

    public GroceryIOSController() {
    }

    @FXML
    private void initialize() {
        Grocery grocery = Main.grocerySimulator.getGrocery();

        int otherCtr = 0;
        boolean other = false;
        int[] otherArr = new int[GroceryAgent.Persona.values().length];
        for (int i = 0; i < GroceryAgent.Persona.values().length + 1; i++) {
            for (int j = 0; j < GroceryAgent.Persona.values().length + 1; j++) {
                if (i == 0 || j == 0) {
                    if (i == 0 && j == 0) {
                        gridPane.add(new Label(""), i, j);
                    }
                    else{
                        if (i == 0) {
                            Label l = new Label(GroceryAgent.Persona.values()[j - 1].name());
                            l.setAlignment(Pos.CENTER);
                            gridPane.add(l, i + otherCtr, j);
                            GridPane.setHalignment(l, HPos.CENTER);
                        }
                        else {
                            Label l;
                            if (other) {
                                l = new Label(GroceryAgent.Persona.values()[i - 1].name() + "_OTHER");
                            }
                            else {
                                l = new Label(GroceryAgent.Persona.values()[i - 1].name());
                            }
                            l.setAlignment(Pos.CENTER);
                            gridPane.add(l, i + otherCtr, j);
                            GridPane.setHalignment(l, HPos.CENTER);
                        }
                    }
                }
                else {
                    if (other) {
                        if ((GroceryAgent.Persona.values()[j - 1] == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER && GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER)
                                || (GroceryAgent.Persona.values()[j - 1] == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER && GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER)
                                || (GroceryAgent.Persona.values()[j - 1] == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER && GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER)) {
                            otherArr[j - 1]++;
                            TextField tf = new TextField(grocery.getIOSScales().get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", ""));
                            tf.setAlignment(Pos.CENTER);
                            gridPane.add(tf, i + otherCtr, j);
                            GridPane.setHalignment(tf, HPos.CENTER);
                        }
                        else {
                            TextField tf = new TextField("");
                            tf.setDisable(true);
                            gridPane.add(tf, otherCtr, j);
                        }
                    }
                    else {
                        TextField tf = new TextField(grocery.getIOSScales().get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", ""));
                        tf.setAlignment(Pos.CENTER);
                        gridPane.add(tf, i + otherCtr, j);
                        GridPane.setHalignment(tf, HPos.CENTER);
                    }
                    if (j == GroceryAgent.Persona.values().length && other) {
                        other = false;
                    }
                    else if (j == GroceryAgent.Persona.values().length) {
                        if (GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER
                                || GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER
                                || GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER) {
                            other = true;
                            i--;
                            otherCtr++;
                        }
                    }
                }
            }
        }
    }

    public void cancelChanges() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void resetToDefault() {
        Group g = (Group) gridPane.getChildren().get(0);
        gridPane.getChildren().removeAll(gridPane.getChildren());
        gridPane.getChildren().add(0, g);
        int otherCtr = 0;
        boolean other = false;
        int[] otherArr = new int[GroceryAgent.Persona.values().length];
        for (int i = 0; i < GroceryAgent.Persona.values().length + 1; i++) {
            for (int j = 0; j < GroceryAgent.Persona.values().length + 1; j++) {
                if (i == 0 || j == 0) {
                    if (i == 0 && j == 0) {
                        gridPane.add(new Label(""), i, j);
                    }
                    else{
                        if (i == 0) {
                            Label l = new Label(GroceryAgent.Persona.values()[j - 1].name());
                            l.setAlignment(Pos.CENTER);
                            gridPane.add(l, i + otherCtr, j);
                            GridPane.setHalignment(l, HPos.CENTER);
                        }
                        else{
                            if (other) {
                                Label l = new Label(GroceryAgent.Persona.values()[i - 1].name() + "_OTHER");
                                l.setAlignment(Pos.CENTER);
                                gridPane.add(l, i + otherCtr, j);
                                GridPane.setHalignment(l, HPos.CENTER);
                            }
                            else {
                                Label l = new Label(GroceryAgent.Persona.values()[i - 1].name());
                                l.setAlignment(Pos.CENTER);
                                gridPane.add(l, i + otherCtr, j);
                                GridPane.setHalignment(l, HPos.CENTER);
                            }

                        }
                    }
                }
                else {
                    if (other) {
                        if ((GroceryAgent.Persona.values()[j - 1] == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER && GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER)
                                || (GroceryAgent.Persona.values()[j - 1] == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER && GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER)
                                || (GroceryAgent.Persona.values()[j - 1] == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER && GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER)) {
                            otherArr[j - 1]++;
                            TextField tf = new TextField(Grocery.defaultIOS.get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", ""));
                            tf.setAlignment(Pos.CENTER);
                            gridPane.add(tf, i + otherCtr, j);
                            GridPane.setHalignment(tf, HPos.CENTER);
                        }
                        else {
                            TextField tf = new TextField("");
                            tf.setDisable(true);
                            gridPane.add(tf, otherCtr, j);
                        }
                    }
                    else {
                        TextField tf = new TextField(Grocery.defaultIOS.get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", ""));
                        tf.setAlignment(Pos.CENTER);
                        gridPane.add(tf, i + otherCtr, j);
                        GridPane.setHalignment(tf, HPos.CENTER);
                    }
                    if (j == GroceryAgent.Persona.values().length && other) {
                        other = false;
                    }
                    else if (j == GroceryAgent.Persona.values().length) {
                        if (GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER
                                || GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER
                                || GroceryAgent.Persona.values()[i - 1] == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER) {
                            other = true;
                            i--;
                            otherCtr++;
                        }
                    }
                }
            }
        }
    }

    public void saveChanges() {
        boolean validIOS = false;

        for (Node node: gridPane.getChildren()) {
            if (node.getClass() == TextField.class && !node.isDisabled()) {
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
            Grocery grocery = Main.grocerySimulator.getGrocery();
            CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> newIOS = new CopyOnWriteArrayList<>();
            for (int i = 0; i < GroceryAgent.Persona.values().length + 1; i++) {
                if (i > 0) {
                    newIOS.add(new CopyOnWriteArrayList<>());
                }
                for (int j = 0; j < GroceryAgent.Persona.values().length + 1 + 3; j++) {
                    int index = 1 + j * (GroceryAgent.Persona.values().length + 1) + i;
                    if (index > GroceryAgent.Persona.values().length + 1 && index % (GroceryAgent.Persona.values().length + 1) - 1 != 0) {
                        String s = ((TextField) gridPane.getChildren().get(index)).getText();
                        if (!s.equals("")) {
                            Integer[] IOSArr = Arrays.stream(s.replace(" ", "").split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
                            newIOS.get(i - 1).add(new CopyOnWriteArrayList<>(List.of(IOSArr)));
                        }
                    }
                }
            }
            grocery.setIOSScales(newIOS);
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