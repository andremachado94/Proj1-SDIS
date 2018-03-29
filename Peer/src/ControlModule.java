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

    public ControlModule(String ip, int port){
        storedMap = new ConcurrentHashMap<String, List<Integer>>();
        channel = new MulticastControlChannel(ip, port);
        this.ip = ip;
        this.port = port;
        InitializeControlChannelListener();
    }

    //TODO define criteria to clean Maps

    public void SendControlMessage(byte[] msg){
        channel.SendControlMessage(msg);
    }

    private void InitializeControlChannelListener(){
        channel.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(byte[] msg) {
                if(controlMessageParser.GetMessageType(new String(msg)) == ControlMessageParser.TYPE_STORED){
                    System.out.println("Controlo recebido tipo STORED");
                    StoredMessage storedMessage;
                    if((storedMessage = controlMessageParser.ParseStoredMessage(new String(msg))) != null){
                        List<Integer> peers = storedMap.get(storedMessage.getFileId() + "_" + storedMessage.getChunkNumber());
                        if(peers == null){
                            peers = new ArrayList<Integer>();
                        }
                        peers.add(storedMessage.getPeerId());
                        storedMap.put(storedMessage.getFileId() + "_" + storedMessage.getChunkNumber(), peers);
                    }
                    else{
                        System.out.println("Controlo recebido tipo STORED");
                        System.out.println("Erro a processar mensagem tipo STORED");
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
        System.out.print("key: " + key);
        if(storedMap.containsKey(key)) {
            System.out.println(" found " + storedMap.get(key).size() + " times");
            if (storedMap.get(key).size() >= repDeg)
                return true;
        }
        System.out.println(" not found");
        return false;
    }
}
