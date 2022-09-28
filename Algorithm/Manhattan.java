package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import Arena.Arena;
import Constants.Direction;

/*
 * Calculates the manhattan (grid distance) between two points
 * while providing some space allowance between obstacle and robot
 */

public class Manhattan {

    Arena arena;
    int rowNum[] = { -1, 0, 0, 1 };
    int colNum[] = { 0, -1, 1, 0 };
    // predecessor
    int[][][] pred;
    Direction direction2;

    public Object[][] manhattan(Arena arena, int x1, int y1, int x2, int y2) {
        int distance = bfs(arena, x1, y1, x2, y2);
        ArrayList<Object[]> path = new ArrayList<Object[]>();
        Object[] crawl = { x2, y2, " " };
        path.add(crawl);
        while (pred[(int) crawl[0]][(int) crawl[1]][0] != -1 && pred[(int) crawl[0]][(int) crawl[1]][1] != -1) {
            Object[] tempPred = { pred[(int) crawl[0]][(int) crawl[1]][0], pred[(int) crawl[0]][(int) crawl[1]][1],
                    " " };
            path.add(tempPred);
            crawl = tempPred;
        }
        // path.set(path.size() - 1, new Object[] { path.get(path.size() - 1)[0],
        // path.get(path.size() - 1)[1], " " });
        Object[][] returnPath = new Object[path.size()][3];
        for (int i = path.size() - 1; i >= 0; i--) {
            returnPath[path.size() - i - 1] = path.get(i);
        }
        return returnPath;
    }

    public int bfs(Arena arena, int x1, int y1, int x2, int y2) {
        this.arena = arena;
        this.direction2 = direction2;

        // initialise the predecessors of all nodes to (-1, -1)
        pred = new int[arena.getSize()[0]][arena.getSize()[1]][3];
        for (int x = 0; x < arena.getSize()[0]; x++) {
            for (int y = 0; y < arena.getSize()[1]; y++) {
                pred[x][y] = new int[] { -1, -1 };
            }
        }

        if (arena.entityClash(new int[] { x1, y1 }) || arena.entityClash(new int[] { x2, y2 })) {
            // if the starting position is invalid, return -1
            return -1;
        }

        boolean[][] visited = new boolean[arena.getSize()[0]][arena.getSize()[1]];

        // mark the source as visited
        visited[x1][y1] = true;

        // data structure for queue in BFS
        class queueNode {
            int x;
            int y;
            int dist; // distance from source

            public queueNode(int x, int y, int dist) {
                this.x = x;
                this.y = y;
                this.dist = dist;
            }
        }

        // create a queue for BFS
        Queue<queueNode> q = new LinkedList<>();
        // distance of source is 0
        // enqueue source cell
        queueNode source = new queueNode(x1, y1, 0);
        q.add(source);

        // perform BFS starting from source
        while (!q.isEmpty()) {
            queueNode curr = q.peek();
            int currX = curr.x;
            int currY = curr.y;

            // if we have reached the destination
            // we are done
            if (currX == x2 && currY == y2)
                return curr.dist;
            // otherwise dequeue front cell in the queue...
            q.remove();
            // ...and enqueue its adjacent cells
            for (int i = 0; i < 4; i++) {
                int row = currX + rowNum[i];
                int col = currY + colNum[i];
                // if the adjacent cell is valid, has path
                // if also not visited, enqueue
                if (!arena.entityClash(new int[] { row, col }) && !visited[row][col]) {
                    // mark cell as visited and enqueue it
                    visited[row][col] = true;
                    queueNode Adjcell = new queueNode(row, col, curr.dist + 1);
                    // add this front cell as a predecessor...
                    pred[row][col] = new int[] { currX, currY };
                    q.add(Adjcell);
                }
            }

        }

        // return -1 if cell cannot be reached
        return -1;


    }

    /*
     * // check whether a given coordinate is obstacle or not
     * this function is replaced by Arena class built in checker
     * private boolean isValid(int x, int y) {
     * int minX = arena.getRobot().getPaddingSize();
     * int maxX = arena.getSize()[0] - minX - 1;
     * int minY = minX;
     * int maxY = maxX;
     * if (arena.getArena()[x][y].getType() == Type.OBSTACLE || x < minX || x > maxX
     * || y < minY || y > maxY) {
     * return false;
     * }
     * return true;
     * }
     */

}
