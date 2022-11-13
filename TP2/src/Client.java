import java.net.*;
import java.util.random.RandomGenerator;
import java.io.*;

class Client{
    // The client program reciveis as arguments: hostaddress:port , NAME , TYPE VALUE , FLAG[optional] 
    public static void main(String[] args) throws Exception {
        try { 
            //split sting into host and port
            String[] hostPort = args[0].split(":");
            //create a socket to connect to the server
            Socket clientSocket = new Socket(hostPort[0], Integer.parseInt(hostPort[1]));
            System.out.println("Connected to server");
            //create DNSmessage
            //genertae random id between 0 and 65535
            RandomGenerator rand = RandomGenerator.getDefault();
            int id = rand.nextInt(0, 65535);
            //get flags from last args
            String[] flags = new String[args.length - 3];
            for(int i = 3, j =0 ; i < args.length ; i++ , j++){
                flags[j] = args[i];
            }
            DNSmessage message = new DNSmessage(id, flags, 0, 0, 0, 0, args[1], args[2], null, null, null);
            //System.out.println(message.toString());
            //send the message to the server
            System.out.println("Sending message to server");
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(message.toByteArray());
            System.out.println("Message sent to server");
            //receive message from server
            InputStream is = clientSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            //decrypt dns message
            DNSmessage messageDecrypted = DNSmessage.decrypt(messageReceived);
            dis.read(messageDecrypted.toByteArray());
            System.out.println("Message received from server");
            // Close the socket
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

}