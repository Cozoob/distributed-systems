import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Carrier {
    private static final String CARRY_PEOPLE_KEY = "people";
    private static final String CARRY_CARGO_KEY = "cargo";
    private static final String SET_SATELLITE_KEY = "satellite";

    public static void main(String[] argv) throws Exception {
        // info, get name of Carrier
        System.out.println("Carrier");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter carrier's name: ");
        String ID = br.readLine() + "-" + System.currentTimeMillis();

        // choose which services provide
        printHelpMessageChoosingServices();
        String message, checker, args[];
        while(true) {
            // read msg
            System.out.print("\nEnter services: ");
            message = br.readLine();

            // check message
            if("help".equalsIgnoreCase(message)) {
                printHelpMessageChoosingServices();
                continue;
            }

            checker = message.replaceAll("[01]", "");
            if(message.length() != 3 || !checker.isBlank()) {
                System.out.println("Invalid argument!");
                continue;
            }

            // parse correct message for services
            args = message.split("");

            if(args[0].equals(args[1]) && args[0].equals(args[2])) {
                System.out.println("Carrier must have at least 1 service and at max 2 services!");
                continue;
            }

            break;
        }

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        // queues for services
        channel.queueDeclare(CARRY_PEOPLE_KEY, false, false, false, null);
        channel.queueDeclare(CARRY_CARGO_KEY, false, false, false, null);
        channel.queueDeclare(SET_SATELLITE_KEY, false, false, false, null);

        // queues & binds
        if(args[0].equals("1")) {
            // carry people
            // consumer (message handling)
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("\nCarrying people - service (5s): " + message);
                    String agencyId = message.split(":")[0];

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    channel.basicAck(envelope.getDeliveryTag(), false);
                    System.out.println("Carrier(" + ID + ") finished carrying people from agency(" +  agencyId + ")");

                    // send approval for agency
                    String approvalMessage = "Task(" + message + ") approved by " + ID + ".";
                    channel.basicPublish("", agencyId, null, approvalMessage.getBytes(StandardCharsets.UTF_8));
                }
            };

            channel.basicConsume(CARRY_PEOPLE_KEY, false, consumer);
        }

        if(args[1].equals("1")) {
            // carry cargo
            // consumer (message handling)
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("\nCarrying cargo - service (2s): " + message);
                    String agencyId = message.split(":")[0];

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    System.out.println("Carrier(" + ID + ") finished carrying cargo from agency(" +  agencyId + ")");

                    // send approval for agency
                    String approvalMessage = "Task(" + message + ") approved by " + ID + ".";
                    channel.basicPublish("", agencyId, null, approvalMessage.getBytes(StandardCharsets.UTF_8));
                }
            };

            channel.basicConsume(CARRY_CARGO_KEY, false, consumer);
        }

        if(args[2].equals("1")) {
            // set a satellite
            // consumer (message handling)
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("\nSetting satellite - service (10s): " + message);
                    String agencyId = message.split(":")[0];

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    System.out.println("Carrier(" + ID + ") finished setting satellite from agency(" +  agencyId + ")");

                    // send approval for agency
                    String approvalMessage = "Task(" + message + ") approved by " + ID + ".";
                    channel.basicPublish("", agencyId, null, approvalMessage.getBytes(StandardCharsets.UTF_8));
                }
            };

            channel.basicConsume(SET_SATELLITE_KEY, false, consumer);
        }

        System.out.println("Listening to all chosen services...");

    }

    private static void printHelpMessageChoosingServices() {
        System.out.println("===== HELP =====");
        System.out.println("Choose service you want provide simply typing 1, otherwise type 0.");
        System.out.println("System accepts always only 3 digit number that has only 1's and 0's.");
        System.out.println("For example:");
        System.out.println("010");
        System.out.println("101");
        System.out.println("001");
        System.out.println("etc...\n");
        System.out.println("===== MEANING OF THE DIGITS =====");
        System.out.println("X__ - carry people (1-yes, 0-no)");
        System.out.println("_X_ - carry cargo (1-yes, 0-no)");
        System.out.println("__X - set a satellite (1-yes, 0-no)\n");
        System.out.println("If you want to see this help message again just type \"help\"");
    }

}
