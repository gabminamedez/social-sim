package com.socialsim.controller.office.controls;

import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.Main;
import com.socialsim.controller.office.graphics.OfficeGraphicsController;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchobject.passable.gate.OfficeGate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class OfficeScreenController extends ScreenController {

    @FXML
    private ScrollPane scrollPane;
    @FXML private Group canvasGroup;
    @FXML private StackPane stackPane;
    @FXML private Canvas backgroundCanvas;
    @FXML private Canvas foregroundCanvas;
    @FXML private Canvas markingsCanvas;
    @FXML private TabPane sidebar;
    @FXML private Spinner guardsSpinner;
    @FXML private Spinner janitorsSpinner;
    @FXML private Spinner professorsSpinner;
    @FXML private Spinner studentsSpinner;
    @FXML private Slider spawnsSlider;
    @FXML private Text elapsedTimeText;
    @FXML private Button initializeButton;
    @FXML private ToggleButton playButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;

    private final double CANVAS_SCALE = 0.3;

    public OfficeScreenController() {
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    @FXML
    public void initializeAction() {
        int width = 60; // Value may be from 25-100
        int length = 100; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 130 columns
        Office office = Office.OfficeFactory.create(rows, columns);
        initializeOffice(office);
        setElements();
    }

    public void initializeOffice(Office office) {
        Main.officeSimulator.resetToDefaultConfiguration(office);
        OfficeGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.officeSimulator.getOffice().getRows();
        mapOffice();
        drawInterface();
    }

    public void mapOffice() {
        Office office = Main.officeSimulator.getOffice();
        int rows = office.getRows();
        int cols = office.getColumns();
    }

    private void drawInterface() {
        drawOfficeViewBackground(Main.officeSimulator.getOffice()); // Initially draw the Office environment
        drawOfficeViewForeground(Main.officeSimulator.getOffice(), false); // Then draw the agents in the Office
    }

    public void drawOfficeViewBackground(Office office) { // Draw the office view background
        OfficeGraphicsController.requestDrawOfficeView(stackPane, office, OfficeGraphicsController.tileSize, true, false);
    }

    public void drawOfficeViewForeground(Office office, boolean speedAware) { // Draw the office view foreground
        OfficeGraphicsController.requestDrawOfficeView(stackPane, office, OfficeGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.officeSimulator.getSimulationTime().getTime();
        long elapsedTime = Main.officeSimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS);
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Elapsed time: " + timeString + " (" + elapsedTime + " s)");
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.officeSimulator.getOffice().getRows() * OfficeGraphicsController.tileSize;
        double columnsScaled = Main.officeSimulator.getOffice().getColumns() * OfficeGraphicsController.tileSize;

        stackPane.setPrefWidth(columnsScaled);
        stackPane.setPrefHeight(rowsScaled);

        backgroundCanvas.setWidth(columnsScaled);
        backgroundCanvas.setHeight(rowsScaled);

        foregroundCanvas.setWidth(columnsScaled);
        foregroundCanvas.setHeight(rowsScaled);

        markingsCanvas.setWidth(columnsScaled);
        markingsCanvas.setHeight(rowsScaled);
    }

    @FXML
    public void playAction() {
        if (!Main.officeSimulator.isRunning()) { // Not yet running to running (play simulation)
            Main.officeSimulator.setRunning(true);
            Main.officeSimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.officeSimulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        Main.officeSimulator.reset();

        // Clear all agents
//        clearOffice(Main.simulator.getOffice());

        drawOfficeViewForeground(Main.officeSimulator.getOffice(), false); // Redraw the canvas

        if (Main.officeSimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    @Override
    protected void closeAction() {
    }

}