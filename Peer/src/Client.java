import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

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
    private JTextField mccField;
    private JTextField protverField;
    private JButton restartRMIButton;
    private JTextArea textArea1;
    private JButton launchNewPeerButton;
    private JLabel statusField;
    private JButton reclaimButton;
    private JTextField ipField;
    private JTextField portField;
    private JTextField serverIdField;
    private JTextField posfixField;
    private JTextField mbcField;
    private JTextField mrcField;
    private JList<String> localApList;

    public Client() {
        searchButton.addActionListener(e -> apList.setListData(getAccessPoints(lookupField.getText())));
        lookupField.addActionListener(e -> apList.setListData(getAccessPoints(lookupField.getText())));
        apList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {accessPointField.setText(apList.getSelectedValue());}
        });
        backupButton.addActionListener(e -> {
            // pick file
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(panelMain);
            if(returnVal != JFileChooser.APPROVE_OPTION) {
                System.err.println("Error: File chooser unexpectedly returned code "+returnVal);
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
                statusField.setText("Error: failed to execute BACKUP operation.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        });
        restoreButton.addActionListener(e -> {
            // ask for filename
            String filename = JOptionPane.showInputDialog("Enter desired file's name:");
            System.out.println("User entered filename: "+filename);

            // restore
            byte[] result;
            try {
                BackupInterface peer = (BackupInterface)Naming.lookup(accessPointField.getText());
                result = peer.restore(filename);
            } catch (Exception ex) {
                statusField.setText("Error: failed to execute RESTORE operation.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
                return;
            }
            if (result == null || result.length==0) {
                statusField.setText("Error: " + filename + " not found.");
                return;
            }
            else
                statusField.setText("Successfully retrieved "+filename);


            // where to save
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Specify where to save file");
            chooser.setSelectedFile(new File(filename));
            int returnVal = chooser.showSaveDialog(panelMain);
            if(returnVal != JFileChooser.APPROVE_OPTION) {
                System.err.println("Error: File chooser unexpectedly returned code "+returnVal);
                return;
            }
            String filePath = chooser.getSelectedFile().getAbsolutePath();

            // save
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(result);
            } catch (Exception ex) {
                statusField.setText("Error: failed to save file at path specified.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        });
        deleteButton.addActionListener(e -> {
            // ask for filename
            String filename = JOptionPane.showInputDialog("Enter desired file's name:");
            System.out.println("User entered filename: "+filename);

            // delete
            String result;
            try {
                BackupInterface peer = (BackupInterface)Naming.lookup(accessPointField.getText());
                result = peer.delete(filename);
                assert result != null;
                statusField.setText("Successfully removed "+filename);
            } catch (Exception ex) {
                statusField.setText("Error: failed to execute DELETE operation.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        });
        reclaimButton.addActionListener(e -> {
            // ask for replication degree
            int maxSpace = Integer.parseInt(JOptionPane.showInputDialog("Enter desired maximum space:"));
            if (maxSpace < 0){
                System.err.println("User entered max space < 0");
                statusField.setText("Error: max space < 0");
                return;
            }
            System.out.println("User chose max space: "+maxSpace);

            // backup
            try {
                BackupInterface peer = (BackupInterface)Naming.lookup(accessPointField.getText());
                String result = peer.reclaim(maxSpace);
                statusField.setText(result);
            } catch (Exception ex) {
                statusField.setText("Error: failed to execute RECLAIM operation.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        });
        stateButton.addActionListener(e -> {
            // state
            String result;
            try {
                BackupInterface peer = (BackupInterface)Naming.lookup(accessPointField.getText());
                result = peer.state();
                assert result != null;
                statusField.setText("Successfully retrieved Peer's state.");
                System.out.println(result);
                JOptionPane.showMessageDialog(panelMain, result, "Peer's State response", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) {
                statusField.setText("Error: failed to execute STATE operation.");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        });
        restartRMIButton.addActionListener(e -> {
            try {
                Runtime.getRuntime().exec("pkill -9 -f rmiregistry").waitFor();
                Runtime.getRuntime().exec("rmiregistry");
                //Runtime.getRuntime().exec("pkill -9 -f Peer");
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        serverIdField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e) {posfixField.setText("BackupPeer"+serverIdField.getText());}
            public void removeUpdate(DocumentEvent e) {posfixField.setText("BackupPeer"+serverIdField.getText());}
            public void changedUpdate(DocumentEvent e) {posfixField.setText("BackupPeer"+serverIdField.getText());}
        });
        launchNewPeerButton.addActionListener(e -> {
            String[] mcc = mccField.getText().split(":");
            String[] mbc = mbcField.getText().split(":");
            String[] mrc = mrcField.getText().split(":");
            try {
                String command = "java Peer "
                        + protverField.getText() + serverIdField.getText() + accessPointField.getText()
                        + mcc[0] + mcc[1] + mbc[0] + mbc[1] + mrc[0] + mrc[1];
                System.out.println(command);
                Process p = Runtime.getRuntime().exec(command);
                //p.getOutputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            localApList.setListData(getAccessPoints("//localhost:1099"));
        });
    }

    private String [] getAccessPoints(String host){
        try {
            String [] namingList = Naming.list(host);
            System.out.println("Java RMI list gotten.");
            statusField.setText("Success: Java RMI list gotten.");
            for (String s: namingList) {
                System.out.println("\t"+s);
            }
            return namingList;
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
            System.err.println("RMI Registry not found. Start rmiregistry beforehand.");
            statusField.setText("Error: RMI Registry not found.");
            return new String[0];
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
            statusField.setText("Error: Malformed URL.");
            return new String[0];
        }

    }

    public static void main(String args[]) throws MalformedURLException,RemoteException,NotBoundException {
        // GUI start
        if (args.length==0){
            // set L&F to GTK
            /*try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); //Windows Look and feel
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }*/

            // init jframe
            JFrame frame = new JFrame("Client");
            frame.setContentPane(new Client().panelMain);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
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
                case "BACKUP": // e.g.: java Client BackupPeer0 BACKUP test1.pdf 3
                    if (args.length == 3 || args.length == 4){
                        int repDegree = args.length!=4 ? 1 : Integer.parseInt(args[3]);
                        System.out.println(peer.backup(args[2], repDegree)); // pathname, rep_degree
                    }
                    else throw new IllegalArgumentException("BACKUP operation requires parameters <pathname> or <pathname> <rep_degree>");
                    break;
                case "RESTORE": // e.g.: java Client BackupPeer0 RESTORE test1.pdf
                    if (args.length == 3) System.out.println(Arrays.toString(peer.restore(args[2]))); // pathname
                    else throw new IllegalArgumentException("RESTORE operation requires parameter <filename>");
                    break;
                case "DELETE": // e.g.: java Client BackupPeer0 DELETE test1.pdf
                    if (args.length == 3) System.out.println(peer.delete(args[2])); // pathname
                    else throw new IllegalArgumentException("DELETE operation requires parameter <filename>");
                    break;
                case "RECLAIM": // e.g.: java Client BackupPeer0 RECLAIM 10000
                    if (args.length == 3) System.out.println(peer.reclaim(Integer.parseInt(args[2]))); // pathname
                    else throw new IllegalArgumentException("DELETE operation requires parameter <filename>");
                    break;
                case "STATE": // e.g.: java Client BackupPeer0 STATE
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
        System.out.println("\t\t\tjava Client <peer_ap> BACKUP <pathname> <rep_degree>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client BackupPeer0 BACKUP mynotebook.txt");
        System.out.println("\t\t\tjava Client BackupPeer0 BACKUP mynotebook.txt 3");
        System.out.println();
        System.out.println("\tRESTORE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tRestore a file, specifying its filename.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> RESTORE <filename>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client BackupPeer0 RESTORE mynotebook.txt");
        System.out.println();
        System.out.println("\tDELETE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tDelete a file, specifying its filename.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> DELETE <filename>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client BackupPeer0 DELETE mynotebook.txt");
        System.out.println();
        System.out.println("\tRECLAIM : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tReclaim the disk space, given a max usable space (in KBytes).");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> RECLAIM <max_space>");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client BackupPeer0 RECLAIM 10000");
        System.out.println();
        System.out.println("\tSTATE : ");
        System.out.println("\t\tDescription: ");
        System.out.println("\t\t\tGet the current Peer's state.");
        System.out.println("\t\tSyntax:");
        System.out.println("\t\t\tjava Client <peer_ap> STATE");
        System.out.println("\t\te.g.:");
        System.out.println("\t\t\tjava Client BackupPeer0 STATE");
    }
}