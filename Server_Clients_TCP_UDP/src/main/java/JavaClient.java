import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class JavaClient {

    public static void main(String[] args) {
        // Constant value
        final String exitValue = "--exit";

        // Client provides its nickname or it is generated randomly
        Console cnsl = System.console();
        String nickname =  cnsl.readLine("Provide your nickname:");


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
                try{
                    String response;
                    while((response = in.readLine()) != null){
                        System.out.println(response);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Send messages to server
            while (!exitValue.equals(line)) {

                // read message from user
                // line = cnsl.readLine("me>"); // TODO HOW MAKE IT STICKY WHEN RECEIVING NEW MSG FROM OTHERS?
                line = cnsl.readLine();

                // check if user want to leave
                if(exitValue.equals(line)){
                    continue;
                }

                // send msg to server
                out.println(line);
                //out.flush(); // ensures that all data that has been written to that stream is output
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
