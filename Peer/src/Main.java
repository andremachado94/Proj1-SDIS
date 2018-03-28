public class Main {

    public static void main(String[] args) {
        Peer peer = new Peer();

        if(args.length == 1){
            peer.StartBackupRequest(args[0], "1.1", 2, args[0]);
        }
    }
}
