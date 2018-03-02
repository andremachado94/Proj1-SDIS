import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andremachado on 02/03/2018.
 */
public class Util {

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

    public void printInvalidHeadersMessage(){
        System.out.println("Invalid headers. Usage:");
        System.out.println("\tjava ServerlessFileSystem <MessageType> <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>");
    }

}
