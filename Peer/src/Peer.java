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
    private MulticastRecoveryChannel mrc;

    private String mbc_ip = "239.0.0.0";
    private int mbc_port = 1234;

    private String mcc_ip = "239.1.0.0";
    private int mcc_port = 5678;

    private int peer_id;
    ControlModule controlModule;
    BackupController backupController;

    public Peer(){

        //TODO peer_id = random number betwee 0 and a lot
        Random rand = new Random();
        peer_id = rand.nextInt(100000);

        System.out.println("My id is: " + peer_id);


        controlModule = new ControlModule(mcc_ip, mcc_port);
        backupController = new BackupController(mbc_ip, mbc_port, peer_id, controlModule);

    }

    public void StartBackupRequest(String filePath){
        backupController.StartBackupRequest(filePath);
    }




}
