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
            ServerSocket socket = new ServerSocket(8080);
            while (!Server.EXIT) {
                Socket client = socket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String query = in.readLine();
                System.out.println(query);
                String[] querySplit = query.split(" ");
                if (querySplit[0].equals("SOA")) {
                    // String query = "SOA " + domain + " " + ipLocal + " " + port + " " +
                    // db.getSOASERIAL();
                    if (ssList.contains(new Pair(querySplit[1], querySplit[2]))) {
                        ParseDBFile db = new ParseDBFile(Server.getBDName(dbList, querySplit[1]));
                        db.parseFile();
                        System.out.println(db.getPathFile());
                        if (Integer.parseInt(querySplit[4]) < db.getSOASERIAL()) {
                            query = "SOA entries: " + db.getNumOfEntries();
                            System.out.println(query);
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println(query);
                        } else {
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println("Serial number is the same");
                            System.out.println("Serial number is the same");
                            client.close();
                            break;
                        }
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        query = in.readLine();
                        System.out.println(query);
                        if (query.equals("OK: " + db.getNumOfEntries())) {
                            int i = 1;
                            for (String entry : db.getEntries()) {
                                query = i + ": " + entry;
                                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                                out.println(query);
                                i++;
                            }
                        }
                        client.close();
                        System.out.println("Client closed");
                    } else {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println("Domain not found or Server not permited");
                        client.close();
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
