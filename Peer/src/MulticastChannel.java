import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Created by andremachado on 02/03/2018.
 */
public abstract class MulticastChannel extends Thread{
    private String ip;
    private int port;
    private InetAddress group_address;
    private MulticastSocket r_socket;
    private MulticastSocket s_socket;

    private OnMessageReceivedListener onMessageReceivedListener;


    public MulticastChannel(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    protected void ConnectToChannel(String channelType){
        try {
            group_address = InetAddress.getByName(Util.IPV4_Validator(ip));

            try {
                r_socket = new MulticastSocket(port);
                r_socket.joinGroup(group_address);

                s_socket = new MulticastSocket();
                s_socket.setTimeToLive(1);

                System.out.println(channelType + " connected to:\n\tPort: " + port + "\n\tAddr: " + Util.IPV4_Validator(ip) + "\n\tG_Addr: " + group_address.getHostAddress() + "\n");

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

        try {
            String message = msg;

            byte[] sendData = message.getBytes();
            InetAddress addr = InetAddress.getByName(ip);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);


            s_socket.send(sendPacket);

            return true;


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    protected boolean SendMessage(byte[] msg) {

        try {

            InetAddress addr = InetAddress.getByName(ip);
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, addr, port);


            s_socket.send(sendPacket);
            System.out.println("Sent " + msg.length + " bytes");
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
        byte[] buffer = new byte[65000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        byte[] data;

        while (true) {
            try {
                r_socket.receive(packet);
                data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                System.out.println("Received before " + data.length + " bytes");
                onMessageReceivedListener.OnMessageReceived(data);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
