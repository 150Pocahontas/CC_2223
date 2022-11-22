import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//Write in file
class WriteLog{
    //Write in file
    public static void write(String line,String logFile){
        try{
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(line);
            out.close();
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}