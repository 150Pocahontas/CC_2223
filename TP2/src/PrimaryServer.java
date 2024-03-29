import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Classe que implementa um Servidor Primário
 */
public class PrimaryServer implements Runnable {
    private List<Pair> ssList;

    /**
     * Contrutor do Servidor Primário
     * 
     * @throws IOException
     * 
     */
    public PrimaryServer(ArrayList<Pair> ssList) throws IOException {
        this.ssList = ssList;
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
                    // String query = "SOA " + domain + " " + ipLocal + " " + port + " " +
                    if(ssList.contains(new Pair(querySplit[1], querySplit[2]))) {
                        ParseDBFile db = new ParseDBFile(Server.getvalue(Server.configFile.getdbList(), querySplit[1]));
                        db.parseFile();
                        WriteLog writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), db.getDef()));
                        String date = Server.sdf.format(new Date());
                        writeLog.write(date + " EV @ db-file-read " + db.getPathFile());
                        writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                        date = Server.sdf.format(new Date());
                        writeLog.write(date + " EV @ db-file-read " + db.getPathFile());
                        if (Integer.parseInt(querySplit[4]) < db.toInt(db.getSOASERIAL())) {
                            query = "SOA entries: " + db.getNumOfEntries();
                            System.out.println(query);
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println(query);
                            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            query = in.readLine();
                            System.out.println(query);
                            if (query.equals("OK: " + db.getNumOfEntries())) {
                                writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), querySplit[1]));
                                date = Server.sdf.format(new Date());
                                writeLog.write(date + " ZT inicalizada" + querySplit[2] + " SP " + db.getPathFile());
                                writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                                date = Server.sdf.format(new Date());
                                writeLog.write(date + " ZT inicializada" + querySplit[2] + " SP " + db.getPathFile());
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
                        writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), querySplit[1]));
                        date = Server.sdf.format(new Date());
                        writeLog.write(date + " ZT terminada" + querySplit[2] + " SP " + db.getPathFile());
                        writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                        date = Server.sdf.format(new Date());
                        writeLog.write(date + " ZT terminada" + querySplit[2] + " SP " + db.getPathFile());
                        client.close();
                        socket.close();
                        System.out.println("Client closed");
                    } else {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println("Domain not found or Server not permited");
                        WriteLog writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), querySplit[1]));
                        String date = Server.sdf.format(new Date());
                        writeLog.write(date + " ZT " + querySplit[2] + " SP Domain not found" );
                        writeLog = new WriteLog(Server.getvalue(Server.configFile.getlogFile(), "all"));
                        date = Server.sdf.format(new Date());
                        writeLog.write(date + " ZT " + querySplit[2] + " SP domain not found");
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
