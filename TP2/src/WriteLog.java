import java.io.*;

//Write in file
class WriteLog{

    private final String name;
    private final String domain;

    public WriteLog(String domain, String name){
        File file = new File(name);
        if(!file.exists()) 
            file.delete();
        this.name = name;
        this.domain = domain;
    }

    //Write in file
    public void write(String line){
        try{
            FileWriter fw = new FileWriter(name, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(line);
            out.close();
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}

