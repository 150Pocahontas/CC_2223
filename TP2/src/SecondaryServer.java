import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
                    Thread.sleep(db.getSOAREFRESH());
                    InetAddress ip = InetAddress.getLocalHost();
                    String ipLocal = ip.getHostAddress();
                    query = "SOA " + domain + " " + ipLocal + " " + port + " " + db.getSOASERIAL();
                    System.out.println();
                } else{
                    InetAddress ip = InetAddress.getLocalHost();
                    String ipLocal = ip.getHostAddress();
                    this.db = new ParseDBFile("../files/SS/" + domain + "_" + ipLocal);
                    query = "SOA " + domain + " " + ipLocal + " " + port + " " + 0;
                }
                try {
                    Socket socket = new Socket(ipPrimary,port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(query);
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
                        db.rewriteFile(db.getPathFile());
                        Thread thread = new Thread(removebd(db.getSOAEXPIRE()));
                        thread.start();
                        Server.configFile.addDbFile(new Pair(domain,db.getPathFile()));
                        Server.cache.registerEntry(Server.cacheList, db.getDef(),"MX",db.getMX(), db.getTTL(), "SP", Server.index, Server.cache.getStatus());
                        Server.cache.registerEntry(Server.cacheList, db.getDef(),"NS", db.getNS(), db.getTTL(), "SP", Server.index, Server.cache.getStatus());
                
                    }
                    socket.close();
                } catch (Exception e) {
                    System.out.println("[Menssagem recebida] " + e.getMessage());
                    Thread.sleep(db.getSOARETRY());
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
        //remove db from list of server
        Server.configFile.removeDbFile(new Pair(domain,db.getPathFile()));
        //turn entry in cache free
        return null;
    }
}
