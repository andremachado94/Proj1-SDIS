import java.util.Random;

/**
 * Created by andremachado on 02/03/2018.
 */
public class Peer {
    public static int TYPE_BACKUP = 1;
    public static int TYPE_RESTORE = 2;
    public static int TYPE_DELETE = 3;
    public static int TYPE_RECLAIM = 4;
    public static int TYPE_ERROR = -1;

    private MulticastBackupChannel mbc;
    private MulticastControlChannel mcc;
    private MulticastRestoreChannel mrc;

    private String mbc_ip = "239.0.0.0";
    private int mbc_port = 1234;

    private String mcc_ip = "239.1.0.0";
    private int mcc_port = 5678;

    private String mrc_ip = "239.2.0.0";
    private int mrc_port = 5618;

    private int peer_id;
    ControlModule controlModule;
    BackupController backupController;
    RestoreController restoreController;

    public Peer(){

        //TODO peer_id = random number betwee 0 and a lot
        Random rand = new Random();
        peer_id = rand.nextInt(100000);

        System.out.println("My id is: " + peer_id);


        controlModule = new ControlModule(mcc_ip, mcc_port, restoreController);
        backupController = new BackupController(mbc_ip, mbc_port, peer_id, controlModule);
        restoreController = new RestoreController(mrc_ip, mrc_port, peer_id, controlModule);

    }

    public void StartBackupRequest(String filePath, String version, int repDeg, String fileName){
        backupController.StartBackupRequest(filePath, version, repDeg, fileName);
    }


    public void StartRestoreRequest(String fileName, String version) {
        restoreController.StartRestoreRequest(fileName, version);
    }
}
