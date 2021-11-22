package com.socialsim.model.core.environment.patch.position;

import com.socialsim.model.core.environment.university.BaseUniversityObject;
import com.socialsim.model.core.environment.Environment;
import com.socialsim.model.core.environment.university.University;

public class Location extends BaseUniversityObject implements Environment {

    public static MatrixPosition screenCoordinatesToMatrixPosition( // Convert the given continuous screen coordinates to a discrete row and column
            University university,
            double x,
            double y,
            double tileSize
    ) {
        double rawX = x;
        double rawY = y;

        double scaledX = rawX / tileSize;
        double scaledY = rawY / tileSize;

        int truncatedX = (int) Math.floor(scaledX);
        int truncatedY = (int) Math.floor(scaledY);

        MatrixPosition matrixPosition = new MatrixPosition(truncatedY, truncatedX);

        if (MatrixPosition.inBounds(matrixPosition, university)) {
            return matrixPosition;
        }
        else {
            return null;
        }
    }

}