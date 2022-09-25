package Connectivity;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.microedition.io.StreamConnection;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;

import Algorithm.PathPlanner;

public class ProcessConnectionThread implements Runnable {

    private StreamConnection mConnection;

    // Constant that indicate command from devices
    private static final int EXIT_CMD = -1;
    private static final int KEY_RIGHT = 1;
    private static final int KEY_LEFT = 2;

    public ProcessConnectionThread(StreamConnection connection) {
        mConnection = connection;
    }

    @Override
    public void run() {
        try {

            // prepare to receive data
            InputStream inputStream = mConnection.openInputStream();
            OutputStream outputStream = mConnection.openOutputStream();

            System.out.println("waiting for inputs");
            int input;
            String inputString = "";
            boolean readConfigString = false;

            while (true) {

                while ((input = inputStream.read()) != -1) {

                    if ((char) input == '*') {
                        if (readConfigString == false) {
                            readConfigString = true;
                            continue;
                        } else {
                            readConfigString = false;
                            String configString = inputString.replace('/', '\n');
                            PathPlanner.gridPath(configString);
                            String ack = "ok!";
                            byte[] b = ack.getBytes(StandardCharsets.UTF_8);
                            outputStream.write(b);
                            inputString = "";
                        }
                    }

                    if (readConfigString) {
                        inputString = inputString + (char) input;
                        System.out.println(inputString);
                    }
                }

                /*
                 * if (command == EXIT_CMD) {
                 * System.out.println("finish process");
                 * break;
                 * }
                 * 
                 * processCommand(command);
                 */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private void processConfigFile()

    /**
     * Process the command from client
     * 
     * @param command the command code
     */
    private void processCommand(int command) {
        try {
            Robot robot = new Robot();
            switch (command) {
                case 1:
                    robot.keyPress(KeyEvent.VK_H);
                    System.out.println("H");
                    break;
                case KEY_LEFT:
                    robot.keyPress(KeyEvent.VK_LEFT);
                    System.out.println("Left");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
