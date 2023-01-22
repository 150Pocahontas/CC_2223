import java.util.*;

public class Cache {
    private String name;
    private String type;
    private List<String> value;
    private String ttl;
    private String origin; // File, SP, Others
    private long timestamp; 
    private int index;
    private String status; // Free, Valid
    
    public Cache(String status){
        this.status = status;
    }
    // Constructor
    public Cache(String name, String type, List<String> value, String ttl, String origin, long timestamp, int index, String status){
        this.name = name;
        this.type = type;
        this.value = value;
        this.ttl = ttl;
        
        this.origin = origin;
        this.timestamp = timestamp;
        this.index = index;
        this.status = status;
    }
   
    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public List<String> getValue(){
        return value;
    }

    public String getTTL(){
        return ttl;
    }
    
    public String getOrigin(){
        return origin;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public int getIndex(){
        return index;
    }

    public String getStatus(){
        return status;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setValue(List<String> value){
        this.value = value;
    }

    public void setTTL(String ttl){
        this.ttl = ttl;
    }
    
    public void setOrigin(String origin){
        this.origin = origin;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String toString(){
        return "Name: " + name + "\n" +
        " Type: " + type + "\n" +
        " Value: " + value + "\n" +
        " TTL: " + ttl  + "\n" +
        " Origin: " + origin + "\n" +
        " Timestamp: " + timestamp + "\n" +
        " Index: " + index + "\n" +
        " Status: " + status + "\n" ; 
    }
     
    // retorna o indice da entrada correspondentes
    public int findEntry1(List<Cache> cache, int index, String name, String type){
        System.out.println(type);
        int i = index;
        while(i < cache.size()){
            if(cache.get(i).getStatus().equals("VALID")){
                if(cache.get(i).getOrigin().equals("OTHERS")){
                    if(System.currentTimeMillis() - cache.get(i).getTimestamp() > Integer.parseInt(cache.get(i).getTTL())){
                        cache.get(i).setStatus("FREE");
                    }
                }
                if (cache.get(i).getName().equals(name) && cache.get(i).getType().equals(type)){
                    return i;
                }
            }
            i++;
        }
        return i;
    }
    public int findEntry(List<Cache> cache, String name, String type){
        for(int i = 0; i < cache.size(); i++){
            if(cache.get(i).getStatus().equals("VALID")){
                if (cache.get(i).getName().equals(name) && cache.get(i).getType().equals(type)){
                    return i;
                }
            }
        }
        return 0;
    }
       
    public void registerEntry(List<Cache> cache, String name, String type, List<String> value, String ttl, String origin, int index, String status){
        int i = index;
        if((origin.equals("FILE") || origin.equals("SP"))){

            while(i < cache.size()){

                if(cache.get(i).getStatus().equals("FREE")){
                    cache.get(i).setName(name);
                    cache.get(i).setType(type);
                    cache.get(i).setValue(value);
                    cache.get(i).setTTL(ttl);
                    cache.get(i).setOrigin(origin);
                    cache.get(i).setTimestamp(System.currentTimeMillis());
                    cache.get(i).setStatus("VALID");
                }
                i++;
            }
        }else if(origin.equals("OTHERS")){
            i = findEntry1(cache, index, name, type);
            if(i < cache.size()){
                if(cache.get(i).getOrigin().equals("OTHERS")){
                    cache.get(i).setTimestamp(System.currentTimeMillis());
                    cache.get(i).setStatus("VALID");
                }
            }else{
                i = index;
                while(i < cache.size()){
                    if(cache.get(i).getStatus().equals("FREE")){
                        cache.get(i).setName(name);
                        cache.get(i).setType(type);
                        cache.get(i).setValue(value);
                        cache.get(i).setTTL(ttl);
                        cache.get(i).setOrigin(origin);
                        cache.get(i).setTimestamp(System.currentTimeMillis());
                        cache.get(i).setStatus("VALID");
                    }
                    i++;
                }
            }
        }
        addStatus(cache, "FREE", i);
    }

    public void addType(List<Cache> cache, ParseDBFile bd, String origins){
        int index = 0;

        String[] split = bd.getDef().split("\\. ");
        String name = split[0];
        String ttl = bd.getTTL();
        String status = "VALID";
        if(name!= null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getDef());
            registerEntry(cache, name, "DEFAULT", values, ttl, origins, index, status);
            index++;
        }
        if(ttl!= null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getTTL());
            registerEntry(cache, name, "TTL", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getSOASP() != null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getSOASP());
            registerEntry(cache, name, "SOASP", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getSOAADMIN() != null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getSOAADMIN());
            registerEntry(cache, name, "SOAADMIN", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getSOASERIAL() != null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getSOASERIAL());
            registerEntry(cache, name, "SOASERIAL", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getSOAREFRESH() != null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getSOAREFRESH());
            registerEntry(cache, name, "SOAREFRESH", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getSOARETRY() != null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getSOARETRY());
            registerEntry(cache, name, "SOARETRY", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getSOAEXPIRE() != null){
            List<String> values = new ArrayList<String>();
            values.add(bd.getSOAEXPIRE());
            registerEntry(cache, name, "SOAEXPIRE", values, ttl, origins, index, status);
            index++;
        }
        if(bd.getMXvalues()!= null){
            registerEntry(cache, name, "MX", bd.getMXvalues(), ttl, origins, index, status);
            index++;
        }
        if(bd.getNSvalues()!= null){
            registerEntry(cache, name, "NS", bd.getNSvalues(), ttl, origins, index, status);
            index++;
        }
        if(bd.getExtraValues()!= null){
            registerEntry(cache, name, "A", bd.getExtraValues(), ttl, origins, index, status);
            index++;
        }
        if(bd.getCnamevalues()!= null){
            registerEntry(cache, name, "CNAME", bd.getCnamevalues(), ttl, origins, index, status);
            index++;
        }
        if(bd.getPtrvalues()!= null){
            registerEntry(cache, name, "PTR", bd.getPtrvalues(), ttl, origins, index, status);
            index++;
        }
    
    }
    public void updateCache(List<Cache> cache, String name){
        int i = 0;
        while(i < cache.size()){
            if(cache.get(i).getName().equals(name)){
                cache.get(i).setStatus("FREE");
            }
            i++;
        }
    }
    
    public void printCache(List<Cache> cache){
        int i = 0;
        while(i < cache.size()){
            System.out.println(cache.get(i).toString());
            i++;
        }
    }

    
    //adiciona nova entrada na lista de caches com status FREE
    public void addStatus(List<Cache> cache, String status, int index){
        Cache newCache = new Cache("FREE");
        newCache.setStatus(status);
        cache.add(index, newCache);
    }

    //torna cahe free
    public void freeCache(List<Cache> cache, String name){
        int i = 0;
        while(i < cache.size()){
            if(cache.get(i).getName().equals(name)){
                cache.get(i).setStatus("FREE");
            }
            i++;
        }
    }

    public List<Cache> getByName(List<Cache> cache, String name){
        List<Cache> cacheList = new ArrayList<Cache>();
        int i = 0;
        while(i < cache.size()){
            if(cache.get(i).getName().equals(name)){
                cacheList.add(cache.get(i));
            }
            i++;
        }
        return cacheList;
    }
   

}