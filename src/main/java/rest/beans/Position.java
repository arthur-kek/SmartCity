package rest.beans;

import entities.DSPosition;
import enums.ChargeStationEnum;

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
        return new Position(ChargeStationEnum.get(new Random().nextInt(4) + 1).getPosition());
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
