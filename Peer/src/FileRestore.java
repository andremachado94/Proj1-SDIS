import java.util.ArrayList;
import java.util.List;

/**
 * Created by andremachado on 29/03/2018.
 */
public class FileRestore {
    private String fileId;
    private String version;
    private String fileName;
    private List<GetChunkMessage> chunks;

    public FileRestore(String fileId, String version, String fileName){
        this.fileId = fileId;
        this.version = version;
        this.fileName = fileName;
        chunks = new ArrayList<GetChunkMessage>();
    }

    public void Add(GetChunkMessage rm){

    }


}
