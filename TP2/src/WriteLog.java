import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

//Write in file
class WriteLog{

    private final String name;

    public WriteLog(String name){
        File file = new File(name);
        if(!file.exists()) 
            file.delete();
        this.name = name;
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

