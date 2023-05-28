import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Z1_Producer_clear {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Z1 PRODUCER");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // queue
        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);        

        // producer (publish msg)
        String message = "";
        while(!message.equals("CLOSE")) {
            System.out.println("Type \"CLOSE\" to cloase channel.");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter message:");
            message = br.readLine();

            channel.basicPublish("", QUEUE_NAME, null,
                    message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent: " + message);
        }

        // close
        channel.close();
        connection.close();
    }
}
