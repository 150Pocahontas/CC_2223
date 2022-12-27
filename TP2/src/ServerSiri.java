import java.net.*;
import java.util.*;

import javax.xml.transform.Source;

import java.io.*;

class ServerSiri{
    public static void main(String[] args) throws Exception {
        // modo debug
        if(args[0].equals("-d")){
            try{    
                ParseConfigFile conf = new ParseConfigFile(args[1]);
                System.out.println("Config file parsed " + args[1]);  
                Map<String, ParseDBFile> db = new HashMap<String, ParseDBFile>();
                for(String dbFile : conf.getDatabase())
                    db.put(dbFile, new ParseDBFile("../files/SP/" + dbFile));
                DatagramSocket datagramSocket = new DatagramSocket(1234);
                System.out.println("Listening on port 1234");
                byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
                DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
                datagramSocket.receive(datagramPacket);
                System.out.println("Message received from client");
                DNSmessage message = new DNSmessage(messageReceived);
                String[] name = (message.getName()).split("\\.");
                DNSmessage response = null;
                String dbName = name[name.length-2] + "." + name[name.length-1];
                String type = message.getTypeOfValue();
                //System.out.println(type);
                if(db.containsKey(dbName)){
                    response = new DNSmessage(message.getId(), message.getFlags(), 0, db.get(dbName).getNumRV(type), db.get(dbName).getNumAV(type), db.get(dbName).getNumExtra(), message.getName(), message.getTypeOfValue(), db.get(dbName).getResponseValues(type) ,db.get(dbName).getAuthoritativeValues(type), db.get(dbName).getExtraValues());
                }else{
                    response = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
                }   
                //System.out.println(response);
                byte[] messageToSend = response.toByteArray();
                System.out.println("Sending message to client");
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                datagramPacket = new DatagramPacket(messageToSend, messageToSend.length, clientAddress, clientPort);
                datagramSocket.send(datagramPacket);
                System.out.println(response);
                System.out.println("Message sent to client");
                datagramSocket.close();

            }catch(Exception e){
                System.out.println("Error: " + e );
            }
        }else{
            try{    
                ParseConfigFile conf = new ParseConfigFile(args[0]);
                System.out.println("Config file parsed " + args[0]);  
                Map<String, ParseDBFile> db = new HashMap<String, ParseDBFile>();
                for(String dbFile : conf.getDatabase())
                    db.put(dbFile, new ParseDBFile("../files/SP/" + dbFile));
                DatagramSocket datagramSocket = new DatagramSocket(1234);
                System.out.println("Listening on port 1234");
                // receive encrypted message from client
                byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
                DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
                datagramSocket.receive(datagramPacket);
                System.out.println("Message received from client");
                // decrypt message
                byte[] messageDecrypted = DNSmessage.decrypt(messageReceived);
                //System.out.println(messageDecrypted);
                DNSmessage message = new DNSmessage(messageDecrypted);
                //System.out.println(message);
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
                // encrypt message
                byte[] messageEncrypted = DNSmessage.encrypt(response.toByteArray());
                //send the message to the client
                System.out.println("Sending message to client");
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                datagramPacket = new DatagramPacket(messageEncrypted, messageEncrypted.length, clientAddress, clientPort);
                datagramSocket.send(datagramPacket);
                System.out.println("Message sent to client");
                datagramSocket.close();

            }catch(Exception e){
                System.out.println("Error: " + e );
            }
        }

    }
}