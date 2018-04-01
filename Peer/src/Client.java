import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class Client {
    private JButton searchButton;
    private JPanel panelMain;
    private JTabbedPane tabbedPane1;
    private JTextField lookupField;
    private JList<String> apList;
    private JTextField accessPointField;
    private JButton backupButton;
    private JButton restoreButton;
    private JButton deleteButton;
    private JButton stateButton;
    private JTextField a239000TextField;
    private JTextField a10TextField;
    private JButton restartRMIButton;
    private JTextArea textArea1;
    private JButton launchNewPeerButton;
    private JLabel statusField;

    public Client() {
        /*searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"Hello World!");
            }
        });*/
        restartRMIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO execute pkill -9 -f rmiregistry
                //TODO execute rmiregistry
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAccessPoints(lookupField.getText());
            }
        });
        lookupField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAccessPoints(lookupField.getText());
            }
        });
        apList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    accessPointField.setText(apList.getSelectedValue().toString());
                }
            }
        });
        backupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pick file
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(panelMain);
                if(returnVal != JFileChooser.APPROVE_OPTION) {
                    System.err.println("File chooser unexpectedly returned code "+returnVal);
                    return;
                }
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                System.out.println("User chose this file: " + filePath);

                // ask for replication degree
                int repDegree = Integer.parseInt(JOptionPane.showInputDialog("Enter desired replication degree:"));
                if (repDegree < 1){
                    System.err.println("Replication degree < 1");
                    statusField.setText("Error: replication degree <1");
                    return;
                }
                System.out.println("User chose replication degree: "+repDegree);

                // backup
                try {
                    BackupInterface peer = (BackupInterface)Naming.lookup(accessPointField.getText());
                    String result = peer.backup(chooser.getSelectedFile().getAbsolutePath(),repDegree);
                    statusField.setText(result);
                } catch (Exception ex) {
                    statusField.setText("Error: failed to send BACKUP command.");
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                    return;
                }
            }
        });
    }

    private void updateAccessPoints(String host){
        try {
            String [] namingList = Naming.list(host);
            System.out.println("Java RMI list gotten.");
            statusField.setText("Success: Java RMI list gotten.");
            for (String s: namingList) {
                System.out.println("\t"+s);
            }
            apList.setListData(namingList);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
            System.err.println("RMI Registry not found. Start rmiregistry beforehand.");
            statusField.setText("Error: RMI Registry not found.");
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
            statusField.setText("Error: Malformed URL.");
        }

    }

    public static void main(String args[]) throws MalformedURLException,RemoteException,NotBoundException {
        // GUI start
        if (args.length==0){
            JFrame frame = new JFrame("Client");
            frame.setContentPane(new Client().panelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            return;
        }

        // CLI: help
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
                        int repDegree = args.length!=4 ? 1 : Integer.parseInt(args[3]);
                        System.out.println(peer.backup(args[2], repDegree)); // pathname, rep_degree
                    }
                    else throw new IllegalArgumentException("BACKUP operation requires parameters <pathname> or <pathname> <rep_degree>");
                    break;
                case "RESTORE": // e.g.: java Client AP0 RESTORE test1.pdf
                    if (args.length == 3) System.out.println(peer.restore(args[2])); // pathname
                    else throw new IllegalArgumentException("RESTORE operation requires parameter <filename>");
                    break;
                case "DELETE": // e.g.: java Client AP0 DELETE test1.pdf
                    if (args.length == 3) System.out.println(peer.delete(args[2])); // pathname
                    else throw new IllegalArgumentException("DELETE operation requires parameter <filename>");
                    break;
                case "STATE": // e.g.: java Client AP0 STATE
                    if (args.length == 2) System.out.println(peer.state());
                    else throw new IllegalArgumentException("STATE operation requires no further parameters");
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
        System.out.println("\t\t\tRestore a file, specifying its filename.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE <filename>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE mynotebook.txt");
        System.out.println();
        System.out.println("\tDELETE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tDelete a file, specifying its filename.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> DELETE <filename>");
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
}