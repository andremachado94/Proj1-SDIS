/**
 * Created by andremachado on 16/03/2018.
 */
public class ControlMessageParser {
    public static int TYPE_UNKOWN = -1;
    public static int TYPE_STORED = 0;

    private Util u = new Util();

    public ControlMessageParser(){

    }

    public int GetMessageType(String msg){
        String data[] = msg.split(" ");
        if(data[0].equalsIgnoreCase("STORED")){
            return TYPE_STORED;
        }
        return TYPE_UNKOWN;
    }

    public StoredMessage ParseStoredMessage(String msg){
        String unparsedData[] = msg.split(" ");
        String unparsedMessageData[] = new String[6];

        //Delete excessive white space

        int j = 0;
        for (int i = 0 ; i<unparsedData.length ; i++)
        {
            if(j >= 6){
                System.out.println("Invalid (excessive) number of arguments in STORED\n");
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
            System.out.println("Invalid (defective) number of arguments in STORED\n");
            return null;
        }

        if(u.MessageTypeValidator(unparsedMessageData[0]) != Util.TYPE_STORED){
            System.out.println("Invalid type for STORED ");
            return null;
        }

        Version version;

        try {
            version = new Version(unparsedData[1]);
        }
        catch (IllegalArgumentException e){
            return null;
        }


        int peerId = Integer.parseInt(unparsedData[2]);

        if(false){ //TODO
            System.out.println("Invalid peerId number in STORED");
            return null;
        }



        String fileId = unparsedData[3];

        if(fileId.length() == 0){
            System.out.println("Invalid fileId in STORED");
            return null;
        }

        int chunkNum = Integer.parseInt(unparsedData[4]);

        if(false){ //TODO
            System.out.println("Invalid chunk number in STORED");
            return null;
        }

        return new StoredMessage(version,peerId,fileId,chunkNum);
    }
}
