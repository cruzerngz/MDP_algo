package Connectivity;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import javax.imageio.spi.ImageTranscoderSpi;

import Algorithm.PathPlanner;

import java.io.*;

/*
 * AlgoServer connects to RPI via the RPI wifi
 */

public class AlgoServer {

    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private String address;
    private int port;

    Object[] pathInstructions;
    int instructionCount = 0;
    boolean robotStarted = false;
    int imageCaptureStep = 0;
    String obstacleId = "";

    public AlgoServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void createSocket() {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
            createReadThread();
            createWriteThread();
        } catch (UnknownHostException u) {
            u.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void createReadThread() {
        Thread readThread = new Thread() {
            public void run() {
                while (socket.isConnected()) {

                    try {
                        byte[] readBuffer = new byte[200];
                        int num = inStream.read(readBuffer);

                        if (num > 0) {
                            byte[] arrayBytes = new byte[num];
                            System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                            String recvedMessage = new String(arrayBytes, "UTF-8");
                            switch (recvedMessage.substring(0, 3)) {
                                case "AND":
                                    androidHandler(recvedMessage);
                                    break;
                                case "STM":
                                    stmHandler(recvedMessage);
                                    break;
                                case "IMG":
                                    imgHandler(recvedMessage);
                                    break;
                                default:
                            }

                        } /*
                           * else {
                           * // notify();
                           * }
                           */
                        ;
                        // System.arraycopy();
                    } catch (SocketException se) {
                        System.exit(0);

                    } catch (IOException i) {
                        i.printStackTrace();
                    }

                }
            }
        };
        readThread.setPriority(Thread.MAX_PRIORITY);
        readThread.start();
    }

    public void createWriteThread() {
        Thread writeThread = new Thread() {
            public void run() {
                while (socket.isConnected()) {

                    try {
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                        sleep(100);
                        String typedMessage = inputReader.readLine();
                        if (typedMessage != null && typedMessage.length() > 0) {
                            synchronized (socket) {
                                outStream.write(typedMessage.getBytes("UTF-8"));
                                sleep(100);
                            }
                        }
                        ;
                        // System.arraycopy();

                    } catch (IOException i) {
                        i.printStackTrace();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }

                }
            }
        };
        writeThread.setPriority(Thread.MAX_PRIORITY);
        writeThread.start();
    }

    private void androidHandler(String message) {

        String output = "", ack = "", internal = "";

        System.out.println(message);

        // create path from config string
        if (message.length() >= 10) {
            pathInstructions = new Object[] {};
            try {
                pathInstructions = PathPlanner.gridPath((message.replace(",", "\n")).substring(4));
            } catch (Exception e) {
                ack = "AND: Path creation unsuccessful. Check the values sent...";
                internal = "Received wrong format for robot and obstacles from android\n\n";
            }

            if (pathInstructions.length == 0) {
                ack = "AND: Path creation unsuccessful. The arrangement of obstacles do not allow movement";
                internal = "Impossible path\n\n";
            } else {
                ack = "AND: Path creation successful";
                internal = "Received robot and obstacles from android\nSent ack to android\npath plan created\n\n";
                robotStarted = false;
                instructionCount = 0;
                robotStarted = false;
                imageCaptureStep = 0;
                obstacleId = "";
            }
            synchronized (socket) {
                try {
                    outStream.write(ack.getBytes("UTF-8"));
                    // outStream.write(output.getBytes("UTF-8"));
                } catch (Exception e) {
                }
            }
        }

        if (message.equals("AND:START")) {
            String ack2 = "";
            if (pathInstructions.length == 0) {
                ack = "AND:No path has been loaded";
                internal = "Android called start but no path loaded\n\n";
            } else if (robotStarted) {
                ack = "AND:Robot has already been started";
                internal = "Android called start but robot already moving\n\n";
            } else {
                output = "STM:" + (String) pathInstructions[instructionCount];
                instructionCount++;
                ack2 = "AND:Robot started";
                internal = "Android called start successfully -- sent first instruction to STM\n\n";
                robotStarted = true;
            }
            synchronized (socket) {
                try {
                    outStream.write(ack.getBytes("UTF-8"));
                    outStream.write(ack2.getBytes("UTF-8"));
                    outStream.write(output.getBytes("UTF-8"));
                } catch (Exception e) {
                }
            }
        }

        System.out.println(internal);
    }

    private void stmHandler(String message) {
        String output = "", output2 = "", output3 = "", output4 = "", internal = "";
        System.out.println(message);

        // after successful robot movement, send the next instruction
        if (message.matches("STM:&")) {
            if (instructionCount < pathInstructions.length) {

                // if it is a image capture instruction
                // request capture, send robot coordinates, next instruction for stm
                if (((String) pathInstructions[instructionCount]).matches("CAP.*")) {
                    output = "IMG:CAP";
                    // imageCaptureStep = instructionCount;

                    // send coordinate to android
                    String[] splitInstr = ((String) pathInstructions[instructionCount]).split(",");
                    output3 = "AND:ROBOT," + String.format("%s,%s,%s", splitInstr[2], splitInstr[3], splitInstr[4]);
                    obstacleId = splitInstr[1];

                } else {
                    // send next move instruction
                    output = "STM:" + (String) pathInstructions[instructionCount];
                    internal = String.format("Sent move command %s to robot\n\n", output);
                }
                instructionCount++;
            }

            synchronized (socket) {
                try {
                    if (output3 != "") {
                        outStream.write(output3.getBytes("UTF-8"));
                        Thread.sleep(1000);
                    }
                    if (output2 != "") {
                        outStream.write(output2.getBytes("UTF-8"));
                        Thread.sleep(1000);
                    }
                    if (output4 != "") {
                        outStream.write(output4.getBytes("UTF-8"));
                        Thread.sleep(1000);
                    }
                    outStream.write(output.getBytes("UTF-8"));
                } catch (Exception e) {
                }
            }

        }

        System.out.println(internal);

    }

    private void imgHandler(String message) {
        String output = "", output2 = "", output3 = "", internal = "";
        System.out.println(message);
        if (message.matches("IMG:.*")) {
            // send image information to android
            output = String.format("AND:TARGET,%s,%s", obstacleId, message.substring(4));

            // after which nexxt instruction may be run
            instructionCount++;
            if (instructionCount < pathInstructions.length) {
                output2 = "STM:" + (String) pathInstructions[instructionCount];
                internal = String.format(
                        "Sent image to android\nSend next instructions %s to STM\n\n",
                        output2);
            } else {
                output2 = "IMG:DONE";
                internal = "No more instructions send done camera";
            }
            synchronized (socket) {
                try {
                    if (output2 != "") {
                        outStream.write(output2.getBytes("UTF-8"));
                    }
                    outStream.write(output.getBytes("UTF-8"));
                } catch (Exception e) {
                }
            }
            internal = "Sent image id to android\n\n";
        }

        System.out.println(internal);

    }

    public static void algoServer(String address, int port) {
        AlgoServer algoServer = new AlgoServer(address, port);
        algoServer.createSocket();
    }
}

/*
 * - when my algo side sends a command to stm, will stm acknowledgement be sent
 * directly back to algo? or sent to everyone? to everyone
 * 
 * - just want to confirm, for commands to stm, it end with ';', and if it is
 * successful, the stm acknowledgement is ';' a special movecomplete &
 * 
 * - to confirm for the image recognition, the algo server must explicitly tell
 * the rPI to take a photo? yes
 * 
 * - to let everyone know for the Algorithm, there won't be any update function.
 * For example, if you want to change an obstacle you must send the whole string
 * again
 * 
 * 
 * 
 * AlgoServer recieve the configuration from Android
 * AlgoServer plans the path
 * AlgoServer sends the inst[0] to STM
 * AlgoServer recieve acknowledgment from STM
 * AlgoServer send inst[1]
 * let's say inst[1] is taking a photo, send to Camera
 * AlgoServer recieve acknowledgemtn from Camera
 * // disable PARKING CHALLENGE RESPONSE
 * AlgoServer send inst[n]
 * 
 * When does the challenge stop? When all photos are taken? or...
 */
