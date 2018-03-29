import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andremachado on 16/03/2018.
 */
public class BackupInitializer extends Thread{

    private String ip;
    private int port;
    private int id;
    private MulticastBackupChannel channel;
    private  ControlModule controlModule;

    public BackupInitializer(String ip, int port, int id, ControlModule controlModule, MulticastBackupChannel channel){
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.channel = channel;
        this.controlModule = controlModule;
    }



    public void StartBackupRequest(String filePath, String version, int repDeg){
        Thread backupRequest = new Thread(() -> {
            //TODO Open file, slice it and get messages (String data)
            FileManager fm = new FileManager();
            ArrayList<byte[]> data = FileManager.SliceFile(filePath, fm.PreSize("backup", id));
            //TODO put msg in the right format - wont work like this

            ExecutorService executor = Executors.newFixedThreadPool(5);//creating a pool of 5 threads
            for (int i = 0; i < data.size(); i++) {
                byte[] msg = Chunk.GetPutChunkMessage(data.get(i), i, version, id, repDeg);
                System.out.println("Message sent has " + msg.length + " bytes");
                Runnable worker = new BackupSenderThread(msg, controlModule, channel);
                executor.execute(worker);
                try {
                    Thread.sleep((long) 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
            while (!executor.isTerminated()) {   }

            System.out.println("Finished all threads that were sending " + filePath);


        });

        backupRequest.start();
    }


}