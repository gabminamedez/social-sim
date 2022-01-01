package com.socialsim.controller.office.graphics;

import com.socialsim.controller.Main;
import com.socialsim.controller.generic.Controller;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.generic.position.Location;
import com.socialsim.model.core.environment.generic.position.MatrixPosition;
import com.socialsim.model.core.environment.office.Office;
import com.socialsim.model.core.environment.office.patchfield.*;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Sink;
import com.socialsim.model.core.environment.office.patchobject.passable.goal.Toilet;
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

public class OfficeGraphicsController extends Controller {

    private static final Image AMENITY_SPRITES = new Image(OfficeAmenityGraphic.AMENITY_SPRITE_SHEET_URL);
    private static final Image AMENITY_SPRITES2 = new Image(OfficeAmenityGraphic.AMENITY_SPRITE_SHEET_URL2);
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
        firstPortalAmenityBlocks = null;
        isDrawingStraightX = false;
        isDrawingStraightY = false;
        lockedX = null;
        lockedY = null;
        drawMeasurement = false;
        measurementStartPatch = null;
        willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    // Send a request to draw the office view on the canvas
    public static void requestDrawOfficeView(StackPane canvases, Office office, double tileSize, boolean background, boolean speedAware) {
        if (speedAware) { // If the speed-aware option is true, only perform canvas refreshes after a set interval has elapsed; This is done to avoid having too many refreshes within a short period of time
            final int millisecondsIntervalBetweenCalls = 2000;
            long currentTimeMilliseconds = System.currentTimeMillis();

            // If enough time has passed between the current time and the time of last canvas refresh, do the canvas refresh
            if (currentTimeMilliseconds - millisecondsLastCanvasRefresh < millisecondsIntervalBetweenCalls) {
                return;
            }
            else {
                millisecondsLastCanvasRefresh = System.currentTimeMillis(); // If a canvas refresh will be performed, reset the time of last canvas refresh
            }
        }

        Platform.runLater(() -> {
            drawOfficeView(canvases, office, tileSize, background);
        });
    }

    // Draw all that is requested on the office view on the canvases
    private static void drawOfficeView(StackPane canvases, Office office, double tileSize, boolean background) {
        // Get the canvases and their graphics contexts
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        // Get the height and width of the canvases
        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        drawOfficeObjects(office, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize);
    }

    private static void drawOfficeObjects(Office office, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize) {
        // Draw all the patches of this office
        // If the background is supposed to be drawn, draw from all the patches; If not, draw only from the combined agent and amenity set
        List<Patch> patches;

        if (background) {
            patches = Arrays.stream(office.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<Patch> amenityAgentSet = new TreeSet<>(); // Combine this office's amenity and agent set into a single set

            amenityAgentSet.addAll(new ArrayList<>(office.getAmenityPatchSet()));
            amenityAgentSet.addAll(new ArrayList<>(office.getAgentPatchSet()));

            patches = new ArrayList<>(amenityAgentSet);
        }

        for (Patch patch : patches) {
            if (patch == null) {
                continue;
            }

            int row = patch.getMatrixPosition().getRow();
            int column = patch.getMatrixPosition().getColumn();
            Patch currentPatch = office.getPatch(row, column); // Get the current patch

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

                    if (patchAmenity.getClass() == Toilet.class || patchAmenity.getClass() == Sink.class) {
                        foregroundGraphicsContext.drawImage(
                                AMENITY_SPRITES2,
                                amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                                amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                                column * tileSize + ((OfficeAmenityGraphic) drawablePatchAmenity. getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                row * tileSize + ((OfficeAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                                tileSize * ((OfficeAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                                tileSize * ((OfficeAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());
                    }
                    else {
                        foregroundGraphicsContext.drawImage(
                                AMENITY_SPRITES,
                                amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                                amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                                column * tileSize + ((OfficeAmenityGraphic) drawablePatchAmenity. getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                row * tileSize + ((OfficeAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                                tileSize * ((OfficeAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                                tileSize * ((OfficeAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());
                    }

                    if (drawGraphicTransparently) { // Reset transparency if previously added
                        foregroundGraphicsContext.setGlobalAlpha(1.0);
                    }
                }
            }

            if (patchNumPair != null) {
                PatchField patchPatchField = patchNumPair.getKey();

                if (patchPatchField.getClass() == Wall.class) {
                    patchColor = Color.rgb(104, 101, 101);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Bathroom.class) {
                    patchColor = Color.rgb(244, 174, 67);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Breakroom.class) {
                    patchColor = Color.rgb(241, 169, 225);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == MeetingRoom.class) {
                    patchColor = Color.rgb(120, 174, 238);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == OfficeRoom.class) {
                    patchColor = Color.rgb(234, 133, 101);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Reception.class) {
                    patchColor = Color.rgb(224, 156, 156);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
            }

            if (!background) { // Draw each agent in this patch, if the foreground is to be drawn
//                for (Agent agent : patch.getAgents()) {
//                    OfficeAgent officeAgent = (OfficeAgent) agent;
//                    AgentGraphicLocation agentGraphicLocation = officeAgent.getAgentGraphic().getGraphicLocation();
//
//                    Image CURRENT_URL = null;
//                    if (officeAgent.getType() == OfficeAgent.Type.GUARD || officeAgent.getType() == OfficeAgent.Type.JANITOR || officeAgent.getType() == OfficeAgent.Type.OFFICER) {
//                        CURRENT_URL = AGENT_SPRITES_4;
//                    }
//                    else if (officeAgent.getType() == OfficeAgent.Type.PROFESSOR) {
//                        CURRENT_URL = AGENT_SPRITES_3;
//                    }
//                    else if (officeAgent.getType() == OfficeAgent.Type.STUDENT && officeAgent.getGender() == OfficeAgent.Gender.MALE) {
//                        CURRENT_URL = AGENT_SPRITES_1;
//                    }
//                    else if (officeAgent.getType() == OfficeAgent.Type.STUDENT && officeAgent.getGender() == OfficeAgent.Gender.FEMALE) {
//                        CURRENT_URL = AGENT_SPRITES_2;
//                    }
//
//                    foregroundGraphicsContext.drawImage(
//                            CURRENT_URL,
//                            agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
//                            agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
//                            OfficeGraphicsController.getScaledAgentCoordinates(officeAgent).getX() * tileSize - tileSize,
//                            OfficeGraphicsController.getScaledAgentCoordinates(officeAgent).getY() * tileSize - tileSize * 2,
//                            tileSize * 2, tileSize * 2 + tileSize * 0.25);
//                }
            }
        }
    }

    private static Patch retrievePatchFromMouseClick(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // If a straight x-axis draw is requested, and there is no locked x coordinate yet, set the locked x coordinate
        if (isDrawingStraightX && lockedX == null) {
            lockedX = mouseX;
        }

        // If a straight y-axis draw is requested, and there is no locked y coordinate yet, set the locked y coordinate
        if (isDrawingStraightY && lockedY == null) {
            lockedY = mouseY;
        }

        // Take into account whether the mouse x or y coordinates are to be used
        MatrixPosition matrixPosition = Location.screenCoordinatesToMatrixPosition(
                Main.officeSimulator.getOffice(),
                isDrawingStraightX ? lockedX : mouseX,
                isDrawingStraightY ? lockedY : mouseY,
                tileSize);

        if (matrixPosition != null) { // When the position given is a null, this means the mouse has been dragged out of bounds
            Patch patchAtMousePosition = Main.officeSimulator.getOffice().getPatch(matrixPosition);

            if (drawMeasurement) { // If a measurement is requested, compute it; Compute for the start coordinates, if one hasn't been computed yet
                if (measurementStartPatch == null) {
                    measurementStartPatch = Main.officeSimulator.getOffice().getPatch(matrixPosition);
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
//        return OfficeGraphicsController.getScaledCoordinates(agentPosition);
//    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}