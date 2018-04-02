import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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

    private long maxPeerCapacity = 1700;

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
        SendControlMessage(DeleteMessage.GetDeleteMessage(version, id,  fileName).getBytes());
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
