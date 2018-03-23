import java.util.ArrayList;


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

    public void StartBackupRequest(String filePath){
        initializer.StartBackupRequest(filePath, "1.1", 2);
    }


    /*
    private MulticastControlChannel mcc;
    private MulticastBackupChannel mbc;
    private int peerId;
    private boolean sendingFile;
    private FileManager fileManager;
    private int[] fileRepDegCounter;


    public BackupController(MulticastControlChannel mcc, String ip, int port, int peerId){
        mbc = new MulticastBackupChannel(ip, port);
        this.mcc = mcc;
        this.peerId = peerId;
        this.sendingFile = false;
    }

    private void InitializeBackupChannelListener(){
        mbc.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(String msg) {
                mcc.SendControlMessage("Envio control a a dizer que recebi a msg: " + msg);
                return "ok";
            }
        });
        mbc.start();
    }

    private void BackupRequest(String filePath){

        if(sendingFile==false) {
            sendingFile = true;
            this.fileManager = new FileManager(filePath, 64000);
            start();
        }
        else {
            System.out.println("Please wait for the current backup request to finish");
        }

    }

    @Override
    public void run(){
        if(fileManager != null){
            ArrayList<byte[]> file = fileManager.SliceFile();
            fileRepDegCounter = new int[file.size()];

        }

        sendingFile = false;
        fileRepDegCounter = null;
    }

    */

}























