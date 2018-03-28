import java.net.DatagramPacket;

/**
 * Created by andremachado on 16/03/2018.
 */
public class BackupReceiverThread extends Thread {

    private byte[] message;
    private Chunk chunk;
    private int id;
    private ControlModule controlModule;

    public BackupReceiverThread(byte[] message, int id, ControlModule controlModule){
        this.controlModule = controlModule;
        this.message = message;
        this.id = id;
    }

    @Override
    public void run() {
        chunk = Chunk.ParsePutChunkMessage(message);

        System.out.println("DATA SIZE AFTER PARSE IS " + chunk.getData().length + " bytes");

        if(chunk != null && this.id != chunk.getPeerId()){
            //Sleep for rand time between 0 - 400 ms
            try {
                Thread.sleep((long)(Math.random() * 400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }





            FileManager.WriteChunckToBinFile(chunk, this.id);
            //Check control - enhancement - not needed for now
            //TODO

            //Store chunk
            //TODO


            //Send STORED control message
            byte[] msg = chunk.GetStoredMessage(id).getBytes();
            controlModule.SendControlMessage(msg);
            //TODO call control sender method
        }
    }
}
