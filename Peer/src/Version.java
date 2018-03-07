/**
 * Created by andremachado on 07/03/2018.
 */
public class Version {
    private final int version;
    private final int subVersion;

    public Version(int version, int subVersion){
        this.version = version;
        this.subVersion = subVersion;
    }

    public int getVersion() {
        return version;
    }

    public int getSubVersion() {
        return subVersion;
    }
}
