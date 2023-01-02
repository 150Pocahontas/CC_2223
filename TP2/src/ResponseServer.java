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
        try {
            DNSmessage message = new DNSmessage(dnsMessage);
            if(Server.DEBUG){
                System.out.println("[Received]: \n "+ message.toStringDebug());
            }
            else {
                System.out.println("[Received]: \n "+ message);
            }
            if(Server.cache.findEntry(Server.cacheList, Server.cacheList.size(), message.getName(),message.getTypeOfValue()) < Server.cacheList.size()){
                System.out.println("entry found in cache");
                DNSmessage response = new DNSmessage(message.getId(), message.getFlags(), message.getResponseCode(), message.getNumberOfExtra(), message.getNumberOfAuthorities(), message.getNumberOfExtra(), message.getName(), message.getTypeOfValue(), Server.cache.getAnswerList(Server.cacheList, Server.cacheList.size(), message.getName(),message.getTypeOfValue()),null , null);
                dnsMessage = response.toByteArray();
                System.out.println("Sending message to client");
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                datagramPacket = new DatagramPacket(dnsMessage, dnsMessage.length, clientAddress, clientPort);
                DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
                datagramSocket.send(datagramPacket);
                if(Server.DEBUG){
                    System.out.println("[Sent]: \n "+ response.toStringDebug());
                }
                else {
                    System.out.println("[Sent]: \n "+ response);
                }
            }else{
                System.out.println("entry not found in cache");
                DNSmessage response = null;
                String type = message.getTypeOfValue();
                int[] flags = new int[3];
                flags[0] = 0;
                flags[1] = 1;
                flags[2] = 0; 
                if(Server.getBDName(dbList, message.getName()) != null){
                    ParseDBFile db = new ParseDBFile(Server.getBDName(dbList, message.getName()));
                    db.parseFile();
                    response = new DNSmessage(message.getId(), flags, 0, db.getNumRV(type), db.getNumNS(), db.getNumExtra(), message.getName(), message.getTypeOfValue(), db.getResponseValues(type) ,db.getExtraValues(), db.getExtraValues());
                }else{
                    response = new DNSmessage(message.getId(), flags, 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                }
                // encrypt message
                dnsMessage = response.toByteArray();
                //send the message to the client
                System.out.println("Sending message to client");
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                datagramPacket = new DatagramPacket(dnsMessage, dnsMessage.length, clientAddress, clientPort);
                DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
                datagramSocket.send(datagramPacket);
                if(Server.DEBUG){
                    System.out.println("[Sent]: \n "+ response.toStringDebug());
                }
                else {
                    System.out.println("[Sent]: \n "+ response);
                }
                System.out.println("Message sent to client");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        
        
    }
}