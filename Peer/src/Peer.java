import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by andremachado on 02/03/2018.
 */
public class Peer extends UnicastRemoteObject implements BackupInterface {
    private Version protocolVersion;
    private int serverId;
    private String accessPoint;
    private ControlModule controlModule;
    private BackupController backupController;
    private RestoreController restoreController;

    // protocol_version, server_id, access_point, mc_ip:port, mdb_ip:port, mdr_ip:port
    private Peer(String protocolVersion, int serverId, String accessPoint,
                String mcc_ip, int mcc_port, String mbc_ip, int mbc_port, String mrc_ip, int mrc_port
    ) throws RemoteException {
        super(); // required to avoid the 'rmic' step
        this.protocolVersion = new Version(protocolVersion);
        this.serverId = serverId;
        this.accessPoint = accessPoint;
        this.controlModule = new ControlModule(mcc_ip, mcc_port, serverId);
        this.backupController = new BackupController(mbc_ip, mbc_port, serverId, controlModule);
        this.restoreController = new RestoreController(mrc_ip, mrc_port, serverId, controlModule);
        this.controlModule.SetRestoreController(this.restoreController);

        System.out.println("\n\n\n::::::::::::: NEW PEER CREATED :::::::::::::");
        System.out.println("\tProtocol Version: "+protocolVersion);
        System.out.println("\t       Server ID: "+serverId);
        System.out.println("\t    Access Point: "+accessPoint);
        System.out.println("\t     MCC Address: "+mcc_ip+":"+mcc_port);
        System.out.println("\t     MBC Address: "+mbc_ip+":"+mbc_port);
        System.out.println("\t     MRC Address: "+mrc_ip+":"+mrc_port);
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::\n");
    }

    @Deprecated
    public Peer() throws RemoteException {
        super();
        String mcc_ip = "239.0.0.0";
        int mcc_port = 1234;
        String mbc_ip = "239.1.0.0";
        int mbc_port = 3456;
        String mrc_ip = "239.2.0.0";
        int mrc_port = 5678;
        this.controlModule = new ControlModule(mcc_ip, mcc_port, serverId);
        this.backupController = new BackupController(mbc_ip, mbc_port, serverId, controlModule);
        this.restoreController = new RestoreController(mrc_ip, mrc_port, serverId, controlModule);
        this.controlModule.SetRestoreController(this.restoreController);
    }

    @Deprecated
    public void StartBackupRequest(String filePath, String version, int repDeg, String fileName){
        backupController.StartBackupRequest(filePath, version, repDeg, fileName);
    }

    @Deprecated
    public void StartRestoreRequest(String fileName, String version) {
        byte[] data = restoreController.StartRestoreRequest(fileName, version);
    }

    @Deprecated
    public void StartDeleteRequest(String fileName, String version) {
        controlModule.StartDeleteRequest(fileName, version);
    }

    @Override
    public String backup(String filePath, int repDegree) {
        File file = new File(filePath);
        // file not found
        if (!file.exists()){
            return "Error: File not found!";
        }
        // file found. proceed with backup request
        boolean result = backupController.StartBackupRequest(filePath, "1.1", repDegree, file.getName());
        if (!result)
            return "Error: failed to backup file " + file.getName();

        //success
        return "Backup: successfully backed up " + file.getName();
    }

    @Override
    public byte[] restore(String fileName) {
        return restoreController.StartRestoreRequest(fileName, "1.1");
    }

    @Override
    public String reclaim(int maxSpace) {
        if(controlModule.StartReclaimRequest(maxSpace)){
            return "Reclaim: successfully reclaimed space";
        }
        return "Error: Failed to reclaim space";
    }

    @Override
    public String delete(String fileName) {
        controlModule.StartDeleteRequest(fileName, "1.1");
        return "File " + fileName + " deleted.";
    }

    @Override
    public String state() {
        final String dir = System.getProperty("user.dir");
        final String peerdir = new File(dir).getParent()+"/"+"backup_chunks"+"/"+serverId;
        File f = new File(peerdir);

        long size = Util.PathSize(f.toPath());

        return  "\n:::::::::::::::: PEER STATE ::::::::::::::::" +
                "\n\t Protocol Version: "+protocolVersion+
                "\n\t Server ID: "+serverId+
                "\n\t Access Point: "+accessPoint+
                "\n\t Peer Disk Space: "+controlModule.GetMaxPeerCapacity()+"kB"+
                "\n\t Peer Used Space: "+(size/1000)+"kB"+
                "\n\t Peer Available Space: "+(controlModule.GetMaxPeerCapacity()-(size/1000))+"kB"+
                "\n::::::::::::::::::::::::::::::::::::::::::::";
    }

    public String getAccessPoint() {
        return accessPoint;
    }

    public static void main(String args[]) throws Exception {
        // get rmi registry using default PORT
        try {
            LocateRegistry.getRegistry(HOST);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
            System.err.println("Please start rmiregistry beforehand.");
            throw e;
        }
        System.out.println("Java RMI registry gotten.");

        // crawl through this object instance to the name "BackupPeer"
        ArrayList<Integer> accessPoints = new ArrayList<>();
        String [] namingList = Naming.list(HOST);
        String BASE_URL = HOST+"/BackupPeer";
        for (String str: namingList) {
            if (str.contains(BASE_URL)) {
                String substr = str.substring(BASE_URL.length());
                Integer access_point = Integer.valueOf(substr);
                accessPoints.add(access_point);
            }
        }
        System.out.println("Already existent Access Points: "+accessPoints);

        // initialize Peer
        Peer peer;
        String protocolVersion;
        int serverId;
        String accessPoint;
        String mcc_ip, mbc_ip, mrc_ip;
        int mcc_port, mbc_port, mrc_port;
        // initialize automatically
        if (args.length == 0){
            protocolVersion = "1.0";
            serverId = new Random().nextInt(100000);
            int iAccessPoint = 0;
            while (accessPoints.contains(iAccessPoint)) ++iAccessPoint;
            accessPoint = "//localhost:1099/BackupPeer"+iAccessPoint;
            mcc_ip = "239.0.0.0";
            mcc_port = 1234;
            mbc_ip = "239.1.0.0";
            mbc_port = 1234;
            mrc_ip = "239.2.0.0";
            mrc_port = 1234;

        }
        // initialize manually
        else if (args.length == 9){
            protocolVersion = args[0];
            serverId = Integer.parseInt(args[1]);
            accessPoint = args[2];
            mcc_ip = args[3];
            mcc_port = Integer.parseInt(args[4]);
            mbc_ip = args[5];
            mbc_port = Integer.parseInt(args[6]);
            mrc_ip = args[7];
            mrc_port = Integer.parseInt(args[8]);
        }
        // error: invalid argument number
        else {
            throw new IllegalArgumentException("Invalid Arguments number: different than 9.");
        }

        // bind!
        peer = new Peer(protocolVersion, serverId, accessPoint, mcc_ip, mcc_port, mbc_ip, mbc_port, mrc_ip, mrc_port);
        Naming.rebind(peer.getAccessPoint(), peer);
        //Naming.unbind(peer.getUrl());
    }
}
