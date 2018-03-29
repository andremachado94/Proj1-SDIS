import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by andremachado on 02/03/2018.
 */
public class Peer extends UnicastRemoteObject implements BackupInterface {
    public static final int CHUNK_SIZE = 64000; //64 KBytes, 64000 Bytes
    public static final int TYPE_BACKUP = 1;
    public static final int TYPE_RESTORE = 2;
    public static final int TYPE_DELETE = 3;
    public static final int TYPE_RECLAIM = 4;
    public static final int TYPE_ERROR = -1;

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

    public Peer() throws RemoteException {
        super(); // required to avoid the 'rmic' step

        //TODO peer_id = random number betwee 0 and a lot
        Random rand = new Random();
        peer_id = rand.nextInt(100000);

        System.out.println("My id is: " + peer_id);

        controlModule = new ControlModule(mcc_ip, mcc_port);
        backupController = new BackupController(mbc_ip, mbc_port, peer_id, controlModule);
        restoreController = new RestoreController(mrc_ip, mrc_port, peer_id, controlModule);

    }

    public void StartBackupRequest(String filePath, String version, int repDeg, String fileName){
        backupController.StartBackupRequest(filePath, version, repDeg, fileName);
    }

    public void StartRestoreRequest(String fileName, String version) {
        restoreController.StartRestoreRequest(fileName, version);
    }

    @Override
    public String backup(BackupFile file, int repDegree) throws RemoteException {
        backupController.StartBackupRequest(file.getPathname(), file.getVersion(), file.GetRepDegree(), file.getFileName());
        return "BACKUP processed";
    }

    @Override
    public String restore(String pathname) throws RemoteException {
        restoreController.StartRestoreRequest(pathname, "1.0");
        return "BACKUP processed";
    }

    @Override
    public String delete(String pathname) throws RemoteException {
        return null;
    }

    @Override
    public String state() throws RemoteException {
        return null;
    }

    public static void main(String args[]) throws Exception {
        System.out.println("RMI server started");

        try {
            // previously run in terminal: rmiregistry <PORT>
            // default PORT is 1099
            LocateRegistry.getRegistry(PORT);
            System.out.println("Java RMI registry gotten.");
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }

        // Bind this object instance to the name "BackupPeer"
        ArrayList<Integer> accessPoints = new ArrayList<>();
        String [] namingList = Naming.list(BASE_URL);
        for (String str: namingList) {
            if (str.contains(BASE_URL)) {
                String substr = str.substring(BASE_URL.length());
                Integer access_point = Integer.valueOf(substr);
                accessPoints.add(access_point);
            }
        }
        System.out.println("Already existent Access Points: "+accessPoints);


        int i=0;
        while (accessPoints.contains(i)) ++i;
        Naming.rebind(BASE_URL+i, new Peer());
        System.out.println("BackupPeer bound: "+BASE_URL+i);
        //Naming.unbind(url+i);
        //System.out.println("BackupPeer unbound: "+url+i);
    }
}
