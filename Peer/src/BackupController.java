/**
 * Created by andremachado on 09/03/2018.
 */
public class BackupController{

    BackupDispatcher dispatcher;
    BackupInitializer initializer;
    private MulticastBackupChannel channel;

    private int id;

    public BackupController(String ip, int port, int id, ControlModule controlModule){
        channel = new MulticastBackupChannel(ip,port);
        this.id = id;
        dispatcher = new BackupDispatcher(ip, port, id, controlModule, channel);
        initializer = new BackupInitializer(ip, port, id, controlModule, channel);
    }

    public boolean StartBackupRequest(String filePath, String version, int repDeg, String fileName){
        return initializer.StartBackupRequest(filePath, version, repDeg, fileName);
    }

}























