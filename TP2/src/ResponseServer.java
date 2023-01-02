import java.util.*;

import javax.xml.transform.Source;

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
            
            if(Server.cache.findEntry(Server.cacheList, message.getName(),message.getTypeOfValue()) < Server.cacheList.size()){
                Cache c = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),message.getTypeOfValue()));
                Cache aV = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),"NS"));
                Cache eXV = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),"A"));
                System.out.println("entry found in cache");
                int[] flags = new int[3];
                flags[0] = 0;
                flags[1] = message.getFlags()[1];
                flags[2] = 1;
                DNSmessage response = new DNSmessage(message.getId(), flags, 0, c.getValue().size(),aV.getValue().size() , eXV.getValue().size(), message.getName(), message.getTypeOfValue(), c.getValue(),aV.getValue(), eXV.getValue());
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
                System.out.println("Message sent to client");
                return;
            }
            else{
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
                    response = new DNSmessage(message.getId(), flags, 0, db.getNumRV(type), db.getNumNS(), db.getNumExtra(), message.getName(), message.getTypeOfValue(), db.getResponseValues(type) ,db.getAuthoritativeValues(type), db.getExtraValues());
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