import java.io.*;
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



    public static ArrayList<byte[]> SliceFile(String path){

        ArrayList<byte[]> slicedFile = new ArrayList<byte[]>();

        int chunkSize = MAX_CHUNK_SIZE;

        if(chunkSize > MIN_CHUNK_SIZE && chunkSize <= MAX_CHUNK_SIZE) {
            try {
                FileInputStream is = new FileInputStream(new File(path));
                byte[] buffer = new byte[chunkSize];
                System.out.println("CHUNK SIZE: " + chunkSize);
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

    private static String GetChunkPathName(PutChunk putChunk){
        return putChunk.getFileId();
    }


    public static void WriteChunckToBinFile(PutChunk putChunk, int id){
        String filePathString = null;
        final String dir = System.getProperty("user.dir");

        final String peerdir = new File(dir).getParent()+"/"+"backup_chunks"+"/"+id;

        File f2 = new File(peerdir);
        if(!f2.exists()){
            try{
                f2.mkdir();
            }
            catch(SecurityException se){
                se.printStackTrace();
            }
        }


        try {
            filePathString = new File(dir).getParent()+"/"+"backup_chunks"+"/"+id+"/"+ Util.GetCleanId(putChunk.getFileId());
            System.out.println("PATH: " + filePathString);

            File f = new File(new File(dir).getParent()+"/"+"backup_chunks");

            if(!f.exists()){
                try{
                    f.mkdir();
                }
                catch(SecurityException se){
                    se.printStackTrace();
                }
            }

            f = new File(filePathString);
            if(!f.exists()){
                try{
                    f.mkdir();
                }
                catch(SecurityException se){
                    se.printStackTrace();
                }
            }

            if(f.exists() && f.isDirectory()) {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(filePathString+"/"+ putChunk.getChunkNumber()+".bin");
                    try {
                        fos.write(putChunk.getData(), 0, putChunk.getData().length);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("FILE NOT FOUND");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
