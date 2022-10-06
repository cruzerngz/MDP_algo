package Arena;

import java.util.ArrayList;
import Entities.*;

public class Arena {

    int width;
    int height;
    int[][] border;
    int[] previousRobotCoord;
    public int[][] invalidCoords;

    Entity[][] arena;
    Obstacle[] obstacleArray;
    Robot robot;

    boolean robotCanBeSet;

    public Arena(int width, int height, Robot robot, Obstacle[] obstacleArray) {
        this.width = width;
        this.height = height;
        this.robot = robot;
        previousRobotCoord = robot.getCoords();

        arena = new Entity[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                arena[i][j] = new EmptyGrid(i, j);
            }
        }

        setObstacles(obstacleArray);
        setBorder(robot);

        /*
         * invalidCoords = new int[obstacleArray.length + border.length][2];
         * int index = 0;
         * for (Obstacle obstacle : obstacleArray) {
         * invalidCoords[index] = obstacle.getCoords();
         * index++;
         * }
         * for (int[] borderCoord : border) {
         * invalidCoords[index] = borderCoord;
         * index++;
         * }
         */

        setRobot(robot.getCoords()[0], robot.getCoords()[1], robot.getDirection().getDegree());
        // System.out.println(Arrays.deepToString(invalidCoords));

    }

    public Entity[][] getArena() {
        return arena;
    }

    public int[] getSize() {
        return new int[] { width, height };
    }

    public Robot getRobot() {
        return robot;
    }

    public Obstacle[] getObstacleArray() {
        return obstacleArray;
    }

    public void setObstacles(Obstacle[] obstacleArray) {
        this.obstacleArray = obstacleArray;
        if (obstacleArray.length != 0) {
            for (Obstacle obstacle : obstacleArray) {
                arena[obstacle.getCoords()[0]][obstacle.getCoords()[1]] = obstacle;
            }
        }
    }

    public boolean setRobot(int x, int y, Object direction) {
        clearRobot();
        robotCanBeSet = true;
        if (entityClash(new int[] { x, y })) {
            System.out.println("Robot cannot be set");
            robotCanBeSet = false;
        }
        if (robotCanBeSet) {
            previousRobotCoord = new int[] { x, y };
            robot.setCoords(x, y);
            robot.setDirection(direction);
            arena[robot.getCoords()[0]][robot.getCoords()[1]] = robot;
        }
        return robotCanBeSet;
    }

    public void printArena() {
        for (int yCoord = height - 1; yCoord >= 0; yCoord--) {
            for (int xCoord = 0; xCoord < width; xCoord++) {
                Entity current = arena[xCoord][yCoord];
                switch (current.getType()) {
                    case ROBOT:
                        System.out.print(" " + robot.getDirection().getDirectionSymbol() + " ");
                        break;
                    case OBSTACLE:
                        System.out.print(" " + current.getSymbol() + " ");
                        break;
                    case GRID:
                        if (isPadding(current.getCoords())) {
                            System.out.print(" " + robot.getSymbol() + " ");
                        } else {
                            System.out.print(" " + current.getSymbol() + " ");
                        }
                        break;
                    default:
                }
            }
            System.out.println("");
        }
    }

    public int[][] getBorder() {
        return border;
    }

    public boolean entityClash(int[] coords) {

        // check if the robot fall outside of border
        for (int[] cell : border) {
            if (coords[0] == cell[0] && coords[1] == cell[1]) {
                return true;
            }
        }
        // check if the coordinates clash within the allowance area of obstacles
        for (Obstacle obstacle : obstacleArray) {
            for (int[] dangerCoord : obstacle.getDangerCoords()) {
                if (coords[0] == dangerCoord[0] && coords[1] == dangerCoord[1]) {
                    return true;
                }
            }
        }

        // check if the provided coords are outside the arena
        if (coords[0] < 0 || coords[0] >= width || coords[1] < 0 || coords[1] >= height) {
            return true;
        }

        return false;
    }

    private void clearRobot() {
        if (robot != null) {
            arena[previousRobotCoord[0]][previousRobotCoord[1]] = new EmptyGrid(
                    previousRobotCoord[0],
                    previousRobotCoord[1]);
        }
    }

    private void setBorder(Robot robot) {
        ArrayList<int[]> tempBorder = new ArrayList<int[]>();
        /*
         * int minX = 0 + robot.getPaddingSize();
         * int minY = 0 + robot.getPaddingSize();
         * int maxX = width - robot.getPaddingSize() - 1;
         * int maxY = height - robot.getPaddingSize() - 1;
         * 
         * // top, bottom borders
         * for (int x = minX; x <= maxX; x++) {
         * tempBorder.add(new int[] { x - 1, minY - 1 });
         * tempBorder.add(new int[] { x - 1, maxY - 1 });
         * }
         * 
         * // left, right borders
         * for (int y = minY; y <= maxY; y++) {
         * tempBorder.add(new int[] { minX - 1, y - 1 });
         * tempBorder.add(new int[] { maxX - 1, y - 1 });
         * }
         */

        for (int layer = 0; layer < robot.getPaddingSize(); layer++) {
            for (int x = layer; x < width - layer; x++) {
                tempBorder.add(new int[] { x, layer });
                tempBorder.add(new int[] { x, height - layer - 1 });
            }
            for (int y = layer; y < height - layer; y++) {
                tempBorder.add(new int[] { layer, y });
                tempBorder.add(new int[] { width - layer - 1, y });
            }
        }

        border = new int[tempBorder.size()][2];
        border = tempBorder.toArray(border);
    }

    private boolean isPadding(int[] coords) {
        int[][] padding = robot.getPadding();
        if (coords[0] == robot.getCoords()[0] && coords[1] == robot.getCoords()[1]) {
            return false;
        }
        for (int[] padCoord : padding) {
            if (coords[0] == padCoord[0] && coords[1] == padCoord[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean robotCanBeSet() {
        return robotCanBeSet;
    }

}
