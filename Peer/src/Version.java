/**
 * Created by andremachado on 07/03/2018.
 */
public class Version {
    private final int version;
    private final int subVersion;

    public Version(String version) throws IllegalArgumentException{
        String unparsedData[] = version.split(".");

        if(unparsedData.length != 2){
            System.out.println("Invalid version format");
            throw new IllegalArgumentException();
        }

        this.version = Integer.parseInt(unparsedData[0]);
        this.subVersion = Integer.parseInt(unparsedData[1]);
    }

    public Version(int version, int subVersion){
        this.version = version;
        this.subVersion = subVersion;
        throw new IllegalArgumentException();
    }

    public int getVersion() {
        return version;
    }

    public int getSubVersion() {
        return subVersion;
    }

    @Override
    public String toString(){
        return version + "." + subVersion;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return hashCode() == other.hashCode();
    }
}
