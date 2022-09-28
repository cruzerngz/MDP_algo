package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import javax.print.DocPrintJob;

import Arena.Arena;
import micycle.dubinscurves.DubinsPath;
import micycle.dubinscurves.DubinsPathType;

public class DubinsPathDriver {

    public static Object[][] gridPath(double sx, double sy, double syaw, double ex, double ey, double eyaw,
            double turning_radius, DubinsPathType pathType) {
        double sample_step_size = 1;
        double q0[] = { sx, sy, syaw }; // initial configuration
        double q1[] = { ex, ey, eyaw }; // terminating configuration
        DubinsPath path = new DubinsPath(q0, q1, turning_radius, pathType);

        ArrayList<Object[]> output = new ArrayList<Object[]>();

        path.sampleMany(sample_step_size, (double[] q, double t) -> {
            double dubinDir = Math.floor(q[2] * (180 / Math.PI));
            output.add(new Object[] { Math.floor(q[0]), Math.floor(q[1]), dubinDir });
            return 0;
        });

        Object[][] dubinsPath = new Object[output.size()][4];
        dubinsPath = output.toArray(dubinsPath);

        return dubinsPath;
    }

    public static DubinsPath pathObject(double sx, double sy, double syaw, double ex, double ey, double eyaw,
            double turning_radius, DubinsPathType pathType) {
        double q0[] = { sx, sy, syaw }; // initial configuration
        double q1[] = { ex, ey, eyaw }; // terminating configuration
        DubinsPath path = new DubinsPath(q0, q1, turning_radius, pathType);
        return path;
    }

    public static Object[] dubinsPathInst(double sx, double sy, double syaw, double ex, double ey, double eyaw,
            double turning_radius) {
        DubinsPathType[] pathTypes;
        // check if next node is...
        // to the left
        if ((ex <= sx & ey >= sy) || (ex >= sx & ey <= sy)) {
            pathTypes = new DubinsPathType[] { DubinsPathType.LSL, DubinsPathType.LSR, DubinsPathType.LRL };
        } else {
            // to the right
            pathTypes = new DubinsPathType[] { DubinsPathType.RLR, DubinsPathType.RSL, DubinsPathType.RSR };
        }

        // get the most appropriate dubins path
        double[] pathLengths = {
                DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[0]).getLength(),
                DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[1]).getLength(),
                DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[2]).getLength(),
        };
        double minLength = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < pathLengths.length; i++) {
            if (pathLengths[i] < minLength & pathLengths[i] != 0) {
                minLength = pathLengths[i];
                index = i;
            }
        }

        DubinsPath DPObject = DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius,
                pathTypes[index]);

        String[] paths = typeToInstr(pathTypes[index]);
        Object[] returnObject = { paths[0], DPObject.getSegmentLength(0),
                paths[1], DPObject.getSegmentLength(1),
                paths[2], DPObject.getSegmentLength(2)
        };

        returnObject[1] = arcToAngle(DPObject.getSegmentLength(0), turning_radius);
        returnObject[5] = arcToAngle(DPObject.getSegmentLength(2), turning_radius);
        if (paths[1] != "S") {
            returnObject[2] = arcToAngle(DPObject.getSegmentLength(1), turning_radius);
        }

        return returnObject;
    }

    public static Object[][] dubinsPathGrid(double sx, double sy, double syaw, double ex, double ey, double eyaw,
            double turning_radius) {
        DubinsPathType[] pathTypes;
        // check if next node is...
        // to the left
        if ((ex <= sx & ey >= sy) || (ex >= sx & ey <= sy)) {
            pathTypes = new DubinsPathType[] { DubinsPathType.LSL, DubinsPathType.LSR, DubinsPathType.LRL };
        } else {
            // to the right
            pathTypes = new DubinsPathType[] { DubinsPathType.RLR, DubinsPathType.RSL, DubinsPathType.RSR };
        }

        // get the most appropriate dubins path
        double[] pathLengths = {
                DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[0]).getLength(),
                DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[1]).getLength(),
                DubinsPathDriver.pathObject(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[2]).getLength(),
        };
        double minLength = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < pathLengths.length; i++) {
            if (pathLengths[i] < minLength & pathLengths[i] != 0) {
                minLength = pathLengths[i];
                index = i;
            }
        }
        Object[][] tempDP = DubinsPathDriver.gridPath(sx, sy, syaw, ex, ey, eyaw, turning_radius, pathTypes[index]);
        return tempDP;
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

    private static double arcToAngle(double arcLength, double radius) {
        return (arcLength / radius) * 180 / Math.PI;
    }
}
