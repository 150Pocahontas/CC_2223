import java.io.*;
import java.util.List;
import java.net.*;

/**
 * Classe que implementa um Servidor
 */
public class Server {
    public static boolean EXIT = false;
    public static boolean DEBUG = false;
    public static int index = 0;
    
    /**
     * Método que inicaliza o Servidor
     * @param args Argumentos
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if(args[index].equals("-d")){
            DEBUG = true;
            index = 1;
        } 
        ParseConfigFile configFile = new ParseConfigFile(args[index]);
        DatagramSocket ds = new DatagramSocket(8080);
        
        for(Pair ss: configFile.getps()){
            if(ss.getvalue().matches(".+:.+")){
                String[] ipPort = ss.getvalue().split(":");
                Thread secondaryServer = new Thread(new SecondaryServer(ss.getdomain(), ipPort[0], ipPort[1]));
                secondaryServer.start();

            }else{ 
                Thread secondaryServer = new Thread(new SecondaryServer(ss.getdomain(), ss.getvalue(), "8080"));
                secondaryServer.start();
            }
        }

        if(configFile.getss() != null){ 
            Thread primaryServer = new Thread(new PrimaryServer(configFile.getss(),configFile.getdbList()));
            primaryServer.start();
        }
     
        while(!EXIT){
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
            System.out.println("Waiting for a socket");
            ds.receive(datagramPacket);
            Thread thread = new Thread(new ResponseServer(datagramPacket, messageReceived ,configFile.getdbList()));
            thread.start();
        }
        

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String query = reader.readLine();

        while(!query.equals("exit")){
            query = reader.readLine();
        }

        System.out.println("Pedido de encerramento enviado\n");

        Thread.sleep(1000);
        System.exit(0);
    }    
    
    
    public static String getBDName(List<Pair> dbList2, String domain){
        for(Pair p: dbList2){
            if(p.getdomain().equals(domain)){
                return p.getvalue();
            }
        }
        return null;
    }
}