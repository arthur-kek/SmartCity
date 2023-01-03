package core.entities;

import grpc.protocols.PositionOuterClass;
import rest.beans.Position;

import java.util.Objects;

public class DSPosition {

    private int x, y;

    public DSPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public DSPosition(Position p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public DSPosition(PositionOuterClass.Position p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "DSPosition{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DSPosition that = (DSPosition) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
