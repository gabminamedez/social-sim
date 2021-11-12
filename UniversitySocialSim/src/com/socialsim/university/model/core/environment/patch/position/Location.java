package com.socialsim.university.model.core.environment.patch.position;

import com.socialsim.university.model.core.environment.BaseUniversityObject;
import com.socialsim.university.model.core.environment.Environment;
import com.socialsim.university.model.core.environment.University;

public class Location extends BaseUniversityObject implements Environment {

    public static MatrixPosition screenCoordinatesToMatrixPosition(
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
        } else {
            return null;
        }
    }

}