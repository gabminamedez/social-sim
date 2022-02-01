package com.socialsim.controller.university.controls;

import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.simulator.SimulationTime;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.*;
import javafx.util.*;

import java.util.Arrays;

public class IOSController {

    @FXML TableView tableView;
    @FXML Button btnCancel;;
    @FXML Button btnResetToDefault;
    @FXML Button btnSave;

    public IOSController() {
    }

    @FXML
    private void initialize() {
        //TODO: Create columns of IOS levels
        resetToDefault();
    }

    public void cancelChanges(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void resetToDefault(){

//        ObservableList<String> headerCells = FXCollections.observableArrayList();

        String[] headerCells = new String[UniversityAgent.Persona.values().length];
        for (int i = 0; i < UniversityAgent.Persona.values().length; i++) {
            headerCells[i] = UniversityAgent.Persona.values()[i].name();
        }
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.add(headerCells);
        for (int i = 0; i < UniversityAgent.Persona.values().length; i++) {
            TableColumn tc = new TableColumn();
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((UniversityAgent.Persona.values()[colNo].name()));
                }
            });
            tc.setCellFactory(TextFieldTableCell.forTableColumn());
            tc.setPrefWidth(90);
            tableView.getColumns().add(tc);
        }
        tableView.setItems(data);
        tableView.setEditable(true);
        tableView.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                // Get the table header
                Pane header = (Pane)tableView.lookup("TableHeaderRow");
                if(header!=null && header.isVisible()) {
                    header.setMaxHeight(0);
                    header.setMinHeight(0);
                    header.setPrefHeight(0);
                    header.setVisible(false);
                    header.setManaged(false);
                }
            }
        });
    }

    public void saveChanges(){
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
