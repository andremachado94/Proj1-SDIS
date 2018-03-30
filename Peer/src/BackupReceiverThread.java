/**
 * Created by andremachado on 16/03/2018.
 */
public class BackupReceiverThread extends Thread {

    private byte[] message;
    private PutChunk putChunk;
    private int id;
    private ControlModule controlModule;

    public BackupReceiverThread(byte[] message, int id, ControlModule controlModule){
        this.controlModule = controlModule;
        this.message = message;
        this.id = id;
    }

    @Override
    public void run() {
        putChunk = PutChunk.ParsePutChunkMessage(message);

        System.out.println("DATA SIZE AFTER PARSE IS " + putChunk.getData().length + " bytes");

        if(putChunk != null && this.id != putChunk.getPeerId()){
            //Sleep for rand time between 0 - 400 ms
            try {
                Thread.sleep((long)(Math.random() * 400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }





            FileManager.WriteChunckToBinFile(putChunk, this.id);
            //Check control - enhancement - not needed for now
            //TODO

            //Store chunk
            //TODO


            //Send STORED control message
            byte[] msg = putChunk.GetStoredMessage(id).getBytes();
            controlModule.SendControlMessage(msg);
            //TODO call control sender method
        }
    }
}
