package Connectivity;

import java.net.*;
import java.io.*;

public class AlgoServer {
    private static Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;

    // constructor with port
    public AlgoServer(int port) {
        // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String line = "";
            String ack = "";

            // just keep the server on and reading clients
            while (true) {
                try {
                    line = in.readUTF();

                    // for sending back to client
                    ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());

                    // process configuration string messages

                    if (line.matches("^ALG:[*].*")) {
                        handleConfigString(line);
                        ack = "ack";
                    } else {
                        ack = "";
                    }

                    oout.writeObject(ack);
                    oout.flush();

                } catch (IOException i) {
                    System.out.println(i);
                }
            }
            // close connection
            // socket.close();
            // in.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void algoServer() {
        AlgoServer server = new AlgoServer(5000);
    }

    /*---FUNCTIONS---*/

    private static void handleConfigString(String line) {
        System.out.println(line);
    }

}
