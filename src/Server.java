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

//TODO: basic write-ahead log (WAL) for logging
//Maybe even a .db file for it
public class Server { 
    //Initializes Default port, timeout, thread pool size, and concurrentMap
    private static final int DEFAULT_PORT = 6379;
    private static final int SOCKET_TIMEOUT = 1800000;
    //change the to set pool size
    private static final int THREAD_POOL_SIZE = 10;
    private static volatile int port = DEFAULT_PORT;
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    //ThreadPool executtor to manage threads
    private static final ExecutorService threadPool = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 60L, TimeUnit.SECONDS, 
    new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        //add ShutDownHook to dictate how to shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Shutting down Server");
            CloseServer();
        }));
        //starts server
        StartServer();
    }


    private static void StartServer(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            //Creates ONE server and sets config
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            //while the current thread is not interrupted
            while(!Thread.currentThread().isInterrupted()){
                try{
                    //we create a new client when one wishes to connect
                    Socket clientSocket = serverSocket.accept();
                    //then create a new thread for the client to run on
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
        //shuts down threadpool
        threadPool.shutdown();
        try{
            //waits for 45 seconds for it to shutdown, if fails forcefully shutsdown
            if(!threadPool.awaitTermination(45L, TimeUnit.SECONDS)){
                threadPool.shutdownNow();
            }
        }catch(Exception e){
            System.out.println("==========Encountered Error CloseServer===========");
            //If error forces shutdown and interrupts current thread
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
        try(InputStream inputStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));){

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
