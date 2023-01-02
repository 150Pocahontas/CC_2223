import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

public class DNSmessage implements Serializable {
    public static final int MAX_SIZE_DATA = 1028;
    public static final int MAX_SIZE_MESSAGE = MAX_SIZE_DATA + 32;
    public static final String ENCRIPTION_KEY = "CC22-TP6-Grupo02";

    //Header Fields
    private final int id;
    private final int[] flags;
    private final int responseCode;
    private final int numberOfValues;
    private final int numberOfAuthorities;
    private final int numberOfExtra;
    //Data Fields
    // -> 2 Campos Query Info
    private static String name;
    private static String typeOfValue;

    private List<String> responseValue;
    private List<String> authoritiesValues;
    private List<String> extraValues;

    public DNSmessage(int id, int[] flags, int responseCode, int numberOfValues, int numberOfAuthorities, int numberOfExtra, String name, String typeOfValue,  List<String> responseValue,  List<String> authoritiesValues,  List<String> extraValues) {
        this.id = id;
        this.flags = flags;
        this.responseCode = responseCode;
        this.numberOfValues = numberOfValues;
        this.numberOfAuthorities = numberOfAuthorities;
        this.numberOfExtra = numberOfExtra;
        DNSmessage.name = name;
        DNSmessage.typeOfValue = typeOfValue;
        this.responseValue = responseValue;
        this.authoritiesValues = authoritiesValues;
        this.extraValues = extraValues;
    }

    //Método que transforma um dns message num array de bytes encriptado
    public byte[] toByteArray() throws Exception {

        byte[] byteM = new byte[MAX_SIZE_MESSAGE];
        byte[] id = intToByteArray(this.id);
        System.arraycopy(id, 0, byteM, 0, 4);
        byte[] flags = new byte[3];
        flags[0] = (byte) this.flags[0];
        flags[1] = (byte) this.flags[1];
        flags[2] = (byte) this.flags[2];
        System.arraycopy(flags, 0, byteM, 4, 3);
        byte[] responseCode = intToByteArray(this.responseCode);
        System.arraycopy(responseCode, 0, byteM, 7, 4);
        byte[] numberOfValues = intToByteArray(this.numberOfValues);
        System.arraycopy(numberOfValues, 0, byteM, 11, 4);
        byte[] numberOfAuthorities = intToByteArray(this.numberOfAuthorities);
        System.arraycopy(numberOfAuthorities, 0, byteM, 15, 4);
        byte[] numberOfExtra = intToByteArray(this.numberOfExtra);
        System.arraycopy(numberOfExtra, 0, byteM, 19, 4);
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(name, 0, byteM, 23, 11);
        byte[] typeOfValue = this.typeOfValue.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(typeOfValue, 0, byteM, 34, 2);
        int index = 36;
        if(this.numberOfValues != 0){
            for (String s : this.responseValue) {
                byte[] size = intToByteArray(s.length());
                System.arraycopy(size, 0, byteM, index, 4);
                index += 4;
                byte[] value = s.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(value, 0, byteM, index, s.length());
                index += s.length();
            }
        }
        if(this.numberOfAuthorities != 0){
            for (String s : this.authoritiesValues) {
                byte[] size = intToByteArray(s.length());
                System.arraycopy(size, 0, byteM, index, 4);
                index += 4;
                byte[] value = s.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(value, 0, byteM, index, s.length());
                index += s.length();
            }
        }
        if(this.numberOfExtra != 0){
            for (String s : this.extraValues) {
                byte[] size = intToByteArray(s.length());
                System.arraycopy(size, 0, byteM, index, 4);
                index += 4;
                byte[] value = s.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(value, 0, byteM, index, s.length());
                index += s.length();
            }
        }
        return byteM;
    }
    public DNSmessage(byte[] message) throws Exception {
        byte[] arrayBytes = message;
        this.id = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 0, 4)).getInt();
        this.flags = new int[3];
        this.flags[0] = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 4, 5)).get();
        this.flags[1] = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 5, 6)).get();
        this.flags[2] = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 6, 7)).get();

        this.responseCode = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 7, 11)).get();
        this.numberOfValues = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 11, 15)).getInt();
        this.numberOfAuthorities = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 15, 19)).getInt();
        this.numberOfExtra = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 19, 23)).getInt();
        this.name = new String(Arrays.copyOfRange(arrayBytes, 23, 34), StandardCharsets.UTF_8);
        this.typeOfValue = new String(Arrays.copyOfRange(arrayBytes, 34, 36), StandardCharsets.UTF_8);
        int index = 36;
        // verify if there are values to be read and read them
        if(this.numberOfValues != 0){
            this.responseValue = new ArrayList<>();
            for (int i = 0; i < this.numberOfValues; i++) {
                int size = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, index, index+4)).getInt();
                index += 4;
                String value = new String(Arrays.copyOfRange(arrayBytes, index, index+size), StandardCharsets.UTF_8);
                index += size;
                this.responseValue.add(value);
            }
        }
        // verify if there are authorities to be read and read them
        if(this.numberOfAuthorities != 0){
            this.authoritiesValues = new ArrayList<>();
            for (int i = 0; i < this.numberOfAuthorities; i++) {
                int size = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, index, index+4)).getInt();
                index += 4;
                String value = new String(Arrays.copyOfRange(arrayBytes, index, index+size), StandardCharsets.UTF_8);
                index += size;
                this.authoritiesValues.add(value);
            }
        }
        // verify if there are extra values to be read and read them
        if(this.numberOfExtra != 0){
            this.extraValues = new ArrayList<>();
            for (int i = 0; i < this.numberOfExtra; i++) {
                int size = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, index, index+4)).getInt();
                index += 4;
                String value = new String(Arrays.copyOfRange(arrayBytes, index, index+size), StandardCharsets.UTF_8);
                index += size;
                this.extraValues.add(value);
            }
        }
    }


    /**
     * Método que converte um inteiro num array de bytes
     * @param inteiro   Inteiro a ser convertido
     * @return          Array de bytes com o inteiro convertido
    */
    private byte[] intToByteArray (int inteiro) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(inteiro);
        return byteBuffer.array();
    }

    //encrypt the message with the key
    public static byte[] encrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(ENCRIPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(message);
    }

    //decrypt the message with the key
    public static byte[] decrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(ENCRIPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(message);
    }


    public int getId() {
        return id;
    }

    public int[] getFlags() {
        return flags;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getNumberOfValues() {
        return numberOfValues;
    }

    public int getNumberOfAuthorities() {
        return numberOfAuthorities;
    }

    public int getNumberOfExtra() {
        return numberOfExtra;
    }

    public String getName() {
        return name;
    }

    public String getTypeOfValue() {
        return typeOfValue;
    }

    private String[] flagToS(){
        int j=0;
        for(int i=0; i<3; i++){
            if(flags[i] == 1) j++;
        }
        String[] flag = new String[j];
        j=0;
        for(int i=0; i<3;i++){ 
            if(i == 0 && this.flags[i] == 1){
                flag[j] = "Q";
                j++;
            } 
            else if(i == 1 && this.flags[i] == 1){
                flag[j] = "R";
                j++;
            } 
            else if(i == 2 && this.flags[i] == 1) {
                flag[j] = "A";
                j++;
            } 
        }
        return flag;
    }


    @Override
    public String toString() {
        
        String[] flag = flagToS();
        String responseV = "";

        if(responseValue==null){
            responseV += "\nRESPONSE-VALUES = null ,";
        }else{
            for(String g : responseValue){
                responseV += "\nRESPONSE-VALUES = " + g +",";
            }
        }

        String av = "";
        if(authoritiesValues==null){
            av += "\nAUTHORITIES-VALUES = null ,";
        }else{
            for(String g : authoritiesValues){
                av += "\nAUTHORITIES-VALUES = " + g +",";
            }
        }

        String ev = "";
        if(extraValues==null){
            ev += "\nEXTRA-VALUES = null ,";
        }else{
            for(String g : extraValues){
                ev += "\nEXTRA-VALUES = " + g +",";
            }
        }
                 
        return "# Header\n" +
        "MESSAGE-ID = " + id +
        ", FLAGS = " + Stream.of(flag).collect(Collectors.joining("+")) +
        ", RESPONSE-CODE = " + responseCode +
        ", N-VALUES = " + numberOfValues +
        ", N-AUTHORITIES = " + numberOfAuthorities +
        ", N-EXTRA = " + numberOfExtra +
        ",\n# Data: Query Info\n" +
        "QUERY-INFO.NAME = " + name +  
        ", QUERY-INFO.TYPE = " + typeOfValue +
        ",;\n# Data: list os Response, Authorities and Extra Values" + 
         responseV + av + ev;
    }

    public String toStringDebug(){
        String[] flag = flagToS();
        return id + "," + Stream.of(flag).collect(Collectors.joining("+")) + "," + responseCode + ',' + numberOfValues + ',' + numberOfAuthorities + ',' + numberOfExtra + ',' + name + ',' + typeOfValue;
    }
}
