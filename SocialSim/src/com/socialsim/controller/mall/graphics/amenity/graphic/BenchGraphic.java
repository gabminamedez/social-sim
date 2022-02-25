package com.socialsim.controller.mall.graphics.amenity.graphic;

import com.socialsim.controller.mall.graphics.amenity.MallAmenityGraphic;
import com.socialsim.model.core.environment.mall.patchobject.passable.goal.Bench;
import java.util.Objects;

public class BenchGraphic extends MallAmenityGraphic {

    private static final int ROW_SPAN_VERTICAL = 2;
    private static final int COLUMN_SPAN_VERTICAL = 1;

    private static final int ROW_SPAN_HORIZONTAL = 1;
    private static final int COLUMN_SPAN_HORIZONTAL = 2;

    private static final int NORMAL_ROW_OFFSET = 0;
    private static final int NORMAL_COLUMN_OFFSET = 0;

    public BenchGraphic(Bench bench, String facing) {
        super(bench,
                Objects.equals(facing, "RIGHT") || Objects.equals(facing, "LEFT") ? ROW_SPAN_VERTICAL : ROW_SPAN_HORIZONTAL,
                Objects.equals(facing, "RIGHT") || Objects.equals(facing, "LEFT") ? COLUMN_SPAN_VERTICAL : COLUMN_SPAN_HORIZONTAL,
                NORMAL_ROW_OFFSET, NORMAL_COLUMN_OFFSET);

        switch (facing) {
            case "UP", "DOWN" -> this.graphicIndex = 0;
            case "RIGHT", "LEFT" -> this.graphicIndex = 1;
        }
    }

}