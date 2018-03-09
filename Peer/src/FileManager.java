import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by andremachado on 05/03/2018.
 */
public class FileManager {

    private Util u = new Util();

    private static int MIN_CHUNK_SIZE = 0;
    private static int MAX_CHUNK_SIZE = 64000;

    public ArrayList<byte[]> SliceFile(String path, int chunkSize){

        ArrayList<byte[]> slicedFile = new ArrayList<byte[]>();

        if(chunkSize > MIN_CHUNK_SIZE && chunkSize <= MAX_CHUNK_SIZE) {
            try {
                FileInputStream is = new FileInputStream(new File(path));
                byte[] buffer = new byte[chunkSize];
                int n;
                try {
                    while((n = is.read(buffer)) > 0) {
                        slicedFile.add(buffer);
                    }
                    return slicedFile;
                } catch (IOException e) {
                    System.out.println("Error reading file.");
                    try {
                        is.close();
                    } catch (IOException e1) {
                        System.out.println("Error closing file.");
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }

                try {
                    is.close();
                    return null;
                } catch (IOException e) {
                    System.out.println("Error closing file after reading file.");
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                System.out.println("File " + path + " not found.");
                e.printStackTrace();
            }
        }
        else {
            System.out.println(chunkSize + " is an invalid chunk size.\nShould be between 1 and 64000");
        }
        return null;
    }

    public String GetPutChunkMessage(byte[] chunk, int chunkNumber, String version, int peerId, int repDegree){

        if(version == null){
            version = "1.0"; //default version
        }

        String fileId = "CCC";
        String data = new String(chunk).trim();

        return "PUTCHUNK " + version + " " + fileId + " " + chunkNumber + " " + repDegree + " " + u.CRLF_CRLF + data;
    }

    public Chunk ParsePutChunkMessage(String msg){

        String unparsedData[] = msg.split(" ");
        String unparsedMessageData[] = new String[6];

        //Delete excessive white space

        int j = 0;
        for (int i = 0 ; i<unparsedData.length ; i++)
        {
            if(j >= 6){
                System.out.println("Invalid (excessive) number of arguments\n");
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



        String fileId = unparsedData[2];

        if(fileId.length() == 0){
            System.out.println("Invalid fileId in PUTCHUNK");
            return null;
        }

        int chunkNum = Integer.getInteger(unparsedData[3]);

        if(false){ //TODO
            System.out.println("Invalid chunk number in PUTCHUNK");
            return null;
        }

        int repDegree = Integer.getInteger(unparsedData[4]);

        if(false){ //TODO
            System.out.println("Invalid replication degree in PUTCHUNK");
            return null;
        }

        byte data[];

        if((data = u.ParseDataString(unparsedData[5])) == null){
            System.out.println("Invalid data field in PUTCHUNK");
            return null;
        }


        return new Chunk(version, fileId, chunkNum, repDegree, data);
    }


}
