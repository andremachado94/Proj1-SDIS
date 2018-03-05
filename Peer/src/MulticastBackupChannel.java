/**
 * Created by andremachado on 02/03/2018.
 */
public class MulticastBackupChannel extends MulticastChannel{


    public MulticastBackupChannel(String ip, int port){
        super(ip, port);
        //ConnectToChannel();
    }

    public boolean SendBackupRequest(String msg){
        if(msg.length() > 0)
            return SendMessage(msg);
        else
            return false;
    }
}
