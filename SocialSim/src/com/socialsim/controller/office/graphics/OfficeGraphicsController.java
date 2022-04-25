package com.socialsim.controller.office.graphics;

import com.socialsim.controller.generic.Controller;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.office.graphics.agent.OfficeAgentGraphic;
import com.socialsim.controller.office.graphics.amenity.OfficeAmenityGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.office.OfficeAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.position.Coordinates;
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
import java.util.*;
import java.util.stream.Collectors;

public class OfficeGraphicsController extends Controller {

    private static final Image AMENITY_SPRITES = new Image(OfficeAmenityGraphic.AMENITY_SPRITE_SHEET_URL);
    private static final Image AMENITY_SPRITES2 = new Image(OfficeAmenityGraphic.AMENITY_SPRITE_SHEET_URL2);
    private static final Image AGENT_SPRITES1 = new Image(OfficeAgentGraphic.AGENTS_URL_1);
    private static final Image AGENT_SPRITES2 = new Image(OfficeAgentGraphic.AGENTS_URL_2);
    private static final Image AGENT_SPRITES3 = new Image(OfficeAgentGraphic.AGENTS_URL_3);
    private static final Image AGENT_SPRITES4 = new Image(OfficeAgentGraphic.AGENTS_URL_4);

    public static List<Amenity.AmenityBlock> firstPortalAmenityBlocks;
    public static double tileSize;
    public static boolean willPeek;
    private static long millisecondsLastCanvasRefresh;

    static {
        OfficeGraphicsController.firstPortalAmenityBlocks = null;
        OfficeGraphicsController.willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    public static void requestDrawOfficeView(StackPane canvases, Office office, double tileSize, boolean background, boolean speedAware) {
        if (speedAware) {
            final int millisecondsIntervalBetweenCalls = 2000;
            long currentTimeMilliseconds = System.currentTimeMillis();

            if (currentTimeMilliseconds - millisecondsLastCanvasRefresh < millisecondsIntervalBetweenCalls) {
                return;
            }
            else {
                millisecondsLastCanvasRefresh = System.currentTimeMillis();
            }
        }

        Platform.runLater(() -> {
            drawOfficeView(canvases, office, tileSize, background);
        });
    }

    private static void drawOfficeView(StackPane canvases, Office office, double tileSize, boolean background) {
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        clearCanvases(office, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize, canvasWidth, canvasHeight);
        drawOfficeObjects(office, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize);
    }

    private static void clearCanvases(Office office, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize, double canvasWidth, double canvasHeight) {
        if (!background) {
            foregroundGraphicsContext.clearRect(0, 0, office.getColumns() * tileSize, office.getRows() * tileSize);
        }
        else {
            foregroundGraphicsContext.clearRect(0, 0, office.getColumns() * tileSize, office.getRows() * tileSize);
            backgroundGraphicsContext.setFill(Color.rgb(244, 244, 244));
            backgroundGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        }
    }

    private static void drawOfficeObjects(Office office, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize) {
        List<Patch> patches;

        if (background) {
            patches = Arrays.stream(office.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<Patch> amenityAgentSet = new TreeSet<>();
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
            Patch currentPatch = office.getPatch(row, column);

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

                    if (drawGraphicTransparently) {
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
                    if (patchNumPair.getValue() == 1) {
                        patchColor = Color.rgb(232, 116, 206);
                    }
                    else {
                        patchColor = Color.rgb(118, 237, 244);
                    }
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
                else if (patchPatchField.getClass() == SecretaryRoom.class) {
                    patchColor = Color.rgb(244, 174, 67);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
            }

            if (!background) {
                if (!patch.getAgents().isEmpty()) {
                    for (Agent agent : patch.getAgents()) {
                        OfficeAgent officeAgent = (OfficeAgent) agent;
                        AgentGraphicLocation agentGraphicLocation = officeAgent.getAgentGraphic().getGraphicLocation();

                        Image CURRENT_URL = null;
                        if (officeAgent.getType() == OfficeAgent.Type.GUARD || officeAgent.getType() == OfficeAgent.Type.RECEPTIONIST || officeAgent.getType() == OfficeAgent.Type.JANITOR || officeAgent.getType() == OfficeAgent.Type.VISITOR || officeAgent.getType() == OfficeAgent.Type.SECRETARY || officeAgent.getType() == OfficeAgent.Type.DRIVER) {
                            CURRENT_URL = AGENT_SPRITES1;
                        }
                        else if (officeAgent.getType() == OfficeAgent.Type.BUSINESS || officeAgent.getType() == OfficeAgent.Type.RESEARCHER) {
                            CURRENT_URL = AGENT_SPRITES2;
                        }
                        else if (officeAgent.getType() == OfficeAgent.Type.TECHNICAL || officeAgent.getType() == OfficeAgent.Type.BOSS || officeAgent.getType() == OfficeAgent.Type.MANAGER) {
                            CURRENT_URL = AGENT_SPRITES3;
                        }
                        else if (officeAgent.getType() == OfficeAgent.Type.CLIENT) {
                            CURRENT_URL = AGENT_SPRITES4;
                        }

                        foregroundGraphicsContext.drawImage(
                                CURRENT_URL,
                                agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
                                agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
                                getScaledAgentCoordinates(officeAgent).getX() * tileSize,
                                getScaledAgentCoordinates(officeAgent).getY() * tileSize,
                                tileSize * 0.7, tileSize * 0.7);
                    }
                }
            }
        }
    }

    public static Coordinates getScaledAgentCoordinates(OfficeAgent agent) {
        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        return OfficeGraphicsController.getScaledCoordinates(agentPosition);
    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}