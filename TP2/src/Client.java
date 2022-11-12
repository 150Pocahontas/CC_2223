import java.net.*;
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
            DNSmessage message = new DNSmessage(0, new String[]{"0", "0", "0"}, 0, 0, 0, 0, args[1], args[2], null, null, null);
            //send the message to the server
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(message.toByteArray());
            System.out.println("Message sent to server");
            //receive message from server
            InputStream is = clientSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            dis.read(messageReceived);
            System.out.println("Message received from server");
            // Close the socket
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}