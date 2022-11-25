import java.util.*;
import java.io.*;

class ParseDBFile{

    String fileName;
    String def;
    String ttl;
    List<String> mxValues;
    List<String> nsValues;
    List<String> extraValues;
    
    ParseDBFile(String fileName) throws IOException{
        BufferedReader reader;
        mxValues = new ArrayList<>();
        nsValues = new ArrayList<>();
        extraValues = new ArrayList<>();
        try{
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while(line != null){
                String[] words = line.split(" ");
                if(!words[0].equals("#") && !words[0].equals("@")){
                    if(words[0].equals("TTL"))
                        ttl = words[2];
                    else if(words[1].equals("A") ){ // || words[1].equals("CNAME")
                            extraValues.add(words[0] + def + " A " + words[2] + " " + ttl);
                    }
                }else if(words[0].equals("@") && !words[1].equals("DEFAULT")){
                    if(words[1].equals("MX")){
                        mxValues.add(def + " MX " + words[2] + " " + ttl);
                    }
                    if(words[1].equals("NS")){
                        nsValues.add(def + " NS " + words[2] + " " + ttl);
                    }
                } else if(words[1].equals("DEFAULT")){
                    def = words[2];
                }

                line = reader.readLine();
            }
            reader.close();
            //System.out.println(infoDomain);
        }catch(FileNotFoundException e){
            System.out.println("File not found: " + e);
        }
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


}
