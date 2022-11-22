
//Write in file
class WriteLog{
    //Write in file
    public static void write(String line){
        try{
            FileWriter fw = new FileWriter("log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(line);
            out.close();
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}