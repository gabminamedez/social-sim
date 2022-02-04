package com.socialsim.controller.mall.controls;

import com.socialsim.controller.Main;
import com.socialsim.model.core.agent.university.UniversityAction;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.university.University;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class MallEditInteractionsController {

    @FXML VBox container;
    @FXML GridPane gridPane;
    @FXML Button btnCancel;
    @FXML Button btnResetToDefault;
    @FXML Button btnSave;

    public MallEditInteractionsController() {
    }

    @FXML
    private void initialize() {
        //TODO: Create columns of interaction types
        University university = Main.universitySimulator.getUniversity();

        for (int i = 0; i < UniversityAgent.PersonaActionGroup.values().length + 1; i++) {
            for (int j = 0; j < UniversityAction.Name.values().length + 1; j++) {
                if (i == 0 || j == 0){
                    if (i == 0 && j == 0)
                        gridPane.add(new Label(""), i, j);
                    else{
                        if (i == 0)
                            gridPane.add(new Label(UniversityAction.Name.values()[j - 1].name()), i, j);
                        else
                            gridPane.add(new Label(UniversityAgent.PersonaActionGroup.values()[i - 1].name()), i, j);
                    }
                }
                else{
                    gridPane.add(new TextField(university.getInteractionTypeChances().get(i - 1).get(j - 1).toString().replace("]", "").replace("[", "")), i, j);
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
                    ((TextField) node).setText(University.defaultInteractionTypeChances.get(column - 1).get(row - 1).toString().replace("]", "").replace("[", ""));
            }
        }
    }

    public void saveChanges(){
        boolean validInteractionChances = false;

        for (Node node: gridPane.getChildren()){
            if (node.getClass() == TextField.class){
                validInteractionChances = this.checkValidInteraction(((TextField) node).getText());
                if (!validInteractionChances){
                    System.out.println(((TextField) node).getText());
                    break;
                }
            }
        }
        if (!validInteractionChances){
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            Label label = new Label("Failed to parse. Please make sure the interaction type chances are from 0-100 only, and separate them with commas (,). Also ensure that the sum of the three is only either 0 or 100.");
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }
        else{
            University university = Main.universitySimulator.getUniversity();
            for (Node node: gridPane.getChildren()){
                if (node.getClass() == TextField.class){
                    int row = GridPane.getRowIndex(node), column = GridPane.getColumnIndex(node);
                    String s = ((TextField) node).getText();
                    Integer[] interactionArr = Arrays.stream(s.replace(" ", "").split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
                    if (row > 0 && column > 0)
                        university.getInteractionTypeChances().get(column - 1).set(row - 1, new CopyOnWriteArrayList<>(List.of(interactionArr)));
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            Label label = new Label("Interaction Type Chances successfully parsed.");
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

    public boolean checkValidInteraction(String s){
        s = s.replace(" ", "");
        if (s.matches("^([0-9]|[1-9][0-9]|(100))(,([0-9]|[1-9][0-9]|(100)))*$")){
            int[] interactionArr = Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
            if (!((IntStream.of(interactionArr).sum() == 0 || IntStream.of(interactionArr).sum() == 100) && interactionArr.length == 3)){

                System.out.println(s);
                System.out.println("invalid");
            }
            return (IntStream.of(interactionArr).sum() == 0 || IntStream.of(interactionArr).sum() == 100) && interactionArr.length == 3;
        }
        else{
            return false;
        }
    }
}
