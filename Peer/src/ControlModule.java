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

    public void SendControlMessage(String msg){
        channel.SendControlMessage(msg);
    }

    private void InitializeControlChannelListener(){
        channel.SetOnMessageReceivedListener(new OnMessageReceivedListener() {
            @Override
            public String OnMessageReceived(String msg) {
                if(controlMessageParser.GetMessageType(msg) == ControlMessageParser.TYPE_STORED){
                    System.out.println("Controlo recebido tipo STORED - " + msg);
                    StoredMessage storedMessage;
                    if((storedMessage = controlMessageParser.ParseStoredMessage(msg)) != null){
                        List<Integer> peers = storedMap.get(storedMessage.getFileId() + "_" + storedMessage.getChunkNumber());
                        if(peers == null){
                            peers = new ArrayList<Integer>();
                        }
                        peers.add(storedMessage.getPeerId());
                        storedMap.put(storedMessage.getFileId() + "_" + storedMessage.getChunkNumber(), peers);
                    }
                    else{
                        System.out.println("Controlo recebido tipo STORED - " + msg);
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
}
