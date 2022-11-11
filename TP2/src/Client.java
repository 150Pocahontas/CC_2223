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
            //get input from user   
            String message = "query";
            //send the message to the server
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(message);    
            // Close the socket
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}