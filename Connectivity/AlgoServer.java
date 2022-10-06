package Connectivity;

import java.net.*;
import java.util.Arrays;

import Algorithm.PathPlanner;
import java.io.*;

/*
 * AlgoServer connects to RPI via the RPI wifi
 */

public class AlgoServer {

    private int SLEEPO = 1000;

    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private String address;
    private int port;

    Object[] pathInstructions;
    int instructionCount = 0;
    boolean robotStarted = false;
    int imageCaptureStep = 1;
    String OBSTACLE_ID = "";

    int ADJUSTMENT_COUNT = 0;
    boolean IS_ADJUSTING = false;
    int BACKWARD_COUNT = 0;
    boolean AFTER_ADJUST = false;

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
                            System.out.println("From RPI: " + recvedMessage);
                            try {
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
                            } catch (Exception e) {
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
        // build arena instruction
        if (message.length() >= 10) {
            // reset exisiting pathInstructions, instruction count
            pathInstructions = new Object[] {};
            instructionCount = 0;
            imageCaptureStep = 1;
            try {
                // see if a path can actually be built
                pathInstructions = PathPlanner.psuedoDubins((message.replace(",", "\n")).substring(4));
            } catch (Exception e1) {
                // if path cannot be built
                // send an error message to android
                synchronized (socket) {
                    System.out.println(Arrays.toString(pathInstructions) + "\n\n\n");
                    String msg1 = "AND: Path creation unsuccessful. Check the values sent...";
                    String internal = "Received wrong format for robot and obstacles from android\n\n";
                    try {
                        outStream.write(msg1.getBytes("UTF-8"));
                        System.out.println(internal);
                    } catch (Exception e2) {
                    }
                }
                return;
            }
            // if an empty path was created, it is probably due to impossible arrangement of obstacles
            if (pathInstructions.length == 0) {
                synchronized (socket) {
                    String msg1 = "AND: Path creation unsuccessful. The arrangement of obstacles do not allow movement";
                    String internal = "Impossible path\n\n";
                    try {
                        outStream.write(msg1.getBytes("UTF-8"));
                        //Thread.sleep(3000);
                        System.out.println(internal);
                    } catch (Exception e2) {
                    }
                }

            }
            // otherwise the path created was successful
            // send ack to android and first instruction to stm
            else {
                synchronized (socket) {
                    System.out.println(Arrays.toString(pathInstructions));
                    String msg1 = "AND: Path creation successful";
                    String toSTM = "STM:" + (String) pathInstructions[instructionCount];
                    instructionCount++; //increment the instruction index
                    String internal = "Received robot and obstacles from android\nSent ack to android\npath plan created\nFirst instruction sent to STM\n\n";
                    try {
                        outStream.write(msg1.getBytes("UTF-8"));
                        System.out.println("Waiting...\n");
                        Thread.sleep(SLEEPO);
                        outStream.write(toSTM.getBytes("UTF-8"));
                        System.out.println(internal);
                        System.out.println(Arrays.deepToString(pathInstructions) + "\n\n");

                    } catch (Exception e2) {
                    }
                }
            }
        }
    }

    private void stmHandler(String message) {
        // after successful robot movement and ack from stm...
        if (message.matches("STM:&.*") & IS_ADJUSTING == false & AFTER_ADJUST == false) {
            // if there are still instructions left, send next instruction
            if (instructionCount < pathInstructions.length) {
                // if next instruction is image capture, ask robot to take picture, and send current coords to android
                if (((String) pathInstructions[instructionCount]).matches("CAP.*")) {

                    synchronized (socket) {
                        String toCamera = "IMG:CAP";
                        String[] splitInstr = ((String) pathInstructions[instructionCount]).split(",");
                        String toAndroid = "AND:ROBOT,"
                                + String.format("%s,%s,%s", splitInstr[2], splitInstr[3], splitInstr[4]);
                        OBSTACLE_ID = splitInstr[1]; // save the current obstacle id
                        imageCaptureStep++;
                        try {

                            outStream.write(toCamera.getBytes("UTF-8"));
                            outStream.write(toAndroid.getBytes("UTF-8"));
                            //'Thread.sleep(1000);
                            //Thread.sleep(3000);
                        } catch (Exception e) {
                        }
                    }

                } else {
                    // if it is not a image capture, then just send nex instruction
                    synchronized (socket) {
                        try {
                            String toSTM = "STM:" + (String) pathInstructions[instructionCount];
                            String internal = String.format("Sent move command %s to robot\n\n",
                                    (String) pathInstructions[instructionCount]);
                            System.out.println(internal);
                            Thread.sleep(SLEEPO);
                            outStream.write(toSTM.getBytes("UTF-8"));
                            //Thread.sleep(5000);

                        } catch (Exception e) {
                        }
                    }
                }
                instructionCount++;
            }
        } else if (message.matches("STM:&.*") & IS_ADJUSTING == true & AFTER_ADJUST == false) {
            synchronized (socket) {
                String toCamera = "IMG:CAP";
                try {
                    outStream.write(toCamera.getBytes("UTF-8"));
                    Thread.sleep(SLEEPO);
                } catch (Exception e) {
                }
            }
        } else if (message.matches("STM:&.*") & AFTER_ADJUST == true) {
            AFTER_ADJUST = false;
            if (instructionCount < pathInstructions.length) {
                String toSTM = "STM:" + (String) pathInstructions[instructionCount];
                instructionCount++;
                String internal = String.format(
                        "Sent image to android\nSend next instructions %s to STM\n\n",
                        (String) pathInstructions[instructionCount]);
                synchronized (socket) {
                    try {
                        System.out.println(internal);
                        Thread.sleep(SLEEPO);
                        outStream.write(toSTM.getBytes("UTF-8"));
                        //Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                }
            } else {
                String toCamera = "IMG:DONE";
                String internal = "No more instructions send done camera";
                synchronized (socket) {
                    try {
                        System.out.println(internal);
                        Thread.sleep(SLEEPO);
                        outStream.write(toCamera.getBytes("UTF-8"));
                        //Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private void imgHandler(String message) {
        // receive image from camera
        if (message.matches("IMG:CAP:.*")) {
            String imageId = "";
            try {
                // if the image recognition did not get anything, [], perform readjustment
                if ((message.matches("IMG:CAP:-1.*") || message.matches("IMG:CAP:-2.*")) & ADJUSTMENT_COUNT < 2) {
                    System.out.println("No image capture. Performing readjustment...\n\n");
                    readjustment();
                    return;
                } else {
                    // otherwise...
                    if (IS_ADJUSTING) {
                        IS_ADJUSTING = false;
                        ADJUSTMENT_COUNT = 0;
                        AFTER_ADJUST = true;

                        imageId = message.substring(8, 10);
                        synchronized (socket) {
                            String toAndroid = String.format("AND:TARGET,%s,%s", OBSTACLE_ID, imageId);
                            String toSTM = "\\fmf" + BACKWARD_COUNT * 10 + ";";
                            try {
                                outStream.write(toSTM.getBytes("UTF-8"));
                                if (message.matches("IMG:CAP:-1.*") || message.matches("IMG:CAP:-2.*")) {
                                    // do nothing
                                } else {
                                    outStream.write(toAndroid.getBytes("UTF-8"));
                                }

                            } catch (Exception e) {
                            }
                        }
                        BACKWARD_COUNT = 0;
                        return;
                    } else {
                        imageId = message.substring(8, 10);
                        synchronized (socket) {
                            String toAndroid = String.format("AND:TARGET,%s,%s", OBSTACLE_ID, imageId);
                            try {

                                outStream.write(toAndroid.getBytes("UTF-8"));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                System.out.println("Printed imageId " + imageId + "\n\n");
            } catch (Exception e) {
            }

            // after which send next instruction to STM
            if (instructionCount < pathInstructions.length) {
                String toSTM = "STM:" + (String) pathInstructions[instructionCount];
                instructionCount++;
                String internal = String.format(
                        "Sent image to android\nSend next instructions %s to STM\n\n",
                        (String) pathInstructions[instructionCount]);
                synchronized (socket) {
                    try {
                        System.out.println(internal);
                        Thread.sleep(SLEEPO);
                        outStream.write(toSTM.getBytes("UTF-8"));
                        //Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                }
            } else {
                String toCamera = "IMG:DONE";
                String internal = "No more instructions send done camera";
                synchronized (socket) {
                    try {
                        System.out.println(internal);
                        Thread.sleep(SLEEPO);
                        outStream.write(toCamera.getBytes("UTF-8"));
                        //Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private void readjustment() {
        IS_ADJUSTING = true;
        ADJUSTMENT_COUNT++;
        BACKWARD_COUNT++;
        synchronized (socket) {
            try {
                String toSTM = "STM:\\fmb10;";
                outStream.write(toSTM.getBytes("UTF-8"));
            } catch (Exception e) {
            }
        }
    }

    public static void algoServer(String address, int port) {
        AlgoServer algoServer = new AlgoServer(address, port);
        algoServer.createSocket();
    }
}
