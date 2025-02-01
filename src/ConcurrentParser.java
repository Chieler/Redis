import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentParser {
    String input;
    ConcurrentHashMap<String, String> map;

    public ConcurrentParser(ConcurrentHashMap<String, String> map){
        this.map = map;


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

            case "QUIT":
                return "QUIT";
            default:
                return "UNKNOWN";
        }
    }
}

