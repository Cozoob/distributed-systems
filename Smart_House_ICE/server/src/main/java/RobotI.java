import SmartHouse.*;
import com.zeroc.Ice.Current;

import java.time.LocalTime;

public class RobotI implements SmartHouse.Robot {
    private boolean active;
    private byte energy;
    private Position position;
    private TimeOfDay time;

    public RobotI() {
        active = false;
        energy = 10;
        position = new Position();
        time = new TimeOfDay();
        setCurrentTime();
    }


    @Override
    public boolean isActive(Current current) {
        return active;
    }

    @Override
    public byte getEnergy(Current current) {
        return energy;
    }

    @Override
    public float getPositionX(Current current) {
        return position.x;
    }

    @Override
    public float getPositionY(Current current) {
        return position.y;
    }

    @Override
    public void move(Movement m, Current current) throws OutOfEnergyException, InvalidPositionException, RobotNotActiveException {
        if(!active) {
            setCurrentTime();
            throw new RobotNotActiveException(time);
        }
        if (energy <= 0) {
            setCurrentTime();
            throw new OutOfEnergyException(time);
        }
        switch (m) {
            case UP -> position.y++;
            case DOWN -> position.y--;
            case LEFT -> position.x--;
            case RIGHT -> position.x++;
        }
        if (position.x < 0 || position.y < 0) {
            position.x = position.x < 0 ? 0: position.x;
            position.y = position.y < 0 ? 0: position.y;
            setCurrentTime();
            throw new InvalidPositionException(time);
        }

        energy--;
        System.out.println("Robot " + current.id.name + " moved to position: (" + position.x +", " + position.y + ").");
    }

    @Override
    public void setNewPosition(Position p, Current current) throws RobotNotActiveException, InvalidPositionException {
        if(!active) {
            setCurrentTime();
            throw new RobotNotActiveException(time);
        }
        if (p.x < 0 || p.y < 0) {
            setCurrentTime();
            throw new InvalidPositionException(time);
        }
        position = p;
        System.out.println("Set robot " + current.id.name + " position to: (" + position.x + ", " + position.y + ")");
    }

    @Override
    public void turnOff(Current current) {
        active = false;
        System.out.println("Robot " + current.id.name + " turned off");
    }

    @Override
    public void turnOn(Current current) {
        active = true;
        System.out.println("Robot " + current.id.name + " turned on");
    }

    private void setCurrentTime() {
        LocalTime localTime = LocalTime.now();
        time.hour = (short) localTime.getHour();
        time.minute = (short) localTime.getMinute();
        time.second = (short) localTime.getSecond();
    }
}