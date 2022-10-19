package Algorithm;

import Constants.Direction;

public class TurningAlgo {
    public static int turningAlgo(Object prevDir, Object pX, Object pY, Object cX, Object cY) {
        Direction direction = new Direction(prevDir);
        int turning = 0;
        int preX = (int) pX;
        int preY = (int) pY;
        int curX = (int) cX;
        int curY = (int) cY;

        switch (direction.getDegree()) {
            case 90:
                if (curX > preX) {
                    turning = -90;
                } else if (curX < preX) {
                    turning = +90;
                } else if (curY > preY) {
                    turning = 0;
                } else if (curY < preY) {
                    turning = 180;
                }
                break;
            case 270:
                if (curX > preX) {
                    turning = -270;
                } else if (curX < preX) {
                    turning = -90;
                } else if (curY > preY) {
                    turning = -180;
                } else if (curY < preY) {
                    turning = 0;
                }
                break;
            case 180:
                if (curX > preX) {
                    turning = -180;
                } else if (curX < preX) {
                    turning = 0;
                } else if (curY > preY) {
                    turning = -90;
                } else if (curY < preY) {
                    turning = 90;
                }
                break;
            case 0:
                if (curX > preX) {
                    turning = 0;
                } else if (curX < preX) {
                    turning = 180;
                } else if (curY > preY) {
                    turning = 90;
                } else if (curY < preY) {
                    turning = 270;
                }
                break;
            default:
        }

        return direction.getDegree() + turning;
    }
}
