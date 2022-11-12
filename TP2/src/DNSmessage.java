import javax.crypto.*;
import java.io.Serializable;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DNSmessage implements Serializable {
    public static final int MAX_SIZE_DATA = 1024;
    public static final int MAX_SIZE_MESSAGE = MAX_SIZE_DATA + 28;
    public static final String ENCRIPTION_KEY = "CC-G6-02";

    //Header Fields
    private final int id;
    private final String[] flags;
    private final int responseCode;
    private final int numberOfValues;
    private final int numberOfAuthorities;
    private final int numberOfExtra;
    //Data Fields
    // -> 2 Campos Query Info
    private final String name;
    private final String typeOfValue;
    private final String responseValue;
    private final String authoritiesValues;
    private final String extraValues;

    public DNSmessage(int id, String[] flags, int responseCode, int numberOfValues, int numberOfAuthorities, int numberOfExtra, String name, String typeOfValue, String responseValue, String authoritiesValues, String extraValues) {
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

    public DNSmessage(byte[] message) {
        ByteBuffer buffer = ByteBuffer.wrap(message);
        this.id = buffer.getShort();
        this.flags = new String[4];
        this.flags[0] = String.valueOf(buffer.getShort());
        this.flags[1] = String.valueOf(buffer.getShort());
        this.flags[2] = String.valueOf(buffer.getShort());
        this.flags[3] = String.valueOf(buffer.getShort());
        this.responseCode = buffer.getShort();
        this.numberOfValues = buffer.getShort();
        this.numberOfAuthorities = buffer.getShort();
        this.numberOfExtra = buffer.getShort();
        this.name = new String(buffer.array(), 28, 255, StandardCharsets.UTF_8);
        this.typeOfValue = new String(buffer.array(), 283, 255, StandardCharsets.UTF_8);
        this.responseValue = new String(buffer.array(), 538, 255, StandardCharsets.UTF_8);
        this.authoritiesValues = new String(buffer.array(), 793, 255, StandardCharsets.UTF_8);
        this.extraValues = new String(buffer.array(), 1048, 255, StandardCharsets.UTF_8);
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_SIZE_MESSAGE);
        buffer.putShort((short) this.id);
        buffer.putShort(Short.parseShort(this.flags[0]));
        buffer.putShort(Short.parseShort(this.flags[1]));
        buffer.putShort(Short.parseShort(this.flags[2]));
        buffer.putShort(Short.parseShort(this.flags[3]));
        buffer.putShort((short) this.responseCode);
        buffer.putShort((short) this.numberOfValues);
        buffer.putShort((short) this.numberOfAuthorities);
        buffer.putShort((short) this.numberOfExtra);
        buffer.put(this.name.getBytes(StandardCharsets.UTF_8));
        buffer.put(this.typeOfValue.getBytes(StandardCharsets.UTF_8));
        buffer.put(this.responseValue.getBytes(StandardCharsets.UTF_8));
        buffer.put(this.authoritiesValues.getBytes(StandardCharsets.UTF_8));
        buffer.put(this.extraValues.getBytes(StandardCharsets.UTF_8));
        return buffer.array();
    }

    public byte[] encrypt() throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(ENCRIPTION_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(this.toByteArray());
    }

    public static DNSmessage decrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(ENCRIPTION_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new DNSmessage(cipher.doFinal(message));
    }



}
