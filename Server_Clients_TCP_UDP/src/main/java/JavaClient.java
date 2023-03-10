import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class JavaClient {
    // Constant value
    private final static String EXIT_VALUE = "--exit";
    private final static String NICKNAME_NOT_UNIQUE = "--nickname-error";
    public static void main(String[] args) {

        // Client provides its nickname or it is generated randomly
        Console cnsl = System.console();
        String nickname = cnsl.readLine("Provide your nickname:");


        if (nickname.isBlank()) {
            byte[] arr = new byte[4];
            new Random().nextBytes(arr);
            nickname = new String(arr, StandardCharsets.UTF_8);
        }

        // Welcome messages for client
        System.out.println("Welcome \"" + nickname + "\"!");
        System.out.println("Commands you can use:\n" + "--exit \t close the connection");

        String hostName = "localhost";
        int portNumber = 12345;

        // Create socket
        try (Socket socket = new Socket(hostName, portNumber)) {

            // in/out streams from/to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // send nickname to server
            out.println(nickname);

            String line = null;

            // Listen to the response from server
            String finalNickname = nickname;
            new Thread(() -> {
                try {
                    String response;
                    // check if nickname error
                    if(in.readLine().equals(NICKNAME_NOT_UNIQUE)){
                        System.out.println("Connection to the server has been closed since the nickname \"" + finalNickname + "\" is not unique.");
                       return;
                    }

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
            while (!EXIT_VALUE.equals(line)) {

                // read message from user
                // line = cnsl.readLine("me>"); // TODO HOW TO MAKE IT STICKY WHEN RECEIVING NEW MSG FROM OTHERS?
                line = cnsl.readLine();

                // send msg to server
                out.println(line);
            }
        } catch (ConnectException ex) {
            System.out.println("Connection has been refused.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
