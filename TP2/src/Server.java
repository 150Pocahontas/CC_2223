import java.net.*;
import java.io.*;
import java.util.*;


//Server Primary and secundary (also SDT and ST)
class Server{
    
    public static void main(String[] args) throws IOException{
        //parser
        ParseConfigFile parser = new ParseConfigFile();
        parser.Parse(args[1]);
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
        //read the message from the client
        String message = dis.readUTF();
        System.out.println("Message received from client: ");
        // Close the socket
        clientSocket.close();
        serverSocket.close();
    }

    //contructer

}