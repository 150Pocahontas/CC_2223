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
    public static final String ENCRIPTION_KEY = "CC22-Pl6-Grupo02";
    private static final SecretKey key = new SecretKeySpec(ENCRIPTION_KEY.getBytes(), "AES");

    //Header Fields
    private final int id;
    private final int[] flags;
    private final int responseCode;
    private final int numberOfValues;
    private final int numberOfAuthorities;
    private final int numberOfExtra;
    //Data Fields
    // -> 2 Campos Query Info
    private final String name;
    private final String typeOfValue;

    private final List<String> responseValue;
    private final List<String> authoritiesValues;
    private String extraValues;

    public DNSmessage(int id, int[] flags, int responseCode, int numberOfValues, int numberOfAuthorities, int numberOfExtra, String name, String typeOfValue, List<String> responseValue, List<String> authoritiesValues, String extraValues) {
        this.id = id;
        this.flags = flags;
        this.responseCode = responseCode;
        this.numberOfValues = numberOfValues;
        this.numberOfAuthorities = numberOfAuthorities;
        this.numberOfExtra = numberOfExtra;
        this.name = name;
        this.typeOfValue = typeOfValue;
        this.responseValue = responseValue;
        this.authoritiesValues = authoritiesValues;
        this.extraValues = extraValues;
    }

    public DNSmessage(byte[] message) throws Exception {

        byte[] arrayBytes;
        arrayBytes = decrypt(message);
        this.id = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 0, 2)).getShort();
        this.flags = new int[3];
        this.flags[0] = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 2, 3)).get();
        this.flags[1] = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 3, 4)).get();
        this.flags[2] = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 4, 5)).get();
        this.responseCode = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 5, 6)).get();
        this.numberOfValues = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 6, 8)).getShort();
        this.numberOfAuthorities = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 8, 10)).getShort();
        this.numberOfExtra = ByteBuffer.wrap(Arrays.copyOfRange(arrayBytes, 10, 12)).getShort();
        this.name = new String(Arrays.copyOfRange(arrayBytes, 12, 12 + arrayBytes[12]));
        this.typeOfValue = new String(Arrays.copyOfRange(arrayBytes, 12 + arrayBytes[12] + 1, 12 + arrayBytes[12] + 1 + arrayBytes[12 + arrayBytes[12] + 1]));
        this.responseValue = new ArrayList<>();
        this.authoritiesValues = new ArrayList<>();
        this.extraValues = new String(Arrays.copyOfRange(arrayBytes, 12 + arrayBytes[12] + 1 + arrayBytes[12 + arrayBytes[12] + 1] + 1, arrayBytes.length));
        int index = 12 + arrayBytes[12] + 1 + arrayBytes[12 + arrayBytes[12] + 1] + 1;
        for (int i = 0; i < this.numberOfValues; i++) {
            this.responseValue.add(new String(Arrays.copyOfRange(arrayBytes, index, index + arrayBytes[index])));
            index += arrayBytes[index] + 1;
        }
        for (int i = 0; i < this.numberOfAuthorities; i++) {
            this.authoritiesValues.add(new String(Arrays.copyOfRange(arrayBytes, index, index + arrayBytes[index])));
            index += arrayBytes[index] + 1;
        }
    }
    
    //Método que transforma um dns message num array de bytes encriptado
   
    public byte[] toByteArray() throws Exception {

        byte[] byteM = new byte[MAX_SIZE_MESSAGE];
        byte[] id = intToByteArray(this.id);
        System.arraycopy(id, 0, byteM, 0, 2);
        byte[] flags = new byte[3];
        flags[0] = (byte) this.flags[0];
        flags[1] = (byte) this.flags[1];
        flags[2] = (byte) this.flags[2];
        System.arraycopy(flags, 0, byteM, 2, 3);
        byte[] responseCode = intToByteArray(this.responseCode);
        System.arraycopy(responseCode, 0, byteM, 5, 1);
        byte[] numberOfValues = intToByteArray(this.numberOfValues);
        System.arraycopy(numberOfValues, 0, byteM, 6, 2);
        byte[] numberOfAuthorities = intToByteArray(this.numberOfAuthorities);
        System.arraycopy(numberOfAuthorities, 0, byteM, 8, 2);
        byte[] numberOfExtra = intToByteArray(this.numberOfExtra);
        System.arraycopy(numberOfExtra, 0, byteM, 10, 2);
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(name, 0, byteM, 12, name.length);
        byte[] typeOfValue = this.typeOfValue.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(typeOfValue, 0, byteM, 13 + name.length, typeOfValue.length);
        return encrypt(byteM);
    }

    /**
     * Método que converte um inteiro num array de bytes
     *
     * @param inteiro   Inteiro a ser convertido
     * @return          Array de bytes com o inteiro convertido
    */
    private byte[] intToByteArray (int inteiro) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(inteiro);
        return byteBuffer.array();
    }

    public byte[] encrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message,0,message.length);
    }

    public byte[] decrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return (cipher.doFinal(message,0,message.length));
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
        String[] flag = new String[1];
        for(int i=0, j=0; i<3;i++){
            if(i== 0 && this.flags[i] == 1){
                flag[j] = "Q";
                j++;
            } 
            else if(i== 1 && this.flags[i] == 1){
                flag[j] = "R";
                j++;
            } 
            else if(i== 2 && this.flags[i] == 1) {
                flag[j] = "A";
                j++;
            } 
        }
        return flag;
    }


    @Override
    public String toString() {
        
        String[] flag = flagToS();
         
        return "# Header\n" +
                "MESSAGE-ID = " + id +
                ", FLAGS = " + Stream.of(flag).collect(Collectors.joining("+")) +
                ", RESPONSE-CODE = " + responseCode +
                ", N-VALUES = " + numberOfValues +
                ", N-AUTHORITIES = " + numberOfAuthorities +
                ", N-EXTRA = " + numberOfExtra +
                ",;\n# Data: Query Info\n" +
                "QUERY-INFO.NAME = " + name +  
                ", QUERY-INFO.TYPE = " + typeOfValue;
                
    }

    public String toStringC(){
        String[] flag = flagToS();
        return id + "," + flag + "," + responseCode + ',' + numberOfValues + ',' + numberOfAuthorities + ',' + numberOfExtra + ',' + name + ',' + typeOfValue;
    }
}
