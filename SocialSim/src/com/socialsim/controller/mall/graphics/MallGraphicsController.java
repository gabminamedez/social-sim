package com.socialsim.controller.mall.graphics;

import com.socialsim.controller.generic.Controller;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.mall.graphics.agent.MallAgentGraphic;
import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.mall.MallAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.mall.Mall;
import com.socialsim.model.core.environment.mall.patchfield.*;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Sink;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.StoreAisle;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Toilet;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import java.util.*;
import java.util.stream.Collectors;

public class MallGraphicsController extends Controller {

    private static final Image AMENITY_SPRITES = new Image(MallAmenityGraphic.AMENITY_SPRITE_SHEET_URL);
    private static final Image AMENITY_SPRITES2 = new Image(MallAmenityGraphic.AMENITY_SPRITE_SHEET_URL2);
    private static final Image AGENT_SPRITES = new Image(MallAgentGraphic.AGENTS_URL);
    private static final Image AGENT_SPRITES1 = new Image(MallAgentGraphic.AGENTS_URL1);
    public static List<Amenity.AmenityBlock> firstPortalAmenityBlocks;
    public static double tileSize;
    public static boolean willPeek;
    private static long millisecondsLastCanvasRefresh;

    static {
        MallGraphicsController.firstPortalAmenityBlocks = null;
        MallGraphicsController.willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    public static void requestDrawMallView(StackPane canvases, Mall mall, double tileSize, boolean background, boolean speedAware) {
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
            drawMallView(canvases, mall, tileSize, background);
        });
    }

    private static void drawMallView(StackPane canvases, Mall mall, double tileSize, boolean background) {
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        clearCanvases(mall, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize, canvasWidth, canvasHeight);
        drawMallObjects(mall, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize);
    }

    private static void clearCanvases(Mall mall, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize, double canvasWidth, double canvasHeight) {
        if (!background) {
            foregroundGraphicsContext.clearRect(0, 0, mall.getColumns() * tileSize, mall.getRows() * tileSize);
        }
        else {
            foregroundGraphicsContext.clearRect(0, 0, mall.getColumns() * tileSize, mall.getRows() * tileSize);
            backgroundGraphicsContext.setFill(Color.rgb(244, 244, 244));
            backgroundGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        }
    }

    private static void drawMallObjects(Mall mall, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize) {
        List<Patch> patches;

        if (background) {
            patches = Arrays.stream(mall.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<Patch> amenityAgentSet = new TreeSet<>();
            amenityAgentSet.addAll(new ArrayList<>(mall.getAmenityPatchSet()));
            amenityAgentSet.addAll(new ArrayList<>(mall.getAgentPatchSet()));
            patches = new ArrayList<>(amenityAgentSet);
        }

        for (Patch patch : patches) {
            if (patch == null) {
                continue;
            }

            int row = patch.getMatrixPosition().getRow();
            int column = patch.getMatrixPosition().getColumn();
            Patch currentPatch = mall.getPatch(row, column);

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

                    if (patchAmenity.getClass() == Toilet.class || patchAmenity.getClass() == Sink.class || patchAmenity.getClass() == StoreAisle.class) {
                        foregroundGraphicsContext.drawImage(
                                AMENITY_SPRITES2,
                                amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                                amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                                column * tileSize + ((MallAmenityGraphic) drawablePatchAmenity. getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                row * tileSize + ((MallAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                                tileSize * ((MallAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                                tileSize * ((MallAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());
                    }
                    else {
                        foregroundGraphicsContext.drawImage(
                                AMENITY_SPRITES,
                                amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                                amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                                column * tileSize + ((MallAmenityGraphic) drawablePatchAmenity. getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                row * tileSize + ((MallAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                                tileSize * ((MallAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                                tileSize * ((MallAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());
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
                else if (patchPatchField.getClass() == Dining.class || patchPatchField.getClass() == Restaurant.class) {
                    patchColor = Color.rgb(244, 118, 118);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Showcase.class) {
                    patchColor = Color.rgb(219, 232, 78);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
                else if (patchPatchField.getClass() == Store.class) {
                    patchColor = Color.rgb(224, 156, 156);
                    backgroundGraphicsContext.setFill(patchColor);
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
            }

            if (!background) {
                for (Agent agent : patch.getAgents()) {
                    MallAgent mallAgent = (MallAgent) agent;
                    AgentGraphicLocation agentGraphicLocation = mallAgent.getAgentGraphic().getGraphicLocation();

                    if (((MallAgent) agent).getType() == MallAgent.Type.JANITOR || ((MallAgent) agent).getType() == MallAgent.Type.CONCIERGER) {
                        foregroundGraphicsContext.drawImage(
                                AGENT_SPRITES1,
                                agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
                                agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
                                getScaledAgentCoordinates(mallAgent).getX() * tileSize,
                                getScaledAgentCoordinates(mallAgent).getY() * tileSize,
                                tileSize * 0.7, tileSize * 0.7);
                    }
                    else {
                        foregroundGraphicsContext.drawImage(
                                AGENT_SPRITES,
                                agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
                                agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
                                getScaledAgentCoordinates(mallAgent).getX() * tileSize,
                                getScaledAgentCoordinates(mallAgent).getY() * tileSize,
                                tileSize * 0.7, tileSize * 0.7);
                    }
                }
            }
        }
    }

    public static Coordinates getScaledAgentCoordinates(MallAgent agent) {
        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        return MallGraphicsController.getScaledCoordinates(agentPosition);
    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}