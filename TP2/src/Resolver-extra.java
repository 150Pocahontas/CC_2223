import java.net.*;
import java.util.*;
import java.io.*;

class ResolverServer{
    public static void main(String[] args) throws Exception {
        try{    
            ParseConfigFile conf = new ParseConfigFile(args[1]);
            System.out.println("Config file parsed " + args[1]);  
            //parse database file from each batabase
            Map<String, ParseDBFile> db = new HashMap<String, ParseDBFile>();
            for(String dbFile : conf.getDatabase()){
                db.put(dbFile, new ParseDBFile(dbFile));
                System.out.println("Database file parsed " + dbFile);
            } 
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Listening on port 1234");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");
            InputStream is = clientSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            dis.read(messageReceived);
            System.out.println("Message received from client" + Arrays.toString(messageReceived));
            DNSmessage message = new DNSmessage(messageReceived);
            String[] name = message.getName().split(".");
            String nameToSearch = name[name.length-2] + "." + name[name.length-1];
            //verify if key is in database
            DNSmessage response = null;
            if(db.containsKey(nameToSearch)){
                String ip = db.get(nameToSearch).getFromDB(message.getName(), message.getTypeOfValue());
                if(ip.equals(null)){
                    response = new DNSmessage(message.getId(), message.getFlags(), 1, 0, 0, 0, message.getName(), message.getTypeOfValue(), null, null, null);
                }
                response = new DNSmessage(message.getId(), message.getFlags(), 0, 0, 1, 0, message.getName(), message.getTypeOfValue(), null,null, ip);
            }else{
                response = new DNSmessage(message.getId(), message.getFlags(), 2, 0, 0, 0, message.getName(), message.getTypeOfValue(), null,null, null);
            }
             //send the message to the client
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(response.toByteArray());
            System.out.println("Message sent to client" + Arrays.toString(response.toByteArray()));
            clientSocket.close();
            serverSocket.close();
        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}