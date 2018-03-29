import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public class BackupFile implements Serializable { // TODO google serializable
    protected byte[] dataBytes;
    protected String pathname;
    protected long length;
    protected long lastModified;

    // TODO everyting must be done at constructor
    // BackupFile must not depend on File
    public BackupFile(){}
    public BackupFile(String pathname) throws IOException {
        init(pathname);
    }

    public void init(String pathname) throws IOException {

        File file = new File(pathname);
        if (!file.exists()) throw new FileNotFoundException(pathname);
        this.dataBytes = Files.readAllBytes(file.toPath());

        this.pathname = pathname;
        this.length = file.length();
        this.lastModified = file.lastModified();

    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public String getPathname() {
        return pathname;
    }

    public long getLength() {
        return length;
    }

    public long getLastModified() {
        return lastModified;
    }
}
