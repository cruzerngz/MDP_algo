package Entities;

public class EmptyGrid extends Entity {

    public EmptyGrid(int x, int y) {
        super.x = x;
        super.y = y;
        super.type = Type.GRID;
        super.symbol = ".";
    }
}
