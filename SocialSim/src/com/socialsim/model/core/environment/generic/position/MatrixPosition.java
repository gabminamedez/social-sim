package com.socialsim.model.core.environment.generic.position;

import com.socialsim.model.core.environment.Environment;
import java.util.Objects;

public class MatrixPosition extends Location {

    private final int row;
    private final int column;

    public MatrixPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static boolean inBounds(MatrixPosition matrixPosition, Environment environment) {
        return matrixPosition.getRow() >= 0 && matrixPosition.getRow() < environment.getRows() && matrixPosition.getColumn() >= 0 && matrixPosition.getColumn() < environment.getColumns();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatrixPosition that = (MatrixPosition) o;

        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + " " + column + ")";
    }

}