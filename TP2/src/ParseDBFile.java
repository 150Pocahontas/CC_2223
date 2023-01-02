import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

class ParseDBFile{

    private String pathFile;
    private String name;
    private static String ttl;
    private String sp;
    private String admin;
    private String serial;
    private String refresh;
    private String retry;
    private String expire;
    private List<String> mxValues;
    private List<String> nsValues;
    private List<String> extraValues;
    private List<String> cnamevalues;
    private List<String> ptrvalues;
    private static int numOfEntries;
    private List<String> entries;
    
    ParseDBFile(String fileName) {
        pathFile = fileName;
        mxValues = new ArrayList<>();
        nsValues = new ArrayList<>();
        extraValues = new ArrayList<>();
        entries = new ArrayList<>();
        cnamevalues = new ArrayList<>();
        ptrvalues = new ArrayList<>();
        numOfEntries = 0;
        cnamevalues = new ArrayList<>();
        ptrvalues = new ArrayList<>();
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
                        else if(words[1].equals("A"))
                            extraValues.add(words[0] + name + " A " + words[2] + " " + ttl);
                        else if(words[1].equals("CNAME")){
                            cnamevalues.add(words[0] + name + " CNAME " + words[2] + " " + ttl);
                        }else if(words[1].equals("PRT")){
                            ptrvalues.add(words[0] + name + " PTR " + words[2] + " " + ttl);
                        }
                    }else{
                        if(!words[1].equals("DEFAULT")){}
                        if(words[1].equals("MX")){
                            mxValues.add(name + " MX " + words[2] + " " + ttl);
                        }else if(words[1].equals("NS")){
                            nsValues.add(name + " NS " + words[2] + " " + ttl);
                        } else if(words[1].equals("DEFAULT")){
                            name = words[2];
                        }else if(words[1].equals("SOASP")){
                            sp = words[2];
                        }else if(words[1].equals("SOAADMIN")){
                            admin = words[2];
                        }else if(words[1].equals("SOASERIAL")){
                            serial = words[2];
                        }else if(words[1].equals("SOAREFRESH")){
                            refresh =  words[2];
                        }else if(words[1].equals("SOARETRY")){  
                            retry =  words[2];
                        }else if(words[1].equals("SOAEXPIRE")){
                            expire =  words[2];
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
        return name;
    }

    public String getTTL(){
        return ttl;
    }

    public String getSOASP(){
        return sp;
    }

    public String getSOAADMIN(){
        return admin;
    }

    public String getSOASERIAL(){
        return serial;
    }
    public String getSOAREFRESH(){
        return refresh;
    }
    public String getSOARETRY(){
        return retry;
    } 
    public String getSOAEXPIRE(){
        return expire;
    }

    public List<String> getMXvalues(){
        return mxValues;
    }

    public List<String> getNSvalues(){
        return nsValues;
    }

    public List<String> getExtraValues(){
        return extraValues;
    }

    public int getNumExtra(){
        return extraValues.size();
    }

    public List<String> getCnamevalues(){
        return cnamevalues;
    }

    public List<String> getPtrvalues(){
        return ptrvalues;
    }

    public int getNumRV(String type){
        if(type.equals("DEFAULT")){
            return 1;
        }else if(type.equals("TTL")){
            return 1;
        }else if(type.equals("SOASP")){
            return 1;
        }else if(type.equals("SOAADMIN")){
            return 1;
        }else if(type.equals("SOASERIAL")){
            return 1;
        }else if(type.equals("SOAREFRESH")){
            return 1;
        }else if(type.equals("SOARETRY")){
            return 1;
        }else if(type.equals("SOAEXPIRE")){
            return 1;
        }else if(type.equals("MX")){
            return mxValues.size();
        }else if(type.equals("NS")){
            return nsValues.size();
        }else if(type.equals("A")){
            return extraValues.size();
        }else if(type.equals("CNAME")){
            return cnamevalues.size();
        }else if(type.equals("PTR")){
            return ptrvalues.size();
        }       
        return 0;
    }

    public int getNumNS(){
        if(nsValues != null)
            return nsValues.size();
        return 0;
    }
    
    public List<String> getResponseValues(String type){
        if(type.equals("DEFAULT")){
            //crete new list and add item
            List<String> list = new ArrayList<>();
            list.add("DEFAULT " + name);
            return list;
        }else if(type.equals("TTL")){
            List<String> list = new ArrayList<>();
            list.add("TTL " + ttl);
            return list;
        }else if(type.equals("SOASP")){
            List<String> list = new ArrayList<>();
            list.add("SOASP " + sp);
            return list;
        }else if(type.equals("SOAADMIN")){
            List<String> list = new ArrayList<>();
            list.add("SOAADMIN " + admin);
            return list;
        }else if(type.equals("SOASERIAL")){
            List<String> list = new ArrayList<>();
            list.add("SOASERIAL " + serial);
            return list;
        }else if(type.equals("SOAREFRESH")){
            List<String> list = new ArrayList<>();
            list.add("SOAREFRESH " + refresh);
            return list;
        }else if(type.equals("SOARETRY")){
            List<String> list = new ArrayList<>();
            list.add("SOARETRY " + retry);
            return list;
        }else if(type.equals("SOAEXPIRE")){
            List<String> list = new ArrayList<>();
            list.add("SOAEXPIRE " + expire);
            return list;
        }else if(type.equals("MX")){
            return mxValues;
        }else if(type.equals("NS")){
            return nsValues;
        }else if(type.equals("CNAME")){
            return cnamevalues;
        }else if(type.equals("PTR")){
            return ptrvalues;
        }else if(type.equals("A")){
            return extraValues;
        }
        return null;
    }

    public int getNumOfEntries(){
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

    public int toInt(String s){
        return Integer.parseInt(s);
    }

}
