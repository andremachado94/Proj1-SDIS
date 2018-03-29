/**
 * Created by andremachado on 29/03/2018.
 */
public class GetChunkMessage {

    private String fileId;
    private Version version;
    private int chunkNumber;

    private int peerId;
    private Util u = new Util();

    public GetChunkMessage(Version version, int peerId, String fileId, int chunkNumber){
        this.chunkNumber = chunkNumber;
        this.fileId = fileId;
        this.peerId = peerId;
        this.version = version;
    }

    public String GetFileId() {
        return fileId;
    }

    public Version GetVersion() {
        return version;
    }

    public int GetChunkNumber() {
        return chunkNumber;
    }

    public int GetPeerId() {
        return peerId;
    }

    public String GetMessage(){
        return "GETCHUNK " + version.toString() + " " + peerId + " " + fileId + " " + chunkNumber + u.getCRLF_CRLF();
    }
}
