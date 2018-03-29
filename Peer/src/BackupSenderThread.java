/**
 * Created by andremachado on 19/03/2018.
 */
public class BackupSenderThread implements Runnable {

    private byte[] msg;
    private final int chunkNumber;
    private final int repDeg;
    private final ControlModule controlModule;
    private final MulticastBackupChannel channel;
    private byte[] fileId;

    public BackupSenderThread(byte[] msg, int chunkNumber, String fileName, int repDeg, MulticastBackupChannel channel, ControlModule controlModule){
        this.msg=msg;
        this.chunkNumber = chunkNumber;
        this.fileId = Util.SHA256(fileName);
        this.repDeg = repDeg;
        this.controlModule = controlModule;
        this.channel = channel;
    }

    public void run() {
        long waitTime = 200;

        while(!controlModule.ReceivedStoredMessages(Util.GetCleanId(new String(fileId)), chunkNumber, repDeg)){
            channel.SendBackupRequest(msg);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waitTime*=2;
        }
    }
}
