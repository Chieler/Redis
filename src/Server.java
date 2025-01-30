import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    //Initializes Default port, timeout, thread pool size, and concurrentMap
    private static final int DEFAULT_PORT = 6379;
    private static final int SOCKET_TIMEOUT = 1800000;
    //change the to set pool size
    private static final int THREAD_POOL_SIZE = 10;
    private static volatile int port = DEFAULT_PORT;
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();



    private static final ExecutorService threadPool = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 60L, TimeUnit.SECONDS, 
    new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Shutting down Server");
            CloseServer();
        }));
        StartServer();
    }



    // }
    private static void StartServer(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            while(!Thread.currentThread().isInterrupted()){
                try{
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(new ClientHandler(clientSocket, map));
                }catch(Exception e){
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }catch(Exception e){
            System.out.println("==========Encountered Error StartServer===========");
        }
    }
    private static void CloseServer(){
        threadPool.shutdown();
        try{
            if(!threadPool.awaitTermination(30L, TimeUnit.SECONDS)){
                threadPool.shutdownNow();
            }
        }catch(Exception e){
            System.out.println("==========Encountered Error CloseServer===========");
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    private static void welcomeMessage(Socket clientSocket){
        try{
            clientSocket.getOutputStream().write("======Welcome to Redis=======\r\n".getBytes());
        }catch(Exception e){
            System.out.println("==========Encountered welcomeMessage Error===========");
        }
       
    }
    private static class ClientHandler implements Runnable{
        private final Socket clientSocket;
        private final ConcurrentHashMap<String, String> map;
        private final ConcurrentParser parser;
        public ClientHandler(Socket socket, ConcurrentHashMap<String, String> map){
            this.clientSocket = socket;
            this.map = map;
            parser = new ConcurrentParser(map);
        }
        
        @Override
        public void run(){
        System.out.println("Ready for taking input from " + clientSocket.getRemoteSocketAddress());
        welcomeMessage(clientSocket);
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
                        System.out.println("=======Quit=======");
                        clientSocket.close();
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
}
