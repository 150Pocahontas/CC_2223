import java.net.InetAddress;
import java.util.List;

public class Cache {
    private String name;
    private String type;
    private InetAddress value;
    private String ttl;
    private String origin; // File, SP, Others
    private long timestamp; 
    private int index;
    private String status; // Free, Valid
    
    // Constructor
    public Cache(String name, String type, InetAddress value, String ttl, String origin, long timestamp, int index, String status){
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

    public InetAddress getValue(){
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

    public void setValue(InetAddress value){
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
     
    public int findEntry(List<Cache> cache, int index, String name, String type){
        int i = index;
        while(i < cache.size()){
            if(cache.get(i).getStatus().equals("VALID")){
                if(cache.get(i).getOrigin().equals("OTHERS")){
                    if(System.currentTimeMillis() - cache.get(i).getTimestamp() > Integer.parseInt(cache.get(i).getTTL())){
                        cache.get(i).setStatus("FREE");
                    }
                }
                if(cache.get(i).getName().equals(name) && cache.get(i).getType().equals(type)){
                    return i;
                }
            }
            i++;
        }
        return i;
    }
       
    public void registerEntry(List<Cache> cache, String name, String type, InetAddress value, String ttl, String origin, int index){
        int i = index;
        if(origin.equals("FILE") || origin.equals("SP")){
            while(i < cache.size()){
                if(cache.get(i).getStatus().equals("FREE")){
                    cache.get(i).setName(name);
                    cache.get(i).setType(type);
                    cache.get(i).setValue(value);
                    cache.get(i).setTTL(ttl);
                    cache.get(i).setOrigin(origin);
                    cache.get(i).setTimestamp(System.currentTimeMillis());
                    cache.get(i).setStatus("VALID");
                    break;
                }
                i++;
            }
        }else if(origin.equals("OTHERS")){
            i = findEntry(cache, index, name, type);
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
                        break;
                    }
                    i++;
                }
            }
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
}
