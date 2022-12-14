package core.entities;

import rest.beans.Position;

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
}
