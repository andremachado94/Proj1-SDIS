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

    public Peer(){

        Random rand = new Random();
        peer_id = rand.nextInt(100 + 1);

        InitializeChannels();
        mbc.SendBackupRequest("Hey, tudo bem?");
    }

    private void InitializeChannels(){
        mbc = new MulticastBackupChannel(mbc_ip, mbc_port);
        mcc = new MulticastControlChannel(mcc_ip, mcc_port);

        InitializeChannelListeners();
    }

    private void InitializeChannelListeners(){
        InitializeBackupChannelListener();
        InitializeControlChannelListener();
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

    private void InitializeControlChannelListener(){
        mcc.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(String msg) {
                System.out.println("Controlo recebido - " + msg);
                return "ok";
            }
        });
        mcc.start();
    }


}
