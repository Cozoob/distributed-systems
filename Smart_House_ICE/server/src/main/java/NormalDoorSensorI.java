import SmartHouse.DoorSensorException;
import SmartHouse.NormalDoorSensor;
import com.zeroc.Ice.Current;

public class NormalDoorSensorI extends DoorSensorI implements NormalDoorSensor {
    private boolean locked = false;

    @Override
    public boolean isLocked(Current current) {
        return this.locked;
    }

    @Override
    public void lockDoor(Current current) throws DoorSensorException {
        if(this.locked) {
            setCurrentTime(current);
            throw new DoorSensorException(time, "Cannot perform action - doors are already locked.");
        }
        this.locked = true;
    }

    @Override
    public void unlockDoor(Current current) throws DoorSensorException {
        if(!this.locked) {
            setCurrentTime(current);
            throw new DoorSensorException(time, "Cannot perform action - doors are already unlocked.");
        }
        this.locked = false;
    }
}
