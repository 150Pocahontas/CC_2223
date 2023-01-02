import java.util.*;
import java.io.*;
import java.net.*;

public class newServer implements Runnable {

    private DatagramPacket datagramPacket;
    private byte[] dnsMessage;
    private List<Pair> dbList;

    public newServer(DatagramPacket datagramPacket, byte[] dnsMessage ,List<Pair> dbList) {
        this.datagramPacket = datagramPacket;
        this.dnsMessage = dnsMessage;
        this.dbList = dbList;
    }

    //run
    public void run() {
        try {
            DNSmessage message = new DNSmessage(dnsMessage);
            DNSmessage response = null;
            String type = message.getTypeOfValue();
            if(Server.type == "SR"){
                WriteLog writeLog =  new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                String date = Server.sdf.format(new Date());
                writeLog.write(date + " QR  " + message.toStringDebug());
                //vê na cahe não tem contacta 
                if() {

                }else{
                    BufferedReader reader;
                    reader = new BufferedReader(new FileReader(Server.configFile.getrootFile()));
                    String ip = reader.readLine();
                    reader.close();
                    message = new DNSmessage(message.getId(), message.getFlags(), 0, 0, 0, 0, message.getName(),message.getTypeOfValue(), null, null, null);
                }

            }else if(Server.type == "SP"){
                if(cache){
                    
                }else{
                    response = null;
                    int[] flags = new int[3];
                    type = message.getTypeOfValue();
                    flags[0] = 0;
                    flags[1] = 1;
                    flags[2] = 0; 
                    if(Server.getvalue(dbList, message.getName()) != null){
                        ParseDBFile db = new ParseDBFile(Server.getvalue(dbList, message.getName()));
                        db.parseFile();
                        response = new DNSmessage(message.getId(), flags, 0, db.getNumRV(type), db.getNumNS(), db.getNumExtra(), message.getName(), message.getTypeOfValue(), db.getResponseValues(type) ,db.getAuthoritativeValues(type), db.getExtraValues());
                    }else{
                        response = new DNSmessage(message.getId(), flags, 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                    }
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
                }
            }else if(Server.type == "OTHER"){
                if(cache){
                    
                }else{
                    response = null;
                    if(Server.getvalue(dbList, message.getName()) != null){
                        ParseDBFile db = new ParseDBFile(Server.getvalue(dbList, message.getName()));
                        db.parseFile();
                        response = new DNSmessage(message.getId(), flags, 0, 0, db.getNumNS(), db.getNumExtra(), message.getName(), message.getTypeOfValue(),null ,db.getNSvalues(), db.getExtraValues());
                    }else{
                        response = new DNSmessage(message.getId(), flags, 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                    }
                    dnsMessage = response.toByteArray();
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
