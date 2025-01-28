import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
//TODO: set time out for server
//
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("=====================REDIS RUNNING=====================R");
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        Parser parser;
        int port = 6379;
        //key -> value
        HashMap<String, String> map;
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                    clientSocket.getOutputStream().write("======Welcome to Redis=======\r\n".getBytes());
                    map = new HashMap<>();
                    parser = new Parser(map);
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
