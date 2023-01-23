import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;
import java.text.SimpleDateFormat;

/**
 * Classe que implementa um Servidor
 */
public class Server {
    public static boolean EXIT = false;
    public static boolean DEBUG = false;
    public static String type = "OTHER";
    public static ParseConfigFile configFile;
    public static int index = 0;
    public static int start = (int) System.currentTimeMillis();
    public static ArrayList<Cache> cacheList = new ArrayList<>();
    public static Cache cache = new Cache("FREE");
    public static DatagramSocket ds;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy.HH:mm:ss:SSS");

    /**
     * Método que inicaliza o Servidor
     * @param args Argumentos
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ds = new DatagramSocket(8080);
        cacheList.add(cache);

        if(args.length == 0){
            System.out.println("No config file specified");
            System.exit(0);
        }
        
        if(args[index].equals("-d")){
            DEBUG = true;
            index = 1;
        } 

        configFile = new ParseConfigFile(args[index]);

        for(Pair log : configFile.getlogFile()){
            WriteLog writeLog = new WriteLog(log.getvalue());
            String date = sdf.format(new Date());
            writeLog.write(date + " EV @ conf-file-read " + args[index]);
            writeLog.write(date + " EV @ log-file-create " + log.getvalue());
        } 
        if(configFile.getdbList().isEmpty()){
            type = "SR";
        }else{
            for(Pair p : configFile.getdbList()){
                ParseDBFile db = new ParseDBFile(p.getvalue());
                db.parseFile();
                //System.out.println(Server.getvalue(Server.configFile.getlogFile(), db.getDef()));
                WriteLog writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), db.getDef()));
                String date = Server.sdf.format(new Date());
                writeLog.write(date + " EV @ db-file-read " + db.getPathFile());
                writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                date = Server.sdf.format(new Date());
                writeLog.write(date + " EV @ db-file-read " + db.getPathFile());
                cache.addType(cacheList, db, "FILE");
            }    
        }
        if(!(configFile.getps().isEmpty())){
            type = "SP";
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
        }

        if(!(configFile.getss().isEmpty())){ 
            Thread primaryServer = new Thread(new PrimaryServer(configFile.getss()));
            primaryServer.start();
            type = "SP";
        }
        Executor executor = Executors.newFixedThreadPool(5); // Create a thread pool with 5 threads
        while(!EXIT){
            byte[] messageReceived = new byte[DNSmessage.MAX_SIZE_MESSAGE];
            DatagramPacket datagramPacket = new DatagramPacket(messageReceived, messageReceived.length);
            System.out.println("Waiting for a socket");
            ds.receive(datagramPacket);
            executor.execute(new ResponseServer(datagramPacket, messageReceived, configFile.getdbList()));
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String query = reader.readLine();

        while(!query.equals("exit")){
            query = reader.readLine();
        }
        ds.close();
        System.out.println("Pedido de encerramento enviado\n");
        Thread.sleep(1000);
        System.exit(0);
        
    }    
    
    
    
    public static String getvalue(List<Pair> list, String domain){
        for(Pair p: list){
            //System.out.println(p.getdomain() + " " + domain);
            if(p.getdomain().equals(domain)){
                return p.getvalue();
            }
        }
        return null;
    }
    
}