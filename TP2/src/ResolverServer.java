import java.net.*;
import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import java.io.*;

class ResolverServer{
    public static void main(String[] args) throws Exception {
        try{    
            ParseConfigFile conf = new ParseConfigFile(args[0]);
            System.out.println("Config file parsed " + args[0]);  
            Map<String, ParseDBFile> db = new HashMap<String, ParseDBFile>();
            for(String dbFile : conf.getDatabase())
                db.put(dbFile, new ParseDBFile("../files/SP/" + dbFile));
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Listening on port 1234");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");
            InputStream is = clientSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            dis.read(messageReceived);
            System.out.println("Message received from client");
            DNSmessage message = new DNSmessage(messageReceived);
            //System.out.println(message.toString());
            System.out.println(message.getId());
            String[] name = (message.getName()).split("\\.");
            DNSmessage response = null;
            String dbName = name[name.length-2] + "." + name[name.length-1];
            if(db.containsKey(dbName)){
                String ip = db.get(dbName).getFromDB(name[0]);
                if(ip == null){
                    response = new DNSmessage(message.getId(), message.getFlags(), 1, 0, 0, 0, message.getName(), message.getTypeOfValue(), null, null, null);
                }
                else response = new DNSmessage(message.getId(), message.getFlags(), 0, 0, 1, 0, message.getName(), message.getTypeOfValue(), null,null, ip);
            }else{
                response = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
            }
            //System.out.println(message.getId());
            //send the message to the client
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(response.toByteArray());
            System.out.println("Message sent to client");
            clientSocket.close();
            serverSocket.close();
        }catch(Exception e){
            System.out.println("Error: " + e );
        }
    }
}