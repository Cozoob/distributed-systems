import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class JavaClient {

    public static void main(String[] args) {
        // Constant value
        final String exitValue = "--exit";

        // Client provides its nickname or it is generated randomly
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Provide your nickname:");
        String nickname = null;
        try {
            nickname = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (nickname == null || nickname.isBlank()) {
            byte[] arr = new byte[4];
            new Random().nextBytes(arr);
            nickname = new String(arr, StandardCharsets.UTF_8);
        }

        // Welcome messages for client
        System.out.println("Welcome \"" + nickname + "\"!");
        System.out.println("Commands you can use:\n" + "--exit \t\t\t close the connection");

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

            while (!exitValue.equals(line)) {

                // read message from user
                line = reader.readLine();

                // send msg to server
                out.println(line);
                out.flush(); // ensures that all data that has been written to that stream is output

                // String response = in.readLine();
                // System.out.println("Server: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
