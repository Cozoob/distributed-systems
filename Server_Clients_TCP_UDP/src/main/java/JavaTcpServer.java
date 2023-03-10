import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class JavaTcpServer {

    public static void main(String[] args) throws IOException {

        System.out.println("JAVA TCP SERVER");
        int portNumber = 12345;
        ServerSocket serverSocket = null;

        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);

            while(true){

                // accept client
                Socket clientSocket = serverSocket.accept();
//                System.out.println("client connected");


                // TODO create new thread...


                // in & out streams
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String nickname = in.readLine();
                System.out.println("Client " + nickname + " connected to the server!");

                while(!clientSocket.isClosed()){
                    try {
                        // read msg, send response
                        String msg = in.readLine();
                        if(!msg.isBlank()){
                            System.out.println(nickname + ">" + msg);
                            out.println("Pong Java Tcp");
                        }
//                        System.out.println(nickname + ">" + msg);
//                        out.println("Pong Java Tcp");
                    } catch (IOException e) {
                        // to jako dodatkowe zabezpieczenie gdyby nagle klient stracil polaczenie?
                        e.printStackTrace();
                        clientSocket.close();
                    } finally {
//                        clientSocket.close();
//                        System.out.println(clientSocket);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (serverSocket != null){
                serverSocket.close();
            }
        }
    }

}
