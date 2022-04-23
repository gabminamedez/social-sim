package com.socialsim.controller.university.graphics;

import com.socialsim.controller.generic.Controller;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.controller.university.graphics.agent.UniversityAgentGraphic;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.university.graphics.amenity.UniversityAmenityGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.university.UniversityAgent;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.university.University;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.university.patchfield.*;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
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
    public static boolean willPeek;
    private static long millisecondsLastCanvasRefresh;

    static {
        UniversityGraphicsController.firstPortalAmenityBlocks = null;
        UniversityGraphicsController.willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    public static void requestDrawUniversityView(StackPane canvases, University university, double tileSize, boolean background, boolean speedAware) {
        if (speedAware) {
            final int millisecondsIntervalBetweenCalls = 2000;
            long currentTimeMilliseconds = System.currentTimeMillis();

            if (currentTimeMilliseconds - UniversityGraphicsController.millisecondsLastCanvasRefresh < millisecondsIntervalBetweenCalls) {
                return;
            }
            else {
                UniversityGraphicsController.millisecondsLastCanvasRefresh = System.currentTimeMillis();
            }
        }

        Platform.runLater(() -> {
            drawUniversityView(canvases, university, tileSize, background);
        });
    }

    private static void drawUniversityView(StackPane canvases, University university, double tileSize, boolean background) {
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        clearCanvases(university, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize, canvasWidth, canvasHeight);
        drawUniversityObjects(university, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize);
    }

    private static void clearCanvases(University university, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize, double canvasWidth, double canvasHeight) {
        if (!background) {
            foregroundGraphicsContext.clearRect(0, 0, university.getColumns() * tileSize, university.getRows() * tileSize);
        }
        else {
            foregroundGraphicsContext.clearRect(0, 0, university.getColumns() * tileSize, university.getRows() * tileSize);
            backgroundGraphicsContext.setFill(Color.rgb(244, 244, 244));
            backgroundGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        }
    }

    private static void drawUniversityObjects(University university, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize) {
        List<Patch> patches;

        if (background) {
            patches = Arrays.stream(university.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<Patch> amenityAgentSet = new TreeSet<>();
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
            Patch currentPatch = university.getPatch(row, column);

            boolean drawGraphicTransparently;
            drawGraphicTransparently = false;

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
                            column * tileSize + ((UniversityAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                            row * tileSize + ((UniversityAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                            tileSize * ((UniversityAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                            tileSize * ((UniversityAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());

                    if (drawGraphicTransparently) {
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
                else if(patchPatchField.getClass() == Cafeteria.class) {
                    patchColor = Color.rgb(229, 126, 126);
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

                else if(patchPatchField.getClass() == StudyArea.class) {
                    patchColor = Color.rgb(153, 222, 142);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == StaffOffice.class){
                    patchColor = Color.rgb(234, 133, 101);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Wall.class) {
                    patchColor = Color.rgb(104, 101, 101);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
            }

            if (!background) {
                if (!patch.getAgents().isEmpty()) {
                    for (Agent agent : patch.getAgents()) {
                        UniversityAgent universityAgent = (UniversityAgent) agent;
                        AgentGraphicLocation agentGraphicLocation = universityAgent.getAgentGraphic().getGraphicLocation();

                        Image CURRENT_URL = null;
                        if (universityAgent.getType() == UniversityAgent.Type.GUARD || universityAgent.getType() == UniversityAgent.Type.JANITOR || universityAgent.getType() == UniversityAgent.Type.STAFF) {
                            CURRENT_URL = AGENT_SPRITES_4;
                        }
                        else if (universityAgent.getType() == UniversityAgent.Type.PROFESSOR) {
                            CURRENT_URL = AGENT_SPRITES_3;
                        }
                        else if (universityAgent.getType() == UniversityAgent.Type.STUDENT && universityAgent.getGender() == UniversityAgent.Gender.MALE) {
                            CURRENT_URL = AGENT_SPRITES_1;
                        }
                        else if (universityAgent.getType() == UniversityAgent.Type.STUDENT && universityAgent.getGender() == UniversityAgent.Gender.FEMALE) {
                            CURRENT_URL = AGENT_SPRITES_2;
                        }

                        foregroundGraphicsContext.drawImage(
                                CURRENT_URL,
                                agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
                                agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
                                getScaledAgentCoordinates(universityAgent).getX() * tileSize,
                                getScaledAgentCoordinates(universityAgent).getY() * tileSize,
                                tileSize * 0.7, tileSize * 0.7);
                    }
                }
            }
        }
    }

    public static Coordinates getScaledAgentCoordinates(UniversityAgent agent) {
        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        return UniversityGraphicsController.getScaledCoordinates(agentPosition);
    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}