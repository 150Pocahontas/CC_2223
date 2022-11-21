import java.net.*;
import java.io.*;

class ResolverServer{

    String STfile;
    String defaultServer;
    ArrayList<InetAddress> topServers = new ArrayList<InetAddress>();
    String logFile;
    
    public static void main(String[] args) throws Exception {
        //parser
        Parser parser = new Parser(args[1]);
        //resolver
        Resolver resolver = new Resolver(parser.getDatabase(), parser.getServers(), parser.getDefault(), parser.getLogFile(), parser.getSTFile());
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
}