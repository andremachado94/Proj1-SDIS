import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andremachado on 28/03/2018.
 */
public class RestoreInitializer {

    private String ip;
    private int port;
    private int id;
    private ControlModule controlModule;
    private MulticastRestoreChannel channel;

    private ConcurrentHashMap<String,Integer> ongoingRestore = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,byte[]> restoredChunks = new ConcurrentHashMap<>();


    public RestoreInitializer(String ip, int port, int id, ControlModule controlModule, MulticastRestoreChannel channel) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.controlModule = controlModule;
        this.channel = channel;
    }


    public byte[] StartRestoreRequest(String fileName, String version) {

        String fileId = new String(Util.SHA256(fileName)); //TODO

        ongoingRestore.put(Util.GetCleanId(fileId), -1);
        System.out.println("Adding key: " + Util.GetCleanId(fileId));


        Thread restoreRequest = new Thread(() -> {

            ExecutorService executor = Executors.newFixedThreadPool(5);//creating a pool of 5 threads
            //TODO Change 3 to 1000000
            for (int i = 0; i < 1000000; i++) {

                System.out.println("Requester thread number " + i);
                //TODO
                controlModule.SendRestoreRequest(fileId, version, i);


                try {
                    Thread.sleep((long) 50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(ongoingRestore.get(Util.GetCleanId(fileId)) != -1){
                    return;
                }

            }
            executor.shutdown();
            while (!executor.isTerminated()) {   }

            System.out.println("Finished all threads requesting " + fileName);


        });

        restoreRequest.start();


        while (true) {
            try {
                Thread.sleep((long) 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(ongoingRestore.get(Util.GetCleanId(fileId)) != -1){
                System.out.println("SAVING FILE");

                return SaveFile(Util.GetCleanId(fileId), fileName);
            }
        }

    }



    public void FinishRequest(String fileId, int i){
        ongoingRestore.put(fileId, i);
    }

    public boolean FinishedRequest(String fileId){
        return ongoingRestore.get(fileId)!=-1;
    }

    public boolean ChunkExists(String key){
        return restoredChunks.containsKey(key);
    }

    public void AddChunk(String key, byte[] data){
        restoredChunks.put(key, data);
    }

    public void Notifier(GetChunkMessage getChunkMessage) {
        //TODO update some regestry table
    }


    public byte[] SaveFile(String fileId, String fileName){
        byte [] data = null;
        if(ongoingRestore.containsKey(fileId)){
            int chunks = ongoingRestore.get(fileId);

            for(int i = 0 ; i <= chunks ; i++){
                if(data == null){
                    data = restoredChunks.get(fileId + "_" + i);
                }
                else {
                    data = ByteConcat(data, restoredChunks.get(fileId + "_" + i));
                }
            }
        }
        return data;
    }

    private byte[] ByteConcat(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }


}
