import java.util.ArrayList;
import java.util.Arrays;

import Algorithm.PathPlanner;
import Arena.Arena;
import Connectivity.*;
import Simulator.SetupArena;
import Simulator.Simulator3;
import micycle.dubinscurves.DubinsPath;
import micycle.dubinscurves.DubinsPathDriver;

public class Main {
  public static void main(String[] args) {

    // Simulator3 guiSim = new Simulator3();
    // guiSim.guiSim();

    // String configFile = "1 1 90\n5 7 270 17\n5 13 180 35\n12 9 0 31\n15 15 270
    // 16\n15 4 90 34\n0 19 270 22";
    // String configFile = "1 1 90\n5 7 270 17\n5 13 180 35\n12 9 0 31\n15 15 270
    // 16\n15 4 90 34\n0 19 270 22";
    // PathPlanner.gridPath(configFile);

    // AlgoConnect.pairDevice();
    // MyDiscoveryListener.testConnect();
    RemoteBluetoothServer.remoteBluetoothServer();

  }

}
