import java.util.*;
import java.io.*;


class Pair {
    private String domain;
    private String value;

    public Pair(String domain, String value) {
        this.domain = domain;
        this.value = value;
    }

    public String getdomain() {
        return domain;
    }

    public String getvalue() {
        return value;
    }

    public boolean equals(Object o) {
        if(! (o instanceof Pair)) return false;
        Pair p = (Pair) o;
        return p.getdomain().equals(this.domain) && p.getvalue().equals(this.value);
    }

}

/**
 * Classe que faz parse do ficehiro de configurações
  */
class ParseConfigFile{

    private static final Exception FileFormatException = null;
    private static final String InterruptedException = null;
    List<Pair> dbFile;
    List<Pair> ps;
    List<Pair> ss;
    List<Pair> dd;
    List<Pair> logFile;
    String rootFile;
    
    ParseConfigFile(String fileName) throws Exception{
        BufferedReader reader;
        this.dbFile = new ArrayList<>();
        this.ps = new ArrayList<>();
        this.ss = new ArrayList<>();
        this.dd = new ArrayList<>();
        this.logFile = new ArrayList<>();
    
        reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while(line != null){
            String[] words = line.split(" ");
            if(words[1].equals("DB")){
                dbFile.add(new Pair(words[0], words[2]));
            }else if(words[1].equals("SP")){ // então é servidor secundario par5a esses dominios
                ps.add(new Pair(words[0], words[2]));
            } else if( words[1].equals("SS")){ // então é servidor primário par5a esses dominios
                ss.add(new Pair(words[0], words[2]));
            } else if(words[1].equals("DD")){
                dd.add(new Pair(words[0], words[2]));
            }else if(words[1].equals("LG")){
                logFile.add(new Pair(words[0], words[2]));
            }else if(words[1].equals("ST")){
                if(!words[0].equals("root")){
                    reader.close();
                    throw FileFormatException;
                }else rootFile = words[2];
            }else if(words[0].equals("#")){
            }else{
                reader.close();
                throw FileFormatException;
            } 
            line = reader.readLine();
        }   
        reader.close();
    }

    public String getdbFile(String name){
        for(Pair i: this.dbFile){
            if(i.getdomain().equals(name)){
                return i.getvalue();
            }
        }
        return InterruptedException;
    }

    public ArrayList<Pair> getdbList(){
        return (ArrayList<Pair>) dbFile;
    }

    public ArrayList<Pair> getps(){
        return (ArrayList<Pair>) ps;
    }

    public ArrayList<Pair> getss(){
        return (ArrayList<Pair>) ss;
    }

    public ArrayList<Pair> getdd(){
        return (ArrayList<Pair>) dd;
    }

    public ArrayList<Pair> getlogFile(){
        return (ArrayList<Pair>) logFile;
    }

    public String getrootFile(){
        return rootFile;
    }

    public void addDbFile(Pair dbFile){
        this.dbFile.add(dbFile);
    }
}
