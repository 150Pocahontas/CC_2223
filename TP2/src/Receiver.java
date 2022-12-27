import java.net.*;
import java.util.*;

public class Receiver {

    private DatagramSocket datagramSocket;
    private DNSmessageQueue queue;
    private List<Pair> dbList;

    public Receiver(DatagramSocket ds, DNSmessageQueue queue, List<Pair> dbList){
        this.datagramSocket = ds;
        this.queue = queue;
        this.dbList = dbList;
    }
    
    public void run() {
        while(!Server.EXIT){
            try {
                byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
                DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
                datagramSocket.receive(datagramPacket);
                System.out.println("Message received from client");

                DNSmessage message = new DNSmessage(messageReceived);
                queue.add(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

