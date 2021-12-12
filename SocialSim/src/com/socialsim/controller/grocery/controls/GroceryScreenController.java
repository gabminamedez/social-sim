package com.socialsim.controller.grocery.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.grocery.graphics.GroceryGraphicsController;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.grocery.Grocery;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class GroceryScreenController extends ScreenController {

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

    public GroceryScreenController() {
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
        Grocery grocery = Grocery.GroceryFactory.create(rows, columns);
        initializeGrocery(grocery);
        setElements();
    }

    public void initializeGrocery(Grocery grocery) {
        Main.grocerySimulator.resetToDefaultConfiguration(grocery);
        GroceryGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.grocerySimulator.getGrocery().getRows();
        mapGrocery();
        drawInterface();
    }

    public void mapGrocery() {
        Grocery grocery = Main.grocerySimulator.getGrocery();
        int rows = grocery.getRows();
        int cols = grocery.getColumns();
    }

    private void drawInterface() {
        drawGroceryViewBackground(Main.grocerySimulator.getGrocery()); // Initially draw the Grocery environment
        drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), false); // Then draw the agents in the Grocery
    }

    public void drawGroceryViewBackground(Grocery grocery) { // Draw the grocery view background
        GroceryGraphicsController.requestDrawGroceryView(stackPane, grocery, GroceryGraphicsController.tileSize, true, false);
    }

    public void drawGroceryViewForeground(Grocery grocery, boolean speedAware) { // Draw the grocery view foreground
        GroceryGraphicsController.requestDrawGroceryView(stackPane, grocery, GroceryGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    private void requestUpdateInterfaceSimulationElements() { // Update the interface elements pertinent to the simulation
        Platform.runLater(this::updateSimulationTime); // Update the simulation time
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.grocerySimulator.getSimulationTime().getTime();
        long elapsedTime = Main.grocerySimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS);
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Elapsed time: " + timeString + " (" + elapsedTime + " s)");
    }

    public void setElements() {
        stackPane.setScaleX(CANVAS_SCALE);
        stackPane.setScaleY(CANVAS_SCALE);

        double rowsScaled = Main.grocerySimulator.getGrocery().getRows() * GroceryGraphicsController.tileSize;
        double columnsScaled = Main.grocerySimulator.getGrocery().getColumns() * GroceryGraphicsController.tileSize;

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
        if (!Main.grocerySimulator.isRunning()) { // Not yet running to running (play simulation)
            Main.grocerySimulator.setRunning(true);
            Main.grocerySimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
        }
        else {
            Main.grocerySimulator.setRunning(false);
            playButton.setText("Play");
        }
    }

    @FXML
    public void resetAction() {
        Main.grocerySimulator.reset();

        // Clear all agents
//        clearGrocery(Main.simulator.getGrocery());

        drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), false); // Redraw the canvas

        if (Main.grocerySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    @Override
    protected void closeAction() {
    }

}