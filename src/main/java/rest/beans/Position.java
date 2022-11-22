package rest.beans;

import core.entities.DSPosition;
import core.enums.ChargingStation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Random;

@XmlRootElement
public class Position {

    private int x;
    private int y;

    public Position() {

    }

    public Position(DSPosition p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Position getRandomSpawnPosition(){
        return new Position(ChargingStation.get(new Random().nextInt(4) + 1).getPosition());
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
