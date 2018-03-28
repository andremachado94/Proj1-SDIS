/**
 * Created by andremachado on 02/03/2018.
 */
public class MulticastControlChannel extends MulticastChannel{
    public MulticastControlChannel(String ip, int port){
        super(ip, port);
        ConnectToChannel("Control Channel");
    }

    public boolean SendControlMessage(byte[] msg){
        if(msg.length > 0) {
            return SendMessage(msg);
        }
        else {
            return false;
        }
    }
}
