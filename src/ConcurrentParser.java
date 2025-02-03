import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentParser {
    String input;
    ConcurrentHashMap<String, String> map;
    DataStore ds;
    public ConcurrentParser(ConcurrentHashMap<String, String> map, DataStore ds){
        this.map = map;
        this.ds = ds;

    }
    public String parse(String input){
        if(input==null){
            System.out.println("Unable to parse, empty input");
            return "========Empty Result===========\r\n";
        }
        String[] tokens = input.split("\\s+");
        if(tokens.length==1&&tokens[0].equals("QUIT")) return "QUIT";
        if(tokens.length<=1){
            System.out.println("Unable to parse, input format incorrect");
            return "========Empty Result===========\r\n";      
        }
        String command = tokens[0].toUpperCase();
        switch (command) {
            case "GET":
                if(tokens[1]==null) return "Unable to parse, input format incorrect\r\n";
                return map.getOrDefault(tokens[1], "=========Value Not Found=======") + "\r\n";
                
            case "SET":
                if(tokens[1]==null||tokens[2]==null) return ("Unable to parse, input format incorrect\r\n");
                if(map.containsKey(tokens[1])){
                    //handles overwrite
                }else{
                    map.put(tokens[1], tokens[2]);
                }
                return "======Set key: "+ tokens[1] + "==value: " + tokens[2] + "========\r\n";
            case "DELETE":
                if(tokens[1]==null) return "Unable to parse, input format incorrect\r\n";
                if(map.containsKey(tokens[1])){
                    map.remove(tokens[1]);
                    return "======Deleted key: "+ tokens[1] + "=======\r\n";
                }
                return "=======Key not found=========\r\n";
            //format: LPUSH listKey value
            case "LPUSH":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    ds.lPush(tokens[1], tokens[2]);
                }catch(Exception e){
                    return "=======Key not found=========\r\n";
                }
                return "======Added value: "+ tokens[2] + "==== to List: " + tokens[1]+ "===\r\n";
            //format: LPOP listKey
            case "LPOP":
                if(tokens.length<2||tokens[1]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    String res = ds.lPop(tokens[1]);
                    return "======Value popped: "+ res + "=========\r\n";
                }catch(Exception e){
                    return "Unable to parse, input format incorrect or List is not long enough\r\n";
                }
            //format: LSIZE key
            case "LSIZE":
                if(tokens.length<2||tokens[1]==null) return "Unable to parse, input format incorrect\r\n";
                int res = ds.lSize(tokens[1]);
                return "======Size of List " + tokens[1] + ": " + res + "========\r\n";
            //format: HPUT HashName, key, value
            case "HPUT":
                if(tokens.length<4||tokens[1]==null||tokens[2]==null||tokens[3]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    ds.hSet(tokens[1], tokens[2], tokens[3]);
                    return "====SET key value: " + tokens[2] + "=" + tokens[3] +"===in collection: " + tokens[1] + "=====\r\n";
                }catch(Exception e){
                    return "=======ERR=========\r\n";
                }
            //format: HGET HashName key
            case "HGET":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    String hGet = ds.hGet(tokens[1], tokens[2]);
                    return "====GOT value: " + hGet + "= from Key: " + tokens[2]+ "====from Hash" + tokens[1]+ "=====\r\n";
                }catch(Exception e){
                    return "=======ERR=========\r\n";
                }  
            //format: HDELETE HashName key
            case "HDEL":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    ds.hDelete(tokens[1], tokens[2]);
                    return "======Deleted key: "+ tokens[2] + "==from==" + tokens[1] + "=====\r\n";
                }catch(Exception e){
                    return "=======ERR=========\r\n";
                }
            //format HEXISTS key value
            case "HEXISTS":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    boolean hExists = ds.hExists(tokens[1], tokens[2]);
                    return "====="+ hExists+ "=====\r\n";
                }catch(Exception e){
                    return "=======ERR=========\r\n";
                }
            //format: SSET setName value 
            case "SADD":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    ds.sAdd(tokens[1], tokens[2]);
                }catch(Exception e){
                    return "=======Set not found=========\r\n";
                }
                return "======Added value: "+ tokens[2] + "==== to SET: " + tokens[1]+ "===\r\n";
            //format SREMOVE setName value
            case "SREMOVE":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    ds.sRemove(tokens[1], tokens[2]);
                }catch(Exception e){
                    return "=======Set not found=========\r\n";
                }
                return "======removed value: "+ tokens[2] + "==== from SET: " + tokens[1]+ "===\r\n";
            //format SCONTAINS SET VALUE
            case "SCONTAINS":
                if(tokens.length<3||tokens[1]==null||tokens[2]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    boolean sContains = ds.sContains(tokens[1], tokens[2]);
                    return "======"  + sContains + "======\r\n";
                }catch(Exception e){
                    return "=======Set not found=========\r\n";
                }
            //format SSIZE setName
            case "SSIZE":
                if(tokens.length<2||tokens[1]==null) return "Unable to parse, input format incorrect\r\n";
                try{
                    int sSize = ds.sSize(tokens[1]);
                    return "======"  + sSize + "======\r\n";
                }catch(Exception e){
                    return "=======Set not found=========\r\n";
                }           
            case "QUIT":
                return "QUIT";
            default:
                return "UNKNOWN";
        }
    }
}

