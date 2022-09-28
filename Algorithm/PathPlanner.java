package Algorithm;

import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;

import Arena.Arena;
import Simulator.SetupArena;
import micycle.dubinscurves.DubinsPath;
import micycle.dubinscurves.DubinsPathType;

public class PathPlanner {

    static Object[][] visitingOrder;

    public static Object[][] gridPath(String configFile) {

        // convert configfile into arena
        Arena arena = SetupArena.setupArena(configFile);

        // obtain visiting order
        visitingOrder = Prim.prim(arena);

        // output
        ArrayList<Object[]> finalPath = new ArrayList<Object[]>();
        ArrayList<Object[]> verbosePath = new ArrayList<Object[]>();
        ArrayList<Object> robotInstructions = new ArrayList<Object>();

        double turning_radius = 2;

        // for each item in visiting order
        for (int step = 0; step < visitingOrder.length - 1; step++) {
            double sx = ((Number) visitingOrder[step][0]).doubleValue();
            double sy = ((Number) visitingOrder[step][1]).doubleValue();
            double syaw = ((Number) visitingOrder[step][2]).doubleValue() * (Math.PI / 180);
            double ex = ((Number) visitingOrder[step + 1][0]).doubleValue();
            double ey = ((Number) visitingOrder[step + 1][1]).doubleValue();
            double eyaw = ((Number) visitingOrder[step + 1][2]).doubleValue() * (Math.PI / 180);

            // DubinsPath DPObject = DubinsPathDriver.bestDPObject(sx, sy, syaw, ex, ey,
            // eyaw, turning_radius);
            Object[][] dpGrid = DubinsPathDriver.dubinsPathGrid(sx, sy, syaw, ex, ey, eyaw, turning_radius);
            Object[] dpInst = DubinsPathDriver.dubinsPathInst(sx, sy, syaw, ex, ey, eyaw, turning_radius);

            // check if the dubins path between two nodes is valid
            boolean gotClash = false;
            for (Object[] dubinStep : dpGrid) {
                if (arena.entityClash(
                        new int[] { ((Number) dubinStep[0]).intValue(), ((Number) dubinStep[1]).intValue() })) {
                    gotClash = true;
                    break;
                }
            }

            // if the dubins path is valid...
            if (!gotClash) {
                // System.out.println(String.format("No clash %s to %s", step, step + 1));
                for (Object[] dubinStep : dpGrid) {
                    Object[] toAdd = new Object[] { dubinStep[0], dubinStep[1], dubinStep[2], " " };
                    verbosePath.add(toAdd);
                }

                // add the robot to stm instructions
                for (int i = 0; i < dpInst.length - 1; i += 2) {
                    robotInstructions.add(stmConvert((String) dpInst[i], ((Number) dpInst[i + 1]).intValue()));
                }

                // move on to the next node
                continue;
            }

            // TODO: handle if dubins path cannot make it

        }

        System.out.println();
        Object[][] output = new Object[verbosePath.size()][4];
        output = verbosePath.toArray(output);
        for (Object instro : robotInstructions) {
            System.out.println(instro);
        }

        return output;
    }

    private static String stmConvert(String segment, int amount) {
        String output = "\\f";
        switch (segment) {
            case "L":
                output = output + String.format("tlf%s;", amount);
                break;
            case "R":
                output = output + String.format("trf%s;", amount);
                break;
            case "S":
                output = output + String.format("mf%s;", amount);
                break;
            default:
        }
        return output;
    }

    private static int[] moveBackTo(Arena arena, int x, int y, int degrees, int reversedAmt) {
        int maxReverse = 10;
        int newX = x, newY = y;

        return new int[] { newX, newY };
    }

    private static String turn(double sx, double sy, double syaw, double ex, double ey) {
        switch ((int) syaw) {
            case 0:
                if (ex > sx & ey > sy) {
                    return "L";
                } else if (ex > sx & ey < sy) {
                    return "R";
                }
                break;
            case 90:
                if (ex > sx & ey > sy) {
                    return "R";
                } else if (ex < sx & ey > sy) {
                    return "L";
                }
                break;
            case 180:
                if (ex < sx & ey < sy) {
                    return "L";
                } else if (ex < sx & ey > sy) {
                    return "R";
                }
                break;
            case 270:
                if (ex > sx & ey < sy) {
                    return "L";
                } else if (ex < sx & ey < sy) {
                    return "R";
                }
                break;
            default:
        }
        return "S";
    }
}
