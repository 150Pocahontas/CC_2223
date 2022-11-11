import java.net.*;

class ResolverServer{
    public static void main(String[] args) throws Exception {
        // Create a socket to listen on port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Listening on port 1234");
        // Create a socket to communicate with the client
        Socket clientSocket = serverSocket.accept();
        System.out.println("Connected to client");
        // Close the socket
        clientSocket.close();
        serverSocket.close();
    }
}