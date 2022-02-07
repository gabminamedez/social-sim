package com.socialsim.controller.office.controls;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.environment.office.Office;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OfficeIOSController {

    @FXML VBox container;
    @FXML GridPane gridPane;
    @FXML Button btnCancel;
    @FXML Button btnResetToDefault;
    @FXML Button btnSave;

    public OfficeIOSController() {
    }

    @FXML
    private void initialize() {
        //TODO: Create columns of IOS levels
        Office office = Main.officeSimulator.getOffice();

        int otherCtr = 0;
        boolean other = false;
        int[] otherArr = new int[OfficeAgent.Persona.values().length];
        for (int i = 0; i < OfficeAgent.Persona.values().length + 1; i++) { // column
            for (int j = 0; j < OfficeAgent.Persona.values().length + 1; j++) { // row
                if (i == 0 || j == 0){
                    if (i == 0 && j == 0)
                        gridPane.add(new Label(""), i, j);
                    else{
                        if (i == 0){
                            gridPane.add(new Label(OfficeAgent.Persona.values()[j - 1].name()), i + otherCtr, j);
                        }
                        else{
                            if (other){
                                gridPane.add(new Label(OfficeAgent.Persona.values()[i - 1].name() + "_OTHER_TEAM"), i + otherCtr, j);
                            }
                            else{
                                gridPane.add(new Label(OfficeAgent.Persona.values()[i - 1].name()), i + otherCtr, j);
                            }

                        }
                    }
                }
                else{
                    if (other){
                        if ((OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.MANAGER || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.INT_BUSINESS
                                || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.EXT_BUSINESS || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.INT_RESEARCHER
                                || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.EXT_RESEARCHER || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.INT_TECHNICAL
                                || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.EXT_TECHNICAL) &&
                                (OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.MANAGER || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_BUSINESS
                                        || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_BUSINESS || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_RESEARCHER
                                        || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_RESEARCHER || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_TECHNICAL
                                        || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_TECHNICAL)){
                            otherArr[j - 1]++;
                            gridPane.add(new TextField(office.getIOSScales().get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                        }
                        else{
//                            System.out.println(i + " " + j + " " + OfficeAgent.Persona.values()[i - 1] + " " + OfficeAgent.Persona.values()[j - 1]);
                            TextField tf = new TextField("");
                            tf.setDisable(true);
                            gridPane.add(tf, otherCtr, j);
                        }
                    }
                    else{
                        gridPane.add(new TextField(office.getIOSScales().get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                    }
                    if (j == OfficeAgent.Persona.values().length && other){
                        other = false;
                    }
                    else if (j == OfficeAgent.Persona.values().length){
                        if (OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.MANAGER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_BUSINESS
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_BUSINESS
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_RESEARCHER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_RESEARCHER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_TECHNICAL
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_TECHNICAL){
                            other = true;
                            i--;
                            otherCtr++;
                        }
                    }
                }
            }
        }
    }

    public void cancelChanges(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void resetToDefault(){
        Group g = (Group) gridPane.getChildren().get(0);
        gridPane.getChildren().removeAll(gridPane.getChildren());
        gridPane.getChildren().add(0, g);
        int otherCtr = 0;
        boolean other = false;
        int[] otherArr = new int[OfficeAgent.Persona.values().length];
        for (int i = 0; i < OfficeAgent.Persona.values().length + 1; i++) { // column
            for (int j = 0; j < OfficeAgent.Persona.values().length + 1; j++) { // row
                if (i == 0 || j == 0){
                    if (i == 0 && j == 0)
                        gridPane.add(new Label(""), i, j);
                    else{
                        if (i == 0){
                            gridPane.add(new Label(OfficeAgent.Persona.values()[j - 1].name()), i + otherCtr, j);
                        }
                        else{
                            if (other){
                                gridPane.add(new Label(OfficeAgent.Persona.values()[i - 1].name() + "_OTHER_TEAM"), i + otherCtr, j);
                            }
                            else{
                                gridPane.add(new Label(OfficeAgent.Persona.values()[i - 1].name()), i + otherCtr, j);
                            }

                        }
                    }
                }
                else{
                    if (other){
                        if ((OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.MANAGER || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.INT_BUSINESS
                                || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.EXT_BUSINESS || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.INT_RESEARCHER
                                || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.EXT_RESEARCHER || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.INT_TECHNICAL
                                || OfficeAgent.Persona.values()[j - 1] == OfficeAgent.Persona.EXT_TECHNICAL) &&
                                (OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.MANAGER || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_BUSINESS
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_BUSINESS || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_RESEARCHER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_RESEARCHER || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_TECHNICAL
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_TECHNICAL)){
                            otherArr[j - 1]++;
                            gridPane.add(new TextField(Office.defaultIOS.get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                        }
                        else{
                            TextField tf = new TextField("");
                            tf.setDisable(true);
                            gridPane.add(tf, otherCtr, j);
                        }
                    }
                    else{
                        gridPane.add(new TextField(Office.defaultIOS.get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                    }
                    if (j == OfficeAgent.Persona.values().length && other){
                        other = false;
                    }
                    else if (j == OfficeAgent.Persona.values().length){
                        if (OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.MANAGER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_BUSINESS
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_BUSINESS
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_RESEARCHER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_RESEARCHER
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.INT_TECHNICAL
                                || OfficeAgent.Persona.values()[i - 1] == OfficeAgent.Persona.EXT_TECHNICAL){
                            other = true;
                            i--;
                            otherCtr++;
                        }
                    }
                }
            }
        }
    }

    public void saveChanges(){
        boolean validIOS = false;

        for (Node node: gridPane.getChildren()){
            if (node.getClass() == TextField.class && !node.isDisabled()){
                validIOS = this.checkValidIOS(((TextField) node).getText());
                if (!validIOS){
                    break;
                }
            }
        }
        if (!validIOS){
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            Label label = new Label("Failed to parse. Please make sure IOS levels are from 1-7 only, and separate them with commas (,). Also ensure there are no duplicates in a field.");
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }
        else{
            Office office = Main.officeSimulator.getOffice();
            CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> newIOS = new CopyOnWriteArrayList<>();
            for (int i = 0; i < OfficeAgent.Persona.values().length + 1; i++) { // row
                if (i > 0)
                    newIOS.add(new CopyOnWriteArrayList<>());
                for (int j = 0; j < OfficeAgent.Persona.values().length + 1 + 7; j++) { // column, +7 for 7 OTHER cases
                    int index = 1 + j * (OfficeAgent.Persona.values().length + 1) + i;
                    if (index > OfficeAgent.Persona.values().length + 1 && index % (OfficeAgent.Persona.values().length + 1) - 1 != 0){
                        String s = ((TextField) gridPane.getChildren().get(index)).getText();
                        if (!s.equals("")){
                            Integer[] IOSArr = Arrays.stream(s.replace(" ", "").split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);

                            newIOS.get(i - 1).add(new CopyOnWriteArrayList<>(List.of(IOSArr)));
                        }
                    }
                    else{ // invalid
//                        System.out.println(gridPane.getChildren().get(index));
                    }
                }
            }
            office.setIOSScales(newIOS);
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

    public boolean checkValidIOS(String s){
        s = s.replace(" ", "");
        if (s.matches("^[1-7](,[1-7])*$")){
            Integer[] IOSArr = Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
            HashSet<Integer> IOSSet = new HashSet<>(List.of((IOSArr)));
            return IOSSet.size() == IOSArr.length;
        }
        else{
            return false;
        }
    }
}
