package Connectivity;

// https://luugiathuy.com/2011/02/android-java-bluetooth/

// https://github.com/aviyehuda/BluetoothMultisender/blob/master/BlueToothListener/src/BTMessageSender.java

public class RemoteBluetoothServer {
    public static void remoteBluetoothServer() {
        Thread waitThread = new Thread(new WaitThread());
        waitThread.start();
    }
}
