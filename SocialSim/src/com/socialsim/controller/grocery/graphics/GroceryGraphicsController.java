package com.socialsim.controller.grocery.graphics;

import com.socialsim.controller.generic.Controller;
import com.socialsim.controller.generic.graphics.agent.AgentGraphicLocation;
import com.socialsim.controller.generic.graphics.amenity.AmenityGraphicLocation;
import com.socialsim.controller.grocery.graphics.agent.GroceryAgentGraphic;
import com.socialsim.controller.grocery.graphics.amenity.GroceryAmenityGraphic;
import com.socialsim.model.core.agent.Agent;
import com.socialsim.model.core.agent.grocery.GroceryAgent;
import com.socialsim.model.core.environment.generic.Patch;
import com.socialsim.model.core.environment.generic.patchfield.PatchField;
import com.socialsim.model.core.environment.generic.patchfield.Wall;
import com.socialsim.model.core.environment.generic.patchobject.Amenity;
import com.socialsim.model.core.environment.generic.patchobject.Drawable;
import com.socialsim.model.core.environment.generic.patchobject.passable.NonObstacle;
import com.socialsim.model.core.environment.generic.position.Coordinates;
import com.socialsim.model.core.environment.grocery.Grocery;
import com.socialsim.model.core.environment.grocery.patchfield.BathroomField;
import com.socialsim.model.core.environment.grocery.patchobject.passable.goal.Stall;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class GroceryGraphicsController extends Controller {

    private static final Image AMENITY_SPRITES = new Image(GroceryAmenityGraphic.AMENITY_SPRITE_SHEET_URL);
    private static final Image FOOD_AMENITY_SPRITE = new Image(GroceryAmenityGraphic.FOOD_AMENITY_SPRITE_URL);
    private static final Image AGENT_SPRITES_1 = new Image(GroceryAgentGraphic.AGENTS_URL_1);
    private static final Image AGENT_SPRITES_2 = new Image(GroceryAgentGraphic.AGENTS_URL_2);
    private static final Image AGENT_SPRITES_3 = new Image(GroceryAgentGraphic.AGENTS_URL_3);
    public static List<Amenity.AmenityBlock> firstPortalAmenityBlocks;
    public static double tileSize;
    public static boolean willPeek;
    private static long millisecondsLastCanvasRefresh;

    static {
        GroceryGraphicsController.firstPortalAmenityBlocks = null;
        GroceryGraphicsController.willPeek = false;
        millisecondsLastCanvasRefresh = 0;
    }

    public static void requestDrawGroceryView(StackPane canvases, Grocery grocery, double tileSize, boolean background, boolean speedAware) {
        if (speedAware) {
            final int millisecondsIntervalBetweenCalls = 2000;
            long currentTimeMilliseconds = System.currentTimeMillis();

            if (currentTimeMilliseconds - GroceryGraphicsController.millisecondsLastCanvasRefresh < millisecondsIntervalBetweenCalls) {
                return;
            }
            else {
                GroceryGraphicsController.millisecondsLastCanvasRefresh = System.currentTimeMillis();
            }
        }

        Platform.runLater(() -> {
            drawGroceryView(canvases, grocery, tileSize, background);
        });
    }

    private static void drawGroceryView(StackPane canvases, Grocery grocery, double tileSize, boolean background) {
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        clearCanvases(grocery, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize, canvasWidth, canvasHeight);
        drawGroceryObjects(grocery, background, backgroundGraphicsContext, foregroundGraphicsContext, tileSize);
    }

    private static void clearCanvases(Grocery grocery, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize, double canvasWidth, double canvasHeight) {
        if (!background) {
            foregroundGraphicsContext.clearRect(0, 0, grocery.getColumns() * tileSize, grocery.getRows() * tileSize);
        }
        else {
            foregroundGraphicsContext.clearRect(0, 0, grocery.getColumns() * tileSize, grocery.getRows() * tileSize);
            backgroundGraphicsContext.setFill(Color.rgb(244, 244, 244));
            backgroundGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        }
    }

    private static void drawGroceryObjects(Grocery grocery, boolean background, GraphicsContext backgroundGraphicsContext, GraphicsContext foregroundGraphicsContext, double tileSize) {
        List<Patch> patches;

        if (background) {
            patches = Arrays.stream(grocery.getPatches()).flatMap(Arrays::stream).collect(Collectors.toList());
        }
        else {
            SortedSet<Patch> amenityAgentSet = new TreeSet<>();

            amenityAgentSet.addAll(new ArrayList<>(grocery.getAmenityPatchSet()));
            amenityAgentSet.addAll(new ArrayList<>(grocery.getAgentPatchSet()));

            patches = new ArrayList<>(amenityAgentSet);
        }

        for (Patch patch : patches) {
            if (patch == null) {
                continue;
            }

            int row = patch.getMatrixPosition().getRow();
            int column = patch.getMatrixPosition().getColumn();
            Patch currentPatch = grocery.getPatch(row, column);

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

                    if (patchAmenity.getClass() == Stall.class){
                        foregroundGraphicsContext.drawImage(
                                FOOD_AMENITY_SPRITE,
                                amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                                amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                                column * tileSize + ((GroceryAmenityGraphic) drawablePatchAmenity. getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                row * tileSize + ((GroceryAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                                tileSize * ((GroceryAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                                tileSize * ((GroceryAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());
                    }
                    else{
                        foregroundGraphicsContext.drawImage(
                                AMENITY_SPRITES,
                                amenityGraphicLocation.getSourceX(), amenityGraphicLocation.getSourceY(),
                                amenityGraphicLocation.getSourceWidth(), amenityGraphicLocation.getSourceHeight(),
                                column * tileSize + ((GroceryAmenityGraphic) drawablePatchAmenity. getGraphicObject()).getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                row * tileSize + ((GroceryAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicOffset().getRowOffset() * tileSize,
                                tileSize * ((GroceryAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getColumnSpan(),
                                tileSize * ((GroceryAmenityGraphic) drawablePatchAmenity.getGraphicObject()).getAmenityGraphicScale().getRowSpan());
                    }
                    if (drawGraphicTransparently) {
                        foregroundGraphicsContext.setGlobalAlpha(1.0);
                    }
                }
            }

            if (patchNumPair != null) {
                PatchField patchPatchField = patchNumPair.getKey();

                if (patchPatchField.getClass() == BathroomField.class) {
                    if (patchNumPair.getValue() == 1) {
                        patchColor = Color.rgb(232, 116, 206);
                    }
                    else {
                        patchColor = Color.rgb(118, 237, 244);
                    }
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
                        GroceryAgent groceryAgent = (GroceryAgent) agent;
                        AgentGraphicLocation agentGraphicLocation = groceryAgent.getAgentGraphic().getGraphicLocation();

                        Image CURRENT_URL = null;
                        if (groceryAgent.getPersona() == GroceryAgent.Persona.STTP_ALONE_CUSTOMER || groceryAgent.getPersona() == GroceryAgent.Persona.MODERATE_ALONE_CUSTOMER) {
                            CURRENT_URL = AGENT_SPRITES_1;
                        }
                        else if (groceryAgent.getPersona() == GroceryAgent.Persona.COMPLETE_FAMILY_CUSTOMER || groceryAgent.getPersona() == GroceryAgent.Persona.HELP_FAMILY_CUSTOMER || groceryAgent.getPersona() == GroceryAgent.Persona.DUO_FAMILY_CUSTOMER) {
                            CURRENT_URL = AGENT_SPRITES_2;
                        }
                        else {
                            CURRENT_URL = AGENT_SPRITES_3;
                        }

                        foregroundGraphicsContext.drawImage(
                                CURRENT_URL,
                                agentGraphicLocation.getSourceX(), agentGraphicLocation.getSourceY(),
                                agentGraphicLocation.getSourceWidth(), agentGraphicLocation.getSourceHeight(),
                                getScaledAgentCoordinates(groceryAgent).getX() * tileSize,
                                getScaledAgentCoordinates(groceryAgent).getY() * tileSize,
                                tileSize * 0.7, tileSize * 0.7);
                    }
                }
            }
        }
    }

    public static Coordinates getScaledAgentCoordinates(GroceryAgent agent) {
        Coordinates agentPosition = agent.getAgentMovement().getPosition();

        return GroceryGraphicsController.getScaledCoordinates(agentPosition);
    }

    public static Coordinates getScaledCoordinates(Coordinates coordinates) {
        return new Coordinates(coordinates.getX() / Patch.PATCH_SIZE_IN_SQUARE_METERS, coordinates.getY() / Patch.PATCH_SIZE_IN_SQUARE_METERS);
    }

}