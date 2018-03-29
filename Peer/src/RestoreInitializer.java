import java.util.ArrayList;
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

    public RestoreInitializer(String ip, int port, int id, ControlModule controlModule, MulticastRestoreChannel channel) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.controlModule = controlModule;
        this.channel = channel;
    }


    public void StartRestoreRequest(String fileName, String version) {

        String fileId = fileName; //TODO

        Thread restoreRequest = new Thread(() -> {

            ExecutorService executor = Executors.newFixedThreadPool(5);//creating a pool of 5 threads
            //TODO Change 3 to 1000000
            for (int i = 0; i < 3; i++) {


                //TODO
                controlModule.SendRestoreRequest(fileId, version, i);


                try {
                    Thread.sleep((long) 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
            while (!executor.isTerminated()) {   }

            System.out.println("Finished all threads requesting " + fileName);


        });

        restoreRequest.start();
    }
}
