import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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



    public boolean StartBackupRequest(String filePath, String version, int repDeg, String fileName){


        final int[] size = new int[1];

        Thread backupRequest = new Thread(() -> {
            FileManager fm = new FileManager();
            ArrayList<byte[]> data = FileManager.SliceFile(filePath);

            size[0] = data.size();
            System.out.println("SENDING FILE: -" + fileName + "-");
/*
            ExecutorService executor = Executors.newFixedThreadPool(5000);//creating a pool of 5 threads
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


*/


            ExecutorService executor = Executors.newFixedThreadPool(500);//creating a pool of 5 threads

            for (int i = 0; i < data.size(); i++) {
                Runnable worker = new BackupSenderThread(PutChunk.GetPutChunkMessage(data.get(i), i, version, id, repDeg, fileName), i, fileName, repDeg, channel, controlModule);
                Future<?> runnableFuture = executor.submit(worker);
                try {
                    Thread.sleep((long) 70);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                executor.awaitTermination(5,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.shutdown();


            System.out.println("Finished all threads that were sending " + filePath);


        });

        backupRequest.start();

        try {
            backupRequest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0 ; i < size[0] ; i++){
            if(!controlModule.ReceivedStoredMessages(new String(Util.SHA256(fileName)), i, repDeg)) {
                return false;
            }
        }

        return true;
    }


}
