import java.net.*;
import java.util.List;
import java.util.Map;


public class Sender implements Runnable {

    private DatagramSocket datagramSocket;
    private DNSmessageQueue queue;
    private List<Pair> dbList;

    public Sender(DatagramSocket ds, DNSmessageQueue queue, List<Pair> dbList){
        this.datagramSocket = ds;
        this.queue = queue;
        this.dbList = dbList;
    }
    
    public void run() {
        while(!Server.EXIT || !queue.isEmpty()){
            try {
                DNSmessage message = queue.remove();
                byte[] messageReceived =message.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
                 
                String[] name = (message.getName()).split("\\.");
                DNSmessage response = null;
                String dbName = name[name.length-2] + "." + name[name.length-1];
                String type = message.getTypeOfValue();
                if(db.containsKey(dbName)){
                    response = new DNSmessage(message.getId(), message.getFlags(), 0, db.get(dbName).getNumRV(type), db.get(dbName).getNumAV(type), db.get(dbName).getNumExtra(), message.getName(), message.getTypeOfValue(), db.get(dbName).getResponseValues(type) ,db.get(dbName).getAuthoritativeValues(type), db.get(dbName).getExtraValues());
                }else{
                    response = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                }   
                //System.out.println(response);
                byte[] messageEncrypted = DNSmessage.encrypt(response.toByteArray());
                //send the message to the client
                System.out.println("Sending message to client");
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                datagramPacket = new DatagramPacket(messageEncrypted, messageEncrypted.length, clientAddress, clientPort);
                datagramSocket.send(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
