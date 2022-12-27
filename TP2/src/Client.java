import java.net.*;
import java.util.*;
import java.io.*;

class Client{
    // The client program reciveis as arguments: hostaddress:port , NAME , TYPE VALUE , FLAG[optional] 
    public static void main(String[] args) throws Exception {
        try { 
            String[] hostPort = args[0].split(":");
            Socket clientSocket = new Socket(hostPort[0], Integer.parseInt(hostPort[1]));
            System.out.println("Connected to server");
            //generate random number
            Random rand = new Random();
            int id = rand.nextInt(65535);
            int[] flags = new int[3];
            for(int i = 2; i < args.length ; i++){
                if(args[i].equals("Q")) flags[0] = 1;
                if(args[i].equals("R")) flags[1] = 1;
                if(args[i].equals("A")) flags[2] = 1;
            } 
            //System.out.println(flags[2]);
            DNSmessage message = new DNSmessage(id, flags, 0, 0, 0, 0, args[1], args[2], null, null, null);
            //System.out.println(message.getId());
            System.out.println("Sending message to server");
            OutputStream os = clientSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(message.toByteArray());
            //System.out.println(message.toByteArray()[0]);
            System.out.println("Message sent to server");
            //receive message from server
            InputStream is = clientSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            //decrypt dns message
            dis.read(messageReceived);
            DNSmessage response = new DNSmessage(messageReceived);
            System.out.println("Message received from server");
            //System.out.println(response);
            // Close the socket
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);

        // modo debug
        if(args[0].equals("-d")){
            try { 
                String[] hostPort = args[1].split(":");
                DatagramSocket clientSocket = new DatagramSocket();
                System.out.println("Connected to server");
                RandomGenerator rand = RandomGenerator.getDefault();
                int id = rand.nextInt(0, 65535);
                int[] flags = new int[3];
                for(int i = 2; i < args.length ; i++){
                    if(args[i].equals("Q")) flags[0] = 1;
                    if(args[i].equals("R")) flags[1] = 1;
                    if(args[i].equals("A")) flags[2] = 1;
                } 
                DNSmessage message = new DNSmessage(id, flags, 0, 0, 0, 0, args[2], args[3], null, null, null);
                System.out.println("Sending message to server");
                //send message to server
                byte[] messageToSend = message.toByteArray();
                //System.out.println(messageToSend);
                DatagramPacket datagramPacket = new DatagramPacket(messageToSend, messageToSend.length, InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]));
                clientSocket.send(datagramPacket);
                System.out.println("Message sent to server");
                
                //receive message from server 
                System.out.println("Receiving message from server");
                byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
                datagramPacket = new DatagramPacket(messageReceived, messageReceived.length, InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]));
                clientSocket.receive(datagramPacket);
                DNSmessage response = new DNSmessage(messageReceived);
                System.out.println(response);
                System.out.println("Message received from server");
                //System.out.println(response);
                //close socket
                clientSocket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }else{
            // normal mode encrypt and decrypt messages
            try { 
                String[] hostPort = args[0].split(":");
                DatagramSocket clientSocket = new DatagramSocket();
                System.out.println("Connected to server");
                RandomGenerator rand = RandomGenerator.getDefault();
                int id = rand.nextInt(0, 65535);
                int[] flags = new int[3];
                for(int i = 2; i < args.length ; i++){
                    if(args[i].equals("Q")) flags[0] = 1;
                    if(args[i].equals("R")) flags[1] = 1;
                    if(args[i].equals("A")) flags[2] = 1;
                } 
                //System.out.println(flags[2]);
                DNSmessage message = new DNSmessage(id, flags, 0, 0, 0, 0, args[1], args[2], null, null, null);
                //System.out.println(message.getId());
                System.out.println("Sending message to server");
                //send message to server
                byte[] messageToSend = DNSmessage.encrypt(message.toByteArray());
                DatagramPacket datagramPacket = new DatagramPacket(messageToSend, messageToSend.length, InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]));
                clientSocket.send(datagramPacket);
                System.out.println("Message sent to server");
                //receive message from server
                byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
                datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
                clientSocket.receive(datagramPacket);
                DNSmessage response = new DNSmessage(messageReceived);
                //decrypt message
                DNSmessage.decrypt(response.toByteArray());
                System.out.println("Message received from server");
                System.out.println(response);
                // Close the socket
                clientSocket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
}