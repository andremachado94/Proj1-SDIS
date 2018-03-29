import java.rmi.RemoteException;

public class Main {

    public static void main(String[] args) throws RemoteException {
        Peer peer = new Peer();

        if(args.length == 1){
            peer.StartBackupRequest(args[0], "1.1", 2, args[0]);
        }
        else if(args.length == 2){
            peer.StartRestoreRequest(args[0], "1.1");
        }
    }
}
