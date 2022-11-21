
class Parser{
    
    String database;
    //list of servers
    ArrayList<Server> servers = new ArrayList<Server>();
    String default;
    String logFile;
    String STFile;
    
    public Parser(String fileName){
        BufferReader reader;
        try{
            reader = new BufferReader(new FileReader(fileName));
            String line = reader.readline();
            while(line != null){
                System.out.println(line);
                line = reader.readline();
            }
        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }
    }
    

}