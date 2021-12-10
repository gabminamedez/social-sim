package com.socialsim.controller.university.graphics;

import com.socialsim.controller.generic.Controller;
import com.socialsim.controller.Main;
import com.socialsim.controller.university.graphics.agent.UniversityAgentGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.Location;
import com.socialsim.model.core.environment.generic.position.MatrixPosition;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.Bathroom;
import com.socialsim.model.core.environment.university.patchfield.Classroom;
import com.socialsim.model.core.environment.university.patchfield.Laboratory;
import com.socialsim.model.core.environment.university.patchfield.Wall;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

public class UniversityGraphicsController extends Controller {

    private static final Image AMENITY_SPRITES = new Image(UniversityAmenityGraphic.AMENITY_SPRITE_SHEET_URL);
    private static final Image AGENT_SPRITES_1 = new Image(UniversityAgentGraphic.AGENTS_URL_1);
    private static final Image AGENT_SPRITES_2 = new Image(UniversityAgentGraphic.AGENTS_URL_2);
    private static final Image AGENT_SPRITES_3 = new Image(UniversityAgentGraphic.AGENTS_URL_3);
    private static final Image AGENT_SPRITES_4 = new Image(UniversityAgentGraphic.AGENTS_URL_4);
    public static List<Amenity.AmenityBlock> firstPortalAmenityBlocks;
    public static double tileSize;
    private static boolean isDrawingStraightX;
    private static boolean isDrawingStraightY;
    private static Double lockedX;
    private static Double lockedY;
    private static boolean drawMeasurement;
    private static Patch measurementStartPatch;
    public static boolean willPeek;
    private static long millisecondsLastCanvasRefresh;

    static {
        UniversityGraphicsController.firstPortalAmenityBlocks = null;
        UniversityGraphicsController.isDrawingStraightX = false;
        UniversityGraphicsController.isDrawingStraightY = false;
        UniversityGraphicsController.lockedX = null;
        UniversityGraphicsController.lockedY = null;
        UniversityGraphicsController.drawMeasurement = false;
        UniversityGraphicsController.measurementStartPatch = null;
        UniversityGraphicsController.willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    // Send a request to draw the university view on the canvas
    public static void requestDrawUniversityView(StackPane canvases, University university, double tileSize, boolean background, boolean speedAware) {
        if (speedAware) { // If the speed-aware option is true, only perform canvas refreshes after a set interval has elapsed; This is done to avoid having too many refreshes within a short period of time
            final int millisecondsIntervalBetweenCalls = 2000;
            long currentTimeMilliseconds = System.currentTimeMillis();

            // If enough time has passed between the current time and the time of last canvas refresh, do the canvas refresh
            if (currentTimeMilliseconds - UniversityGraphicsController.millisecondsLastCanvasRefresh < millisecondsIntervalBetweenCalls) {
                return;
            }
            else {
                UniversityGraphicsController.millisecondsLastCanvasRefresh = System.currentTimeMillis(); // If a canvas refresh will be performed, reset the time of last canvas refresh
            }
        }

        Platform.runLater(() -> {
            drawUniversityView(canvases, university, tileSize, background);
        });
    }

    // Draw all that is requested on the university view on the canvases
    private static void drawUniversityView(StackPane canvases, University university, double tileSize, boolean background) {
        // Get the canvases and their graphics contexts
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        // Get the height and width of the canvases
        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        drawUniversityObjects(university, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize);
    }

    private static void drawUniversityObjects(University university, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize) {
        // Draw all the patches of this university
        // If the background is supposed to be drawn, draw from all the patches; If not, draw only from the combined agent and amenity set
        List<Patch> patches;

        if (background) {
            patches = Arrays.stream(university.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<Patch> amenityAgentSet = new TreeSet<>(); // Combine this university's amenity and agent set into a single set

            amenityAgentSet.addAll(new ArrayList<>(university.getAmenityPatchSet()));
            amenityAgentSet.addAll(new ArrayList<>(university.getAgentPatchSet()));

            patches = new ArrayList<>(amenityAgentSet);
        }

        for (Patch patch : patches) {
            if (patch == null) {
                continue;
            }

            int row = patch.getMatrixPosition().getRow();
            int column = patch.getMatrixPosition().getColumn();
            Patch currentPatch = university.getPatch(row, column); // Get the current patch

            boolean drawGraphicTransparently;
            drawGraphicTransparently = false;

            // Draw graphics corresponding to whatever is in the content of the patch; If the patch has no amenity on it, just draw a blank patch
            Amenity.AmenityBlock patchAmenityBlock = currentPatch.getAmenityBlock();
            Pair<PatchField, Integer> patchNumPair = currentPatch.getPatchField();
            Color patchColor;

            if (patchAmenityBlock == null) {
                patchColor = Color.rgb(244, 244, 244);
                backgroundGraphicsContext.setFill(patchColor);
                backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
            }
            else {
                Amenity patchAmenity = currentPatch.getAmenityBlock().getParent();

                if (patchAmenityBlock.hasGraphic()) {
                    Drawable drawablePatchAmenity = (Drawable) patchAmenity;

                    if (patchAmenity instanceof NonObstacle) {
                        if (!((NonObstacle) patchAmenity).isEnabled()) {
                            drawGraphicTransparently = true;
                        }
                    }

                    if (drawGraphicTransparently) {
                        foregroundGraphicsContext.setGlobalAlpha(0.2);
                    }

                    AmenityGraphicLocation amenityGraphicLocation = drawablePatchAmenity.getGraphicLocation();

                    foregroundGraphicsContext.drawImage(
                            AMENITY_SPRITES,
                            amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                            amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                            column * tileSize + drawablePatchAmenity.getGraphicObject().getAmenityGraphicOffset().getColumnOffset() * tileSize,
                            row * tileSize + drawablePatchAmenity.getGraphicObject().getAmenityGraphicOffset().getRowOffset() * tileSize,
                            tileSize * drawablePatchAmenity.getGraphicObject().getAmenityGraphicScale().getColumnSpan(),
                            tileSize * drawablePatchAmenity.getGraphicObject().getAmenityGraphicScale().getRowSpan());

                    if (drawGraphicTransparently) { // Reset transparency if previously added
                        foregroundGraphicsContext.setGlobalAlpha(1.0);
                    }
                }
            }

            if (patchNumPair != null) {
                PatchField patchPatchField = patchNumPair.getKey();

                if (patchPatchField.getClass() == Bathroom.class) {
                    if (patchNumPair.getValue() == 1) {
                        patchColor = Color.rgb(232, 116, 206);
                    }
                    else {
                        patchColor = Color.rgb(118, 237, 244);
                    }
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if(patchPatchField.getClass() == Classroom.class) {
                    patchColor = Color.rgb(243, 222, 206);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if(patchPatchField.getClass() == Laboratory.class) {
                    patchColor = Color.rgb(225, 220, 218);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Wall.class) {
                    patchColor = Color.rgb(104, 101, 101);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
            }

            if (!background) { // Draw each agent in this patch, if the foreground is to be drawn
//                for (Agent agent : patch.getAgents()) {
//                    UniversityAgent universityAgent = (UniversityAgent) agent;
//                    AgentGraphicLocation agentGraphicLocation = universityAgent.getAgentGraphic().getGraphicLocation();
//
//                    Image CURRENT_URL = null;
//                    if (universityAgent.getType() == UniversityAgent.Type.GUARD || universityAgent.getType() == UniversityAgent.Type.JANITOR || universityAgent.getType() == UniversityAgent.Type.OFFICER) {
//                        CURRENT_URL = AGENT_SPRITES_4;
//                    }
//                    else if (universityAgent.getType() == UniversityAgent.Type.PROFESSOR) {
//                        CURRENT_URL = AGENT_SPRITES_3;
//                    }
//                    else if (universityAgent.getType() == UniversityAgent.Type.STUDENT && universityAgent.getGender() == UniversityAgent.Gender.MALE) {
//                        CURRENT_URL = AGENT_SPRITES_1;
//                    }
//                    else if (universityAgent.getType() == UniversityAgent.Type.STUDENT && universityAgent.getGender() == UniversityAgent.Gender.FEMALE) {
//                        CURRENT_URL = AGENT_SPRITES_2;
//                    }
//
//                    foregroundGraphicsContext.drawImage(
//                            CURRENT_URL,
//                            agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
//                            agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
//                            UniversityGraphicsController.getScaledAgentCoordinates(universityAgent).getX() * tileSize - tileSize,
//                            UniversityGraphicsController.getScaledAgentCoordinates(universityAgent).getY() * tileSize - tileSize * 2,
//                            tileSize * 2, tileSize * 2 + tileSize * 0.25);
//                }
            }
        }
    }

    private static Patch retrievePatchFromMouseClick(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // If a straight x-axis draw is requested, and there is no locked x coordinate yet, set the locked x coordinate
        if (UniversityGraphicsController.isDrawingStraightX && UniversityGraphicsController.lockedX == null) {
            UniversityGraphicsController.lockedX = mouseX;
        }

        // If a straight y-axis draw is requested, and there is no locked y coordinate yet, set the locked y coordinate
        if (UniversityGraphicsController.isDrawingStraightY && UniversityGraphicsController.lockedY == null) {
            UniversityGraphicsController.lockedY = mouseY;
        }

        // Take into account whether the mouse x or y coordinates are to be used
        MatrixPosition matrixPosition = Location.screenCoordinatesToMatrixPosition(
                Main.universitySimulator.getUniversity(),
                UniversityGraphicsController.isDrawingStraightX ? UniversityGraphicsController.lockedX : mouseX,
                UniversityGraphicsController.isDrawingStraightY ? UniversityGraphicsController.lockedY : mouseY,
                tileSize);

        if (matrixPosition != null) { // When the position given is a null, this means the mouse has been dragged out of bounds
            Patch patchAtMousePosition = Main.universitySimulator.getUniversity().getPatch(matrixPosition);

            if (UniversityGraphicsController.drawMeasurement) { // If a measurement is requested, compute it; Compute for the start coordinates, if one hasn't been computed yet
                if (UniversityGraphicsController.measurementStartPatch == null) {
                    UniversityGraphicsController.measurementStartPatch = Main.universitySimulator.getUniversity().getPatch(matrixPosition);
                }
            }

            // Retrieve the patch at that location
            return patchAtMousePosition;
        }
        else {
            return null;
        }
    }

//    public static Coordinates getScaledAgentCoordinates(Agent agent) {
//        Coordinates agentPosition = agent.getAgentMovement().getPosition();
//
//        return UniversityGraphicsController.getScaledCoordinates(agentPosition);
//    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}