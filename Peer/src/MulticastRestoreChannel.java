/**
 * Created by andremachado on 02/03/2018.
 */
public class MulticastRestoreChannel extends MulticastChannel{
    public MulticastRestoreChannel(String ip, int port){
        super(ip, port);
        ConnectToChannel("Restore Channel");
    }

    public boolean SendRestoreMessage(byte[] msg){
        if(msg.length > 0) {
            return SendMessage(msg);
        }
        else {
            return false;
        }
    }
}

