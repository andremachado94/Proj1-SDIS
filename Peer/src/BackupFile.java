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
    protected int repDegree;
    protected String filename;
    protected Version version;

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
        this.filename = file.getName();
        // TODO check to see if file is different from the ones backed-up. If so, increment the version
        this.version = new Version("1.0");
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

    public String getVersion() {
        return version.toString();
    }

    public int GetRepDegree() {
        return repDegree;
    }

    public String getFileName() {
        return filename;
    }
}
