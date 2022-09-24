package Algorithm;

import java.time.format.DateTimeFormatterBuilder;
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

            // check if the dubins path between two nodes is valid
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
                int maxReverse = 10;
                boolean altPathFound = false;
                // reverse -> check any clash -> no clash -> test dubins -> check clash
                for (int reverse = 1; reverse < maxReverse + 1; reverse++) {
                    double testX = sx, testY = sy;
                    switch (((Number) visitingOrder[step][2]).intValue()) {
                        case 0:
                            testX = testX - reverse;
                            break;
                        case 90:
                            testY = testY - reverse;
                            break;
                        case 180:
                            testX = testX + reverse;
                            break;
                        case 270:
                            testY = testY + reverse;
                            break;
                        default:
                    }
                    // there is no entity clash during reversing
                    if (!arena.entityClash(new int[] { (int) testX, (int) testY })) {
                        DubinsPath testDPObject = DubinsPathDriver.bestDPObject(testX, testY, syaw, ex, ey, eyaw,
                                turning_radius);
                        Object[][] testDP = DubinsPathDriver.bestDPPath(testX, testY, syaw, ex, ey, eyaw,
                                turning_radius);

                        // check if there exists dubins path is valid after reversing
                        gotClash = false;
                        for (Object[] dubinStep : testDP) {
                            if (arena.entityClash(
                                    new int[] { ((Number) dubinStep[0]).intValue(),
                                            ((Number) dubinStep[1]).intValue() })) {
                                gotClash = true;
                                break;
                            }
                        }
                        // if there is a clash...
                        if (gotClash) {
                            // attempt manhattan distance
                            // robot to follow manhattan distance
                            Manhattan manhattan = new Manhattan();
                            Object[][] mPath = manhattan.manhattan(arena, (int) testX, (int) testY, (int) ex, (int) ey);
                            double curYaw = syaw;
                            for (int mStep = 0; mStep < mPath.length - 1; mStep++) {
                                double subSX = ((Number) mPath[mStep][0]).doubleValue();
                                double subSY = ((Number) mPath[mStep][1]).doubleValue();
                                double subEX = ((Number) mPath[mStep + 1][0]).doubleValue();
                                double subEY = ((Number) mPath[mStep + 1][1]).doubleValue();
                                String action = turn(subSX, subSY, curYaw, subEX, subEY);
                                double value = action == "S" ? 1 : 90;
                                robotInstructions.add(String.format("%s-%s", action, value));
                            }
                            altPathFound = true;

                        } else {
                            // if there is no clash of the dubins path
                            for (Object[] dubinStep : testDP) {
                                Object[] toAdd = new Object[] { dubinStep[0], dubinStep[1], dubinStep[2], " " };
                                verbosePath.add(toAdd);
                            }
                            altPathFound = true;
                            System.out.println("Found");
                            // adding the reverse action
                            String rev = "Reverse";
                            double revAmt = reverse;
                            robotInstructions.add(String.format("%s-%s", rev, revAmt));
                            // adding the first segment
                            String s1 = typeToInstr(testDPObject.getPathType())[0];
                            double l1 = arcToAngle(testDPObject.getSegmentLength(0), turning_radius);
                            robotInstructions.add(String.format("%s-%s", s1, l1));
                            // 2nd segment
                            String s2 = typeToInstr(testDPObject.getPathType())[1];
                            double l2 = s2 == "S" ? testDPObject.getSegmentLength(1)
                                    : arcToAngle(testDPObject.getSegmentLength(1), turning_radius);
                            robotInstructions.add(String.format("%s-%s", s2, l2));
                            // 3rd segment
                            String s3 = typeToInstr(testDPObject.getPathType())[2];
                            double l3 = arcToAngle(testDPObject.getSegmentLength(2), turning_radius);
                            robotInstructions.add(String.format("%s-%s", s3, l3));
                        }

                    }
                    // here we go again with the conditional nesting
                    // if an altpath has been found, great! we can continue the traversring to the
                    // next node
                    if (altPathFound) {
                        break;
                    }

                }

            }
        }

        // System.out.println(Arrays.deepToString(visitingOrder));
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
        // System.out.println(Arrays.deepToString(output));
        // System.out.println(robotInstructions);
        for (Object instro : robotInstructions) {
            System.out.println(instro);
        }

        return output;
    }

    private static int[] moveBackTo(Arena arena, int x, int y, int degrees, int reversedAmt) {
        int maxReverse = 10;
        int newX = x, newY = y;

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
