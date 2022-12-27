import java.io.*;
import java.net.DatagramSocket;

/**
 * Classe que implementa um Servidor
 */
public class Server {
    public static boolean EXIT = false;
    public static boolean DEBUG = false;
    
    /**
     * Método que inicaliza o Servidor
     * @param args Argumentos
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ParseConfigFile configFile = new ParseConfigFile(args[0]);
        DatagramSocket ds = new DatagramSocket(8888);
        DNSmessageQueue queue = new DNSmessageQueue();
        if(args[2] != null && args[1].equals("-d")) DEBUG = true;
        
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

        if(configFile.getss() != null){
            Thread primaryServer = new Thread();
            primaryServer.start();
        }

        Thread receiver = new Thread();
        Thread sender = new Thread(new Sender(ds, queue, configFile.getdbList()));
        receiver.start();
        sender.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String query = reader.readLine();

        while(!query.equals("exit")){
            query = reader.readLine();
        }

        System.out.println("Pedido de encerramento enviado\n");

        Thread.sleep(1000);
        System.exit(0);
    }        
}