import java.util.*;
import java.io.*;

class ParseConfigFile{

    ArrayList<String> dbFile;
    Map<String,String> servers;
    ArrayList<String> logFile;
    String rootFile;
    
    ParseConfigFile(String fileName) throws IOException{
        BufferedReader reader;
        this.dbFile = new ArrayList<>();
        this.logFile = new ArrayList<>();
        this.servers = new HashMap<>();
        try{
            reader = new BufferedReader(new FileReader(fileName));
            //read lines
            String line = reader.readLine();
            while(line != null){
                //if 2 word of line
                String[] words = line.split(" ");
                if(words[1].equals("DB")){
                    dbFile.add(words[0]);
                }else if(words[1].equals("SP") || words[1].equals("SS") || words[1].equals("DD")){
                    servers.put(words[0], words[2]);
                }else if(words[1].equals("LG")){
                    logFile.add(words[2] + ":" + words[0]);
                }else if(words[1].equals("ST")){
                    rootFile = words[2];
                }
                line = reader.readLine();
            }
            reader.close();
        }catch(FileNotFoundException e){
            System.out.println("File not found: " + e);
        }
    }

    //getter
    public ArrayList<String> getDatabase(){
        return dbFile;
    }
    public Map<String,String> getServers(){
        return servers;
    }
    public ArrayList<String> logFile(){
        return logFile;
    }
    public ArrayList<String> getrootFile(){
        return logFile;
    }
}
