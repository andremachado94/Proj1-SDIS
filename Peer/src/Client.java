import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private static void printHelp(){
        System.out.println("HELP DIALOG");
        System.out.println("\tSyntax:");
        System.out.println("\t\tjava Client help");
        System.out.println("\t\tjava Client <peer_ap> <operation> <param1> <param2>");
        System.out.println();
        System.out.println("Operations available:");
        System.out.println("\tBACKUP : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tBackup a file, specifying the file pathname and the desired replication degree. If omitted, a replication degree of 1 is assumed.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava BACKUP <pathname> <rep_degree>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client <peer_ap> BACKUP mynotebook.txt");
        System.out.println("\t\t\tjava Client <peer_ap> BACKUP mynotebook.txt 3");
        System.out.println();
        System.out.println("\tRESTORE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tRestore a file, specifying its pathname.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE <pathname>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE mynotebook.txt");
        System.out.println();
        System.out.println("\tDELETE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tDelete a file, specifying its pathname.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> DELETE <pathname>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client <peer_ap> DELETE mynotebook.txt");
        System.out.println();
        System.out.println("\tRESTORE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tRestore a file, specifying its pathname.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE <pathname>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE mynotebook.txt");
    }

    public static void main(String args[]) throws MalformedURLException,RemoteException,NotBoundException {
        if (args[0].equalsIgnoreCase("help")){
            printHelp();
            return;
        }

        // <peer_ap>
        // You should use as access point the name of the remote object providing the "testing" service.
        String peer_ap = args[0];
        BackupInterface peer = (BackupInterface)Naming.lookup(peer_ap);

        // <operation>
        // Is the operation the peer of the backup service must execute. It can be either the triggering of the
        // subprotocol to test, or the retrieval of the peer's internal state. In the first case it must be one
        // of: BACKUP, RESTORE, DELETE, RECLAIM. In the case of enhancements, you must append the substring ENH at
        // the end of the respecive subprotocol, e.g. BACKUPENH. To retrieve the internal state, the value of this
        // argument must be STATE
        try {
            String operation = args[1].toUpperCase();
            switch (operation) {
                case "BACKUP": // e.g.: java Client AP0 BACKUP test1.pdf 3
                    if (args.length == 3 || args.length == 4){
                        String pathname = args[2];
                        int repDegree = args.length!=4 ? 1 : Integer.parseInt(args[3]);
                        BackupFile file = new BackupFile(pathname);
                        System.out.println(peer.backup(file, repDegree)); // pathname, rep_degree
                    }
                    else throw new IllegalArgumentException("BACKUP operation requires parameters <pathname> or <pathname> <rep_degree>");
                    break;
                case "RESTORE": // e.g.: java Client AP0 RESTORE test1.pdf
                    if (args.length == 3) System.out.println(peer.restore(args[2])); // pathname
                    else throw new IllegalArgumentException("RESTORE operation requires parameter <pathname>");
                    break;
                case "DELETE": // e.g.: java Client AP0 DELETE test1.pdf
                    if (args.length == 3) System.out.println(peer.delete(args[2])); // pathname
                    else throw new IllegalArgumentException("DELETE operation requires parameter <pathname>");
                    break;
                case "STATE": // e.g.: java Client AP0 STATE
                    if (args.length == 2) System.out.println(peer.state());
                    else throw new IllegalArgumentException("RESTORE operation requires no further parameters");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation. Please enter a known operation.");
            }
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            System.err.println("If you need to help, simply execute 'Client help'.");
        }

    }
}