import java.net.*;
import java.io.*;

class Client{
    public static void main(String[] args) throws Exception {
        // Create a socket to connect to the server
        Socket clientSocket = new Socket("10.2.2.1", 1234);
        System.out.println("Connected to server");
        //get input from user   
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter a message: ");
        String message = br.readLine();
        //send the message to the server
        OutputStream os = clientSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(message);    
        // Close the socket
        clientSocket.close();
    }
}