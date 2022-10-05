package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Arena.Arena;
import Simulator.SetupArena;

public class PathPlanner {

    // static Object[][] visitingOrder;
    static int globalInitDeg;
    static boolean returnVerbose = false;
    static Object[][] globalVerbosePath;

    public static Object[] gridPath(String configFile, int algoNo) {
        if (algoNo == 2) {
            return psuedoDubins(configFile);
        } else {
            return gridPath(configFile);
        }
    }

    public static Object[][] gridPath(String configFile, boolean verbose) {
        if (verbose) {
            //gridPath(configFile);
            psuedoDubins(configFile);
            return globalVerbosePath;
        }

        return null;
    }

    public static Object[] gridPath(String configFile) {

        // convert configfile into arena
        Arena arena = SetupArena.setupArena(configFile);

        // obtain visiting order
        Object[][] visitingOrder = Prim.prim(arena);

        // output
        ArrayList<Object[]> verbosePath = new ArrayList<Object[]>();
        ArrayList<Object> robotInstructions = new ArrayList<Object>();

        double turning_radius = 3;

        // for each item in visiting order
        for (int step = 0; step < visitingOrder.length - 1; step++) {
            double sx = ((Number) visitingOrder[step][0]).doubleValue();
            double sy = ((Number) visitingOrder[step][1]).doubleValue();
            double syaw = ((Number) visitingOrder[step][2]).doubleValue() * (Math.PI / 180);
            double ex = ((Number) visitingOrder[step + 1][0]).doubleValue();
            double ey = ((Number) visitingOrder[step + 1][1]).doubleValue();
            int eDeg = ((Number) visitingOrder[step + 1][2]).intValue();
            double eyaw = ((Number) visitingOrder[step + 1][2]).doubleValue() * (Math.PI / 180);
            int id = ((Number) visitingOrder[step + 1][3]).intValue();

            boolean gotClash = false;
            Object[][] dpGrid = DubinsPathDriver.dubinsPathGrid(sx, sy, syaw, ex, ey, eyaw, turning_radius);
            Object[] dpInst = DubinsPathDriver.dubinsPathInst(sx, sy, syaw, ex, ey, eyaw, turning_radius);
            for (Object[] dubinStep : dpGrid) {
                if (arena.entityClash(
                        new int[] { ((Number) dubinStep[0]).intValue(), ((Number) dubinStep[1]).intValue() })) {
                    gotClash = true;
                    break;
                }
            }
            /*
            if (gotClash == true) { // if the dubins path is valid...
                for (Object[] dubinStep : dpGrid) {
                    Object[] toAdd = new Object[] { dubinStep[0], dubinStep[1], dubinStep[2], " " };
                    verbosePath.add(toAdd);
                }
            
                // add the robot to stm instructions
                for (int i = 0; i < dpInst.length - 1; i += 2) {
                    robotInstructions.add(stmConvert((String) dpInst[i], ((Number) dpInst[i + 1]).intValue()));
                }
            
                // since the robot move to a node, take a picture
                robotInstructions.add(String.format("CAP,%s,%s,%s,%s", id, (int) ex, (int) ey, eDeg));
                verbosePath.get(verbosePath.size() - 1)[3] = "SCAN";
            
                // move on to the next node
                continue; */
            boolean dubu = false;
            if (dubu == true) {
            } else {
                // generate a manhattan path that is less efficient but guaranteed to be safe
                int localInitDeg = ((Number) visitingOrder[step][2]).intValue();
                Manhattan man2 = new Manhattan();
                Object[][] pathing = man2.manhattan(arena, (int) Math.round(sx), (int) Math.round(sy),
                        (int) Math.round(ex), (int) Math.round(ey));
                // for each step in the path generated
                for (int i = 0; i < pathing.length; i++) {
                    // last step (at target position)
                    if (i == pathing.length - 1) {
                        Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], eDeg, " " };
                        verbosePath.add(toAdd);
                        robotInstructions.add(stmConvert(localInitDeg, eDeg));
                        verbosePath.get(verbosePath.size() - 1)[3] = "SCAN";
                        // since the robot move to a node, take a picture
                        robotInstructions.add(String.format("CAP,%s,%s,%s,%s", id, (int) ex, (int) ey, eDeg));
                    } else if ((int) pathing[i][0] == (int) pathing[i + 1][0]) {
                        // same X axis
                        // move south
                        if ((int) pathing[i][1] > (int) pathing[i + 1][1]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 270, " " };
                            verbosePath.add(toAdd);
                            robotInstructions.add(stmConvert(localInitDeg, 270));
                        }
                        // move north
                        if ((int) pathing[i][1] < (int) pathing[i + 1][1]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 90, " " };
                            verbosePath.add(toAdd);
                            robotInstructions.add(stmConvert(localInitDeg, 90));
                        }
                    } else if ((int) pathing[i][0] != (int) pathing[i + 1][0]) {
                        // different X axis
                        // move east
                        if ((int) pathing[i][0] < (int) pathing[i + 1][0]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 0, " " };
                            verbosePath.add(toAdd);
                            robotInstructions.add(stmConvert(localInitDeg, 0));
                        }
                        // move west
                        if ((int) pathing[i][0] > (int) pathing[i + 1][0]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 180, " " };
                            verbosePath.add(toAdd);
                            robotInstructions.add(stmConvert(localInitDeg, 180));
                        }
                    }
                    localInitDeg = globalInitDeg;
                }
            }

        }
        // 

        Object[][] verboseOutput = new Object[verbosePath.size()][4];
        verboseOutput = verbosePath.toArray(verboseOutput);
        globalVerbosePath = verboseOutput;

        Object[] output2 = new Object[robotInstructions.size()];
        output2 = robotInstructions.toArray(output2);

        return output2;
    }

    public static Object[] psuedoDubins(String configFile) {

        // convert configfile into arena
        Arena arena = SetupArena.setupArena(configFile);

        // obtain visiting order
        Object[][] visitingOrder = Prim.prim(arena);

        // output
        ArrayList<Object[]> verbosePath = new ArrayList<Object[]>();
        ArrayList<Object> robotInstructions = new ArrayList<Object>();

        // for each item in visiting order
        for (int step = 0; step < visitingOrder.length - 1; step++) {
            double sx = ((Number) visitingOrder[step][0]).doubleValue();
            double sy = ((Number) visitingOrder[step][1]).doubleValue();
            double syaw = ((Number) visitingOrder[step][2]).doubleValue() * (Math.PI / 180);
            int sDeg = ((Number) visitingOrder[step][2]).intValue();
            double ex = ((Number) visitingOrder[step + 1][0]).doubleValue();
            double ey = ((Number) visitingOrder[step + 1][1]).doubleValue();
            int eDeg = ((Number) visitingOrder[step + 1][2]).intValue();
            double eyaw = ((Number) visitingOrder[step + 1][2]).doubleValue() * (Math.PI / 180);
            int id = ((Number) visitingOrder[step + 1][3]).intValue();

            ///// psuedo dubins /////
            boolean gotClash = false;
            double opp = ex - sx;
            double adj = ey - sy;
            double startDirection = syaw;
            double hyp = 0;
            Object[][] tempDP = DubinsPathDriver.dubinsPathGrid(sx, sy, startDirection, ex, ey, startDirection, 1);
            if (opp != 0 & adj != 0) {
                hyp = Math.sqrt(Math.pow(opp, 2) + Math.pow(adj, 2));
                double theta = Math.abs(Math.toDegrees(Math.atan2(ex - sx, ey - sy))); // base angle
                startDirection = (ex > sx & ey > sy) ? theta
                        : (ex < sx & ey > sy) ? 180 - theta : (ex < sx & ey < sy) ? 270 - theta : 360 - theta;
                tempDP = DubinsPathDriver.dubinsPathGrid(sx, sy, startDirection, ex, ey, startDirection, 1);
                // clash checking
                for (Object[] dpStep : tempDP) {
                    if (arena.entityClash(
                            new int[] { ((Number) dpStep[0]).intValue(), ((Number) dpStep[1]).intValue() })) {
                        gotClash = true;
                        break;
                    }

                }
            } else {
                gotClash = true;
            }

            if (gotClash == false) {

                verbosePath.add(new Object[] { (int) sx, (int) sy, sDeg, " " });
                verbosePath.add(new Object[] { (int) sx, (int) sy, startDirection, " " });
                for (Object[] dpStep : tempDP) {
                    verbosePath.add(new Object[] { dpStep[0], dpStep[1], dpStep[2], " " });
                }

                robotInstructions.add(stmConvert(sDeg, startDirection, "OTS"));
                robotInstructions.add(stmConvert("S", (int) hyp));
                robotInstructions.add(stmConvert(startDirection, eDeg, "OTS"));
                robotInstructions.add(String.format("CAP,%s,%s,%s,%s", id, (int) ex, (int) ey, eDeg));
            } else {
                // generate a manhattan path that is less efficient but guaranteed to be safe
                int localInitDeg = ((Number) visitingOrder[step][2]).intValue();
                Manhattan man2 = new Manhattan();
                Object[][] pathing = man2.manhattan(arena, (int) Math.round(sx), (int) Math.round(sy),
                        (int) Math.round(ex), (int) Math.round(ey));
                ArrayList<Object> tempRobotInst = new ArrayList<Object>();
                // for each step in the path generated
                for (int i = 0; i < pathing.length; i++) {
                    // last step (at target position)
                    if (i == pathing.length - 1) {
                        Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], eDeg, " " };
                        verbosePath.add(toAdd);
                        tempRobotInst.add(stmConvert(localInitDeg, eDeg));
                        verbosePath.get(verbosePath.size() - 1)[3] = "SCAN";
                        // since the robot move to a node, take a picture
                        tempRobotInst.add(String.format("CAP,%s,%s,%s,%s", id, (int) ex, (int) ey, eDeg));
                    } else if ((int) pathing[i][0] == (int) pathing[i + 1][0]) {
                        // same X axis
                        // move south
                        if ((int) pathing[i][1] > (int) pathing[i + 1][1]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 270, " " };
                            verbosePath.add(toAdd);
                            tempRobotInst.add(stmConvert(localInitDeg, 270));
                        }
                        // move north
                        if ((int) pathing[i][1] < (int) pathing[i + 1][1]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 90, " " };
                            verbosePath.add(toAdd);
                            tempRobotInst.add(stmConvert(localInitDeg, 90));
                        }
                    } else if ((int) pathing[i][0] != (int) pathing[i + 1][0]) {
                        // different X axis
                        // move east
                        if ((int) pathing[i][0] < (int) pathing[i + 1][0]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 0, " " };
                            verbosePath.add(toAdd);
                            tempRobotInst.add(stmConvert(localInitDeg, 0));
                        }
                        // move west
                        if ((int) pathing[i][0] > (int) pathing[i + 1][0]) {
                            Object[] toAdd = new Object[] { pathing[i][0], pathing[i][1], 180, " " };
                            verbosePath.add(toAdd);
                            tempRobotInst.add(stmConvert(localInitDeg, 180));
                        }
                    }
                    localInitDeg = globalInitDeg;
                }

                boolean prevIsStr = false;
                int strCount = 0;
                for (Object inst : tempRobotInst) {
                    if (inst == "\\fmf10;" & prevIsStr == false) {
                        strCount++;
                        prevIsStr = true;
                    } else if (inst == "\\fmf10;" & prevIsStr == true) {
                        strCount++;
                    } else if (inst != "\\fmf10;" & prevIsStr == true) {
                        prevIsStr = false;
                        String toAdd = "\\fmf" + strCount * 10 + ";";
                        strCount = 0;
                        robotInstructions.add(toAdd);
                        robotInstructions.add(inst);
                    } else {
                        robotInstructions.add(inst);
                    }
                }
            }

        }
        // 

        Object[][] verboseOutput = new Object[verbosePath.size()][4];
        verboseOutput = verbosePath.toArray(verboseOutput);
        globalVerbosePath = verboseOutput;

        Object[] output2 = new Object[robotInstructions.size()];
        output2 = robotInstructions.toArray(output2);

        return output2;
    }

    private static String stmConvert(String segment, int amount) {
        String output = "\\f";
        switch (segment) {
            case "L":
                output = output + String.format("tlf%s;", (int) (amount));
                break;
            case "R":
                output = output + String.format("trf%s;", (int) (amount));
                break;
            case "S":
                output = output + String.format("mf%s;", (int) (amount * 10));
                break;
            default:
        }
        return output;
    }

    private static String stmConvert(int initDeg, int moveDir) {
        String output = "";
        if (initDeg == 0) {
            if (moveDir == 0) { // move forward
                output = "\\fmf10;";
                globalInitDeg = 0;
            } else if (moveDir == 90) { // on the spot turn to face north
                output = "\\fc12;";
                globalInitDeg = 90;
            } else if (moveDir == 180) { // reverse -- change to turn around
                //output = "\\fmb10;";
                output = "\\fc8;";
                globalInitDeg = 180;
            } else { // moveDir == 270 on the spot turn to face south
                output = "\\fc4;";
                globalInitDeg = 270;
            }
        } else if (initDeg == 90) {
            if (moveDir == 0) { // ots turn to face east
                output = "\\fc4;";
                globalInitDeg = 0;
            } else if (moveDir == 90) { // move forward
                output = "\\fmf10;";
                globalInitDeg = 90;
            } else if (moveDir == 180) { // ots turn to face west
                output = "\\fc12;";
                globalInitDeg = 180;
            } else { // moveDir == 270 reverse -- change to turn around
                //output = "\\fmb10;";
                output = "\\fc8;";
                globalInitDeg = 270;
            }
        }

        else if (initDeg == 180) {
            if (moveDir == 0) { // reverse -- change to turn around
                //output = "\\fmb10;";
                output = "\\fc8;";
                globalInitDeg = 0;
            } else if (moveDir == 90) { // turn to face north
                output = "\\fc4;";
                globalInitDeg = 90;
            } else if (moveDir == 180) { // forward
                output = "\\fmf10;";
                globalInitDeg = 180;
            } else { // moveDir == 270 ots turn to face south
                output = "\\fc12;";
                globalInitDeg = 270;
            }
        } else { // initDeg == 270
            if (moveDir == 0) { // ots turn to face east
                output = "\\fc12;";
                globalInitDeg = 0;
            } else if (moveDir == 90) { // reverse -- change to turn around
                //output = "\\fmb10;";
                output = "\\fc8;";
                globalInitDeg = 90;
            } else if (moveDir == 180) { // ots turn to face west
                output = "\\fc4;";
                globalInitDeg = 180;
            } else { // moveDir == 270 forward
                output = "\\fmf10;";
                globalInitDeg = 270;
            }
        }
        return output;
    }

    private static String stmConvert(double start, double end, String ots) {
        double change = end > start ? 360 - Math.abs(end - start) : Math.abs(end - start);
        if (change <= 0) {
            return "\\fc0;";
        } else if (change <= 22.5) {
            return "\\fc1;";
        } else if (change <= 45) {
            return "\\fc2;";
        } else if (change <= 67.5) {
            return "\\fc3;";
        } else if (change <= 90) {
            return "\\fc4;";
        } else if (change <= 112.5) {
            return "\\fc5;";
        } else if (change <= 135) {
            return "\\fc6;";
        } else if (change <= 157.5) {
            return "\\fc7;";
        } else if (change <= 180) {
            return "\\fc8;";
        } else if (change <= 202.5) {
            return "\\fc9;";
        } else if (change <= 225.0) {
            return "\\fc10;";
        } else if (change <= 247.5) {
            return "\\fc11;";
        } else if (change <= 270) {
            return "\\fc12;";
        } else if (change <= 292.5) {
            return "\\fc13;";
        } else if (change <= 315) {
            return "\\fc14;";
        } else {
            return "\\fc15;";
        }
    }
}
