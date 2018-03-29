/**
 * Created by andremachado on 28/03/2018.
 */
public class RestoreDispatcher {

    private String ip;
    private int port;
    private int id;
    private ControlModule controlModule;
    private MulticastRestoreChannel channel;

    public RestoreDispatcher(String ip, int port, int id, ControlModule controlModule, MulticastRestoreChannel channel) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.controlModule = controlModule;
        this.channel = channel;
    }
}
