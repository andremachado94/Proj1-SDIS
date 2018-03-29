
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackupInterface extends Remote {
    static final int PORT = 1099;
    static final String BASE_URL = "//localhost:"+ PORT +"/BackupPeer";
    public String backup(BackupFile file, int repDegree) throws RemoteException;
    public String restore(String pathname) throws RemoteException;
    public String delete(String pathname) throws RemoteException;
    public String state() throws RemoteException;
}