package com.socialsim.controller.graphics;

import com.socialsim.controller.Controller;
import com.socialsim.controller.Main;
import com.socialsim.controller.graphics.agent.AgentGraphic;
import com.socialsim.controller.graphics.agent.AgentGraphicLocation;
import com.socialsim.controller.graphics.amenity.AmenityGraphic;
import com.socialsim.controller.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.environment.patch.patchobject.Amenity;
import com.socialsim.model.core.environment.patch.patchobject.Drawable;
import com.socialsim.model.core.environment.patch.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.patch.position.Coordinates;
import com.socialsim.model.core.environment.patch.position.Location;
import com.socialsim.model.core.environment.patch.position.MatrixPosition;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.university.UniversityPatch;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

public class GraphicsController extends Controller {

    private static final Image AMENITY_SPRITE_SHEET = new Image(AmenityGraphic.AMENITY_SPRITE_SHEET_URL);
    private static final Image AGENT_SPRITE_SHEET = new Image(AgentGraphic.AGENT_SPRITE_SHEET_URL);
    public static List<Amenity.AmenityBlock> firstPortalAmenityBlocks;
    public static double tileSize;
    private static boolean isDrawingStraightX;
    private static boolean isDrawingStraightY;
    private static Double lockedX;
    private static Double lockedY;
    private static boolean drawMeasurement;
    private static UniversityPatch measurementStartPatch;
    public static boolean willPeek;
    private static long millisecondsLastCanvasRefresh;

    static {
        GraphicsController.firstPortalAmenityBlocks = null;
        GraphicsController.isDrawingStraightX = false;
        GraphicsController.isDrawingStraightY = false;
        GraphicsController.lockedX = null;
        GraphicsController.lockedY = null;
        GraphicsController.drawMeasurement = false;
        GraphicsController.measurementStartPatch = null;
        GraphicsController.willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    // Send a request to draw the university view on the canvas
    public static void requestDrawUniversityView(StackPane canvases, University university, double tileSize, boolean background, boolean speedAware) {
        if (speedAware) { // If the speed-aware option is true, only perform canvas refreshes after a set interval has elapsed; This is done to avoid having too many refreshes within a short period of time
            final int millisecondsIntervalBetweenCalls = 2000;
            long currentTimeMilliseconds = System.currentTimeMillis();

            // If enough time has passed between the current time and the time of last canvas refresh, do the canvas refresh
            if (currentTimeMilliseconds - GraphicsController.millisecondsLastCanvasRefresh < millisecondsIntervalBetweenCalls) {
                return;
            }
            else {
                GraphicsController.millisecondsLastCanvasRefresh = System.currentTimeMillis(); // If a canvas refresh will be performed, reset the time of last canvas refresh
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
        List<UniversityPatch> patches;

        if (background) {
            patches = Arrays.stream(university.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<UniversityPatch> amenityAgentSet = new TreeSet<>(); // Combine this university's amenity and agent set into a single set

            amenityAgentSet.addAll(new ArrayList<>(university.getAmenityPatchSet()));
            amenityAgentSet.addAll(new ArrayList<>(university.getAgentPatchSet()));

            patches = new ArrayList<>(amenityAgentSet);
        }

        for (UniversityPatch patch : patches) {
            if (patch == null) {
                continue;
            }

            int row = patch.getMatrixPosition().getRow();
            int column = patch.getMatrixPosition().getColumn();
            UniversityPatch currentPatch = university.getPatch(row, column); // Get the current patch

            boolean drawGraphicTransparently;
            drawGraphicTransparently = false;

            // Draw graphics corresponding to whatever is in the content of the patch; If the patch has no amenity on it, just draw a blank patch
            Amenity.AmenityBlock patchAmenityBlock = currentPatch.getAmenityBlock();
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
                            AMENITY_SPRITE_SHEET,
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

            if (!background) { // Draw each agent in this patch, if the foreground is to be drawn
//                for (Agent agent : patch.getAgents()) {
//                    AgentGraphicLocation agentGraphicLocation = agent.getAgentGraphic().getGraphicLocation();
//
//                    foregroundGraphicsContext.drawImage(
//                            PASSENGER_SPRITE_SHEET,
//                            agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
//                            agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
//                            GraphicsController.getScaledAgentCoordinates(agent).getX() * tileSize - tileSize,
//                            GraphicsController.getScaledAgentCoordinates(agent).getY() * tileSize - tileSize * 2,
//                            tileSize * 2, tileSize * 2 + tileSize * 0.25);
//                }
            }
        }
    }

    private static UniversityPatch retrievePatchFromMouseClick(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // If a straight x-axis draw is requested, and there is no locked x coordinate yet, set the locked x coordinate
        if (GraphicsController.isDrawingStraightX && GraphicsController.lockedX == null) {
            GraphicsController.lockedX = mouseX;
        }

        // If a straight y-axis draw is requested, and there is no locked y coordinate yet, set the locked y coordinate
        if (GraphicsController.isDrawingStraightY && GraphicsController.lockedY == null) {
            GraphicsController.lockedY = mouseY;
        }

        // Take into account whether the mouse x or y coordinates are to be used
        MatrixPosition matrixPosition = Location.screenCoordinatesToMatrixPosition(
                Main.simulator.getUniversity(),
                GraphicsController.isDrawingStraightX ? GraphicsController.lockedX : mouseX,
                GraphicsController.isDrawingStraightY ? GraphicsController.lockedY : mouseY,
                tileSize);

        if (matrixPosition != null) { // When the position given is a null, this means the mouse has been dragged out of bounds
            UniversityPatch patchAtMousePosition = Main.simulator.getUniversity().getPatch(matrixPosition);

            if (GraphicsController.drawMeasurement) { // If a measurement is requested, compute it; Compute for the start coordinates, if one hasn't been computed yet
                if (GraphicsController.measurementStartPatch == null) {
                    GraphicsController.measurementStartPatch = Main.simulator.getUniversity().getPatch(matrixPosition);
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
//        return GraphicsController.getScaledCoordinates(agentPosition);
//    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / UniversityPatch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}