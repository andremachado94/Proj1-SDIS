import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andremachado on 05/03/2018.
 */
public class FileManager {

    private Util u = new Util();

    private static int MIN_CHUNK_SIZE = 0;
    private static int MAX_CHUNK_SIZE = 64000;

    private String path;
    private int chunkSize;


    public int PreSize(String type, int id){
        return  u.getCRLF_CRLF().getBytes().length +
                type.getBytes().length +
                Integer.toString(id).getBytes().length +
                45;
    }


    public static ArrayList<byte[]> SliceFile(String path, int msgSize){

        ArrayList<byte[]> slicedFile = new ArrayList<byte[]>();

        int chunkSize = MAX_CHUNK_SIZE - msgSize;

        System.out.println("Chunk Size: " + chunkSize);
        System.out.println("PreMsg Size: " + msgSize);

        if(chunkSize > MIN_CHUNK_SIZE && chunkSize <= MAX_CHUNK_SIZE) {
            try {
                FileInputStream is = new FileInputStream(new File(path));
                byte[] buffer = new byte[chunkSize];
                int n;
                try {
                    while((n = is.read(buffer)) >= 0) {
                        byte[] dst = Arrays.copyOf(buffer, n);
                        System.out.println("Buffer Size: " + n);
                        slicedFile.add(dst);
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



}
