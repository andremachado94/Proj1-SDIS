/**
 * Created by andremachado on 30/03/2018.
 */
public class DeleteMessage {
    private String fileId;
    private Version version;
    private int chunkNumber;

    private int peerId;
    private static Util u = new Util();

    public DeleteMessage(Version version, int peerId, String fileId){
        this.fileId = fileId;
        this.peerId = peerId;
        this.version = version;
    }

    public static String GetDeleteMessage(String version, int peerId, String fileName){
        return "DELETE " + version + " " + peerId + " " + new String(Util.SHA256(fileName)) + " " + u.getCRLF_CRLF();
    }

    public String GetFileId() {
        return fileId;
    }

    public String GetVersion() {
        return version.toString();
    }
}
