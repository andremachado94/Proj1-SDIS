import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by andremachado on 05/03/2018.
 */
public class FileManager {

    private static int MIN_CHUNK_SIZE = 0;
    private static int MAX_CHUNK_SIZE = 64000;
    private String crlf(){return Integer.toString(0xD, 16) + Integer.toString(0xA, 16);}

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

        return "PUTCHUNK " + version + " " + fileId + " " + chunkNumber + " " + repDegree + " " + crlf() + crlf() + data;
    }


    public Putchunk ParsePutChunkMessage(String msg){

        String unparsedData[] = msg.split(" ");
        String unparsedMessageData[] = new String[6];
        for (int i = 0 ; i<unparsedData.length ; i++)



        return null;
    }

}
