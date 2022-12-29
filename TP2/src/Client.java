import java.net.*;
import java.util.*;
import java.io.*;

class Client{

    private static boolean DEBUG = false;
    private static int index = 0;
    // The client program reciveis as arguments: modo, hostaddress:port , NAME , TYPE VALUE , FLAG[optional] , more values
    public static void main(String[] args) throws Exception {
        if(args[0].equals("-d")){
            DEBUG = true;
            index = 1;
        }
        try { 
            String[] hostPort = args[index].split(":");
            DatagramSocket clientSocket = new DatagramSocket();
            System.out.println("Connected to server");
            Random rand = new Random();
            int id = rand.nextInt(65535);
            int[] flags = new int[3];
            flags[0] = 1;
            flags[1] = 0;
            flags[2] = 1; 
            DNSmessage message = new DNSmessage(id, flags, 0, 0, 0, 0, args[1 +index], args[2 + index], null, null, null);
            System.out.println("Sending message to server");
            //send message to server
            byte[] messageToSend = message.toByteArray();
            //System.out.println(messageToSend);
            DatagramPacket datagramPacket = new DatagramPacket(messageToSend, messageToSend.length, InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]));
            clientSocket.send(datagramPacket);
            if(DEBUG){
                System.out.println("[Sent]: \n "+ message.toStringDebug());
            }
            else {
                System.out.println("[Sent]: \n "+ message);
            }
            //receive message from server 
            System.out.println("Receiving message from server");
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            datagramPacket = new DatagramPacket(messageReceived, messageReceived.length, InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]));
            clientSocket.receive(datagramPacket);
            message = new DNSmessage(messageReceived);
            if(DEBUG){
                System.out.println("[Received]: \n "+ message.toStringDebug());
            }
            else {
                System.out.println("[Received]: \n "+ message);
            }
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}