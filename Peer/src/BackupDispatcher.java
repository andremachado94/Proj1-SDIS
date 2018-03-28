import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by andremachado on 16/03/2018.
 */
public class BackupDispatcher {
    private final MulticastBackupChannel channel;
    private String ip;
    private int port;
    private int id;

    public BackupDispatcher(String ip, int port, int id, ControlModule controlModule, MulticastBackupChannel channel){
        this.channel = channel;
        this.ip = ip;
        this.port = port;
        this.id = id;
        InitializeBackupChannelListener(channel, controlModule);
    }

    private void InitializeBackupChannelListener(MulticastBackupChannel channel, ControlModule controlModule){
        channel.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(byte[] msg) {
                //TODO received backup message. Need to create worker thread to handle it.

                BackupReceiverThread workerThread = new BackupReceiverThread(msg, id, controlModule);
                workerThread.start();
                //TODO Check control for repDeg


                return "ok";
            }
        });
        channel.start();
    }
}
