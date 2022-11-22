//Server Primary and secundary (also SDT and ST)
class Server{
    
    //Database Dominio , filepath
    ArrayList<String,String> databases = new ArrayList<String>();
    ArrayList<InetAddress> topServers = new ArrayList<InetAddress>();
    String 

    public static main(String[] args){
        //create a new parser
        Parser parser = new Parser(args[1]);
        //create a new resolver
        Resolver resolver = new Server(parser.getDatabase(), parser.getServers(), parser.getDefault(), parser.getLogFile(), parser.getSTFile());
        //create a new server
        ResolverServer server = new ResolverServer(parser.getServers(), resolver);
        //start the server
        server.start();
    }
}