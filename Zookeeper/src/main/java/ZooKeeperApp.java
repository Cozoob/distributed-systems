import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZooKeeperApp implements Watcher {
    private static final String ZNODE_PATH = "/z";
    private static final String COMMAND_TO_OPEN = "mspaint"; // Only for Windows since it runs paint.
    private static final String COMMAND_TO_CLOSE = "taskkill /IM mspaint.exe"; // Only for Windows since it stops paint.

    private ZooKeeper zooKeeper;

    public ZooKeeperApp(String connectString) throws IOException {
        this.zooKeeper = new ZooKeeper(connectString, 5000, this);
    }

    public void createZNode() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(ZNODE_PATH, true);
        if (stat == null) {
            zooKeeper.create(ZNODE_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("ZNode 'z' created");
            startExternalApp();
        }
    }

    public void deleteZNode() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(ZNODE_PATH, false);
        if (stat != null) {
            deleteChildrenOfNode(ZNODE_PATH); // Delete all children under "z"
            zooKeeper.delete(ZNODE_PATH, -1);
            System.out.println("ZNode 'z' deleted");
            stopExternalApp();
        }
    }

    public void displayChildrenCount() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(ZNODE_PATH, true);
        if (stat != null) {
            int childrenCount = countChildrenRecursive(ZNODE_PATH);
            System.out.println("Number of children: " + childrenCount);
        }
    }

    private int countChildrenRecursive(String path) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, true);
        if (stat != null) {
            List<String> children = zooKeeper.getChildren(path, true);
            int count = children.size();
            for (String child : children) {
                String childPath = path + "/" + child;
                count += countChildrenRecursive(childPath); // Recursive call to count children of children
            }
            return count;
        } else {
            return 0;
        }
    }


    public void displayZNodeStructure() throws KeeperException, InterruptedException {
        displayZNodeStructure(ZNODE_PATH, "");
    }

    private void displayZNodeStructure(String path, String indent) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, true);
        if (stat != null) {
            System.out.println(indent + path);
            List<String> children = zooKeeper.getChildren(path, true);
            for (String child : children) {
                String childPath = path + "/" + child;
                displayZNodeStructure(childPath, indent + "  ");
            }
        }
    }

    public void createChildZNode(String childName) throws KeeperException, InterruptedException {
        Thread.sleep(1000);
        String childPath = ZNODE_PATH + "/" + childName;
        Stat stat = zooKeeper.exists(childPath, true);
        if (stat == null) {
            zooKeeper.create(childPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("Child ZNode '" + childName + "' created under 'z'");
        }
    }

    public void deleteChildrenOfNode(String nodePath) throws KeeperException, InterruptedException {
        Thread.sleep(1000);
        List<String> children = zooKeeper.getChildren(nodePath, false);
        for (String child : children) {
            String childPath = nodePath + "/" + child;
            try {
                zooKeeper.delete(childPath, -1);
            } catch (KeeperException ex) {
                deleteChildrenOfNode(childPath);
                Thread.sleep(1000);
                zooKeeper.delete(childPath, -1);
            }
            System.out.println("Child ZNode '" + child + "' deleted");
        }
    }

    private void startExternalApp() {
        try {
            Runtime.getRuntime().exec(COMMAND_TO_OPEN);
            System.out.println("External application started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopExternalApp() {
        try {
            Runtime.getRuntime().exec(COMMAND_TO_CLOSE);
            System.out.println("External application stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeCreated && event.getPath().startsWith(ZNODE_PATH)) {
            String childName = event.getPath().substring(ZNODE_PATH.length() + 1);
            System.out.println("New child ZNode '" + childName + "' created under 'z'");
            try {
                displayChildrenCount();
            } catch (KeeperException | InterruptedException e) {
                // pass...
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please provide the ZooKeeper connection string");
            System.exit(1);
        }

        try {
            ZooKeeperApp app = new ZooKeeperApp(args[0]);
            app.createZNode();
            app.displayZNodeStructure();

            app.createChildZNode("child1");
            app.createChildZNode("child2");
            Thread.sleep(1000);
            app.displayZNodeStructure();

            app.createChildZNode("child1/baba");
            app.createChildZNode("child1/jaga");
            Thread.sleep(1000);
            app.displayZNodeStructure();

            // Wait for user input to keep the program running
            System.in.read();


            app.deleteZNode();
            app.displayZNodeStructure();

            app.zooKeeper.close();
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
