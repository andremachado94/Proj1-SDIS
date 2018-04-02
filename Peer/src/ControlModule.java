import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.sun.imageio.plugins.jpeg.JPEG.version;

/**
 * Created by andremachado on 16/03/2018.
 */
public class ControlModule {
    private ControlMessageParser controlMessageParser = new ControlMessageParser();
    private MulticastControlChannel channel;
    private static Map<String, List<Integer>> storedMap = null;
    String ip;
    int port;
    private int id;

    private long maxPeerCapacity = 40000;

    public void SetRestoreController(RestoreController restoreController) {
        this.restoreController = restoreController;
    }

    private RestoreController restoreController;

    public ControlModule(String ip, int port, int id){
        storedMap = new ConcurrentHashMap<String, List<Integer>>();
        channel = new MulticastControlChannel(ip, port);
        this.ip = ip;
        this.port = port;
        this.id = id;
        InitializeControlChannelListener();
    }


    //TODO define criteria to clean Maps

    public void SendControlMessage(byte[] msg){
        channel.SendControlMessage(msg);
    }

    public void StartDeleteRequest(String fileName, String version){
        String msg = DeleteMessage.GetDeleteMessage(version, id,  fileName);
        SendControlMessage(msg.getBytes());
    }

    public boolean StartReclaimRequest(int maxPeerCapacity){
        this.maxPeerCapacity = maxPeerCapacity;
        final String dir = System.getProperty("user.dir");
        final String peerdir = new File(dir).getParent()+"/"+"backup_chunks"+"/"+id;
        File f = new File(peerdir);
        if(Util.PathSize(f.toPath()) <= maxPeerCapacity){
            return true;
        }

        return ReclaimSpace(f, maxPeerCapacity);
    }

    private boolean ReclaimSpace(File f, int maxPeerCapacity) {
        File directory = f;
        boolean res = false;

        //make sure directory exists
        if(!directory.exists()){
            System.out.println("Directory does not exist.");
            return true;
        }else{
            try{
                res = ReclaimDelete(directory, f, maxPeerCapacity*1000);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return res;
    }

    public boolean ReclaimDelete(File file, File baseFile, int capacity)
            throws IOException{

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
                System.out.println("Directory is deleted : " + file.getAbsolutePath());
                if(ReclaimExitCheck(baseFile, capacity))
                    return true;
            }
            else{
                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    boolean res = ReclaimDelete(fileDelete, baseFile, capacity);
                    if(res) return true;
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : " + file.getAbsolutePath());
                    if(ReclaimExitCheck(baseFile, capacity))
                        return true;
                }
            }

        }else{

            System.out.println("File is deleted : " + file.getAbsolutePath());
            //SendControlMessage(RemovedMessage.GetRemovedMessage("1.0", id, file.getParentFile().getName(), n).getBytes());
            file.delete();
            if(ReclaimExitCheck(baseFile, capacity))
                return true;
        }
        return false;
    }

    private static boolean ReclaimExitCheck(File baseFile, int capacity){
        if(Util.PathSize(baseFile.toPath()) > capacity){
            return false;
        }
        return true;
    }

    private void InitializeControlChannelListener(){
        channel.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(byte[] msg) {
                int messageType = controlMessageParser.GetMessageType(new String(msg));
                if(messageType == ControlMessageParser.TYPE_STORED){
                    StoredMessage storedMessage;
                    if((storedMessage = controlMessageParser.ParseStoredMessage(new String(msg))) != null) {
                        List<Integer> peers = storedMap.get(storedMessage.getFileId() + "_" + storedMessage.getChunkNumber());
                        if (peers == null) {
                            peers = new ArrayList<Integer>();
                        }
                        System.out.println("RECEIVED STORED " + storedMessage.getFileId() + "_" + storedMessage.getChunkNumber() + "\t-\t" + peers.toString());
                        if (!peers.contains(storedMessage.getPeerId())) {
                            peers.add(storedMessage.getPeerId());
                            System.out.println("Adding key: " + storedMessage.getFileId() + "_" + storedMessage);
                            storedMap.put(storedMessage.getFileId() + "_" + storedMessage.getChunkNumber(), peers);
                        }
                    }
                    else{
                        System.out.println("Controlo recebido tipo STORED");
                        System.out.println("Erro a processar mensagem tipo STORED");
                    }
                }
                else if(messageType == ControlMessageParser.TYPE_GETCHUNK){
                    GetChunkMessage getChunkMessage;
                    if((getChunkMessage = controlMessageParser.ParseGetChunkMessage(new String(msg))) != null){
                        System.out.println("Received GETCHUNK msg: " + getChunkMessage.GetFileId());
                        System.out.println("Clean Id: " + Util.GetCleanId(getChunkMessage.GetFileId()));
                        restoreController.SendChunkMessage(getChunkMessage);
                    }
                    else{
                        System.out.println("Controlo recebido tipo GETCHUNK");
                        System.out.println("Erro a processar mensagem tipo GETCHUNK");
                    }
                }
                else if(messageType == ControlMessageParser.TYPE_DELETE){
                    DeleteMessage deleteMessage;
                    if((deleteMessage = controlMessageParser.ParseDeleteMessage(new String(msg))) != null) {
                        System.out.println("DELETE: " + deleteMessage.GetFileId());
                        DeleteFileRecords(deleteMessage.GetFileId(), deleteMessage.GetVersion());
                    }
                    else{
                        System.out.println("Controlo recebido tipo DELETE");
                        System.out.println("Erro a processar mensagem tipo DELETE");
                    }
                }
                else{
                    System.out.println("Controlo recebido tipo ?? - " + msg);
                }
                return "ok";
            }
        });
        channel.start();
    }

    public boolean ReceivedStoredMessages(String fileId, int chunkNumber, int repDeg) {
        String key = fileId + "_" + chunkNumber;
        System.out.println("Checking for " + key);
        if(storedMap.containsKey(key)) {
            if (storedMap.get(key).size() >= repDeg)
                return true;
        }
        return false;
    }

    public void SendRestoreRequest(String fileId, String version, int i) {
        GetChunkMessage chunk = new GetChunkMessage(new Version(version), id, fileId, i);
        System.out.println("Sending GetChunk:\n" + chunk.GetMessage());
        SendControlMessage(chunk.GetMessage().getBytes());

    }

    private void DeleteFileRecords(String fileId, String version){
        String cleanId = Util.GetCleanId(fileId);
        final String dir = System.getProperty("user.dir");
        final String filedir = new File(dir).getParent()+"/"+"backup_chunks"+"/"+id+"/"+cleanId;

        File folder = new File(filedir);

        System.out.println("DELETE: folder: " + filedir);


        if(folder.exists() && folder.isDirectory()) {
            String files[] = folder.list();

            for (String temp : files) {
                System.out.println("DELETING: " + temp);
                String chunkNumber = temp.split("\\.")[0];
                if(chunkNumber != null) {
                    try {
                        int n = Integer.parseInt(chunkNumber);
                        File fileDelete = new File(folder, temp);
                        fileDelete.delete();
                        SendControlMessage(RemovedMessage.GetRemovedMessage(version, id, fileId, n).getBytes());
                    } catch (Exception e) {

                    }
                }
            }

            folder.delete();
        }
        else{
            System.out.println("DELETE: folder doesn't exist");
        }
    }

    public List<Integer> GetPeersThatStored(String fileId, int chunkNumber) {
        if(storedMap.containsKey(fileId + "_" + chunkNumber)){
            return storedMap.get(fileId + "_" + chunkNumber);
        }
        return null;
    }

    public long GetMaxPeerCapacity() {
        return maxPeerCapacity;
    }
}
