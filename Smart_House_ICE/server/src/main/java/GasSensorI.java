import SmartHouse.GasSensor;
import SmartHouse.GasSensorException;
import com.zeroc.Ice.Current;

public class GasSensorI extends SensorI implements GasSensor {
    private double level = 0;

    @Override
    public void gasDetected(double level, Current current) throws GasSensorException {
        if (level < 0) {
            setCurrentTime(current);
            throw new GasSensorException(time, "Cannot set new level - invalid gas value.");
        }
        this.level = level;
        if(level > 75.0) {
            setCurrentTime(current);
            throw new GasSensorException(time, "Gas has been detected! - Run away now!");
        }
    }

    @Override
    public double getGasLevel(Current current) {
        return level;
    }
}
