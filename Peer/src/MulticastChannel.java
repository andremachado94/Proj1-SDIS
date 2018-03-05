import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by andremachado on 02/03/2018.
 */
public abstract class MulticastChannel implements Runnable{
    protected String ip;
    protected int port;
    protected InetAddress group_address;
    protected MulticastSocket m_socket;

    private OnMessageReceivedListener onMessageReceivedListener;


    public MulticastChannel(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    protected void ConnectToChannel(){
        try {
            group_address = InetAddress.getByName(ip);

            try {
                System.out.println("Connected to:\n\tPort: " + port + "\n\tAddr: " + ip + "\n\tG_Addr: " + group_address.getHostAddress());
                m_socket = new MulticastSocket(port);
                m_socket.joinGroup(group_address);

            } catch (IOException e) {
                System.out.println("Failed to connect to " + group_address.getHostAddress());
                e.printStackTrace();
            }

        } catch (UnknownHostException e) {
            System.out.println("Failed to resolve to " + ip + " group address.");
            e.printStackTrace();
        }
    }

    protected boolean SendMessage(String msg) {

        MulticastSocket m_socket = null;
        try {
            m_socket = new MulticastSocket();
            m_socket.setTimeToLive(1);

            String message = msg;

            byte[] sendData = message.getBytes();
            InetAddress addr = InetAddress.getByName(ip);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);

            m_socket.send(sendPacket);

            m_socket.close();

            return true;


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void SetOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener){
        this.onMessageReceivedListener=onMessageReceivedListener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //TODO Add receive from multi-group method
                Thread.sleep(3 * 1000); // sleep for 3 seconds and pretend to be working
                onMessageReceivedListener.OnMessageReceived("Hello"); //TODO Replace "Hello" by the msg received
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
