package micycle.dubinscurves;

import java.util.ArrayList;
import java.util.Arrays;

import javax.print.DocPrintJob;

import Algorithm.Manhattan;
import Arena.Arena;

public class DubinsPathDriver {

    public static Object[][] gridPath(double sx, double sy, double syaw, double ex, double ey, double eyaw,
            double turning_radius) {
        double sample_step_size = 1;
        double q0[] = { sx, sy, syaw }; // initial configuration
        double q1[] = { ex, ey, eyaw }; // terminating configuration
        DubinsPath path = new DubinsPath(q0, q1, turning_radius);

        ArrayList<Object[]> output = new ArrayList<Object[]>();

        path.sampleMany(sample_step_size, (double[] q, double t) -> {
            double dubinDir = Math.floor(q[2] * (180 / Math.PI));
            output.add(new Object[] { Math.floor(q[0]), Math.floor(q[1]), dubinDir });
            return 0;
        });

        // System.out.println(Arrays.deepToString(adj));

        Object[][] dubinsPath = new Object[output.size()][4];
        dubinsPath = output.toArray(dubinsPath);

        return dubinsPath;
    }

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

        // System.out.println(Arrays.deepToString(adj));

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

    public static DubinsPath bestDPObject(double sx, double sy, double syaw, double ex, double ey, double eyaw,
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

        return DPObject;
    }

    public static Object[][] bestDPPath(double sx, double sy, double syaw, double ex, double ey, double eyaw,
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
}
