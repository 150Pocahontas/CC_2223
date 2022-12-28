import java.util.*;
import java.net.*;

public class ResponseServer implements Runnable {
    private DatagramPacket datagramPacket;
    private byte[] dnsMessage;
    private List<Pair> dbList;

    

    //cosntructor
    public ResponseServer(DatagramPacket datagramPacket, byte[] dnsMessage ,List<Pair> dbList) {
        this.datagramPacket = datagramPacket;
        this.dnsMessage = dnsMessage;
        this.dbList = dbList;
    }

    public void run() {
           
        byte[] messageDecrypted = DNSmessage.decrypt(dnsMessage);
        DNSmessage message = new DNSmessage(messageDecrypted);

        String[] name = (message.getName()).split("\\.");
        DNSmessage response = null;
        String dbName = name[name.length-2] + "." + name[name.length-1];
        String type = message.getTypeOfValue();
        if(db.containsKey(dbName)){
            response = new DNSmessage(message.getId(), message.getFlags(), 0, db.get(dbName).getNumRV(type), db.get(dbName).getNumAV(type), db.get(dbName).getNumExtra(), message.getName(), message.getTypeOfValue(), db.get(dbName).getResponseValues(type) ,db.get(dbName).getAuthoritativeValues(type), db.get(dbName).getExtraValues());
        }else{
                    response = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                }

            byte[] messageEncrypted = DNSmessage.encrypt(dnsMessage.toByteArray());
                //send the message to the client
                System.out.println("Sending message to client");
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                datagramPacket = new DatagramPacket(messageEncrypted, messageEncrypted.length, clientAddress, clientPort);
                datagramSocket.send(datagramPacket);
            
            System.out.println("Message sent to client");
    }
}
