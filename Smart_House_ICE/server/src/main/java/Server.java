import SmartHouse.RobotPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;

public class Server {

    public void start(String[] args) {
        int status = 0;
        ArrayList<Identity> identityList = new ArrayList<>();
        Communicator communicator = null;

        try {
            // inicjalizacja ICE
            communicator = Util.initialize(args);

            // konfiguracja adaptera
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter", "tcp -h 127.0.0.1 -p 1000");

            // utworzenie serwantów
            RobotI robot1 = new RobotI();
            RobotI robot2 = new RobotI();

            // dodanie wpisow do tablicy ASM (Array Slice Memory), skojarzenie nazwy obiektu (Identity) z serwantem
            Identity robot1Id = new Identity("robot1", "robot");
            adapter.add(robot1, robot1Id);
            identityList.add(robot1Id);

            Identity robot2Id = new Identity("robot2", "robot");
            adapter.add(robot2, robot2Id);
            identityList.add(robot2Id);

            // Aktywacja adaptera i wejscie w petle przetwarzan zadan
            adapter.activate();

            // List ids
            System.out.println("Identities list:");
            System.out.println("NO. Name - Category");
            Identity id;
            for(int i = 0; i < identityList.size(); i++) {
                id = identityList.get(i);
                System.out.println(i + ". " + id.name + " - " + id.category);
            }

            System.out.println("\nEntering event processing loop...");

            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
            status = 1;
        }
        if (communicator != null) {
            try {
                communicator.destroy();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                status = 1;
            }
        }
        System.exit(status);
    }


    public static void main(String[] args)
    {
        Server app = new Server();
        app.start(args);
    }
}
