import java.util.ArrayList;
import java.util.List;

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
        
        if(putChunk != null && this.id != putChunk.getPeerId()){
            //Sleep for rand time between 0 - 400 ms
            try {
                Thread.sleep((long)(Math.random() * 400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Check number of peers that stored the chunk
            List<Integer> peers = controlModule.GetPeersThatStored(putChunk.getFileId(), putChunk.getChunkNumber());
            if(peers != null && peers.size() >= putChunk.getRepDegree() && !peers.contains(id))
                return;

            //Save chunk
            FileManager.WriteChunckToBinFile(putChunk, this.id);

            //Send STORED control message
            byte[] msg = putChunk.GetStoredMessage(id).getBytes();
            controlModule.SendControlMessage(msg);
        }
    }
}
