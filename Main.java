import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Algorithm.DubinsPathDriver;
import Algorithm.PathPlanner;
import Connectivity.*;
import Simulator.Simulator3;

public class Main {
  public static void main(String[] args) {

    String configFile = "1 1 90\n5 9 270 10\n5 13 180 20\n12 9 0 30\n15 15 270 14\n15 4 90 15";
    String configFile2 = "1 1 90\n1 18 270 1\n6 12 90 2\n10 7 0 3\n15 16 270 4\n19 9 180 5\n13 2 180 6";
    String configFile3 = "1 1 90\n5 9 270 1\n7 14 180 2\n12 9 0 3\n15 4 180 4\n15 15 270 5";
    String address = "192.168.12.1";
    int port = 10003;
    int port2 = 5000;
    String address2 = "192.168.43.1";
    AlgoServer.algoServer(address, port);

    //PathPlanner.psuedoDubins2(configFile);
    // System.out.println(Arrays.toString(PathPlanner.psuedoDubins(configFile3)));
    //System.out.println(PathPlanner.gridPath(configFile, true));
    //Simulator3 guiSim = new Simulator3();
    //guiSim.guiSim();
  }

}