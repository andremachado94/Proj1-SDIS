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



    public void StartBackupRequest(String filePath, String version, int repDeg, String fileName){
        Thread backupRequest = new Thread(() -> {
            FileManager fm = new FileManager();
            ArrayList<byte[]> data = FileManager.SliceFile(filePath, fm.PreSize("backup", id));
            //TODO put msg in the right format - wont work like this ??



            ExecutorService executor = Executors.newFixedThreadPool(5);//creating a pool of 5 threads
            for (int i = 0; i < data.size(); i++) {
                byte[] msg = PutChunk.GetPutChunkMessage(data.get(i), i, version, id, repDeg, fileName);
                Runnable worker = new BackupSenderThread(msg, i, fileName, repDeg, channel, controlModule);
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
