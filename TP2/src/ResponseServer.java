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
           
        byte[] messageDecrypted;
        try {
            
            messageDecrypted = DNSmessage.decrypt(dnsMessage);
            DNSmessage message = new DNSmessage(messageDecrypted);
            String name[] = (message.getName()).split("\\.");
            DNSmessage response = null;
            String dbName = name[name.length-2] + "." + name[name.length-1];
            String type = message.getTypeOfValue();
            if(Server.getBDName(dbList, message.getName()) != null){
                ParseDBFile db = new ParseDBFile(Server.getBDName(dbList, message.getName()));
                db.parseFile();
                response = new DNSmessage(message.getId(), message.getFlags(), 0, db.getNumRV(type), db.getNumAV(type), db.getNumExtra(), message.getName(), message.getTypeOfValue(), db.getResponseValues(type) ,db.getAuthoritativeValues(type), db.getExtraValues());
            }else{
                response = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
            }
             // encrypt message
            byte[] messageEncrypted = DNSmessage.encrypt(response.toByteArray());
             //send the message to the client
            System.out.println("Sending message to client");
            InetAddress clientAddress = datagramPacket.getAddress();
            int clientPort = datagramPacket.getPort();
            datagramPacket = new DatagramPacket(messageEncrypted, messageEncrypted.length, clientAddress, clientPort);
            DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
            datagramSocket.send(datagramPacket);
            System.out.println("Message sent to client");

            

        } catch (Exception e) {
            e.printStackTrace();
        }
        

        
        
    }
}
