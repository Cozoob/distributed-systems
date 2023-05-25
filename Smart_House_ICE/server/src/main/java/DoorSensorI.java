import SmartHouse.DoorSensor;
import SmartHouse.DoorSensorException;
import SmartHouse.Sensor;
import com.zeroc.Ice.Current;

public class DoorSensorI extends SensorI implements DoorSensor {
    protected boolean open = false;

    @Override
    public boolean isOpen(Current current) {
        return this.open;
    }

    @Override
    public void doorOpened(Current current) throws DoorSensorException {
        if (this.open) {
            setCurrentTime(current);
            throw new DoorSensorException(time, "Cannot perform action - doors are already opened.");
        }
        this.open = true;
    }

    @Override
    public void doorClosed(Current current) throws DoorSensorException {
        if (!this.open) {
            setCurrentTime(current);
            throw new DoorSensorException(time, "Cannot perform action - doors are already closed.");
        }
        this.open = false;
    }
}
