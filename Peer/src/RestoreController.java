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
        dispatcher = new RestoreDispatcher(ip, port, id, controlModule, channel);
        initializer = new RestoreInitializer(ip, port, id, controlModule, channel);
    }

    public void StartRestoreRequest(String fileName, String version){
        initializer.StartRestoreRequest(fileName, version);
    }
}
