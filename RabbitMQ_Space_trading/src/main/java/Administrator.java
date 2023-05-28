import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Administrator {
    private static final String ADMIN_KEY = "admin";
    private static final String ADMIN_EXCHANGE = "admin_exchange";
    private static final String ADMIN_KEY_FOR_ALL_AGENCIES = "admin.agencies";
    private static final String ADMIN_KEY_FOR_ALL_CARRIERS = "admin.carriers";

    public static void main(String[] args) throws Exception {
        // info
        System.out.println("ADMINISTRATOR");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        // queue
        channel.queueDeclare(ADMIN_KEY, false, false, false, null);

        // exchange
        channel.exchangeDeclare(ADMIN_EXCHANGE, BuiltinExchangeType.TOPIC);

        // consumer (handle msg)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("\nReceived: " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        // start listening
        System.out.println("Listening for messages...");
        channel.basicConsume(ADMIN_KEY, false, consumer);

        while(true) {
            // read msg
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("\nEnter message: ");
            String message = br.readLine();


            System.out.println("Type \"0\" to send message to all.");
            System.out.println("Type \"1\" to send message to all agencies.");
            System.out.println("Type \"2\" to send message to all carriers.");
            System.out.print("Enter option to send message to: ");
            String key = br.readLine();

            // break condition
            if ("exit".equalsIgnoreCase(key)) {
                break;
            }

            if (!key.equals("0") && !key.equals("1") && !key.equals("2")) {
                System.out.println("Inalid argument.");
                continue;
            }

            switch (key) {
                case "0":
                    // publish
                    channel.basicPublish(ADMIN_EXCHANGE, ADMIN_KEY_FOR_ALL_AGENCIES, null, message.getBytes("UTF-8"));
                    channel.basicPublish(ADMIN_EXCHANGE, ADMIN_KEY_FOR_ALL_CARRIERS, null, message.getBytes("UTF-8"));
                    break;
                case "1":
                    key = ADMIN_KEY_FOR_ALL_AGENCIES;
                    // publish
                    channel.basicPublish(ADMIN_EXCHANGE, key, null, message.getBytes("UTF-8"));
                    break;
                case "2":
                    key = ADMIN_KEY_FOR_ALL_CARRIERS;
                    // publish
                    channel.basicPublish(ADMIN_EXCHANGE, key, null, message.getBytes("UTF-8"));
                    break;
                default:
                    System.out.println("Inalid argument.");
                    continue;
            }

            System.out.println("Sent: " + message);
        }
    }
}
