package Connectivity;
//https://stackoverflow.com/questions/50232557/visual-studio-code-java-extension-how-to-add-a-jar-to-classpath

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// https://stackoverflow.com/questions/42297711/noclassdeffounderror-using-bluetooth-in-java
// https://github.com/aurasphere/bluetooth-java-client/blob/master/src/main/resources/bluecove-2.1.1-SNAPSHOT.jar
// run the following vvv
// java -cp .;bluecove-2.1.1-SNAPSHOT.jar -Dbluecove.native.path=\ Main 
// compile --> javac -cp .;bluecove-2.1.1-SNAPSHOT.jar Main.java
// run --> java -cp .;bluecove-2.1.1-SNAPSHOT.jar Main 

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class AlgoConnect {

    private static boolean BT_CONNECTED = false;
    String RX_STRING = "";
    private boolean BT_SCAN_FINISHED = false;
    private static String BT_DEVICE_ADDRESS = "";
    private static String BT_URL = "btspp://" + BT_DEVICE_ADDRESS + ":1;authenticate=false;encrypt=false;master=false";
    private RemoteDevice BT_DEVICE;
    private String[] BT_SPLIT_STRING;

    private static StreamConnection BT_STREAM_CONNECTION;
    private static OutputStream BT_OUT_STREAM;
    private static InputStream BT_IN_STREAM;
    private static boolean BT_OUT_STREAM_OPEN = false;
    private static boolean BT_IN_STREAM_OPEN = false;

    private Thread BT_SCAN_THREAD;
    private static Thread BT_CONNECT_THREAD;
    private Thread BT_RECEIVE_THREAD;

    // 342EB698464F
    public static void connect() {
        BT_URL = "btspp://" + "342EB698464F" + ":1;authenticate=false;encrypt=false;master=false";

        try {
            BT_STREAM_CONNECTION = (StreamConnection) Connector.open(BT_URL);
            BT_OUT_STREAM = BT_STREAM_CONNECTION.openOutputStream();
            BT_IN_STREAM = BT_STREAM_CONNECTION.openInputStream();
            BT_OUT_STREAM_OPEN = false;
            BT_IN_STREAM_OPEN = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BT_CONNECTED = true;
        BT_OUT_STREAM_OPEN = true;
        BT_IN_STREAM_OPEN = true;
        BT_CONNECT_THREAD.start();

    }

    public static void pairDevice() {
        try {
            LocalDevice device = LocalDevice.getLocalDevice();
            RemoteDevice[] remotedevice = device.getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
            for (RemoteDevice d : remotedevice) {
                System.out.println("Device Name: " + d.getFriendlyName(false));
                System.out.println("Bluetooth Address: " + d.getBluetoothAddress() + "\n");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
