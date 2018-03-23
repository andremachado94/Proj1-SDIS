/**
 * Created by andremachado on 05/03/2018.
 */

//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
//import com.j256.ormlite.field.DataType;
import java.util.Date;

//@DatabaseTable(tableName = "chunks")
public class Chunk {

    private static Util u = new Util();


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

    //@DatabaseField(canBeNull = false)
    private int repDegree;

    //@DatabaseField(dataType = DataType.DATE)
    private Date date;

    //@DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] data;

    private int peerId;

    public Chunk(){
        // ORMLite needs a no-arg constructor
    }

    public Chunk(Version version, int senderId, String fileId, int chunkNumber, int repDegree, byte[] data){
        this.version = version;
        this.peerId = senderId;
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

    public int getPeerId() {
        return peerId;
    }


    public static byte[] GetPutChunkMessage(byte[] chunk,int chunkNumber, String version, int peerId, int repDegree){

        if(version == null){
            version = "1.0"; //default version
        }

        //TODO hash function
        String fileId = "CCasdasdasdasdasdasdasdasC";

        String dataString = "PUTCHUNK " + version + " " + peerId + " ";
        byte preData[] = dataString.getBytes();
        preData = ByteConcat(preData, u.SHA256(fileId));

        dataString = " " + chunkNumber + " " + repDegree + " " + u.CRLF_CRLF;
        preData = ByteConcat(preData, dataString.getBytes());

        byte[] data = ByteConcat(preData, chunk);


        return data;
    }

    private static byte[] ByteConcat(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }

    public static String GetPutChunkMessage(String chunk,int chunkNumber, String version, int peerId, int repDegree){

        if(version == null){
            version = "1.0"; //default version
        }

        //TODO hash function
        String fileId = "CCC";

        return "PUTCHUNK " + version + " " + peerId + " " + fileId + " " + chunkNumber + " " + repDegree + " " + u.CRLF_CRLF + chunk;
    }


    //TODO change this to constructor??
    public static Chunk ParsePutChunkMessage(String msg){

        String crlfSplit[] = msg.split(u.CRLF_CRLF);

        if(crlfSplit.length != 2){
            System.out.println("Invalid message format. <CRLF><CRLF> missing - length:" + crlfSplit.length);
            return null;
        }

        String unparsedData[] = crlfSplit[0].split(" ");
        String unparsedMessageData[] = new String[6];

        //Delete excessive white space

        int j = 0;
        for (int i = 0 ; i<unparsedData.length ; i++)
        {
            if(j > 6){
                System.out.println("Invalid (excessive) number of arguments - " + j);
                return null;
            }
            else if(unparsedData[i].length() == 0){
                continue;
            }
            else{
                unparsedMessageData[j++] = unparsedData[i];
            }
        }

        if(j != 6){
            System.out.println("Invalid (defective) number of arguments\n");
            return null;
        }

        if(u.MessageTypeValidator(unparsedMessageData[0]) != Util.TYPE_PUTCHUNK){
            System.out.println("Invalid type for PUTCHUNK ");
            return null;
        }

        Version version;

        try {
            version = new Version(unparsedData[1]);
        }
        catch (IllegalArgumentException e){
            return null;
        }


        int peerId = Integer.parseInt(unparsedData[2]);

        if(false){ //TODO
            System.out.println("Invalid peerId number in PUTCHUNK");
            return null;
        }



        String fileId = unparsedData[3];

        if(fileId.length() == 0){
            System.out.println("Invalid fileId in PUTCHUNK");
            return null;
        }

        int chunkNum = Integer.parseInt(unparsedData[4]);

        if(false){ //TODO
            System.out.println("Invalid chunk number in PUTCHUNK");
            return null;
        }

        int repDegree = Integer.parseInt(unparsedData[5]);

        if(false){ //TODO
            System.out.println("Invalid replication degree in PUTCHUNK");
            return null;
        }

        byte data[];

        if((data = u.ParseDataString(crlfSplit[1])) == null){
            System.out.println("Invalid data field in PUTCHUNK");
            return null;
        }


        return new Chunk(version, peerId ,fileId, chunkNum, repDegree, data);
    }


    public String GetStoredMessage(int id){
        return "STORED " + version + " " + id + " " + fileId + " " + chunkNumber + " " + u.CRLF_CRLF;
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
