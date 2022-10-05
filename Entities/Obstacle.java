package Entities;

import java.util.Arrays;

import Constants.*;

public class Obstacle extends Entity {

    int pictureId;
    int allowance = 1;
    public int[][] dangerCoords; // coordinates surrounding the obstacle that the robot should not get close to
    int[] safeCoord; // the "expected" location the robot should go to in order to view the obstacle
                     // (this is not always accurate however)
    int robotPaddingSize;

    public Obstacle(int x, int y, Object directionInput, int pictureId, boolean visited, int robotPaddingSize) {
        super.x = x;
        super.y = y;
        super.direction = new Direction(directionInput);
        super.type = Type.OBSTACLE;
        super.symbol = super.direction.getDirectionSymbol();
        this.pictureId = pictureId;
        super.visited = visited;
        this.robotPaddingSize = robotPaddingSize;
        setDangerCoords();
        setSafeCoord();
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public int getAllowance() {
        return allowance;
    }

    public int getRobotPaddingSize() {
        return robotPaddingSize;
    }

    public int[][] getDangerCoords() {
        return dangerCoords;
    }

    private void setDangerCoords() {

        int regionWidth = (allowance + (robotPaddingSize - allowance)) * 2 + 1;
        int regionHeight = (allowance + (robotPaddingSize - allowance)) * 2 + 1;
        int startX = (int) (x - Math.floor(regionWidth / 2));
        int startY = (int) (y - Math.floor(regionHeight / 2));
        int index = 0;
        dangerCoords = new int[regionWidth * regionHeight][2];
        for (int i = startX; i < startX + regionWidth; i++) {
            for (int j = startY; j < startY + regionHeight; j++) {
                int[] paddingCoord = { i, j };
                dangerCoords[index] = paddingCoord;
                index++;
            }
        }
    }

    private void setSafeCoord() {

        int offset = allowance + robotPaddingSize + 1;

        switch (direction.getCompass()) {
            case NORTH:
                safeCoord = new int[] { x, y + offset };
                break;
            case SOUTH:
                safeCoord = new int[] { x, y - offset };
                break;
            case EAST:
                safeCoord = new int[] { x + offset, y };
                break;
            case WEST:
                safeCoord = new int[] { x - offset, y };
                break;
            default:
        }
        System.out.println(Arrays.toString(safeCoord));
    }

    public void setSafeCoord(int x, int y) {
        safeCoord = new int[] { x, y };
    }

    public int[] getSafeCoords() {
        return safeCoord;
    }

}
