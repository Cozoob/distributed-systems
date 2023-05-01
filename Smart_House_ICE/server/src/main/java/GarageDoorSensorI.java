import SmartHouse.DoorSensorException;
import SmartHouse.GarageDoorSensor;
import com.zeroc.Ice.Current;

import java.util.Random;

public class GarageDoorSensorI extends DoorSensorI implements GarageDoorSensor {

    @Override
    public void openGarageDoor(Current current) throws DoorSensorException {
        this.doorOpened(current);
        try {
            System.out.println("Garage doors are opening...");
            this.printMovement();
        } catch (InterruptedException ex) {
            setCurrentTime(current);
            throw new DoorSensorException(time, "Cannot perform action - opening garage doors has been interrupted.");
        }
    }

    @Override
    public void closeGarageDoor(Current current) throws DoorSensorException {
        this.doorClosed(current);
        try {
            System.out.println("Garage doors are closing...");
            this.printMovement();
        } catch (InterruptedException ex) {
            setCurrentTime(current);
            throw new DoorSensorException(time, "Cannot perform action - closing garage doors has been interrupted.");
        }
    }

    private void printMovement() throws InterruptedException{
        Random random = new Random();
        Thread.sleep(500);
        System.out.println(".0 %");
        Thread.sleep(500);
        System.out.println(".." + (random.nextInt(11) + 20) + " %");
        Thread.sleep(500);
        System.out.println("..." + (random.nextInt(11) + 40) + " %");
        Thread.sleep(500);
        System.out.println(".." + (random.nextInt(11) + 60) + " %");
        Thread.sleep(500);
        System.out.println("." + (random.nextInt(20) + 80) + " %");
        Thread.sleep(500);
        System.out.println("100 %");
    }
}
