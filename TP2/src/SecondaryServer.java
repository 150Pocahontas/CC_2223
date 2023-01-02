import java.io.*;
import java.net.*;
import java.util.*;

public class SecondaryServer implements Runnable{
    private String domain;
    private String ipPrimary;
    private int port;
    private ParseDBFile db;

    /** 
     * Contrutor do Servidor Secundário
     * @throws IOException
    */
    public SecondaryServer(String domain, String ip, String port) throws IOException{
        this.domain = domain;
        this.ipPrimary = ip;
        this.port =  Integer.parseInt(port);
    }

    public void run() {   
        try {
            while(!Server.EXIT){
                String query;
                if(db != null){
                    db.parseFile();
                    InetAddress ip = InetAddress.getLocalHost();
                    String ipLocal = ip.getHostAddress();
                    query = "SOA " + domain + " " + ipLocal + " " + port + " " + db.getSOASERIAL();
                    System.out.println();
                } else{
                    InetAddress ip = InetAddress.getLocalHost();
                    String ipLocal = ip.getHostAddress();
                    this.db = new ParseDBFile("../files/Servidor2/" + domain + "_" + ipLocal);
                    query = "SOA " + domain + " " + ipLocal + " " + port + " " + 0;
                }
                try {
                    Socket socket = new Socket(ipPrimary,port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(query);
                    WriteLog writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), domain));
                    String date = Server.sdf.format(new Date());
                    writeLog.write(date + " ZT iniciada" + ipPrimary + " SP " + db.getPathFile());
                    writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                    date = Server.sdf.format(new Date());
                    writeLog.write(date + " ZT ininiada" + ipPrimary + " SP " + db.getPathFile());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = in.readLine();
                    if(response.equals("Serial number is the same")){
                    System.out.println("[Menssagem recebida] Serial number is the same");
                    }else{
                        System.out.println("[Menssagem recebida] " + response);
                        String[] responseSplit = response.split(" ");
                        int numOfEntries = Integer.parseInt(responseSplit[2]);
                        query = "OK: " + numOfEntries;
                        out = new PrintWriter(socket.getOutputStream(), true);
                        System.out.println("[Menssagem enviada] " + query);
                        out.println(query);
                        db.clearListOfEntries();
                        while ((response = in.readLine()) != null){
                            String[] responseSplit2 = response.split(": ");
                            db.addEntry(responseSplit2[1] + "\n");
                        }
                        writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), domain));
                        date = Server.sdf.format(new Date());
                        writeLog.write(date + " ZT terminada" + ipPrimary + " SP " + db.getPathFile());
                        writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                        date = Server.sdf.format(new Date());
                        writeLog.write(date + " ZT terminada" + ipPrimary + " SP " + db.getPathFile());
                        db.rewriteFile(db.getPathFile());
                        Thread thread = new Thread(removebd(db.toInt(db.getSOAEXPIRE())));
                        thread.start();
                        Server.configFile.addDbFile(new Pair(domain,db.getPathFile()));
                        Server.cache.registerEntry(Server.cacheList, db.getDef(),"MX",db.getMXvalues(), db.getTTL(), "SP", Server.index, Server.cache.getStatus());
                        Server.cache.registerEntry(Server.cacheList, db.getDef(),"NS", db.getNSvalues(), db.getTTL(), "SP", Server.index, Server.cache.getStatus());
                    }
                    socket.close();
                    Thread.sleep(db.toInt(db.getSOAREFRESH()));
                } catch (Exception e) {
                    System.out.println("[Menssagem recebida] " + e.getMessage());
                    Thread.sleep(db.toInt(db.getSOARETRY()));
                }
                
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) { 
            e.printStackTrace();
        } 
    }
    
    public Runnable removebd(int time) throws InterruptedException{
        Thread.sleep(time);
        Server.configFile.removeDbFile(new Pair(domain,db.getPathFile()));
        Server.cache.freeCache(Server.cacheList, domain);
        return null;
    }
}
