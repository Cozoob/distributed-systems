import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class JavaClient {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Provide your nickname:");
        String nickname = reader.readLine();
        if(nickname.isBlank()){
            byte[] arr = new byte[4];
            new Random().nextBytes(arr);
            nickname = new String(arr, StandardCharsets.UTF_8);
        }

        System.out.println("JAVA TCP CLIENT \"" + nickname + "\"");
        String hostName = "localhost";
        int portNumber = 12345;
        Socket socket = null;

        try {
            // create socket
            socket = new Socket(hostName, portNumber);

            // in & out streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // send nickname to server...
            out.println(nickname);

            while(true){
                // read message from user
                String line = reader.readLine();

                // send msg, read response
                out.println(line);
                // String response = in.readLine();
                // System.out.println("Server: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null){
                socket.close();
                System.out.println("The server connection has been closed.");
            }
        }
    }

}
