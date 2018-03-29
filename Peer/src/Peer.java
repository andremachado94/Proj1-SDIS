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
    public static final int PORT = 1099;
    public static final int TYPE_BACKUP = 1;
    public static final int TYPE_RESTORE = 2;
    public static final int TYPE_DELETE = 3;
    public static final int TYPE_RECLAIM = 4;
    public static final int TYPE_ERROR = -1;

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

    public Peer() throws RemoteException {
        super(); // required to avoid the 'rmic' step

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

    @Override
    public String backup(BackupFile file, int repDegree) throws RemoteException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //
        String filePathname = file.getPathname();
        long fileLength = file.getLength();
        long fileLastModified = file.getLastModified();

        // print to peer's console
        System.out.println("Received request to backup given file:");
        System.out.println("\tPATH:\t"+filePathname);
        System.out.println("\t LEN:\t"+fileLength);
        System.out.println("\tLMOD:\t"+sdf.format(fileLastModified));

        // split file in chunks, creating 1 worker thread (to putchunk) per chunk
        ArrayList<byte[]> chunksBytes = new ArrayList<>();
        byte[] fileBytes = file.getDataBytes();
        int i = 0, k = i + CHUNK_SIZE;
        int copied = 0;
        do {
            byte[] chunkBytes = Arrays.copyOfRange(fileBytes, i, k);
            chunksBytes.add(chunkBytes);
            i += CHUNK_SIZE;
            k += CHUNK_SIZE;
            copied = chunkBytes.length;
        } while (copied > 0);

        // fix the case where last chunk equals precisely 64,000 bytes
        if (chunksBytes.get(chunksBytes.size() - 1).length == 64000){
            chunksBytes.add(new byte[0]);
        }

        // TODO process these chunkBytes, creating worker threads per chunk and pooling them together

        return "BACKUP: "+filePathname+" has been processed successfully!";
    }

    @Override
    public String restore(String pathname) throws RemoteException {
        return null;
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
        String url = "//localhost:"+ PORT +"/BackupPeer";
        ArrayList<Integer> accessPoints = new ArrayList<>();
        String [] namingList = Naming.list(url);
        for (String str: namingList) {
            if (str.contains(url)) {
                String substr = str.substring(url.length());
                Integer access_point = Integer.valueOf(substr);
                accessPoints.add(access_point);
            }
        }
        System.out.println("Already existent Access Points: "+accessPoints);


        int i=0;
        while (accessPoints.contains(i)) ++i;
        Naming.rebind(url+i, new Peer());
        System.out.println("BackupPeer bound: "+url+i);
        //Naming.unbind(url+i);
        //System.out.println("BackupPeer unbound: "+url+i);
    }
}
