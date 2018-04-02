import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by andremachado on 02/03/2018.
 */
public class Util {

    public static int TYPE_CHUNK = 8;
    public static int TYPE_BACKUP = 1;
    public static int TYPE_RESTORE = 2;
    public static int TYPE_DELETE = 3;
    public static int TYPE_RECLAIM = 4;
    public static int TYPE_PUTCHUNK = 5;
    public static int TYPE_STORED = 6;
    public static int TYPE_GETCHUNK = 7;

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
            digest.update(text.getBytes(StandardCharsets.US_ASCII));

            String hash = new String(digest.digest());
            System.out.println("\t\t\tBefore replace: " + hash);
            hash = hash.replaceAll("\\s+", "");
            System.out.println("\t\t\tAfter replace: " + hash);


            return hash.getBytes(StandardCharsets.US_ASCII);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String GetCleanId(String id){
        return String.valueOf(id.hashCode());
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
        else if(messageType.equalsIgnoreCase("GETCHUNK")){
            return TYPE_GETCHUNK;
        }
        else if(messageType.equalsIgnoreCase("CHUNK")){
            return TYPE_CHUNK;
        }
        else {
            return TYPE_ERROR;
        }
    }

    public byte[] ParseDataString(String data){
        return data.getBytes();
    }

    public static long PathSize(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {

                    System.out.println("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }
}
