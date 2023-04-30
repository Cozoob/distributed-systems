public class Server {
    public static void main(String[] args)
    {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args))
        {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("SimpleRobotAdapter", "default -p 10000");
            com.zeroc.Ice.Object object = new RobotI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimpleRobot"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }
}
