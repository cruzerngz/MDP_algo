import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Algorithm.DubinsPathDriver;
import Algorithm.PathPlanner;
import Connectivity.*;
import Simulator.Simulator3;

public class Server {
    public static void main(String[] args) {

        String address = "192.168.12.1";
        int port = 10003;
        AlgoServer.algoServer(address, port);
    }

}