package com.socialsim.controller.mall.controls;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.mall.Mall;
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

public class MallIOSController {

    @FXML VBox container;
    @FXML GridPane gridPane;
    @FXML Button btnCancel;
    @FXML Button btnResetToDefault;
    @FXML Button btnSave;

    public MallIOSController() {
    }

    @FXML
    private void initialize() {
        //TODO: Create columns of IOS levels
        Mall mall = Main.mallSimulator.getMall();

        int otherCtr = 0;
        boolean other = false;
        int[] otherArr = new int[MallAgent.Persona.values().length];
        for (int i = 0; i < MallAgent.Persona.values().length + 1; i++) { // column
            for (int j = 0; j < MallAgent.Persona.values().length + 1; j++) { // row
                if (i == 0 || j == 0){
                    if (i == 0 && j == 0)
                        gridPane.add(new Label(""), i, j);
                    else{
                        if (i == 0){
                            gridPane.add(new Label(MallAgent.Persona.values()[j - 1].name()), i + otherCtr, j);
                        }
                        else{
                            if (other){
                                gridPane.add(new Label(MallAgent.Persona.values()[i - 1].name() + "_OTHER"), i + otherCtr, j);
                            }
                            else{
                                gridPane.add(new Label(MallAgent.Persona.values()[i - 1].name()), i + otherCtr, j);
                            }
                        }
                    }
                }
                else{
                    if (other){
                        if (((MallAgent.Persona.values()[j - 1] == MallAgent.Persona.STAFF_STORE_SALES || MallAgent.Persona.values()[j - 1] == MallAgent.Persona.STAFF_STORE_CASHIER)
                                && (MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_SALES || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_CASHIER))
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.ERRAND_FAMILY && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FAMILY)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.LOITER_FAMILY && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FAMILY)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.ERRAND_FRIENDS && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FRIENDS)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.LOITER_FRIENDS && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FRIENDS)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.LOITER_COUPLE && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_COUPLE)){
                            otherArr[j - 1]++;
                            gridPane.add(new TextField(mall.getIOSScales().get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                        }
                        else{
//                            System.out.println(i + " " + j + " " + MallAgent.Persona.values()[i - 1] + " " + MallAgent.Persona.values()[j - 1]);
                            TextField tf = new TextField("");
                            tf.setDisable(true);
                            gridPane.add(tf, otherCtr, j);
                        }
                    }
                    else{
                        gridPane.add(new TextField(mall.getIOSScales().get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                    }
                    if (j == MallAgent.Persona.values().length && other){
                        other = false;
                    }
                    else if (j == MallAgent.Persona.values().length){
                        if (MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_SALES
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_CASHIER
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FAMILY
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FAMILY
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FRIENDS
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FRIENDS
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_COUPLE){
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
        int[] otherArr = new int[MallAgent.Persona.values().length];
        for (int i = 0; i < MallAgent.Persona.values().length + 1; i++) { // column
            for (int j = 0; j < MallAgent.Persona.values().length + 1; j++) { // row
                if (i == 0 || j == 0){
                    if (i == 0 && j == 0)
                        gridPane.add(new Label(""), i, j);
                    else{
                        if (i == 0){
                            gridPane.add(new Label(MallAgent.Persona.values()[j - 1].name()), i + otherCtr, j);
                        }
                        else{
                            if (other){
                                gridPane.add(new Label(MallAgent.Persona.values()[i - 1].name() + "_OTHER"), i + otherCtr, j);
                            }
                            else{
                                gridPane.add(new Label(MallAgent.Persona.values()[i - 1].name()), i + otherCtr, j);
                            }

                        }
                    }
                }
                else{
                    if (other){
                        if (((MallAgent.Persona.values()[j - 1] == MallAgent.Persona.STAFF_STORE_SALES || MallAgent.Persona.values()[j - 1] == MallAgent.Persona.STAFF_STORE_CASHIER)
                                && (MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_SALES || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_CASHIER))
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.ERRAND_FAMILY && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FAMILY)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.LOITER_FAMILY && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FAMILY)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.ERRAND_FRIENDS && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FRIENDS)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.LOITER_FRIENDS && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FRIENDS)
                                || (MallAgent.Persona.values()[j - 1] == MallAgent.Persona.LOITER_COUPLE && MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_COUPLE)){
                            otherArr[j - 1]++;
                            gridPane.add(new TextField(Mall.defaultIOS.get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                        }
                        else{
                            TextField tf = new TextField("");
                            tf.setDisable(true);
                            gridPane.add(tf, otherCtr, j);
                        }
                    }
                    else{
                        gridPane.add(new TextField(Mall.defaultIOS.get(j - 1).get(i - 1 + otherArr[j-1]).toString().replace("]", "").replace("[", "")), i + otherCtr, j);
                    }
                    if (j == MallAgent.Persona.values().length && other){
                        other = false;
                    }
                    else if (j == MallAgent.Persona.values().length){
                        if (MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_SALES
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.STAFF_STORE_CASHIER
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FAMILY
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FAMILY
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.ERRAND_FRIENDS
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_FRIENDS
                                || MallAgent.Persona.values()[i - 1] == MallAgent.Persona.LOITER_COUPLE){
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
            Mall mall = Main.mallSimulator.getMall();
            CopyOnWriteArrayList<CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>> newIOS = new CopyOnWriteArrayList<>();
            for (int i = 0; i < MallAgent.Persona.values().length + 1; i++) { // row
                if (i > 0)
                    newIOS.add(new CopyOnWriteArrayList<>());
                for (int j = 0; j < MallAgent.Persona.values().length + 1 + 7; j++) { // column, +7 for 7 OTHER cases
                    int index = 1 + j * (MallAgent.Persona.values().length + 1) + i;
                    if (index > MallAgent.Persona.values().length + 1 && index % (MallAgent.Persona.values().length + 1) - 1 != 0){
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
            mall.setIOSScales(newIOS);
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
