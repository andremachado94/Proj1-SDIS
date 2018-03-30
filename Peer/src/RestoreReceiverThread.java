/**
 * Created by andremachado on 29/03/2018.
 */
public class RestoreReceiverThread extends Thread{
    private byte[] message;
    private ChunkMessage chunk;
    private int id;
    private ControlModule controlModule;
    private RestoreInitializer restoreInitializer;

    public RestoreReceiverThread(byte[] message, int id, ControlModule controlModule, RestoreInitializer restoreInitializer){
        this.controlModule = controlModule;
        this.message = message;
        this.id = id;
        this.restoreInitializer = restoreInitializer;
    }

    @Override
    public void run() {
        chunk = ChunkMessage.ParseChunkMessage(message);

        if(chunk != null && this.id != chunk.getRequesterId()){
            if(!restoreInitializer.ChunkExists(Util.GetCleanId(chunk.getFileId())+"_"+chunk.getChunkNumber())){
                restoreInitializer.AddChunk(Util.GetCleanId(chunk.getFileId())+"_"+chunk.getChunkNumber() , chunk.getData());

                if(chunk.getData().length < 63000){
                    restoreInitializer.FinishRequest(Util.GetCleanId(chunk.getFileId()), chunk.getChunkNumber());
                }
            }
        }
    }
}
