import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

class ParseDBFile{

    String pathFile;
    String def;
    static String ttl;
    String sp;
    String admin;
    int serial;
    int refresh;
    int retry;
    int expire;
    List<String> mxValues;
    List<String> nsValues;
    List<String> extraValues;
    static int numOfEntries;
    List<String> entries;
    
    ParseDBFile(String fileName) {
        pathFile = fileName;
        mxValues = new ArrayList<>();
        nsValues = new ArrayList<>();
        extraValues = new ArrayList<>();
        entries = new ArrayList<>();
        numOfEntries = 0;
    }

    public void parseFile() throws IOException{
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(pathFile));
            String line = reader.readLine();
            while(line != null){
                if(!line.isEmpty() && !(line.split(" "))[0].equals("#")){
                    numOfEntries++;
                    entries.add(line);
                    String[] words = line.split(" ");
                    if(!words[0].equals("@")){
                        if(words[0].equals("TTL"))
                            if(words[2] == null) ttl = "0";
                            else ttl = words[2];
                        else if(words[1].equals("A"))// || words[1].equals("CNAME")
                            extraValues.add(words[0] + def + " A " + words[2] + " " + ttl);
                    }else {
                        if(!words[1].equals("DEFAULT")){}
                        if(words[1].equals("MX")){
                            mxValues.add(def + " MX " + words[2] + " " + ttl);
                        }else if(words[1].equals("NS")){
                            nsValues.add(def + " NS " + words[2] + " " + ttl);
                        } else if(words[1].equals("DEFAULT")){
                            def = words[2];
                        }else if(words[1].equals("SOASP")){
                            sp = words[2];
                        }else if(words[1].equals("SOAADMIN")){
                            admin = words[2];
                        }else if(words[1].equals("SOASERIAL")){
                            serial = Integer.parseInt(words[2]);
                        }else if(words[1].equals("SOAREFRESH")){
                            refresh =  Integer.parseInt(words[2]);
                        }else if(words[1].equals("SOARETRY")){  
                            retry =  Integer.parseInt(words[2]);
                        }else if(words[1].equals("SOAEXPIRE")){
                            expire =  Integer.parseInt(words[2]);
                        }
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        }catch(FileNotFoundException e){
            System.out.println("File not found: " + e);
        }
    }

    public String getPathFile(){
        return pathFile;
    }

    public String getDef(){
        return def;
    }

    public static String getTTL(){
        return ttl;
    }

    public String getSOASP(){
        return sp;
    }

    public String getSOAADMIN(){
        return admin;
    }

    public int getSOASERIAL(){
        return serial;
    }

    public int getSOAREFRESH(){
        return refresh;
    }

    public int getSOARETRY(){
        return retry;
    }

    public int getSOAEXPIRE(){
        return expire;
    }

    public List<String> getMX(){
        return mxValues;
    }
    public List<String> getNS(){
        return nsValues;
    }
    public List<String> getExtra(){
        return extraValues;
    }

    public int getNumExtra(){
        return extraValues.size();
    }

    public int getNumRV(String type){
        if(type.equals("MX"))
            return mxValues.size();
        else if(type.equals("NS"))
            return nsValues.size();
        else
            return 0;
    }
    public int getNumAV(String tipo){
        if(!(tipo.equals("MX")))
            return mxValues.size();
        else if(!(tipo.equals("NS")))
            return nsValues.size();
        else
            return 0;
    }

    // get responsevalues
    public List<String> getResponseValues(String tipo){
        if(tipo.equals("MX")){
            return mxValues;
        }
        else if(tipo.equals("NS")){
            return nsValues;
        }
        else
            return null;
    }

    //get autoritativevalues
    public List<String> getAuthoritativeValues(String tipo){
        if(tipo.equals("MX")){
            return nsValues;
        }
        else if(tipo.equals("NS")){
            return mxValues;
        }
        else
            return null;
    }

    public List<String> getExtraValues(){
        return extraValues;
    }

    public static int getNumOfEntries(){
        return numOfEntries;
    }

    public List<String> getEntries(){
        return entries;
    }

    public void clearListOfEntries(){
        entries.clear();
    }

    public void addEntry(String entry){
        entries.add(entry);
    }

    //rewrite bd file
    public void rewriteFile(String fileName) throws IOException{
        try{                  
            File file = new File(fileName);
            if(file.exists()){
                file.createNewFile();
            }
        

            FileOutputStream out = new FileOutputStream(fileName);

            StringBuilder content = new StringBuilder();
            for(String s : entries){
                content.append(s);
            }
            
            out.write(content.toString().getBytes(StandardCharsets.UTF_8));
            out.close();
            }catch(FileNotFoundException e){
            System.out.println("File not found: " + e);
        }
    }



}
