import java.util.*;
import java.io.*;

class ParseDBFile{

    String fileName;
    Map<String, Map<String,String>> infoDomain; //<paramter,<type,value>>
    
    ParseDBFile(String fileName) throws IOException{
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while(line != null){
                String[] words = line.split(" ");
                infoDomain.put(words[0], new HashMap<String,String>());
                infoDomain.get(words[0]).put(words[1], words[2]);
            }
        }catch(FileNotFoundException e){
            System.out.println("File not found: " + e);
        }
    }
    
    public String getFromDB(String name, String type){
        if(infoDomain.containsKey(type)){
            if(infoDomain.get(type).containsKey(name)){
                return infoDomain.get(type).get(name);
            }
        }
        return null;
    }

    //getter
    public Map<String, Map<String,String>> getDB(){
        return infoDomain;
    }   
}
