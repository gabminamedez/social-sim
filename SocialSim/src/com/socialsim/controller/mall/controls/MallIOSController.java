package com.socialsim.controller.mall.controls;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.mall.Mall;
import javafx.fxml.FXML;
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
        for (int i = 0; i < MallAgent.Persona.values().length + 1; i++) { // column
            for (int j = 0; j < MallAgent.Persona.values().length + 1; j++) { // row
                if (i == 0 || j == 0){
                    if (i == 0 && j == 0)
                        gridPane.add(new Label(""), i, j);
                    else{
                        if (i == 0)
                            gridPane.add(new Label(MallAgent.Persona.values()[j - 1].name()), i, j);
                        else
                            gridPane.add(new Label(MallAgent.Persona.values()[i - 1].name()), i, j);
                    }
                }
                else{
                    gridPane.add(new TextField(mall.getIOSScales().get(i - 1).get(j - 1).toString().replace("]", "").replace("[", "")), i, j);
                }
            }
        }
    }

    public void cancelChanges(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void resetToDefault(){
        for (Node node: gridPane.getChildren()){
            if (node.getClass() == TextField.class){
                int row = GridPane.getRowIndex(node), column = GridPane.getColumnIndex(node);
                if (row > 0 && column > 0)
                    ((TextField) node).setText(Mall.defaultIOS.get(column - 1).get(row - 1).toString().replace("]", "").replace("[", ""));
            }
        }
    }

    public void saveChanges(){
        boolean validIOS = false;

        for (Node node: gridPane.getChildren()){
            if (node.getClass() == TextField.class){
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
            for (Node node: gridPane.getChildren()){
                if (node.getClass() == TextField.class){
                    int row = GridPane.getRowIndex(node), column = GridPane.getColumnIndex(node);
                    String s = ((TextField) node).getText();
                    Integer[] IOSArr = Arrays.stream(s.replace(" ", "").split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
                    if (row > 0 && column > 0)
                        mall.getIOSScales().get(column - 1).set(row - 1, new CopyOnWriteArrayList<>(List.of(IOSArr)));
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
