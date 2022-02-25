package com.socialsim.model.core.environment.generic.position;

import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.generic.BaseObject;

public class Location extends BaseObject {

    public static MatrixPosition screenCoordinatesToMatrixPosition (Environment environment, double x, double y, double tileSize) {
        double rawX = x;
        double rawY = y;

        double scaledX = rawX / tileSize;
        double scaledY = rawY / tileSize;

        int truncatedX = (int) Math.floor(scaledX);
        int truncatedY = (int) Math.floor(scaledY);

        MatrixPosition matrixPosition = new MatrixPosition(truncatedY, truncatedX);

        if (MatrixPosition.inBounds(matrixPosition, environment)) {
            return matrixPosition;
        }
        else {
            return null;
        }
    }

}