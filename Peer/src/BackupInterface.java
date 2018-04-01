
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackupInterface extends Remote {
    int PORT = 1099;
    String BASE_URL = "//localhost:"+ PORT +"/BackupPeer";
    String backup(String filePath, int repDegree) throws RemoteException;
    String restore(String pathname) throws RemoteException;
    String delete(String pathname) throws RemoteException;
    String state() throws RemoteException;
}