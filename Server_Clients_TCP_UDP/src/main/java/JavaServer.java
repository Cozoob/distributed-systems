import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Objects;

public class JavaServer {

    public static void main(String[] args) {
        HashMap<String, Pair> clients = new HashMap<>();

        System.out.println("JAVA TCP SERVER");
        int portNumber = 12345;
        ServerSocket serverSocket = null;

        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);

            while (true) {

                // accept client
                Socket clientSocket = serverSocket.accept();

                // in & out streams
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Show which client has been connected
                String nickname = in.readLine();
                System.out.println(nickname + " connected to the server!");

                // Create new thread for the client
                Thread thread = new Thread(new ClientHandler(clientSocket, nickname, clients));
                clients.put(nickname, new Pair(thread, clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("HERE");
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // ClientHandler Class
    public static class ClientHandler implements Runnable {
        private static final String CLOSE_CONNECTION_VALUE = "--exit";
        private final Socket clientSocket;
        private final HashMap<String, Pair> clients;
        public final String nickname;

        public ClientHandler(Socket socket, String nickname, HashMap<String, Pair> clients) {
            this.clientSocket = socket;
            this.nickname = nickname;
            this.clients = clients;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                // get the output and input stream of the client
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Send message for other users that I joined
                for (String otherUserNickname : clients.keySet()) {
                    if (Objects.equals(otherUserNickname, nickname)) {
                        // Don't send the message to the sender
                        continue;
                    }

                    Socket otherSocket = clients.get(otherUserNickname).getSocket();
                    PrintWriter otherOut = new PrintWriter(otherSocket.getOutputStream(), true);
                    otherOut.println(nickname + " joined the chat.");
                }

                // printing out the received message from client
                String line;
                boolean isConnected;
                while ((line = in.readLine()) != null) {
                    // Check if the user want to close the connection
                    isConnected = !line.equals(CLOSE_CONNECTION_VALUE);

                    if (!isConnected) {
                        System.out.println(nickname + " disconnected :(");
                    }

                    // Send message for other users
                    for (String otherUserNickname : clients.keySet()) {
                        if (Objects.equals(otherUserNickname, nickname)) {
                            // Don't send the message to the sender
                            continue;
                        }

                        Socket otherSocket = clients.get(otherUserNickname).getSocket();
                        PrintWriter otherOut = new PrintWriter(otherSocket.getOutputStream(), true);

                        // Check if the user want to close the connection, send this information to others
                        if (!isConnected) {
                            otherOut.println(nickname + " left the chat.");
                        } else {
                            otherOut.println(nickname + ">" + line);
                        }
                    }
                }
            } catch (SocketException ex) {
                System.out.println(nickname + " has been lost :(");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    clientSocket.close();
                    // Delete this client from hashmap
                    clients.remove(nickname);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Pair class
    public static class Pair {
        private final Thread thread;
        private final Socket socket;

        public Pair(Thread thread, Socket socket) {
            this.thread = thread;
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }

        public Thread getThread() {
            return thread;
        }
    }
}
