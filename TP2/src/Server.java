import java.io.*;

/**
 * Classe que implementa um Servidor
 */
public class Server {
    public static boolean EXIT = false;
    
    /**
     * Método que inicaliza o Servidor
     * @param args Argumentos
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ParseConfigFile configFile = new ParseConfigFile(args[0]);
        
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