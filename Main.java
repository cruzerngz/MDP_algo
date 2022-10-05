import java.util.Arrays;

import Algorithm.DubinsPathDriver;
import Algorithm.PathPlanner;
import Connectivity.*;
import Simulator.Simulator3;

public class Main {
  public static void main(String[] args) {

    String configFile = "1 1 90\n5 5 270 10\n5 13 180 20\n12 9 0 30\n15 15 270 14\n15 4 90 15\n1 19 0 12";
    //String configFile = "5 13 180\n12 9 0 3";
    String address = "192.168.12.1";
    int port = 10003;
    int port2 = 51758;
    String address2 = "192.168.43.1";
    AlgoServer.algoServer(address, port);

    //PathPlanner.psuedoDubins2(configFile);
    System.out.println(Arrays.toString(PathPlanner.psuedoDubins(configFile)));
    //System.out.println(PathPlanner.gridPath(configFile, true));

    //Simulator3 guiSim = new Simulator3();
    // guiSim.guiSim();

    /*
     * [\fc1;, \fmf40;, \fc15;, CAP,10,5,2,90, \fc12;, \fmf20;, \fc4;, \fmf100;, \fc4;, CAP,20,2,13,0, \fc15;, \fmf50;, \fc9;, CAP,12,4,18,180, \fc12;, \fmf20;, \fc12;, \fmf20;, \fc4;, \fmf20;, \fc12;, \fmf70;, \fc12;, CAP,14,15,12,90, \fc8;, \fmf20;, \fc4;, CAP,30,15,9,180, \fc12;, \fmf20;, CAP,15,15,7,270]
     */

  }

}