import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by andremachado on 29/03/2018.
 */
public class ChunkMessage {

    private int id;
    private static Util u = new Util();

    private Version version;
    private String fileId;


    private int requesterId;
    private int chunkNumber;

    private byte[] data;


    //For TCP
    private String requesterIp;
    private int requesterPort;

    public ChunkMessage(GetChunkMessage getChunkMessage, int id){
        this.id = id;
        version = getChunkMessage.GetVersion();
        fileId = getChunkMessage.GetFileId();
        chunkNumber = getChunkMessage.GetChunkNumber();
        requesterId = getChunkMessage.GetPeerId();
    }

    public ChunkMessage(Version version, int peerId, String fileId, int chunkNumber, byte[] data) {
        this.version = version;
        requesterId = peerId;
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
        this.data = data;
    }

    public byte[] GetChunkMessage(){

        final String dir = System.getProperty("user.dir");
        final String filedir = new File(dir).getParent()+"/"+"backup_chunks"+"/"+id+"/"+Util.GetCleanId(fileId)+"/"+chunkNumber+".bin";

        Path path = Paths.get(filedir);

        if(!Files.exists(path)) {
            return null;
        }

        byte[] chunkData;

        try {
            chunkData = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String version;

        if(this.version == null){
            version = "1.0";
        }
        else{
            version = this.version.toString();
        }

        String dataString = "CHUNK " + version + " " + id + " " + fileId + " " + chunkNumber + " " + u.CRLF_CRLF;
        byte preData[] = dataString.getBytes();



        byte[] data = ByteConcat(preData, chunkData);


        return data;
    }

    private static byte[] ByteConcat(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }



    //CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
    public static ChunkMessage ParseChunkMessage(byte[] receivedData) {
        StreamSearcher streamSearcher = new StreamSearcher(u.CRLF_CRLF.getBytes());
        byte data[];

        try {
            long index = streamSearcher.search(receivedData);
            if(index == -1){
                return null;
            }

            data = Arrays.copyOfRange(receivedData, (int) index, receivedData.length);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String msg = new String(receivedData);


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
            if(j > 5){
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

        if(j != 5){
            System.out.println("Invalid (defective) number of arguments\n");
            return null;
        }

        if(u.MessageTypeValidator(unparsedMessageData[0]) != Util.TYPE_CHUNK){
            System.out.println("Invalid type for CHUNK ");
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

        if(false){
            System.out.println("Invalid peerId number in CHUNK");
            return null;
        }



        String fileId = unparsedData[3];

        if(fileId.length() == 0){
            System.out.println("Invalid fileId in CHUNK");
            return null;
        }

        int chunkNum = Integer.parseInt(unparsedData[4]);

        if(false){
            System.out.println("Invalid chunk number in CHUNK");
            return null;
        }

/*
        if((data = u.ParseDataString(crlfSplit[1])) == null){
            System.out.println("Invalid data field in PUTCHUNK");
            return null;
        }
*/


        return new ChunkMessage(version, peerId ,fileId, chunkNum, data);
    }

    public int getRequesterId() {
        return requesterId;
    }
    public byte[] getData() {
        return data;
    }
    public String getFileId() {
        return fileId;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }
}
