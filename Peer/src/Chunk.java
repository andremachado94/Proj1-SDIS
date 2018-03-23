/**
 * Created by andremachado on 05/03/2018.
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DataType;
import java.util.Date;

@DatabaseTable(tableName = "chunks")
public class Chunk {

    @DatabaseField(generatedId = true)
    private int id; //id, no hash needed (autoinc)
    // When an Order object is passed to create and stored to the database,
    // the generated identity value is returned by the database and set on
    // the object by ORMLite. In the majority of database types, the
    // generated value starts at 1 and increases by 1 every time a new row
    // is inserted into the table.

    @DatabaseField(canBeNull = false)
    private String fileId; //hash

    @DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    private Version version;

    @DatabaseField(canBeNull = false)
    private int chunkNumber;

    @DatabaseField(canBeNull = false)
    private int repDegree;

    @DatabaseField(dataType = DataType.DATE)
    private Date date;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] data;

    public Chunk(){
        // ORMLite needs a no-arg constructor
    }

    public Chunk(Version version, String fileId, int chunkNumber, int repDegree, byte[] data){
        this.version = version;
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
        this.repDegree = repDegree;
        this.data = data;
    }

    public Version getVersion() {
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

    public Date getDate() {
        return date;
    }

    public byte[] getData() {
        return data;
    }

    /*
    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return name.equals(((Account) other).name);
    }
    */
}
