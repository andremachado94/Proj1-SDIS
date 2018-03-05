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

    public Peer(){
        InitializeChannels();
    }

    private void InitializeChannels(){
        //TODO Add other channels
        mbc = new MulticastBackupChannel("239.0.0.0", 1234);
        //mcc = new MulticastControlChannel("0.0.0.0", 1234);

        //TODO Add other Listener Initializers
        InitializeBackupChannelListener();

        mbc.SendBackupRequest("Teste");


    }

    private void InitializeBackupChannelListener(){
        mbc.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(String msg) {
                System.out.println("Peer - " + msg);
                return "ok";
            }
        });
        mbc.start();
    }


}
