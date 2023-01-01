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
    // Getters
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
    // Setters
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
    // toString
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

     //Deve ser implementada uma função para procurar uma entrada VALID a partir de três valores Index, Name e Type. 
     //A função deve devolver o índice da primeira entrada encontrada que faça o match com Name e Type.
     // A procura deve começar a partir da entrada com o índice Index.
     // Sempre que é necessário encontrar todas as entradas que façam match ao tuplo [Name,Type] deve começar-se com Index=1 e repetir-se até o índice devolvido ser igual a N+1.
     // Esta função deve também verificar se as entradas visitadas com Origin igual a OTHERS e se o tempo que já passou desde o momento do registo da entrada guardado em TimeStamp for superior a TTL.
     // Nesse caso deve colocar-se essas entradas como FREE.
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
        }
        else if(origin.equals("OTHERS")){
            i = findEntry(cache, index, name, type);
            if(i < cache.size()){
                if(cache.get(i).getOrigin().equals("OTHERS")){
                    cache.get(i).setTimestamp(System.currentTimeMillis());
                    cache.get(i).setStatus("VALID");
                }
            }
            else{
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
    //atualize, em todas as entradas da cache com Name igual ao domínio passado como argumento, o campo Status para FREE.
    // Quando o temporizador associado à idade da base de dados dum SS relativo a um domínio atinge o valor de SOAEXPIRE, então o SS deve executar esta função para esse domínio. 
    //Esta função é exclusiva dos servidores do tipo secundário.
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
