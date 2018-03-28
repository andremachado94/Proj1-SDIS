import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by andremachado on 02/03/2018.
 */
public class Util {

    public static int TYPE_BACKUP = 1;
    public static int TYPE_RESTORE = 2;
    public static int TYPE_DELETE = 3;
    public static int TYPE_RECLAIM = 4;
    public static int TYPE_PUTCHUNK = 5;
    public static int TYPE_STORED = 6;
    public static int TYPE_ERROR = -1;

    public final String CRLF_CRLF = crlf() + crlf();
    public final String CRLF = crlf();

    private String crlf(){return Integer.toString(0xD, 16) + Integer.toString(0xA, 16);}

    public String getCRLF_CRLF(){return crlf() + crlf();}

    public static String IPV4_Validator(String ipString){
        String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipString);
        if (matcher.find()) {
            return matcher.group();
        } else{
            return "0.0.0.0";
        }
    }

    public static byte[] SHA256(String text){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(text.getBytes());
            //byte[] hash = digest.digest(text.getBytes(StandardCharsets.US_ASCII));

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String GetCleanId(String id){
        return String.valueOf(id.hashCode());
    }

    public static String SHA256_String(String text){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.US_ASCII));

            return new String(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void PrintInvalidHeadersMessage(){
        System.out.println("Invalid headers. Usage:");
        System.out.println("\tjava ServerlessFileSystem <MessageType> <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>");
    }

    public int MessageTypeValidator(String messageType){

        if(messageType.equalsIgnoreCase("BACKUP")){
            return TYPE_BACKUP;
        }
        else if(messageType.equalsIgnoreCase("RESTORE")){
            return TYPE_RESTORE;
        }
        else if(messageType.equalsIgnoreCase("DELETE")){
            return TYPE_DELETE;
        }
        else if(messageType.equalsIgnoreCase("RECLAIM")){
            return TYPE_RECLAIM;
        }
        else if(messageType.equalsIgnoreCase("PUTCHUNK")){
            return TYPE_PUTCHUNK;
        }
        else if(messageType.equalsIgnoreCase("STORED")){
            return TYPE_STORED;
        }
        else {
            return TYPE_ERROR;
        }
    }

    public byte[] ParseDataString(String data){
        return data.getBytes();
    }
}
