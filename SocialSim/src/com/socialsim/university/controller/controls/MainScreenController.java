package com.socialsim.university.controller.controls;

import com.socialsim.university.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class MainScreenController extends Controller {

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
    @FXML private Slider classroomsSlider;
    @FXML private ChoiceBox entranceChoice;
    @FXML private Text elapsedTimeText;
    @FXML private ToggleButton playButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;

    @FXML
    private void randomClick() {
        System.out.println(spawnsSlider.getValue());
    }

}