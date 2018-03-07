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

    public static int TYPE_ERROR = -1;

    public final String CRLF_CRLF = crlf() + crlf();
    public final String CRLF = crlf();

    private String crlf(){return Integer.toString(0xD, 16) + Integer.toString(0xA, 16);}

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
        else {
            return TYPE_ERROR;
        }

    }

    public Version VersionParser(String version){
        String unparsedData[] = version.split(".");
        int v;
        int sv;

        if(unparsedData.length != 2){
            System.out.println("Invalid version format");
            return null;
        }

        v = Integer.parseInt(unparsedData[0]);
        sv = Integer.parseInt(unparsedData[1]);

        return new Version(v, sv);
    }

    public byte[] ParseDataString(String data){
        if(data.startsWith(CRLF_CRLF)){
            data = data.substring(4, data.length());
            return data.getBytes();
        }
        else {
            System.out.println("<CRLF><CRLF> not found in data parser");
            return null;
        }
    }
}
