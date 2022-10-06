package Simulator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Algorithm.Manhattan;
import Arena.Arena;
import Entities.Entity;
import Entities.Obstacle;
import Entities.Robot;
import Entities.Entity.Type;

public class SetupArena {
    public static Arena setupArena(String configFile) {

        int arenaWidth = 20;
        int arenaHeight = 20;

        String[] configArray = configFile.split("\n");
        // System.out.println(configArray);
        Robot robot = new Robot(3, 3);
        Obstacle[] obstacleArray = new Obstacle[configArray.length - 1];
        int index = 0;
        for (String config : configArray) {
            String[] tempInfo = config.split(" ");
            // System.out.println(Arrays.toString(tempInfo));
            // first item in configArray is for robot
            if (index == 0) {
                robot.setCoords(Integer.parseInt(tempInfo[0].trim()), Integer.parseInt(tempInfo[1].trim()));
                robot.setDirection(Integer.parseInt(tempInfo[2].trim()));
                index++;
                continue;
            }
            // subsequent items in configArray are the obstacles
            // [x, y, direction, pictureId]
            obstacleArray[index - 1] = new Obstacle(Integer.parseInt(tempInfo[0].trim()),
                    Integer.parseInt(tempInfo[1].trim()),
                    Integer.parseInt(tempInfo[2].trim()), Integer.parseInt(tempInfo[3].trim()), false,
                    robot.getPaddingSize());
            index++;
        }

        Arena arena = new Arena(arenaWidth, arenaHeight, robot, obstacleArray);

        createAdjacency(arena);

        // System.out.println(Arrays.toString(arena.invalidCoords));

        return arena;
    }

    private static void createAdjacency(Arena arena) {
        // create an array containing the Robot and all obstacles
        Entity[] entityArray = new Entity[1 + arena.getObstacleArray().length];
        entityArray[0] = arena.getRobot();
        for (int i = 1; i < entityArray.length; i++) {
            entityArray[i] = arena.getObstacleArray()[i - 1];
        }

        // for each entity, create a map that contains the euclidean distance
        // from itself to other entities
        Manhattan man = new Manhattan();
        int x1, y1, x2, y2;
        for (Entity nodeA : entityArray) {
            Map<Entity, Integer> map = new HashMap<Entity, Integer>();

            if (nodeA.getType() == Type.OBSTACLE) {
                x1 = getClosestValidPosition(arena, (Obstacle) nodeA)[0];
                y1 = getClosestValidPosition(arena, (Obstacle) nodeA)[1];
                ((Obstacle) nodeA).setSafeCoord(x1, y1);
            } else {
                x1 = nodeA.getCoords()[0];
                y1 = nodeA.getCoords()[1];
            }

            for (Entity nodeB : entityArray) {

                if (nodeB.getType() == Type.OBSTACLE) {
                    x2 = getClosestValidPosition(arena, (Obstacle) nodeB)[0];
                    y2 = getClosestValidPosition(arena, (Obstacle) nodeB)[1];
                    // ((Obstacle) nodeB).setSafeCoord(x2, y2);
                } else {
                    x2 = nodeB.getCoords()[0];
                    y2 = nodeB.getCoords()[1];
                }

                int distance = man.bfs(arena, x1, y1, x2, y2);
                map.put(nodeB, distance);

            }
            nodeA.setMap(map);
        }

    }

    public static int[] getClosestValidPosition(Arena arena, Obstacle obstacle) {
        int sweep = 3;
        int obstacleDir = obstacle.getDirection().getDegree();
        int initX = obstacle.getSafeCoords()[0];
        int initY = obstacle.getSafeCoords()[1];

        int[] tempCoords = obstacle.getSafeCoords();
        boolean gotClash = true;

        for (int i = 0; i <= sweep; i++) {
            for (int j = 0; j <= sweep; j++) {
                int[][] tester = { { initX + i, initY + j }, { initX - i, initY + j }, { initX + i, initY - j },
                        { initX - i, initY - j } };
                for (int[] test : tester) {
                    if (!arena.entityClash(test)) {
                        return test;
                    }
                }
            }
        }
        return tempCoords;
    }
}
