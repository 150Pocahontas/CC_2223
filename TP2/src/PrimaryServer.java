import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Classe que implementa um Servidor Primário
 */
public class PrimaryServer implements Runnable {
    private List<Pair> ssList;
    private List<Pair> dbList;

    /**
     * Contrutor do Servidor Primário
     * 
     * @throws IOException
     * 
     */
    public PrimaryServer(ArrayList<Pair> ssList, ArrayList<Pair> dbList) throws IOException {
        this.ssList = ssList;
        this.dbList = dbList;
    }

    public void run() {
        try {
            while (!Server.EXIT) {
                ServerSocket socket = new ServerSocket(8080);
                System.out.println("Waiting for client");
                Socket client = socket.accept();
                System.out.println("Client connected");
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String query = in.readLine();
                System.out.println(query);
                String[] querySplit = query.split(" ");
                if (querySplit[0].equals("SOA")) {
                    if(ssList.contains(new Pair(querySplit[1], querySplit[2]))) {
                        ParseDBFile db = new ParseDBFile(Server.getBDName(dbList, querySplit[1]));
                        db.parseFile();
                        if (Integer.parseInt(querySplit[4]) < db.toInt(db.getSOASERIAL())) {
                            query = "SOA entries: " + db.getNumOfEntries();
                            System.out.println(query);
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println(query);
                            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            query = in.readLine();
                            System.out.println(query);
                            if (query.equals("OK: " + db.getNumOfEntries())) {
                                int i = 1;
                                for (String entry : db.getEntries()) {
                                    query = i + ": " + entry;
                                    out = new PrintWriter(client.getOutputStream(), true);
                                    out.println(query);
                                    i++;
                                }
                            }
                        }else{
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println("Serial number is the same");
                            System.out.println("Serial number is the same");   
                        }
                        client.close();
                        socket.close();
                        System.out.println("Client closed");
                    } else {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println("Domain not found or Server not permited");
                        client.close();
                        socket.close();
                        System.out.println("Client closed");
                    }
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
