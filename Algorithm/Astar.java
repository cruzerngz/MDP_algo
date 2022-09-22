package Algorithm;

import java.util.Arrays;

import Arena.Arena;
import Entities.Entity;
import Entities.Obstacle;

public class Astar {

    int noOfNodes;
    double[][] heuristic;
    Entity[] nodes; // [robot, obstacle1, obstacle2, ...]
    Arena arena;
    Obstacle[] obstacleArray;
    int start = 0;
    int goal;
    int[] distances; // manhattan distance between source node to other nodes

    public int aStar(Arena arena) {

        this.arena = arena;
        obstacleArray = arena.getObstacleArray();
        noOfNodes = 1 + arena.getObstacleArray().length;
        generateNodeArray();
        generateHeuristic();
        generateDistances();
        getGoalNode();

        // contain the priority to visit the nodes
        double[] priorities = new double[noOfNodes];
        // initialise priority with infinity
        Arrays.fill(priorities, Integer.MAX_VALUE);
        // start node has a priority equal to straight line distance to goal
        priorities[start] = heuristic[start][goal];

        // While there are nodes left to visit...
        while (true) {
            // ... find the node with the currently lowest priority...
            double lowestPriority = Integer.MAX_VALUE;
            int lowestPriorityIndex = -1;
            for (int i = 0; i < priorities.length; i++) {
                // ... by going through all nodes that haven't been visited yet
                if (priorities[i] < lowestPriority && !isVisited(i)) {
                    lowestPriority = priorities[i];
                    lowestPriorityIndex = i;
                }
            }

            if (lowestPriorityIndex == -1) {
                // There was no node not yet visited --> Node not found
                return -1;
            } else if (lowestPriorityIndex == goal) {
                // Goal node found
            }
        }

    }

    private void generateNodeArray() {
        nodes = new Entity[noOfNodes];
        nodes[0] = arena.getRobot();
        for (int i = 1; i < noOfNodes; i++) {
            nodes[i] = arena.getObstacleArray()[i - 1];
        }
    }

    private void generateHeuristic() {
        heuristic = new double[noOfNodes][noOfNodes];
        for (int nodeA = 0; nodeA < noOfNodes; nodeA++) {
            for (int nodeB = 0; nodeB < noOfNodes; nodeB++) {
                if (nodeA == nodeB) {
                    heuristic[nodeA][nodeB] = 0;
                    continue;
                } else {
                    int x1 = nodes[nodeA].getCoords()[0];
                    int y1 = nodes[nodeA].getCoords()[1];
                    int x2 = nodes[nodeB].getCoords()[0];
                    int y2 = nodes[nodeB].getCoords()[1];
                    heuristic[nodeA][nodeB] = euclideanDistance(x1, y1, x2, y2);
                }
            }
        }
    }

    private double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private void generateDistances() {
        distances = new int[noOfNodes];
        Manhattan manDist = new Manhattan();

        // get the coordinates of the source entity
        // which is the robots initial position
        Entity srcEntity = nodes[0];
        int x1 = srcEntity.getCoords()[0];
        int y1 = srcEntity.getCoords()[1];

        // calculate the distance from source to all other entities
        for (int dest = 0; dest < noOfNodes; dest++) {
            Entity destEntity = nodes[dest];
            int x2 = destEntity.getCoords()[0];
            int y2 = destEntity.getCoords()[1];
            // distances[dest] = manDist.manhattan(arena, x1, y1, x2, y2);
        }
    }

    private void getGoalNode() {
        int maxAt = 0;
        for (int i = 0; i < noOfNodes; i++) {
            maxAt = distances[i] > distances[i] ? i : maxAt;
        }
        goal = maxAt;
    }

    private boolean isVisited(int index) {
        return nodes[index].getVisited();
    }

    private void setVisited(int index) {
        nodes[index].setVisited();
    }

}
