import Connectivity.*;
import Simulator.Simulator3;

public class Main {
  public static void main(String[] args) {

    //Simulator3 guiSim = new Simulator3();
    //guiSim.guiSim();

    String configFile = "1 1 90\n5 7 270 1\n5 13 180 2\n12 9 0 3\n15 15 270 4\n15 4 90 5";
    String address = "192.168.12.1";
    int port = 10003;
    int port2 = 51758;
    String address2 = "192.168.43.1";
    AlgoServer.algoServer(address2, port2);

  }

}