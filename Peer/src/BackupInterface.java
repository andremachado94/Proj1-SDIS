import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackupInterface extends Remote {
    String HOST = "//localhost:1099";
    String backup(String filePath, int repDegree) throws RemoteException;
    byte[] restore(String fileName) throws RemoteException;
    String reclaim(int maxSpace) throws RemoteException;
    String delete(String fileName) throws RemoteException;
    String state() throws RemoteException;
}