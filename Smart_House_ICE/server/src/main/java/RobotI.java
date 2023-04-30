import SmartHouse.Movement;
import SmartHouse.Position;

public class RobotI implements SmartHouse.Robot {

    @Override
    public boolean isActive(com.zeroc.Ice.Current current) {
        System.out.println("I am alive!");
        return false;
    }

    @Override
    public byte getEnergy(com.zeroc.Ice.Current current) {
        return 0;
    }

    @Override
    public float getPositionX(com.zeroc.Ice.Current current) {
        return 0;
    }

    @Override
    public float getPositionY(com.zeroc.Ice.Current current) {
        return 0;
    }

    @Override
    public void move(Movement m, com.zeroc.Ice.Current current) {

    }

    @Override
    public void setNewPosition(Position p, com.zeroc.Ice.Current current) {

    }

    @Override
    public void turnOff(com.zeroc.Ice.Current current) {

    }

    @Override
    public void turnOn(com.zeroc.Ice.Current current) {

    }
}