/**
 * Created by andremachado on 28/03/2018.
 */
public class RestoreController {
    RestoreDispatcher dispatcher;
    RestoreInitializer initializer;
    private MulticastRestoreChannel channel;

    private int id;

    public RestoreController(String ip, int port, int id, ControlModule controlModule){
        channel = new MulticastRestoreChannel(ip,port);
        this.id = id;
        initializer = new RestoreInitializer(ip, port, id, controlModule, channel);
        dispatcher = new RestoreDispatcher(ip, port, id, controlModule, channel, initializer);

    }

    public byte[] StartRestoreRequest(String fileName, String version){
        return initializer.StartRestoreRequest(fileName, version);
    }

    public void InitializerNotifier(GetChunkMessage getChunkMessage){
        initializer.Notifier(getChunkMessage);
    }

    public void SendChunkMessage(GetChunkMessage getChunkMessage) {

        Thread thread = new Thread() {
            public void run(){
                try {
                    Thread.sleep((long)(Math.random() * 400));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ChunkMessage cm = new ChunkMessage(getChunkMessage, id);
                byte[] msg = cm.GetChunkMessage();

                if(msg != null) {
                    System.out.println("Sending Chunk Message: " + new String(msg));
                    channel.SendRestoreMessage(msg);
                }
            }
        };
        thread.start();
    }
}
