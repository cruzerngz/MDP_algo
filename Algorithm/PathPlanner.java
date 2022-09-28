package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import Arena.Arena;
import Simulator.SetupArena;
import micycle.dubinscurves.DubinsPath;
import micycle.dubinscurves.DubinsPathDriver;
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

        //
        double turning_radius = 1;

        // for each item in visiting order
        for (int step = 0; step < visitingOrder.length - 1; step++) {
            double sx = ((Number) visitingOrder[step][0]).doubleValue();
            double sy = ((Number) visitingOrder[step][1]).doubleValue();
            double syaw = ((Number) visitingOrder[step][2]).doubleValue() * (Math.PI / 180);
            double ex = ((Number) visitingOrder[step + 1][0]).doubleValue();
            double ey = ((Number) visitingOrder[step + 1][1]).doubleValue();
            double eyaw = ((Number) visitingOrder[step + 1][2]).doubleValue() * (Math.PI / 180);

            DubinsPath DPObject = DubinsPathDriver.bestDPObject(sx, sy, syaw, ex, ey, eyaw, turning_radius);
            Object[][] tempDP = DubinsPathDriver.bestDPPath(sx, sy, syaw, ex, ey, eyaw, turning_radius);

            // check if the dubins path is valid
            boolean gotClash = false;
            for (Object[] dubinStep : tempDP) {
                if (arena.entityClash(
                        new int[] { ((Number) dubinStep[0]).intValue(), ((Number) dubinStep[1]).intValue() })) {
                    gotClash = true;
                    break;
                }
            }
            // if the dubins path is valid...
            if (!gotClash) {
                // System.out.println(String.format("No clash %s to %s", step, step + 1));
                for (Object[] dubinStep : tempDP) {
                    Object[] toAdd = new Object[] { dubinStep[0], dubinStep[1], dubinStep[2], " " };
                    verbosePath.add(toAdd);
                }
                // add the robot readable path
                String s1 = typeToInstr(DPObject.getPathType())[0];
                String s2 = typeToInstr(DPObject.getPathType())[1];
                String s3 = typeToInstr(DPObject.getPathType())[2];
                double l1 = arcToAngle(DPObject.getSegmentLength(0), turning_radius);
                double l2 = s2 == "S" ? DPObject.getSegmentLength(1)
                        : arcToAngle(DPObject.getSegmentLength(1), turning_radius);
                double l3 = arcToAngle(DPObject.getSegmentLength(2), turning_radius);
                robotInstructions.add(String.format("%s-%s", s1, l1));
                robotInstructions.add(String.format("%s-%s", s2, l2));
                robotInstructions.add(String.format("%s-%s", s3, l3));
            } else {

                // --- check if a dubins path is possible by moving the robot backwards ---//
                // move robot backwards to a reasonable distance
                int reversedAmt = 0;
                int[] revPos = moveBackTo(arena, (int) sx, (int) sy, ((Number) visitingOrder[step][2]).intValue(),
                        reversedAmt);
                DubinsPath testDPObject = DubinsPathDriver.bestDPObject(
                        (double) revPos[0], (double) revPos[1], syaw, ex, ey, eyaw, turning_radius);
                Object[][] testDP = DubinsPathDriver.bestDPPath((double) revPos[0], (double) revPos[1], syaw, ex, ey,
                        eyaw, turning_radius);
                // check if there exists dubins path is valid after reversing
                gotClash = false;
                for (Object[] dubinStep : testDP) {
                    if (arena.entityClash(
                            new int[] { ((Number) dubinStep[0]).intValue(), ((Number) dubinStep[1]).intValue() })) {
                        gotClash = true;
                        break;
                    }
                }
                // if there are no clashes when reverse and dubins path
                if (!gotClash) {
                    for (Object[] dubinStep : testDP) {
                        Object[] toAdd = new Object[] { dubinStep[0], dubinStep[1], dubinStep[2], " " };
                        verbosePath.add(toAdd);
                    }
                    String s0 = "Reverse";
                    String s1 = typeToInstr(testDPObject.getPathType())[0];
                    String s2 = typeToInstr(testDPObject.getPathType())[1];
                    String s3 = typeToInstr(testDPObject.getPathType())[2];
                    double l0 = reversedAmt;
                    double l1 = arcToAngle(testDPObject.getSegmentLength(0), turning_radius);
                    double l2 = s2 == "S" ? testDPObject.getSegmentLength(1)
                            : arcToAngle(testDPObject.getSegmentLength(1), turning_radius);
                    double l3 = arcToAngle(testDPObject.getSegmentLength(2), turning_radius);
                    robotInstructions.add(String.format("%s-%s", s0, l0));
                    robotInstructions.add(String.format("%s-%s", s1, l1));
                    robotInstructions.add(String.format("%s-%s", s2, l2));
                    robotInstructions.add(String.format("%s-%s", s3, l3));
                }

                // if still got clash after reversing
                if (gotClash) {

                    // reverse (add to path)
                    String s0 = "Reverse";
                    double l0 = reversedAmt;
                    // robotInstructions.add(String.format("%s-%s", s0, l0));

                    // deploy manhattan path starting from reversed position
                    Manhattan manhattan = new Manhattan();
                    Object[][] manPath = manhattan.manhattan(arena, (int) revPos[0], (int) revPos[1], (int) ex,
                            (int) ey);

                    // for every step in the manhattan path
                    int forward = 0;
                    for (int mStep = 1; mStep < manPath.length - 2; mStep++) {
                        int startX = ((Number) manPath[mStep][0]).intValue();
                        int startY = ((Number) manPath[mStep][1]).intValue();
                        int startDir = ((Number) visitingOrder[step][2]).intValue();
                        int endX = ((Number) manPath[mStep + 2][0]).intValue();
                        int endY = ((Number) manPath[mStep + 2][1]).intValue();

                        String instruction;
                        if (endX > startX & endY > startY) {
                            instruction = startDir == 0 ? "L" : "R";
                        } else if (endX > startX & endY < startY) {
                            instruction = startDir == 0 ? "R" : "L";
                        } else if (endX < startX & endY > startY) {
                            instruction = startDir == 180 ? "R" : "L";
                        } else if (endX < startX & endY < startY) {
                            instruction = startDir == 180 ? "L" : "R";
                        } else {
                            // if the robot just going forward, continue with the loop
                            forward++;
                            continue;
                        }

                        // if there is a turn detected
                        // add the forward instruction to the path
                        robotInstructions.add(String.format("%s-%s", "S", forward));
                        forward = 0; // reset forward
                        robotInstructions.add(String.format("%s-%s", instruction, 90));

                    }

                    for (Object[] mStep : manPath) {
                        mStep[2] = 0;
                        // verbosePath.add(mStep);
                    }

                }

            }

        }

        System.out.println(Arrays.deepToString(visitingOrder));
        System.out.println();
        System.out.println();
        // System.out.println(robotInstructions);
        System.out.println();
        Object[][] output = new Object[verbosePath.size()][4];
        output = verbosePath.toArray(output);
        /*
         * for (int i = 0; i < output.length; i++) {
         * output[i][3] = "SCAN";
         * }
         */
        System.out.println(Arrays.deepToString(output));
        // System.out.println(robotInstructions);
        for (Object instro : robotInstructions) {
            System.out.println(instro);
        }

        return output;
    }

    private static int[] moveBackTo(Arena arena, int x, int y, int degrees, int reversedAmt) {
        int maxReverse = 5;
        int newX = x, newY = y;
        switch (degrees) {
            case 0:
                for (int i = 1; i < maxReverse + 1; i++) {
                    if (!arena.entityClash(new int[] { x - i, y })) {
                        newX--;
                    } else {
                        return new int[] { newX, newY };
                    }
                    reversedAmt = i;
                }
                break;
            case 90:
                for (int i = 1; i < maxReverse + 1; i++) {
                    if (!arena.entityClash(new int[] { x, y - i })) {
                        newY--;
                    } else {
                        return new int[] { newX, newY };
                    }
                    reversedAmt = i;
                }
                break;
            case 180:
                for (int i = 1; i < maxReverse + 1; i++) {
                    if (!arena.entityClash(new int[] { x + i, y })) {
                        newX++;
                    } else {
                        return new int[] { newX, newY };
                    }
                    reversedAmt = i;
                }
                break;
            case 270:
                for (int i = 1; i < maxReverse + 1; i++) {
                    if (!arena.entityClash(new int[] { x, y + i })) {
                        newY++;
                    } else {
                        return new int[] { newX, newY };
                    }
                    reversedAmt = i;
                }
                break;
            default:
        }
        return new int[] { newX, newY };
    }

    private static double arcToAngle(double arcLength, double radius) {
        return (arcLength / radius) * 180 / Math.PI;
    }

    private static String[] typeToInstr(DubinsPathType pathType) {
        switch (pathType) {
            case LRL:
                return new String[] { "L", "R", "L" };
            case RLR:
                return new String[] { "R", "L", "R" };
            case LSL:
                return new String[] { "L", "S", "L" };
            case LSR:
                return new String[] { "L", "S", "R" };
            case RSL:
                return new String[] { "R", "S", "L" };
            case RSR:
                return new String[] { "R", "S", "R" };
            default:
        }
        return new String[] {};
    }
}
