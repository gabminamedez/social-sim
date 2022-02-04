package com.socialsim.controller.grocery.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.grocery.graphics.GroceryGraphicsController;
import com.socialsim.controller.grocery.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.agent.university.UniversityAgentMovement;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.university.UniversitySimulator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GroceryScreenController extends ScreenController {

    @FXML private ScrollPane scrollPane;
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
    @FXML private Tab parameters;
    @FXML private Button resetToDefaultButton;
    @FXML private TextField nonverbalMean;
    @FXML private TextField nonverbalStdDev;
    @FXML private TextField cooperativeMean;
    @FXML private TextField cooperativeStdDev;
    @FXML private TextField exchangeMean;
    @FXML private TextField exchangeStdDev;
    @FXML private TextField maxStudents;
    @FXML private TextField maxProfessors;
    @FXML private TextField maxCurrentStudents;
    @FXML private TextField maxCurrentProfessors;
    @FXML private TextField fieldOfView;
    @FXML private Button configureIOSButton;
    @FXML private Button editInteractionButton;

    private final double CANVAS_SCALE = 0.5;

    public GroceryScreenController() {
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    @FXML
    private void initialize() {
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SimulationTime.SLEEP_TIME_MILLISECONDS.set((int) (1.0 / newVal.intValue() * 1000));
        });
    }

    @FXML
    public void initializeAction() {
        if (Main.grocerySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }

        int width = 60; // Value may be from 25-100
        int length = 100; // Value may be from 106-220
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 60 rows
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS); // 100 columns
        Grocery grocery = Grocery.GroceryFactory.create(rows, columns);
        initializeGrocery(grocery);
        setElements();
    }

    public void initializeGrocery(Grocery grocery) {
        Main.grocerySimulator.resetToDefaultConfiguration(grocery);
        GroceryGraphicsController.tileSize = backgroundCanvas.getHeight() / Main.grocerySimulator.getGrocery().getRows();
        mapGrocery();
        Main.grocerySimulator.spawnInitialAgents(grocery);
        drawInterface();
    }

    public void mapGrocery() {
        Grocery grocery = Main.grocerySimulator.getGrocery();
        int rows = grocery.getRows();
        int cols = grocery.getColumns();

        List<Patch> wallPatches = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            wallPatches.add(grocery.getPatch(i, 0));
        }
        for (int i = 0; i < rows; i++) {
            wallPatches.add(grocery.getPatch(i, 99));
        }
        for (int j = 0; j < cols; j++) {
            wallPatches.add(grocery.getPatch(0, j));
        }
        for (int j = 0; j < cols; j++) {
            if (j < 44 || j > 55) {
                wallPatches.add(grocery.getPatch(59, j));
            }
        }
        for (int j = 0; j < 16; j++) {
            wallPatches.add(grocery.getPatch(43, j));
        }
        for (int i = 43; i < 47; i++) {
            for (int j = 15; j < 64; j ++) {
                if (j == 15 || j == 21 || j == 27 || j == 33 || j == 39 || j == 45 || j == 51 || j == 57 || j == 63) {
                    wallPatches.add(grocery.getPatch(i, j));
                }
            }
        }
        Main.grocerySimulator.getGrocery().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> cartRepoPatches = new ArrayList<>();
        cartRepoPatches.add(grocery.getPatch(43,65));
        cartRepoPatches.add(grocery.getPatch(43,68));
        cartRepoPatches.add(grocery.getPatch(43,71));
        CartRepoMapper.draw(cartRepoPatches);

        List<Patch> freshProductsPatches = new ArrayList<>();
        freshProductsPatches.add(grocery.getPatch(22,9));
        freshProductsPatches.add(grocery.getPatch(22,20));
        freshProductsPatches.add(grocery.getPatch(28,9));
        freshProductsPatches.add(grocery.getPatch(28,20));
        FreshProductsMapper.draw(freshProductsPatches);

        List<Patch> frozenProductsPatches = new ArrayList<>();
        frozenProductsPatches.add(grocery.getPatch(10,9));
        frozenProductsPatches.add(grocery.getPatch(10,20));
        frozenProductsPatches.add(grocery.getPatch(16,9));
        frozenProductsPatches.add(grocery.getPatch(16,20));
        FrozenProductsMapper.draw(frozenProductsPatches);

        List<Patch> frozenWallPatches = new ArrayList<>();
        frozenWallPatches.add(grocery.getPatch(6,1));
        frozenWallPatches.add(grocery.getPatch(15,1));
        FrozenWallMapper.draw(frozenWallPatches);

        List<Patch> groceryGateExitPatches = new ArrayList<>();
        groceryGateExitPatches.add(grocery.getPatch(59,44));
        GroceryGateMapper.draw(groceryGateExitPatches, GroceryGate.GroceryGateMode.EXIT);

        List<Patch> groceryGateEntrancePatches = new ArrayList<>();
        groceryGateEntrancePatches.add(grocery.getPatch(59,52));
        GroceryGateMapper.draw(groceryGateEntrancePatches, GroceryGate.GroceryGateMode.ENTRANCE);

        List<Patch> meatSectionPatches = new ArrayList<>();
        meatSectionPatches.add(grocery.getPatch(25,1));
        meatSectionPatches.add(grocery.getPatch(34,1));
        MeatSectionMapper.draw(meatSectionPatches);

        List<Patch> productAislePatches = new ArrayList<>();
        productAislePatches.add(grocery.getPatch(10,31)); // 0
        productAislePatches.add(grocery.getPatch(10,58)); // 1
        productAislePatches.add(grocery.getPatch(10,83)); // 2
        productAislePatches.add(grocery.getPatch(16,31)); // 3
        productAislePatches.add(grocery.getPatch(16,58)); // 4
        productAislePatches.add(grocery.getPatch(16,83)); // 5
        productAislePatches.add(grocery.getPatch(22,31)); // 6
        productAislePatches.add(grocery.getPatch(22,58)); // 7
        productAislePatches.add(grocery.getPatch(22,83)); // 8
        productAislePatches.add(grocery.getPatch(28,31)); // 9
        productAislePatches.add(grocery.getPatch(28,58)); // 10
        productAislePatches.add(grocery.getPatch(28,83)); // 11
        ProductAisleMapper.draw(productAislePatches);

        List<Patch> productShelfPatches = new ArrayList<>();
        productShelfPatches.add(grocery.getPatch(10,47)); // 0
        productShelfPatches.add(grocery.getPatch(10,73)); // 1
        productShelfPatches.add(grocery.getPatch(16,47)); // 2
        productShelfPatches.add(grocery.getPatch(16,73)); // 3
        productShelfPatches.add(grocery.getPatch(22,47)); // 4
        productShelfPatches.add(grocery.getPatch(22,73)); // 5
        productShelfPatches.add(grocery.getPatch(28,47)); // 6
        productShelfPatches.add(grocery.getPatch(28,73)); // 7
        productShelfPatches.add(grocery.getPatch(34,9)); // 8
        productShelfPatches.add(grocery.getPatch(34,20)); // 9
        productShelfPatches.add(grocery.getPatch(34,31)); // 10
        productShelfPatches.add(grocery.getPatch(34,42)); // 11
        productShelfPatches.add(grocery.getPatch(34,53)); // 12
        productShelfPatches.add(grocery.getPatch(34,64)); // 13
        productShelfPatches.add(grocery.getPatch(34,75)); // 14
        productShelfPatches.add(grocery.getPatch(34,86)); // 15
        ProductShelfMapper.draw(productShelfPatches);

        List<Patch> productWallDownPatches = new ArrayList<>();
        productWallDownPatches.add(grocery.getPatch(0,5)); // 0
        productWallDownPatches.add(grocery.getPatch(0,14)); // 1
        productWallDownPatches.add(grocery.getPatch(0,23)); // 2
        productWallDownPatches.add(grocery.getPatch(0,32)); // 3
        productWallDownPatches.add(grocery.getPatch(0,41)); // 4
        productWallDownPatches.add(grocery.getPatch(0,51)); // 5
        productWallDownPatches.add(grocery.getPatch(0,60)); // 6
        productWallDownPatches.add(grocery.getPatch(0,69)); // 7
        productWallDownPatches.add(grocery.getPatch(0,78)); // 8
        productWallDownPatches.add(grocery.getPatch(0,87)); // 9
        ProductWallMapper.draw(productWallDownPatches, "DOWN");

        List<Patch> productWallLeftPatches = new ArrayList<>();
        productWallLeftPatches.add(grocery.getPatch(6,98)); // 10
        productWallLeftPatches.add(grocery.getPatch(15,98)); // 11
        productWallLeftPatches.add(grocery.getPatch(25,98)); // 12
        productWallLeftPatches.add(grocery.getPatch(34,98)); // 13
        ProductWallMapper.draw(productWallLeftPatches, "LEFT");

        List<Patch> tablePatches = new ArrayList<>();
        tablePatches.add(grocery.getPatch(52,2));
        tablePatches.add(grocery.getPatch(55,2));
        tablePatches.add(grocery.getPatch(52,5));
        tablePatches.add(grocery.getPatch(55,5));
        tablePatches.add(grocery.getPatch(52,11));
        tablePatches.add(grocery.getPatch(55,11));
        tablePatches.add(grocery.getPatch(52,14));
        tablePatches.add(grocery.getPatch(55,14));
        tablePatches.add(grocery.getPatch(52,20));
        tablePatches.add(grocery.getPatch(55,20));
        tablePatches.add(grocery.getPatch(52,23));
        tablePatches.add(grocery.getPatch(55,23));
        tablePatches.add(grocery.getPatch(52,29));
        tablePatches.add(grocery.getPatch(55,29));
        tablePatches.add(grocery.getPatch(52,32));
        tablePatches.add(grocery.getPatch(55,32));
        tablePatches.add(grocery.getPatch(52,38));
        tablePatches.add(grocery.getPatch(55,38));
        tablePatches.add(grocery.getPatch(52,41));
        tablePatches.add(grocery.getPatch(55,41));
        tablePatches.add(grocery.getPatch(52,57));
        tablePatches.add(grocery.getPatch(55,57));
        tablePatches.add(grocery.getPatch(52,60));
        tablePatches.add(grocery.getPatch(55,60));
        tablePatches.add(grocery.getPatch(52,66));
        tablePatches.add(grocery.getPatch(55,66));
        tablePatches.add(grocery.getPatch(52,69));
        tablePatches.add(grocery.getPatch(55,69));
        tablePatches.add(grocery.getPatch(52,75));
        tablePatches.add(grocery.getPatch(55,75));
        tablePatches.add(grocery.getPatch(52,78));
        tablePatches.add(grocery.getPatch(55,78));
        tablePatches.add(grocery.getPatch(52,84));
        tablePatches.add(grocery.getPatch(55,84));
        tablePatches.add(grocery.getPatch(52,87));
        tablePatches.add(grocery.getPatch(55,87));
        tablePatches.add(grocery.getPatch(52,93));
        tablePatches.add(grocery.getPatch(55,93));
        tablePatches.add(grocery.getPatch(52,96));
        tablePatches.add(grocery.getPatch(55,96));
        TableMapper.draw(tablePatches);

        List<Patch> cashierCounterPatches = new ArrayList<>();
        cashierCounterPatches.add(grocery.getPatch(43,19));
        cashierCounterPatches.add(grocery.getPatch(43,25));
        cashierCounterPatches.add(grocery.getPatch(43,31));
        cashierCounterPatches.add(grocery.getPatch(43,37));
        cashierCounterPatches.add(grocery.getPatch(43,43));
        cashierCounterPatches.add(grocery.getPatch(43,49));
        cashierCounterPatches.add(grocery.getPatch(43,55));
        cashierCounterPatches.add(grocery.getPatch(43,61));
        CashierCounterMapper.draw(cashierCounterPatches);

        List<Patch> securityPatches = new ArrayList<>();
        securityPatches.add(grocery.getPatch(56,53));
        SecurityMapper.draw(securityPatches);

        List<Patch> serviceCounterPatches = new ArrayList<>();
        serviceCounterPatches.add(grocery.getPatch(44,4));
        serviceCounterPatches.add(grocery.getPatch(44,8));
        serviceCounterPatches.add(grocery.getPatch(44,12));
        ServiceCounterMapper.draw(serviceCounterPatches);

        List<Patch> stallPatches = new ArrayList<>();
        stallPatches.add(grocery.getPatch(58,8));
        stallPatches.add(grocery.getPatch(58,17));
        stallPatches.add(grocery.getPatch(58,26));
        stallPatches.add(grocery.getPatch(58,35));
        stallPatches.add(grocery.getPatch(58,63));
        stallPatches.add(grocery.getPatch(58,72));
        stallPatches.add(grocery.getPatch(58,81));
        stallPatches.add(grocery.getPatch(58,90));
        StallMapper.draw(stallPatches);
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
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
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
        GroceryAgent.clearGroceryAgentCounts();
        clearGrocery(Main.grocerySimulator.getGrocery());
        Main.universitySimulator.spawnInitialAgents(Main.universitySimulator.getUniversity());
        drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), false); // Redraw the canvas
        if (Main.grocerySimulator.isRunning()) { // If the simulator is running, stop it
            playAction();
            playButton.setSelected(false);
        }
    }

    public static void clearGrocery(Grocery grocery) {
        for (GroceryAgent agent : grocery.getAgents()) { // Remove the relationship between the patch and the agents
            agent.getAgentMovement().getCurrentPatch().getAgents().clear();
            agent.getAgentMovement().setCurrentPatch(null);
        }

        // Remove all the agents
        grocery.getAgents().removeAll(grocery.getAgents());
        grocery.getAgents().clear();
        grocery.getAgentPatchSet().clear();
    }

    @Override
    protected void closeAction() {
    }

    public void resetToDefault(){
        nonverbalMean.setText(Integer.toString(UniversityAgentMovement.defaultNonverbalMean));
        nonverbalStdDev.setText(Integer.toString(UniversityAgentMovement.defaultNonverbalStdDev));
        cooperativeMean.setText(Integer.toString(UniversityAgentMovement.defaultCooperativeMean));
        cooperativeStdDev.setText(Integer.toString(UniversityAgentMovement.defaultCooperativeStdDev));
        exchangeMean.setText(Integer.toString(UniversityAgentMovement.defaultExchangeMean));
        exchangeStdDev.setText(Integer.toString(UniversityAgentMovement.defaultExchangeStdDev));
        fieldOfView.setText(Integer.toString(UniversityAgentMovement.defaultFieldOfView));
        maxStudents.setText(Integer.toString(UniversitySimulator.defaultMaxStudents));
        maxProfessors.setText(Integer.toString(UniversitySimulator.defaultMaxProfessors));
        maxCurrentStudents.setText(Integer.toString(UniversitySimulator.defaultMaxCurrentStudents));
        maxCurrentProfessors.setText(Integer.toString(UniversitySimulator.defaultMaxCurrentProfessors));
    }

    public void openIOSLevels(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/UniversityConfigureIOS.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Configure IOS Levels");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void openEditInteractions(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/UniversityEditInteractions.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Interaction Type Chances");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void configureParameters(University university){
        university.setNonverbalMean(Integer.parseInt(nonverbalMean.getText()));
        university.setNonverbalStdDev(Integer.parseInt(nonverbalStdDev.getText()));
        university.setCooperativeMean(Integer.parseInt(cooperativeMean.getText()));
        university.setCooperativeStdDev(Integer.parseInt(cooperativeStdDev.getText()));
        university.setExchangeMean(Integer.parseInt(exchangeMean.getText()));
        university.setExchangeStdDev(Integer.parseInt(exchangeStdDev.getText()));
        university.setFieldOfView(Integer.parseInt(fieldOfView.getText()));
        university.setMAX_STUDENTS(Integer.parseInt(maxStudents.getText()));
        university.setMAX_PROFESSORS(Integer.parseInt(maxProfessors.getText()));
        university.setMAX_CURRENT_STUDENTS(Integer.parseInt(maxCurrentStudents.getText()));
        university.setMAX_CURRENT_PROFESSORS(Integer.parseInt(maxCurrentProfessors.getText()));
    }

    public boolean validateParameters(){
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxStudents.getText()) >= 0 && Integer.parseInt(maxProfessors.getText()) >= 0
                && Integer.parseInt(maxCurrentStudents.getText()) >= 0 && Integer.parseInt(maxCurrentProfessors.getText()) >= 0;
        if (!validParameters){
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            Label label = new Label("Failed to initialize. Please make sure all values are greater than 0, and field of view is not greater than 360 degrees");
            label.setWrapText(true);
            alert.getDialogPane().setContent(label);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }
        return validParameters;
    }
    public void generateHeatMap(){

    }
}