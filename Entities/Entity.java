package Entities;

import java.util.Map;

import Constants.*;

public class Entity {

    public enum Type {
        OBSTACLE,
        ROBOT,
        GRID
    }

    int x;
    int y;
    Type type;
    Direction direction;
    String symbol;
    boolean visited = false;
    Map<Entity, Integer> adjacency;

    public int[] getCoords() {
        return new int[] { x, y };
    }

    public Type getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean getVisited() {
        return visited;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDirection(Object directionInput) {
        direction = new Direction(directionInput);
    }

    public void setVisited() {
        this.visited = true;
    }

    public void resetVisited() {
        this.visited = false;
    }

    public Map<Entity, Integer> getAdjacency() {
        return adjacency;
    }

    public void setMap(Map<Entity, Integer> adjacency) {
        this.adjacency = adjacency;
    }

}
