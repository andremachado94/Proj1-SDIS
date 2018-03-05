import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andremachado on 02/03/2018.
 */
public abstract class MulticastChannel extends Thread{
    protected String ip;
    protected int port;
    protected InetAddress group_address;
    protected MulticastSocket m_socket;
    protected MulticastSocket s_socket;

    private OnMessageReceivedListener onMessageReceivedListener;


    public MulticastChannel(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    protected void ConnectToChannel(){
        try {
            group_address = InetAddress.getByName(Util.IPV4_Validator(ip));

            try {
                m_socket = new MulticastSocket(port);
                m_socket.joinGroup(group_address);

                s_socket = new MulticastSocket();
                s_socket.setTimeToLive(1);

                System.out.println("Connected to:\n\tPort: " + port + "\n\tAddr: " + Util.IPV4_Validator(ip) + "\n\tG_Addr: " + group_address.getHostAddress());

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
            //s_socket.close();

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
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);



        while (true) {
            try {
                m_socket.receive(packet);
                String msg = new String(packet.getData()).trim();
                System.out.println("Message received (on run): " + msg);
                onMessageReceivedListener.OnMessageReceived(msg);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

/*
        while (true) {
            try {
                //TODO Add receive from multi-group method
                Thread.sleep(3 * 1000); // sleep for 3 seconds and pretend to be working
                onMessageReceivedListener.OnMessageReceived("Hello"); //TODO Replace "Hello" by the msg received
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
*/
    }
}
