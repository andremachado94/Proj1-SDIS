import java.util.Date;

/**
 * Created by andremachado on 16/03/2018.
 */
public class StoredMessage {
    //@DatabaseField(generatedId = true)
    private int id; //id, no hash needed (autoinc)
    // When an Order object is passed to create and stored to the database,
    // the generated identity value is returned by the database and set on
    // the object by ORMLite. In the majority of database types, the
    // generated value starts at 1 and increases by 1 every time a new row
    // is inserted into the table.

    //@DatabaseField(canBeNull = false)
    private String fileId; //hash

    //@DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    private Version version;

    //@DatabaseField(canBeNull = false)
    private int chunkNumber;

    private int peerId;

    public StoredMessage(Version version, int peerId, String fileId, int chunkNumber){
        this.chunkNumber = chunkNumber;
        this.fileId = fileId;
        this.peerId = peerId;
        this.version = version;
    }

    public String getFileId() {
        return fileId;
    }

    public Version getVersion() {
        return version;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public int getPeerId() {
        return peerId;
    }
}
