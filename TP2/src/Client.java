import java.net.*;
import java.util.random.RandomGenerator;
import java.io.*;

class Client{
    // The client program reciveis as arguments: hostaddress:port , NAME , TYPE VALUE , FLAG[optional] 
    public static void main(String[] args) throws Exception {
        try { 
            String[] hostPort = args[0].split(":");
            Socket clientSocket = new Socket(hostPort[0], Integer.parseInt(hostPort[1]));
            System.out.println("Connected to server");
            RandomGenerator rand = RandomGenerator.getDefault();
            int id = rand.nextInt(0, 65535);
            int[] flags = new int[3];
            for(int i = 3; i < args.length ; i++){
                if(args[i] == "Q") flags[0] = 1;
                if(args[i] == "R") flags[1] = 1;
                if(args[i] == "A") flags[2] = 1;
            } 
            DNSmessage message = new DNSmessage(id, flags, 0, 0, 0, 0, args[1], args[2], null, null, null);
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
            dis.read(messageReceived);
            System.out.println("Message received from server");
            // Close the socket
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}