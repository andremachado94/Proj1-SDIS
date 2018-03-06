/**
 * Created by andremachado on 05/03/2018.
 */
public class Putchunk {

    private String version;
    private String fileId;
    private int chunkNumber;
    private int repDegree;
    private byte[] data;

    public Putchunk(String version, String fileId, int chunkNumber, int repDegree, byte[] data){
        this.version = version;
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
        this.repDegree = repDegree;
        this.data = data;
    }


    public String getVersion() {
        return version;
    }

    public String getFileId() {
        return fileId;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public int getRepDegree() {
        return repDegree;
    }

    public byte[] getData() {
        return data;
    }
}
