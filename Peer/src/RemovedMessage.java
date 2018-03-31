/**
 * Created by andremachado on 30/03/2018.
 */
public class RemovedMessage {
    private String fileId;
    private Version version;
    private int chunkNumber;

    private int peerId;
    private static Util u = new Util();

    public RemovedMessage(Version version, int peerId, String fileId, int chunkNumber){
        this.fileId = fileId;
        this.peerId = peerId;
        this.version = version;
        this.chunkNumber = chunkNumber;
    }

    public static String GetRemovedMessage(String version, int peerId, String fileName, int chunkNumber){
        return "DELETE " + version + " " + peerId + " " + new String(Util.SHA256(fileName))+ " " + chunkNumber + " " + u.getCRLF_CRLF();
    }

    public String GetFileId() {
        return fileId;
    }

    public int GetChunkNumber(){
        return chunkNumber;
    }

    public int GetPeerId(){
        return peerId;
    }
}
