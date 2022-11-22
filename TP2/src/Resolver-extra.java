import java.net.*;
import java.io.*;

class ResolverServer{
    public static void main(String[] args) throws Exception {
        try{        
            // Create a socket to listen on port 1234
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Listening on port 1234");
            // Create a socket to communicate with the client
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");
            //get the input stream from the client
            InputStream is = clientSocket.getInputStream();
            //create a DataInputStream so we can read data from it.
            DataInputStream dis = new DataInputStream(is);
            //create a byte array to store the message
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            //read the message from the client
            dis.read(messageReceived);
            System.out.println("Message received from client");
            //decrypt dns message
            DNSmessage messageDecrypted = DNSmessage.decrypt(messageReceived);
            //get the name and type of value from the message
            String name = messageDecrypted.getName();
            String typeOfValue = messageDecrypted.getTypeOfValue();
            //create a new DNSmessage to send to the client
            DNSmessage message = new DNSmessage(messageDecrypted.getId(), messageDecrypted.getFlags(), 0, 0, 0, 0, name, typeOfValue, null, null, null);
            //send the message to the client
            System.out.println("Sending message to client");
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(message.toByteArray());
            System.out.println("Message sent to client");
            // Close the socket
            clientSocket.close();
            serverSocket.close();
        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}