import java.net.*;

class Client{
    public static void main(String[] args) throws Exception {
        // Create a socket to connect to the server
        Socket clientSocket = new Socket("localhost", 1234);
        System.out.println("Connected to server");
        // Close the socket
        clientSocket.close();
    }
}