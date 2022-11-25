import java.util.*;
import java.io.*;

class ParseDBFile{

    String fileName;
    Map<String,String> infoDomain; //<paramter,<type,value>>
    
    ParseDBFile(String fileName) throws IOException{
        BufferedReader reader;
        infoDomain = new HashMap<String,String>();
        try{
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while(line != null){
                String[] words = line.split(" ");
                if(!words[0].equals("#") || !words[0].equals("@")){
                    if(words[0].equals("TTL"))
                        infoDomain.put("TTL", words[2]);
                    else if(words[0].equals("Smaller.@"))
                        infoDomain.put(words[1], words[2]);
                    else if(words[1].equals("A") || words[1].equals("CNAME"))
                        infoDomain.put(words[0], words[2]);
                }else if(words[0].equals("@") && !words[1].equals("DEFAULT")){
                        infoDomain.put(words[1], words[2]);
                }
                line = reader.readLine();
            }
            reader.close();
            //System.out.println(infoDomain);
        }catch(FileNotFoundException e){
            System.out.println("File not found: " + e);
        }
    }
    
    public String getFromDB(String name){
        if(infoDomain.containsKey(name)){
            return infoDomain.get(name);
        }
        return null;
    }

    //getter
    public Map<String,String> getDB(){
        return infoDomain;
    }   
}
