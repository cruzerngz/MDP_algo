package Algorithm;

import Arena.*;
import Entities.Entity;
import Entities.Obstacle;
import Entities.Entity.Type;
import Simulator.SetupArena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Prim {

    public static Object[][] prim(Arena arena) {

        // intialise an adjacency object for robot and obstacles
        // AlgorithmHelper.createAdjacency(arena);

        // initialise an array list to store the order to visit the nodes
        ArrayList<Entity> visitingOrder = new ArrayList<Entity>();

        // this array will store the grid based path to visit
        // ArrayList<Object[]> path = new ArrayList<Object[]>();

        // create an array containing the Robot and all obstacles
        Entity[] entityArray = new Entity[1 + arena.getObstacleArray().length];
        entityArray[0] = arena.getRobot();
        for (int i = 1; i < entityArray.length; i++) {
            entityArray[i] = arena.getObstacleArray()[i - 1];
        }

        // get the number of vertices to traverse through
        int noVertices = entityArray.length;

        // System.out.println(noVertices);

        // the starting node is the initial position of the robot
        // so add the robot's coordinates to the path
        visitingOrder.add(arena.getRobot());
        // and also set the robot node as visited
        arena.getRobot().setVisited();

        // now we can start the algorithm
        for (int i = 1; i < noVertices; i++) {
            // get the adjacency of the current node
            Entity curNode = visitingOrder.get(visitingOrder.size() - 1);
            Map<Entity, Integer> curAdjacency = curNode.getAdjacency();

            // iterate through the adjacency
            // to find the closest node that has not been visited
            int distance = Integer.MAX_VALUE;
            Entity nextNode = null;
            for (Entity tempNode : curAdjacency.keySet()) {
                if (!tempNode.getVisited()) {
                    if (curAdjacency.get(tempNode) <= distance && curAdjacency.get(tempNode) != -1) {
                        distance = curAdjacency.get(tempNode);
                        nextNode = tempNode;
                    }
                }
            }

            // System.out.println(visitingOrder);

            // if the nextNode is null, means all nodes have been visitied
            // exit the prim algorithm
            if (nextNode == null) {
                break;
            }

            // otherwise, add the next node to path
            visitingOrder.add(nextNode);
            // and set it as visited
            nextNode.setVisited();

        }

        Object[][] returnVisitingOrder = new Object[visitingOrder.size()][3];
        int index = 0;
        for (Entity entity : visitingOrder) {
            int id = -1;
            if (entity.getType() == Type.ROBOT) {
                returnVisitingOrder[index] = new Object[] { entity.getCoords()[0], entity.getCoords()[1],
                        entity.getDirection().getDegree(), id };
            } else {
                int obstacleDir = entity.getDirection().getDegree();
                int robotToFace = obstacleDir == 0 ? 180 : obstacleDir == 90 ? 270 : obstacleDir == 180 ? 0 : 90;

                int safeX = ((Obstacle) entity).getSafeCoords()[0];
                int safeY = ((Obstacle) entity).getSafeCoords()[1];

                id = ((Obstacle) entity).getPictureId();

                // int safeX = SetupArena.getClosestValidPosition(arena, (Obstacle) entity)[0];
                // int safeY = SetupArena.getClosestValidPosition(arena, (Obstacle) entity)[1];

                /*
                 * if (safeX == 0) {
                 * safeX++;
                 * } else if (safeX == 19) {
                 * safeX--;
                 * }
                 * if (safeY == 0) {
                 * safeY++;
                 * } else if (safeY == 19) {
                 * safeY--;
                 * }
                 */

                returnVisitingOrder[index] = new Object[] { safeX, safeY, robotToFace, id };
            }
            index++;
        }

        return returnVisitingOrder;
    }

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */

    public static Object[][] getVisitingOrder(Arena arena) {

        // intialise an adjacency object for robot and obstacles
        // AlgorithmHelper.createAdjacency(arena);

        // initialise an array list to store the order to visit the nodes
        ArrayList<Entity> visitingOrder = new ArrayList<Entity>();

        // this array will store the grid based path to visit
        // ArrayList<Object[]> path = new ArrayList<Object[]>();

        // create an array containing the Robot and all obstacles
        Entity[] entityArray = new Entity[1 + arena.getObstacleArray().length];
        entityArray[0] = arena.getRobot();
        for (int i = 1; i < entityArray.length; i++) {
            entityArray[i] = arena.getObstacleArray()[i - 1];
        }

        // get the number of vertices to traverse through
        int noVertices = entityArray.length;

        // System.out.println(noVertices);

        // the starting node is the initial position of the robot
        // so add the robot's coordinates to the path
        visitingOrder.add(arena.getRobot());
        // and also set the robot node as visited
        arena.getRobot().setVisited();

        // now we can start the algorithm
        for (int i = 1; i < noVertices; i++) {
            // get the adjacency of the current node
            Entity curNode = visitingOrder.get(visitingOrder.size() - 1);
            Map<Entity, Integer> curAdjacency = curNode.getAdjacency();

            // iterate through the adjacency
            // to find the closest node that has not been visited
            int distance = Integer.MAX_VALUE;
            Entity nextNode = null;
            for (Entity tempNode : curAdjacency.keySet()) {
                if (!tempNode.getVisited()) {
                    if (curAdjacency.get(tempNode) <= distance && curAdjacency.get(tempNode) != -1) {
                        distance = curAdjacency.get(tempNode);
                        nextNode = tempNode;
                    }
                }
            }

            // System.out.println(visitingOrder);

            // if the nextNode is null, means all nodes have been visitied
            // exit the prim algorithm
            if (nextNode == null) {
                break;
            }

            // otherwise, add the next node to path
            visitingOrder.add(nextNode);
            // and set it as visited
            nextNode.setVisited();

        }

        Object[][] returnVisitingOrder = new Object[visitingOrder.size()][3];
        int index = 0;
        for (Entity step : visitingOrder) {
            if (step.getType() == Type.ROBOT) {
                returnVisitingOrder[index][2] = step.getDirection().getRadians();
            } else {
                returnVisitingOrder[index][2] = step.getDirection().getDegree() == 0 ? Math.PI
                        : step.getDirection().getDegree() == 90 ? 3 * Math.PI / 2
                                : step.getDirection().getDegree() == 180 ? 0
                                        : Math.PI / 2;
            }
            returnVisitingOrder[index][0] = step.getCoords()[0];
            returnVisitingOrder[index][1] = step.getCoords()[1];
            index++;
        }
        return returnVisitingOrder;

    }

}
