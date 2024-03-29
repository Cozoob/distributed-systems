import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JavaClient {
    // Constant value
    private final static String HOST_NAME = "localhost";
    private final static Integer PORT_NUMBER = 12345;
    private final static String MULTICAST_IP = "230.0.0.0";
    private final static Integer MULTICAST_PORT = 4000;
    private final static String EXIT_VALUE = "--exit";
    private final static String UDP_VALUE = "--U";
    private final static String MULTICAST_VALUE = "--M";
    private final static String HELP_VALUE = "--help";
    private final static String NICKNAME_NOT_UNIQUE = "--nickname-error";
    private final static String NICKNAME_ASSIGNED = "--nickname-assigned";
    private final static Map<String, String> ASCII_ARTS_PATH = Map.of(
            "CAT1", "./cat1.txt",
            "CAT2", "./cat2.txt",
            "LION1", "./lion1.txt"
    );
    private static final Console cnsl = System.console();

    public static void main(String[] args) {

        // Client provides its nickname or it is generated randomly
        String nickname = cnsl.readLine("Provide your nickname:");


        if (nickname.isBlank()) {
            byte[] arr = new byte[4];
            new Random().nextBytes(arr);
            nickname = new String(arr, StandardCharsets.UTF_8);
        }

        // Welcome messages for client
        System.out.println("Welcome \"" + nickname + "\"!");
        printHelpMessage();

        // Create socket
        try (Socket socket = new Socket(HOST_NAME, PORT_NUMBER)) {
            // Handle udp messages
            handleUDPMessage(socket.getLocalPort());

            // Handle multicast messages
            new Thread(new MulticastReceiver()).start();

            // TPC:

            // in/out streams from/to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // send nickname to server
            out.println(nickname);

            // acknowledge nickname assignment
            while (!NICKNAME_ASSIGNED.equals(in.readLine())) {
                if (in.readLine().equals(NICKNAME_NOT_UNIQUE)) {
                    System.out.println("Connection to the server has been closed since the nickname \"" + nickname + "\" is not unique.");
                    return;
                }
            }

            // Listen to the response from server
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (SocketException ex) {
                    System.out.println("Connection to the server has been closed.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Send messages to server
            String line = null;
            while (!EXIT_VALUE.equals(line)) {

                // read message from user
                line = cnsl.readLine();

                if (line == null) {
                    // Do not send null message
                    break;
                }

                switch (line) {
                    case HELP_VALUE -> printHelpMessage();
                    case UDP_VALUE -> sendMessageUDP(nickname);
                    case MULTICAST_VALUE -> handleSendingMulticastMessage(nickname);
                    // send msg to server
                    default -> out.println(line);
                }
            }
        } catch (ConnectException ex) {
            System.out.println("Connection has been refused.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void sendMessageUDP(String nickname) {
        // Create socket for UDP
        try (DatagramSocket socket = new DatagramSocket()) {

            System.out.println("Choose which ASCII ART send:");
            printExistingASCIIARTS();

            boolean asciiArtExist = false;
            String asciiArt = null;

            // Choose which art to send
            while (!asciiArtExist) {
                asciiArt = cnsl.readLine();

                asciiArtExist = ASCII_ARTS_PATH.containsKey(asciiArt);

                if (!asciiArtExist) {
                    System.out.println("Choose an existing ASCII ART!");
                    printExistingASCIIARTS();
                }
            }

            // Read ASCII ART to String
            Path filePath = Path.of(ASCII_ARTS_PATH.get(asciiArt));
            String art = Files.readString(filePath);
            String message = nickname + "$" + art;

            // Show the client what they sent
            System.out.println("You sent:");
            System.out.println(art);

            // Send ascii art with nickname
            byte[] sendBuffer = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(HOST_NAME), PORT_NUMBER);
            socket.send(sendPacket);
        } catch (SocketException ignored) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void handleUDPMessage(int myPort) {
        new Thread(() -> {
            // create socket for UDP
            try (DatagramSocket serverSocketUDP = new DatagramSocket(myPort)) {
                while (true) {
                    // Wait for the message from the server
                    byte[] receiveBuffer = new byte[1024];
                    Arrays.fill(receiveBuffer, (byte) 0);
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocketUDP.receive(receivePacket);

                    // Convert message to String and print it
                    String receivedMsg = new String(receivePacket.getData());
                    System.out.println(receivedMsg);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static void printExistingASCIIARTS() {
        System.out.println("---  ASCII ARTS  ---");
        for (String name : ASCII_ARTS_PATH.keySet()) {
            System.out.println("* " + name);
        }
        System.out.println("---  ASCII ARTS  ---");
    }

    private static void printHelpMessage() {
        System.out.println("""

                Commands you can use:
                --exit \t close the connection
                --U \t send ASCII ART via UDP
                --M \t send message using multicast (NOT AVAILABLE YET)
                --help \t show help
                                
                """);
    }

    private static void handleSendingMulticastMessage(String nickname) {
        System.out.println("PROVIDE MULTICAST MESSAGE: ");
        String msg = cnsl.readLine();
        sendMulticastMessage(nickname + "(MULTICAST)>" + msg);
    }

    private static void sendMulticastMessage(String message) {
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress multicastAddress = InetAddress.getByName(MULTICAST_IP);
            byte[] buf = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, multicastAddress, MULTICAST_PORT);
            socket.send(packet);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class MulticastReceiver implements Runnable {

        @Override
        public void run() {
            try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {
                InetAddress mcastadrr = InetAddress.getByName(MULTICAST_IP);
                InetSocketAddress group = new InetSocketAddress(mcastadrr, MULTICAST_PORT);
                NetworkInterface netIf = NetworkInterface.getByName("bge0");
                socket.joinGroup(group, netIf);

                byte[] buf = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData());
                    System.out.println(msg);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
