import java.net.DatagramPacket;

/**
 * Created by andremachado on 16/03/2018.
 */
public class BackupReceiverThread extends Thread {

    private String message;
    private Chunk chunk;
    private int id;
    private ControlModule controlModule;

    public BackupReceiverThread(String message, int id, ControlModule controlModule){
        this.controlModule = controlModule;
        this.message = message;
        this.id = id;
    }

    @Override
    public void run() {
        chunk = Chunk.ParsePutChunkMessage(message);

        if(chunk != null && this.id != chunk.getPeerId()){
            //Sleep for rand time between 0 - 400 ms
            try {
                Thread.sleep((long)(Math.random() * 400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Received " + message);

            //Check control - enhancement - not needed for now
            //TODO

            //Store chunk
            //TODO


            //Send STORED control message
            String msg = chunk.GetStoredMessage(id);
            controlModule.SendControlMessage(msg);
            //TODO call control sender method
        }
    }
}
