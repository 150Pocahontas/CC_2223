import java.io.*;
import java.net.*;

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
            Socket socket = new Socket(ipPrimary,port);
            while(!Server.EXIT){
                String query;
                if(db != null){
                    Thread.sleep(db.getSOAREFRESH());
                    InetAddress ip = InetAddress.getLocalHost();
                    String ipLocal = ip.getHostAddress();
                    query = "SOA " + domain + " " + ipLocal + " " + port + " " + db.getSOASERIAL();
                } else{
                    InetAddress ip = InetAddress.getLocalHost();
                    String ipLocal = ip.getHostAddress();
                    this.db = new ParseDBFile("../files/SS/" + domain + "_" + ip);
                    query = "SOA " + domain + " " + ipLocal + " " + port + " " + 0;
                }
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
                        db.addEntry(responseSplit2[1]);
                    }
                    db.rewriteFile(db.getPathFile());
                }
            }
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) { } 
    }   
}
