package Constants;

public class Direction {
    public enum Compass {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NONE
    }

    double radians;
    int degree;
    Compass compass;
    String symbol;

    public Direction(Object directionInput) {
        setDirection(directionInput);
    }

    public void setDirection(Object directionInput) {
        switch (directionInput.getClass().getName()) {
            case "java.lang.Double":
                radians = (double) directionInput;
                break;
            case "java.lang.Integer":
                degree = (int) directionInput;
                radians = degree == 0 ? 0 : (degree == 90 ? Math.PI / 2 : (degree == 180 ? Math.PI : 3 * Math.PI / 2));
                break;
            case "Constants.Direction$Compass":
                compass = (Compass) directionInput;
                radians = compass == Compass.EAST ? 0
                        : (compass == Compass.NORTH ? Math.PI / 2
                                : (compass == Compass.WEST ? Math.PI : 3 * Math.PI / 2));
                break;
            case "java.lang.String":
                symbol = (String) directionInput;
                radians = symbol == ">" ? 0
                        : (symbol == "∧" ? Math.PI / 2 : (symbol == "<" ? Math.PI : 3 * Math.PI / 2));
                break;
            default:
                radians = (double) directionInput;
        }

        if (radians == 0) {
            symbol = ">";
            degree = 0;
            compass = Compass.EAST;
        } else if (radians == Math.PI / 2) {
            symbol = "∧";
            degree = 90;
            compass = Compass.NORTH;
        } else if (radians == Math.PI) {
            symbol = "<";
            degree = 180;
            compass = Compass.WEST;
        } else {
            symbol = "V";
            degree = 270;
            compass = Compass.SOUTH;
        }
    }

    public double getRadians() {
        return radians;
    }

    public int getDegree() {
        return degree;
    }

    public Compass getCompass() {
        return compass;
    }

    public String getDirectionSymbol() {
        return symbol;
    }

}
