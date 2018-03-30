/**
 * Created by andremachado on 28/03/2018.
 */
public class RestoreDispatcher {

    private String ip;
    private int port;
    private int id;
    private ControlModule controlModule;
    private MulticastRestoreChannel channel;
    private RestoreInitializer restoreInitializer;


    public RestoreDispatcher(String ip, int port, int id, ControlModule controlModule, MulticastRestoreChannel channel, RestoreInitializer restoreInitializer) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.controlModule = controlModule;
        this.channel = channel;
        this.restoreInitializer = restoreInitializer;
        InitializeRestoreChannelListener(channel, controlModule);

    }

    private void InitializeRestoreChannelListener(MulticastRestoreChannel channel, ControlModule controlModule){
        channel.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(byte[] msg) {
                //TODO received backup message. Need to create worker thread to handle it.
                RestoreReceiverThread workerThread = new RestoreReceiverThread(msg, id, controlModule, restoreInitializer);
                workerThread.start();
                //TODO Check control for repDeg


                return "ok";
            }
        });
        channel.start();
    }
}
