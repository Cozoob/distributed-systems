import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class JavaServer {
    // Constant value
    private final static String NICKNAME_NOT_UNIQUE = "--nickname-error";
    private final static String NICKNAME_ASSIGNED = "--nickname-assigned";
    private final static String HOST_NAME = "localhost";
    private final static Integer PORT_NUMBER = 12345;
    private final static String SPECIAL_EVENT_MARK = "***";

    public static void main(String[] args) {
        HashMap<String, Pair> clients = new HashMap<>();

        System.out.println("JAVA TCP SERVER");
        ServerSocket serverSocketTCP = null;

        try {
            // create socket for TCP
            serverSocketTCP = new ServerSocket(PORT_NUMBER);

            // Listen to the possible received messages via UDP
            handleUDPMessage(clients);

            while (true) {

                // accept client
                Socket clientSocket = serverSocketTCP.accept();

                // in & out streams
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Check the nickname
                String nickname = in.readLine();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                if (clients.containsKey(nickname)) {
                    out.println(NICKNAME_NOT_UNIQUE);
                    clientSocket.close();
                    continue;
                } else {
                    out.println(NICKNAME_ASSIGNED);
                }

                // Show which client has been connected
                System.out.println(SPECIAL_EVENT_MARK + nickname + " connected to the server!" + SPECIAL_EVENT_MARK);

                // Create new thread for the client
                Thread thread = new Thread(new ClientHandler(clientSocket, nickname, clients));
                clients.put(nickname, new Pair(thread, clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("HERE");
            e.printStackTrace();
        } finally {
            if (serverSocketTCP != null) {
                try {
                    serverSocketTCP.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void handleUDPMessage(HashMap<String, Pair> clients) {
        new Thread(() -> {
            // create socket for UDP
            try (DatagramSocket serverSocketUDP = new DatagramSocket(PORT_NUMBER)) {
                while (true) {
                    // Wait for the message from client
                    byte[] receiveBuffer = new byte[1024];
                    Arrays.fill(receiveBuffer, (byte) 0);
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocketUDP.receive(receivePacket);

                    // Convert message to String and print it
                    String[] receivedMsg = new String(receivePacket.getData()).split("\\$", 2);

                    // Read first line which is nickname of the sender
                    String nickname = receivedMsg[0].trim();
                    String art = receivedMsg[1];

                    // Send to other users
                    for (String otherNickname : clients.keySet()) {
                        if (otherNickname.equals(nickname)) {
                            // Do not send message to the sender
                            continue;
                        }
                        try (DatagramSocket otherSocket = new DatagramSocket()) {
                            int otherPort = clients.get(otherNickname).getSocket().getPort();

                            String message = nickname + "(UDP)>\n" + art;

                            byte[] sendBuffer = message.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(HOST_NAME), otherPort);
                            otherSocket.send(sendPacket);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            } catch (SocketException ex) {
                System.out.println("SOCKET UDP EXCEPTION");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ClientHandler Class
    private static class ClientHandler implements Runnable {
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
                    otherOut.println(SPECIAL_EVENT_MARK + nickname + " joined the chat." + SPECIAL_EVENT_MARK);
                }

                // printing out the received message from client
                String line;
                boolean isConnected;
                while ((line = in.readLine()) != null) {
                    // Check if the user want to close the connection
                    isConnected = !line.equals(CLOSE_CONNECTION_VALUE);

                    if (!isConnected) {
                        System.out.println(SPECIAL_EVENT_MARK + nickname + " disconnected :(" + SPECIAL_EVENT_MARK);
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
                            otherOut.println(SPECIAL_EVENT_MARK + nickname + " left the chat." + SPECIAL_EVENT_MARK);
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
    private static class Pair {
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
