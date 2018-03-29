import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class BackupPeer extends UnicastRemoteObject implements BackupInterface {
    private static final int CHUNK_SIZE = 64000; //64 KBytes, 64000 Bytes
    private static final int PORT = 1099;

    public BackupPeer() throws RemoteException {
        super(); // required to avoid the 'rmic' step
    }

    @Override
    public String backup(BackupFile file, int repDegree) throws RemoteException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //
        String filePath = file.getPath();
        long fileLength = file.length();
        long fileDateMod = file.lastModified();

        // print to peer's console
        System.out.println("Received request to backup given file:");
        System.out.println("\tPATH:\t"+filePath);
        System.out.println("\t LEN:\t"+fileLength);
        System.out.println("\tLMOD:\t"+sdf.format(fileDateMod));

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

        return "BACKUP: "+file.getName()+" has been processed successfully!";
    }

    @Override
    public String restore(String filename) throws RemoteException {
        // TODO create worker threads (pooling them) with GETCHUNKs
        // when the pool size gets to 0, the restore has been finished.
        return "RESTORE: "+filename+" has been processed successfully!";
    }

    @Override
    public String delete(String filename) throws RemoteException {
        // TODO

        return "DELETE: "+filename+" has been processed successfully!";
    }

    @Override
    public String state() throws RemoteException {
        // TODO
        return "STATE: ..."; // TODO
    }

    public static void main(String args[]) throws Exception {
        System.out.println("RMI server started");

        try { //special exception handler for registry creation
            // TODO Problema:
            // se o peer que cria o registo for "ao ar", os outros peers deixam
            // de ser acessiveis, o que é demonstrado abaixo, quando chamamos
            // https://coderanch.com/t/560496/java/multiple-servers-share-RMI-Registry
            LocateRegistry.createRegistry(PORT);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
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
        Naming.rebind(url+i, new BackupPeer());
        System.out.println("BackupPeer bound: "+url+i);
        //Naming.unbind(url+i);
        //System.out.println("BackupPeer unbound: "+url+i);
    }
}
