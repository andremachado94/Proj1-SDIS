import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public class BackupFile extends File implements Serializable {
    protected byte[] dataBytes;

    public BackupFile(String pathname) throws IOException {
        super(pathname);
        if (!this.exists()) throw new FileNotFoundException(pathname);
        dataBytes = Files.readAllBytes(this.toPath());
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }
}
