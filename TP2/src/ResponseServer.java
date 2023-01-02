import java.util.*;
import java.io.*;
import java.net.*;

public class ResponseServer implements Runnable {

    private DatagramPacket datagramPacket;
    private byte[] dnsMessage;
    private List<Pair> dbList;

    public ResponseServer(DatagramPacket datagramPacket, byte[] dnsMessage ,List<Pair> dbList) {
        this.datagramPacket = datagramPacket;
        this.dnsMessage = dnsMessage;
        this.dbList = dbList;
    }

    //run
    public void run() {
        try {
            DNSmessage message = new DNSmessage(dnsMessage);
            String type = message.getTypeOfValue();
            WriteLog writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
            String date = Server.sdf.format(new Date());
            writeLog.write(date + " QR " + datagramPacket.getAddress() + " " + message.toStringDebug());
            if(Server.getvalue(Server.configFile.getlogFile(),message.getName()) !=null){
                writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), message.getName()));
                date = Server.sdf.format(new Date());
            }
            writeLog.write(date + " QR  " + message.toStringDebug());
            if(Server.type == "SR" || Server.type == "SP"){
                //vê na cahe não tem contacta 
                if(Server.cache.findEntry(Server.cacheList, message.getName(),message.getTypeOfValue()) < Server.cacheList.size()){
                    Cache c = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),message.getTypeOfValue()));
                    Cache aV = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),"NS"));
                    Cache eXV = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),"A"));
                    System.out.println("entry found in cache");
                    int[] flags = new int[3];
                    flags[0] = 0;
                    flags[1] = message.getFlags()[1];
                    flags[2] = 1;
                    message = new DNSmessage(message.getId(), flags, 0, c.getValue().size(),aV.getValue().size() , eXV.getValue().size(), message.getName(), message.getTypeOfValue(), c.getValue(),aV.getValue(), eXV.getValue());
                    dnsMessage = message.toByteArray();
                    System.out.println("Sending message to client");
                    InetAddress clientAddress = datagramPacket.getAddress();
                    int clientPort = datagramPacket.getPort();
                    datagramPacket = new DatagramPacket(dnsMessage, dnsMessage.length, clientAddress, clientPort);
                    DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
                    datagramSocket.send(datagramPacket);
                    if(Server.DEBUG){
                        System.out.println("[Sent]: \n "+ message.toStringDebug());
                    }
                    else {
                        System.out.println("[Sent]: \n "+ message);
                    }
                }else if(Server.type == "SR"){
                    BufferedReader reader;
                    reader = new BufferedReader(new FileReader(Server.configFile.getrootFile()));
                    String ip = reader.readLine();
                    reader.close();
                    message = new DNSmessage(message.getId(), message.getFlags(), 0, 0, 0, 0, message.getName(),message.getTypeOfValue(), null, null, null);
                }else if(Server.type == "SP"){
                    int[] flags = new int[3];
                    type = message.getTypeOfValue();
                    flags[0] = 0;
                    flags[1] = 1;
                    flags[2] = 0; 
                    if(Server.getvalue(dbList, message.getName()) != null){
                        ParseDBFile db = new ParseDBFile(Server.getvalue(dbList, message.getName()));
                        db.parseFile();
                        message = new DNSmessage(message.getId(), flags, 0, db.getNumRV(type), db.getNumNS(), db.getNumExtra(), message.getName(), message.getTypeOfValue(), db.getResponseValues(type) ,db.getNSvalues(), db.getExtraValues());
                    }else{
                        message = new DNSmessage(message.getId(), flags, 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                    }
                    dnsMessage = message.toByteArray();
                    System.out.println("Sending message to client");
                    InetAddress clientAddress = datagramPacket.getAddress();
                    int clientPort = datagramPacket.getPort();
                    datagramPacket = new DatagramPacket(dnsMessage, dnsMessage.length, clientAddress, clientPort);
                    DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
                    datagramSocket.send(datagramPacket);
                    if(Server.DEBUG){
                        System.out.println("[Sent]: \n "+ message.toStringDebug());
                    }
                    else {
                        System.out.println("[Sent]: \n "+ message);
                    }
                }

            }else if(Server.type == "OTHER"){
                if(Server.cache.findEntry(Server.cacheList, message.getName(),"NS") < Server.cacheList.size()){
                    Cache aV = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),"NS"));
                    Cache eXV = Server.cacheList.get(Server.cache.findEntry(Server.cacheList, message.getName(),"A"));
                    System.out.println("entry found in cache");
                    int[] flags = new int[3];
                    flags[0] = 0;
                    flags[1] = message.getFlags()[1];
                    flags[2] = 1;
                    message = new DNSmessage(message.getId(), flags, 0, 0,aV.getValue().size() , eXV.getValue().size(), message.getName(), message.getTypeOfValue(), null,aV.getValue(), eXV.getValue());
                    dnsMessage = message.toByteArray();
                    System.out.println("Sending message to client");
                    InetAddress clientAddress = datagramPacket.getAddress();
                    int clientPort = datagramPacket.getPort();
                    datagramPacket = new DatagramPacket(dnsMessage, dnsMessage.length, clientAddress, clientPort);
                    DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
                    datagramSocket.send(datagramPacket);
                    if(Server.DEBUG){
                        System.out.println("[Sent]: \n "+ message.toStringDebug());
                    }
                    else {
                        System.out.println("[Sent]: \n "+ message);
                    }
                }else{
                    if(Server.getvalue(dbList, message.getName()) != null){
                        ParseDBFile db = new ParseDBFile(Server.getvalue(dbList, message.getName()));
                        db.parseFile();
                        message = new DNSmessage(message.getId(), message.getFlags(), 0, 0, db.getNumNS(), db.getNumExtra(), message.getName(), message.getTypeOfValue(),null ,db.getNSvalues(), db.getExtraValues());
                    }else{
                        message = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                    }
                    dnsMessage = message.toByteArray();
                    InetAddress clientAddress = datagramPacket.getAddress();
                    int clientPort = datagramPacket.getPort();
                    datagramPacket = new DatagramPacket(dnsMessage, dnsMessage.length, clientAddress, clientPort);
                    DatagramSocket datagramSocket = new DatagramSocket(datagramPacket.getPort());
                    datagramSocket.send(datagramPacket);
                    if(Server.DEBUG){
                        System.out.println("[Sent]: \n "+ message.toStringDebug());
                    }
                    else {
                        System.out.println("[Sent]: \n "+ message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
