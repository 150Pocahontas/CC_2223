import java.net.*;
import java.util.*;

import javax.sound.midi.Soundbank;

import java.io.*;

/**
 * Classe que implementa um Servidor Primário
 */
public class PrimaryServer implements Runnable{
    private List<Pair> ssList;
    private List<Pair> dbList;

    /** 
     * Contrutor do Servidor Primário
     * @throws IOException
     * 
    */
    public PrimaryServer(ArrayList<Pair> ssList, ArrayList<Pair> dbList) throws IOException{
        this.ssList = ssList;
        this.dbList = dbList;
    }

    public void run() {
        try {   
            ServerSocket socket = new ServerSocket(8080);
            while(!Server.EXIT){
                Socket client = socket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String query = in.readLine();
                System.out.println(query);
                String[] querySplit = query.split(" ");
                if(querySplit[0].equals("SOA")){
                    //String query = "SOA " + domain + " " + ipLocal + " " + port + " " + db.getSOASERIAL();
                    if(ssList.contains(new Pair(querySplit[1],querySplit[2]))){
                        ParseDBFile db = new ParseDBFile(getBDName(dbList,querySplit[1]));
                        db.parseFile();
                        if(Integer.parseInt(querySplit[4]) < db.getSOASERIAL()){
                            query = "SOA entries: " + db.getNumOfEntries();
                            System.out.println(query);
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println(query);
                        }else{
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println("Serial number is the same"); 
                            System.out.println("Serial number is the same");
                            client.close();  
                            break; 
                        }
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        query = in.readLine();
                        System.out.println(query);
                        if(query.equals("OK: " + db.getNumOfEntries())){
                            int i = 0;
                            for(String entry: db.getEntries()){
                                i++;
                                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                                out.println(i + ": " + entry);
                            }
                        }
                        client.close();
                        System.out.println("Client closed");
                    }else{
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println("Domain not found or Server not permited");
                        client.close();
                    }
                }
            }
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        } 
    }  
    
    public String getBDName(List<Pair> dbList2, String domain){
        for(Pair p: dbList2){
            if(p.getdomain().equals(domain)){
                return p.getvalue();
            }
        }
        return null;
    }
}
