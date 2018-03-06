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
    public static int TYPE_ERROR = -1;

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
        else {
            return TYPE_ERROR;
        }

    }
}
