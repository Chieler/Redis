import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
//TODO: create multithreading to allow multi user and multiCollections
/*
 * Command format:
 * QUIT: quit
 * SET key value: sets key associated with value
 * GET key: gets value associated with Key
 * DELETE key: removes key 
 * SSET ADD value: adds value to a Set
 * SSET DELTE value: deletes value from a set
 * SSET CONTAINS value: checks if value is in a set
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("=====================REDIS RUNNING=====================R");
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        Parser parser;
        int port = 6379;
        //time out set as 30 minutes
        int timeoutMillis = 1800000;
        //key -> value
        HashMap<String, String> map;
        HashSet<String> set;
        
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(timeoutMillis);
            map = new HashMap<>();
            set = new HashSet<>();
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                    clientSocket.getOutputStream().write("======Welcome to Redis=======\r\n".getBytes());
                    parser = new Parser(map, set);
                    handleClient(clientSocket, parser);
                    System.out.println("=======Quit=======");
                    break;
                } catch (Exception e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }

        }catch(Exception e){
            System.out.println("==========Encountered Error in Main===========");
            System.out.println((e));
        }finally{
            try{
                if(clientSocket!=null){
                    clientSocket.close();
                    System.out.println("Client connection closed.");
                }
            }catch(Exception e){
                System.out.println("==========Encountered Error in Main when closing client socket===========");
                System.out.println((e));
            }
        }
    }
    public static void handleClient(Socket clientSocket, Parser parser){
        System.out.println("Ready for taking input from " + clientSocket.getRemoteSocketAddress());
        try{
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(true){
                line = bufferedReader.readLine();
                if(line==null) continue;
                System.out.println("============Received Command===========");
                line.trim();
                if(line!=null){
                    String res = parser.parse(line);
                    if(res.equals("QUIT")){
                        break;
                    }else if(res.equals("UNKNOWN")){
                        clientSocket.getOutputStream().write("-ERR unknown command\r\n".getBytes());
                    }else{
                        clientSocket.getOutputStream().write(res.getBytes());
                    }
                }

            }
        }catch(Exception e){
            System.out.println("=======Encountered Error in handleClient======");
            System.out.println((e));
        }


    }
    
}
