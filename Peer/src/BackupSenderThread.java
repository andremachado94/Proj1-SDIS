/**
 * Created by andremachado on 19/03/2018.
 */
public class BackupSenderThread implements Runnable {

    private byte[] msg;
    private final ControlModule controlModule;
    private final MulticastBackupChannel channel;

    public BackupSenderThread(byte[] msg, ControlModule controlModule, MulticastBackupChannel channel){
        this.msg=msg;
        this.controlModule = controlModule;
        this.channel = channel;
    }

    public void run() {
        System.out.println("Sending message: " + msg);
        channel.SendBackupRequest(msg);
    }
}
