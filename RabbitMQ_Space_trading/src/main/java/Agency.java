import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Agency {
    private static final String CARRY_PEOPLE_KEY = "people";
    private static final String CARRY_CARGO_KEY = "cargo";
    private static final String SET_SATELLITE_KEY = "satellite";


    public static void main(String[] argv) throws Exception {
        // info, get name of Agency
        System.out.println("Agency");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter agency's name: ");
        String ID = br.readLine() + "-" + System.currentTimeMillis();

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // queues for services
        channel.queueDeclare(CARRY_PEOPLE_KEY, false, false, false, null);
        channel.queueDeclare(CARRY_CARGO_KEY, false, false, false, null);
        channel.queueDeclare(SET_SATELLITE_KEY, false, false, false, null);
        // queue for approval
        channel.queueDeclare(ID, false, false, false, null);

        // handler for approval queue
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println(message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(ID, false, consumer);

        // getting services
        printHelpMessage();
        String message, checker, args[], peopleServiceId, cargoServiceId, satelliteServiceId;
        while(true) {
            // read msg
            System.out.print("\nEnter services:\n");
            message = br.readLine();

            // check message
            if("help".equalsIgnoreCase(message)) {
                printHelpMessage();
                continue;
            }

            if("exit".equalsIgnoreCase(message)) {
                channel.close();
                connection.close();
                break;
            }

            checker = message.replaceAll("[01]", "");
            if(message.length() != 3 || !checker.isBlank()) {
                System.out.println("Invalid argument!");
                continue;
            }

            // parse correct message for services
            args = message.split("");

            if(args[0].equals("1")) {
                // carry people
                // id of agency : id of current service
                peopleServiceId = ID + ":p" + System.currentTimeMillis();
                System.out.println("Service ID (carry people) = " + peopleServiceId);
                channel.basicPublish("", CARRY_PEOPLE_KEY, null, peopleServiceId.getBytes(StandardCharsets.UTF_8));
            }

            if(args[1].equals("1")) {
                // carry cargo
                cargoServiceId = ID + ":c" + System.currentTimeMillis();
                System.out.println("Service ID (carry cargo) = " + cargoServiceId);
                channel.basicPublish("", CARRY_CARGO_KEY, null, cargoServiceId.getBytes(StandardCharsets.UTF_8));
            }

            if(args[2].equals("1")) {
                // set a satellite
                satelliteServiceId = ID + ":s" + System.currentTimeMillis();
                System.out.println("Service ID (set satellite) = " + satelliteServiceId);
                channel.basicPublish("", SET_SATELLITE_KEY, null, satelliteServiceId.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Services published...");
        }

    }

    private static void printHelpMessage() {
        System.out.println("===== HELP =====");
        System.out.println("If you want to get service, you need to type in 1, otherwise type 0.");
        System.out.println("System accepts always only 3 digit number that has only 1's and 0's.");
        System.out.println("For example:");
        System.out.println("000");
        System.out.println("101");
        System.out.println("001");
        System.out.println("etc...\n");
        System.out.println("===== MEANING OF THE DIGITS =====");
        System.out.println("X__ - carry people (1-yes, 0-no)");
        System.out.println("_X_ - carry cargo (1-yes, 0-no)");
        System.out.println("__X - set a satellite (1-yes, 0-no)\n");
        System.out.println("If you want to see this help message again just type \"help\"");
        System.out.println("If you want to exit just type \"exit\"");
    }

}
