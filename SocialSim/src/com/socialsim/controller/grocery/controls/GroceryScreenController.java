package com.socialsim.controller.grocery.controls;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.controls.ScreenController;
import com.socialsim.controller.grocery.graphics.GroceryGraphicsController;
import com.socialsim.controller.grocery.graphics.amenity.mapper.*;
import com.socialsim.model.core.agent.grocery.GroceryAgentMovement;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchfield.BathroomField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.gate.GroceryGate;
import com.socialsim.model.simulator.SimulationTime;
import com.socialsim.model.simulator.grocery.GrocerySimulator;
import com.socialsim.model.simulator.university.UniversitySimulator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    @FXML private StackPane stackPane;
    @FXML private Canvas backgroundCanvas;
    @FXML private Canvas foregroundCanvas;
    @FXML private Canvas markingsCanvas;
    @FXML private Text elapsedTimeText;
    @FXML private ToggleButton playButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;
    @FXML private Button resetToDefaultButton;
    @FXML private TextField nonverbalMean;
    @FXML private TextField nonverbalStdDev;
    @FXML private TextField cooperativeMean;
    @FXML private TextField cooperativeStdDev;
    @FXML private TextField exchangeMean;
    @FXML private TextField exchangeStdDev;
    @FXML private TextField maxFamily;
    @FXML private TextField maxAlone;
    @FXML private TextField maxCurrentFamily;
    @FXML private TextField maxCurrentAlone;
    @FXML private TextField fieldOfView;
    @FXML private Button configureIOSButton;
    @FXML private Button editInteractionButton;
    @FXML private Label currentFamilyCount;
    @FXML private Label currentAloneCustomerCount;
    @FXML private Label totalFamilyCount;
    @FXML private Label totalAloneCustomerCount;
    @FXML private Label currentNonverbalCount;
    @FXML private Label currentCooperativeCount;
    @FXML private Label currentExchangeCount;
    @FXML private Label averageNonverbalDuration;
    @FXML private Label averageCooperativeDuration;
    @FXML private Label averageExchangeDuration;
    @FXML private Label currentFamilyToFamilyCount;
    @FXML private Label currentCustomerCustomerCount;
    @FXML private Label currentCustomerAisleCount;
    @FXML private Label currentCustomerCashierCount;
    @FXML private Label currentCustomerBaggerCount;
    @FXML private Label currentCustomerGuardCount;
    @FXML private Label currentCustomerButcherCount;
    @FXML private Label currentCustomerServiceCount;
    @FXML private Label currentCustomerFoodCount;
    @FXML private Label currentAisleAisleCount;
    @FXML private Label currentCashierBaggerCount;
    @FXML private Button exportToCSVButton;
    @FXML private Button exportHeatMapButton;

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
        resetToDefault();
        playButton.setDisable(true);
        exportToCSVButton.setDisable(true);
        exportHeatMapButton.setDisable(true);

        int width = 60;
        int length = 100;
        int rows = (int) Math.ceil(width / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        int columns = (int) Math.ceil(length / Patch.PATCH_SIZE_IN_SQUARE_METERS);
        Grocery grocery = Grocery.GroceryFactory.create(rows, columns);
        Main.grocerySimulator.resetToDefaultConfiguration(grocery);
        Grocery.configureDefaultIOS();
        grocery.copyDefaultToIOS();
        Grocery.configureDefaultInteractionTypeChances();
        grocery.copyDefaultToInteractionTypeChances();
    }

    @FXML
    public void initializeAction() {
        if (Main.grocerySimulator.isRunning()) {
            playAction();
            playButton.setSelected(false);
        }

        if (validateParameters()) {
            Grocery grocery = Main.grocerySimulator.getGrocery();
            this.configureParameters(grocery);
            initializeGrocery(grocery);
            grocery.convertIOSToChances();
            setElements();
            playButton.setDisable(false);
            exportToCSVButton.setDisable(true);
            exportHeatMapButton.setDisable(true);
            Main.grocerySimulator.replenishStaticVars();
            disableEdits();
        }
    }

    public void initializeGrocery(Grocery grocery) {
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
        for (int j = 21; j < 33; j++) {
            wallPatches.add(grocery.getPatch(51, j));
        }
        for (int i = 45; i < 59; i++) {
            for (int j = 1; j < 16; j ++) {
                wallPatches.add(grocery.getPatch(i, j));
            }
        }
        Main.grocerySimulator.getGrocery().getWalls().add(Wall.wallFactory.create(wallPatches, 1));

        List<Patch> fBathroomPatches = new ArrayList<>();
        for (int i = 53; i < 59; i++) {
            for (int j = 1; j < 16; j++) {
                fBathroomPatches.add(grocery.getPatch(i, j));
            }
        }
        Main.grocerySimulator.getGrocery().getBathroomFields().add(BathroomField.bathroomFactory.create(fBathroomPatches, 1));

        List<Patch> mBathroomPatches = new ArrayList<>();
        for (int i = 46; i < 52; i++) {
            for (int j = 1; j < 16; j++) {
                mBathroomPatches.add(grocery.getPatch(i, j));
            }
        }
        Main.grocerySimulator.getGrocery().getBathroomFields().add(BathroomField.bathroomFactory.create(mBathroomPatches, 2));

        List<Patch> wallPatches2 = new ArrayList<>();
        wallPatches2.add(grocery.getPatch(56,39));
        wallPatches2.add(grocery.getPatch(57,39));
        wallPatches2.add(grocery.getPatch(58,39));
        Main.grocerySimulator.getGrocery().getWalls().add(Wall.wallFactory.create(wallPatches2, 1));

        List<Patch> cartRepoPatches = new ArrayList<>();
        cartRepoPatches.add(grocery.getPatch(43,65));
        cartRepoPatches.add(grocery.getPatch(43,68));
        cartRepoPatches.add(grocery.getPatch(43,71));
        CartRepoMapper.draw(cartRepoPatches);

        List<Patch> freshProductsPatches = new ArrayList<>();
        freshProductsPatches.add(grocery.getPatch(34,64));
        freshProductsPatches.add(grocery.getPatch(34,75));
        freshProductsPatches.add(grocery.getPatch(34,86));

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
        productAislePatches.add(grocery.getPatch(10,31));
        productAislePatches.add(grocery.getPatch(10,58));
        productAislePatches.add(grocery.getPatch(10,83));
        productAislePatches.add(grocery.getPatch(16,31));
        productAislePatches.add(grocery.getPatch(16,58));
        productAislePatches.add(grocery.getPatch(16,83));
        productAislePatches.add(grocery.getPatch(22,31));
        productAislePatches.add(grocery.getPatch(22,58));
        productAislePatches.add(grocery.getPatch(22,83));
        productAislePatches.add(grocery.getPatch(28,31));
        productAislePatches.add(grocery.getPatch(28,58));
        productAislePatches.add(grocery.getPatch(28,83));


        productAislePatches.add(grocery.getPatch(22,9));
        productAislePatches.add(grocery.getPatch(28,9));
        ProductAisleMapper.draw(productAislePatches);

        List<Patch> productShelfPatches = new ArrayList<>();
        productShelfPatches.add(grocery.getPatch(10,47));
        productShelfPatches.add(grocery.getPatch(10,73));
        productShelfPatches.add(grocery.getPatch(16,47));
        productShelfPatches.add(grocery.getPatch(16,73));
        productShelfPatches.add(grocery.getPatch(22,47));
        productShelfPatches.add(grocery.getPatch(22,73));
        productShelfPatches.add(grocery.getPatch(28,47));
        productShelfPatches.add(grocery.getPatch(28,73));
        productShelfPatches.add(grocery.getPatch(34,9));
        productShelfPatches.add(grocery.getPatch(34,20));
        productShelfPatches.add(grocery.getPatch(34,31));
        productShelfPatches.add(grocery.getPatch(34,42));
        productShelfPatches.add(grocery.getPatch(34,53));

        productShelfPatches.add(grocery.getPatch(22,23));
        productShelfPatches.add(grocery.getPatch(28,23));
        ProductShelfMapper.draw(productShelfPatches);

        List<Patch> productWallDownPatches = new ArrayList<>();
        productWallDownPatches.add(grocery.getPatch(0,5));
        productWallDownPatches.add(grocery.getPatch(0,14));
        productWallDownPatches.add(grocery.getPatch(0,23));
        productWallDownPatches.add(grocery.getPatch(0,32));
        productWallDownPatches.add(grocery.getPatch(0,41));
        productWallDownPatches.add(grocery.getPatch(0,51));
        productWallDownPatches.add(grocery.getPatch(0,60));
        productWallDownPatches.add(grocery.getPatch(0,69));
        productWallDownPatches.add(grocery.getPatch(0,78));
        productWallDownPatches.add(grocery.getPatch(0,87));
        ProductWallMapper.draw(productWallDownPatches, "DOWN");

        List<Patch> productWallLeftPatches = new ArrayList<>();
        productWallLeftPatches.add(grocery.getPatch(6,98));
        productWallLeftPatches.add(grocery.getPatch(15,98));
        productWallLeftPatches.add(grocery.getPatch(25,98));
        productWallLeftPatches.add(grocery.getPatch(34,98));
        ProductWallMapper.draw(productWallLeftPatches, "LEFT");

        List<Patch> tablePatches = new ArrayList<>();
        tablePatches.add(grocery.getPatch(51,61));
        tablePatches.add(grocery.getPatch(54,61));
        tablePatches.add(grocery.getPatch(57,61));
        tablePatches.add(grocery.getPatch(51,65));
        tablePatches.add(grocery.getPatch(54,65));
        tablePatches.add(grocery.getPatch(57,65));
        tablePatches.add(grocery.getPatch(51,70));
        tablePatches.add(grocery.getPatch(54,70));
        tablePatches.add(grocery.getPatch(57,70));
        tablePatches.add(grocery.getPatch(51,74));
        tablePatches.add(grocery.getPatch(54,74));
        tablePatches.add(grocery.getPatch(57,74));
        tablePatches.add(grocery.getPatch(51,79));
        tablePatches.add(grocery.getPatch(54,79));
        tablePatches.add(grocery.getPatch(57,79));
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
        serviceCounterPatches.add(grocery.getPatch(52,22));
        serviceCounterPatches.add(grocery.getPatch(52,26));
        serviceCounterPatches.add(grocery.getPatch(52,30));
        ServiceCounterMapper.draw(serviceCounterPatches);

        List<Patch> stallPatches = new ArrayList<>();
//        stallPatches.add(grocery.getPatch(58,8));
//        stallPatches.add(grocery.getPatch(58,17));
//        stallPatches.add(grocery.getPatch(58,26));
//        stallPatches.add(grocery.getPatch(58,35));
//        stallPatches.add(grocery.getPatch(58,63));
//        stallPatches.add(grocery.getPatch(58,72));
        stallPatches.add(grocery.getPatch(58,83));
        stallPatches.add(grocery.getPatch(58,86));
        stallPatches.add(grocery.getPatch(58,89));
        stallPatches.add(grocery.getPatch(58,92));
        stallPatches.add(grocery.getPatch(58,95));
//        stallPatches.add(grocery.getPatch(58,90));
        StallMapper.draw(stallPatches);


        List<Patch> sinkPatches = new ArrayList<>();
        sinkPatches.add(grocery.getPatch(53,2));
        sinkPatches.add(grocery.getPatch(53,5));
        sinkPatches.add(grocery.getPatch(53,8));
        sinkPatches.add(grocery.getPatch(53,11));
        sinkPatches.add(grocery.getPatch(53,14));

        sinkPatches.add(grocery.getPatch(46,2));
        sinkPatches.add(grocery.getPatch(46,5));
        sinkPatches.add(grocery.getPatch(46,8));
        sinkPatches.add(grocery.getPatch(46,11));
        sinkPatches.add(grocery.getPatch(46,14));

        SinkMapper.draw(sinkPatches);

        List<Patch> toiletPatches = new ArrayList<>();
        toiletPatches.add(grocery.getPatch(58,2));
        toiletPatches.add(grocery.getPatch(58,5));
        toiletPatches.add(grocery.getPatch(58,8));
        toiletPatches.add(grocery.getPatch(58,11));
        toiletPatches.add(grocery.getPatch(58,14));

        toiletPatches.add(grocery.getPatch(51,2));
        toiletPatches.add(grocery.getPatch(51,5));
        toiletPatches.add(grocery.getPatch(51,8));
        toiletPatches.add(grocery.getPatch(51,11));
        toiletPatches.add(grocery.getPatch(51,14));

        ToiletMapper.draw(toiletPatches);
    }

    private void drawInterface() {
        drawGroceryViewBackground(Main.grocerySimulator.getGrocery());
        drawGroceryViewForeground(Main.grocerySimulator.getGrocery(), false);
    }

    public void drawGroceryViewBackground(Grocery grocery) {
        GroceryGraphicsController.requestDrawGroceryView(stackPane, grocery, GroceryGraphicsController.tileSize, true, false);
    }

    public void drawGroceryViewForeground(Grocery grocery, boolean speedAware) {
        GroceryGraphicsController.requestDrawGroceryView(stackPane, grocery, GroceryGraphicsController.tileSize, false, speedAware);
        requestUpdateInterfaceSimulationElements();
    }

    public void exportHeatMap() {
        try {
            GrocerySimulator.exportHeatMap();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void requestUpdateInterfaceSimulationElements() {
        Platform.runLater(this::updateSimulationTime);
        Platform.runLater(this::updateStatistics);
    }

    public void updateSimulationTime() {
        LocalTime currentTime = Main.grocerySimulator.getSimulationTime().getTime();
        long elapsedTime = Main.grocerySimulator.getSimulationTime().getStartTime().until(currentTime, ChronoUnit.SECONDS) / 5;
        String timeString;
        timeString = String.format("%02d", currentTime.getHour()) + ":" + String.format("%02d", currentTime.getMinute()) + ":" + String.format("%02d", currentTime.getSecond());
        elapsedTimeText.setText("Current time: " + timeString + " (" + elapsedTime + " ticks)");
    }

    public void updateStatistics() {
        currentFamilyCount.setText(String.valueOf(GrocerySimulator.currentFamilyCount));
        currentAloneCustomerCount.setText(String.valueOf(GrocerySimulator.currentAloneCustomerCount));
        totalFamilyCount.setText(String.valueOf(GrocerySimulator.totalFamilyCount));
        totalAloneCustomerCount.setText(String.valueOf(GrocerySimulator.totalAloneCustomerCount));
        currentNonverbalCount.setText(String.valueOf(GrocerySimulator.currentNonverbalCount));
        currentCooperativeCount.setText(String.valueOf(GrocerySimulator.currentCooperativeCount));
        currentExchangeCount.setText(String.valueOf(GrocerySimulator.currentExchangeCount));
        averageNonverbalDuration.setText(String.format("%.02f", GrocerySimulator.averageNonverbalDuration));
        averageCooperativeDuration.setText(String.format("%.02f", GrocerySimulator.averageCooperativeDuration));
        averageExchangeDuration.setText(String.format("%.02f", GrocerySimulator.averageExchangeDuration));
        currentFamilyToFamilyCount.setText(String.valueOf(GrocerySimulator.currentFamilyToFamilyCount));
        currentCustomerCustomerCount.setText(String.valueOf(GrocerySimulator.currentCustomerCustomerCount));
        currentCustomerAisleCount.setText(String.valueOf(GrocerySimulator.currentCustomerAisleCount));
        currentCustomerCashierCount.setText(String.valueOf(GrocerySimulator.currentCustomerCashierCount));
        currentCustomerBaggerCount.setText(String.valueOf(GrocerySimulator.currentCustomerBaggerCount));
        currentCustomerGuardCount.setText(String.valueOf(GrocerySimulator.currentCustomerGuardCount));
        currentCustomerButcherCount.setText(String.valueOf(GrocerySimulator.currentCustomerButcherCount));
        currentCustomerServiceCount.setText(String.valueOf(GrocerySimulator.currentCustomerServiceCount));
        currentCustomerFoodCount.setText(String.valueOf(GrocerySimulator.currentCustomerFoodCount));
        currentAisleAisleCount.setText(String.valueOf(GrocerySimulator.currentAisleAisleCount));
        currentCashierBaggerCount.setText(String.valueOf(GrocerySimulator.currentCashierBaggerCount));
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
        if (!Main.grocerySimulator.isRunning()) {
            Main.grocerySimulator.setRunning(true);
            Main.grocerySimulator.getPlaySemaphore().release();
            playButton.setText("Pause");
            exportToCSVButton.setDisable(true);
            exportHeatMapButton.setDisable(true);
        }
        else {
            Main.grocerySimulator.setRunning(false);
            playButton.setText("Play");
            exportToCSVButton.setDisable(false);
            exportHeatMapButton.setDisable(false);
        }
    }

    @Override
    protected void closeAction() {
    }

    public void disableEdits() {
        nonverbalMean.setDisable(true);
        nonverbalStdDev.setDisable(true);
        cooperativeMean.setDisable(true);
        cooperativeStdDev.setDisable(true);
        exchangeMean.setDisable(true);
        exchangeStdDev.setDisable(true);
        fieldOfView.setDisable(true);
        maxFamily.setDisable(true);
        maxAlone.setDisable(true);
        maxCurrentFamily.setDisable(true);
        maxCurrentAlone.setDisable(true);
        resetToDefaultButton.setDisable(true);
        configureIOSButton.setDisable(true);
        editInteractionButton.setDisable(true);
    }

    public void resetToDefault() {
        nonverbalMean.setText(Integer.toString(GroceryAgentMovement.defaultNonverbalMean));
        nonverbalStdDev.setText(Integer.toString(GroceryAgentMovement.defaultNonverbalStdDev));
        cooperativeMean.setText(Integer.toString(GroceryAgentMovement.defaultCooperativeMean));
        cooperativeStdDev.setText(Integer.toString(GroceryAgentMovement.defaultCooperativeStdDev));
        exchangeMean.setText(Integer.toString(GroceryAgentMovement.defaultExchangeMean));
        exchangeStdDev.setText(Integer.toString(GroceryAgentMovement.defaultExchangeStdDev));
        fieldOfView.setText(Integer.toString(GroceryAgentMovement.defaultFieldOfView));
        maxFamily.setText(Integer.toString(GrocerySimulator.defaultMaxFamily));
        maxAlone.setText(Integer.toString(GrocerySimulator.defaultMaxAlone));
        maxCurrentFamily.setText(Integer.toString(GrocerySimulator.defaultMaxCurrentFamily));
        maxCurrentAlone.setText(Integer.toString(GrocerySimulator.defaultMaxCurrentAlone));
    }

    public void openIOSLevels() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/GroceryConfigureIOS.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Configure IOS Levels");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void openEditInteractions() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/socialsim/view/GroceryEditInteractions.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Interaction Type Chances");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void exportToCSV(){
        try {
            GrocerySimulator.exportToCSV();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void configureParameters(Grocery grocery) {
        grocery.setNonverbalMean(Integer.parseInt(nonverbalMean.getText()));
        grocery.setNonverbalStdDev(Integer.parseInt(nonverbalStdDev.getText()));
        grocery.setCooperativeMean(Integer.parseInt(cooperativeMean.getText()));
        grocery.setCooperativeStdDev(Integer.parseInt(cooperativeStdDev.getText()));
        grocery.setExchangeMean(Integer.parseInt(exchangeMean.getText()));
        grocery.setExchangeStdDev(Integer.parseInt(exchangeStdDev.getText()));
        grocery.setFieldOfView(Integer.parseInt(fieldOfView.getText()));
        grocery.setMAX_FAMILY(Integer.parseInt(maxFamily.getText()));
        grocery.setMAX_ALONE(Integer.parseInt(maxAlone.getText()));
        grocery.setMAX_CURRENT_FAMILY(Integer.parseInt(maxCurrentFamily.getText()));
        grocery.setMAX_CURRENT_ALONE(Integer.parseInt(maxCurrentAlone.getText()));
    }

    public boolean validateParameters() {
        boolean validParameters = Integer.parseInt(nonverbalMean.getText()) >= 0 && Integer.parseInt(nonverbalMean.getText()) >= 0
                && Integer.parseInt(cooperativeMean.getText()) >= 0 && Integer.parseInt(cooperativeStdDev.getText()) >= 0
                && Integer.parseInt(exchangeMean.getText()) >= 0 && Integer.parseInt(exchangeStdDev.getText()) >= 0
                && Integer.parseInt(fieldOfView.getText()) >= 0 && Integer.parseInt(fieldOfView.getText()) <= 360
                && Integer.parseInt(maxFamily.getText()) >= 0 && Integer.parseInt(maxAlone.getText()) >= 0
                && Integer.parseInt(maxCurrentFamily.getText()) >= 0 && Integer.parseInt(maxCurrentAlone.getText()) >= 0;

        if (!validParameters) {
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

}