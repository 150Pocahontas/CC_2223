import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.sql.Time;

/**
 * Classe que implementa um Servidor
 */
public class Server {
    public static boolean EXIT = false;
    public static boolean DEBUG = false;
    public static ParseConfigFile configFile;
    public static int index = 0;
    
    /**
     * Método que inicaliza o Servidor
     * @param args Argumentos
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // calculate the time in seconds since the server was started
        int start = (int) System.currentTimeMillis();
        List<Cache> cacheList = new ArrayList<>();
        int entry = ParseDBFile.getNumOfEntries();
        if(args.length == 0){
            System.out.println("No config file specified");
            System.exit(0);
        }
        
        if(args[index].equals("-d")){
            DEBUG = true;
            index = 1;
        } 
        configFile = new ParseConfigFile(args[index]);
        try (DatagramSocket ds = new DatagramSocket(8080)) {
            for(Pair log : configFile.getlogFile()){
                new WriteLog(log.getdomain(), log.getvalue());
            } 
            for(Pair ss: configFile.getps()){
                if(ss.getvalue().matches(".+:.+")){
                    String[] ipPort = ss.getvalue().split(":");
                    Thread secondaryServer = new Thread(new SecondaryServer(ss.getdomain(), ipPort[0], ipPort[1]));
                    secondaryServer.start();

                }else{ 
                    Thread secondaryServer = new Thread(new SecondaryServer(ss.getdomain(), ss.getvalue(), "8080"));
                    secondaryServer.start();
                }
                //Quando um SS arranca deve fazer uma transferência de zona dos SP respetivos e deve registar na sua cache todas as entradas recebidas do SP, utilizando repetidamente a função anterior com o campo Origin igual a SP.
                for(Pair db : configFile.getdbList()){
                    if(db.getdomain().equals(ss.getdomain())){
                        for (Cache cache : cacheList){
                            cache.registerEntry(cacheList, cache.getName(), cache.getType(), cache.getValue(), cache.getTTL(), "SP", entry);
                        }
                    }
                    else if(db.getvalue().equals("SOAEXPIRE")){
                        for (Cache cache : cacheList){
                            cache.updateCache(cacheList, cache.getName());
                        }
                    }
                }
            }

            if(configFile.getss() != null){ 
                Thread primaryServer = new Thread(new PrimaryServer(configFile.getss(),configFile.getdbList()));
                primaryServer.start();
                for(Pair db : configFile.getdbList()){
                    if(db.getdomain().equals(configFile.getss())){
                        for (Cache cache : cacheList){
                            cache.registerEntry(cacheList, cache.getName(), cache.getType(), cache.getValue(), cache.getTTL(), "FILE", entry);
                        }
                    }
                }
                
            }
            

            System.out.println("Server is running");
    
            while(!EXIT){
                byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
                DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
                System.out.println("Waiting for a socket");
                ds.receive(datagramPacket);
                Thread thread = new Thread(new ResponseServer(datagramPacket, messageReceived ,configFile.getdbList()));
                thread.start();
                int timeStamp = (int) System.currentTimeMillis() - start;
                InetAddress value = datagramPacket.getAddress();
                Cache cache = new Cache(DNSmessage.getName(), DNSmessage.getTypeOfValue(), value, ParseDBFile.getTTL(), "OTHERS", timeStamp, entry, "FREE");
                cacheList.add(cache);
                cache.findEntry(cacheList, cache.getIndex(), cache.getName(), cache.getType());
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
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