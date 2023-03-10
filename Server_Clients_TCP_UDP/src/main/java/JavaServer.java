import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class JavaServer {

    public static void main(String[] args) {
        ArrayList<ServerSocket> clients = new ArrayList<>();

        System.out.println("JAVA TCP SERVER");
        int portNumber = 12345;
        ServerSocket serverSocket = null;

        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);

            while(true){

                // accept client
                Socket clientSocket = serverSocket.accept();

                // in & out streams
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Show which client has been connected
                String nickname = in.readLine();
                System.out.println(nickname + " connected to the server!");

                // Create new thread for the client
                ClientHandler clientHandler = new ClientHandler(clientSocket, nickname);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // ClientHandler Class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String nickname;
        public ClientHandler(Socket socket, String nickname) {
            this.clientSocket = socket;
            this.nickname = nickname;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                // get the output and input stream of the client
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // printing out the received message from client
                String line;
                while((line = in.readLine()) != null){
                    // TODO TO DELETE - server shouldn't show clients messages
                    String msg = nickname + ">" + line;
                    System.out.println("SERVER: " + msg);
                    out.println(msg);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try{
                    if(in != null){
                        in.close();
                    }
                    if(out != null) {
                        out.close();
                    }
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
