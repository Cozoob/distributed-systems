import SmartHouse.TemperatureSensor;
import SmartHouse.TemperatureSensorException;
import com.zeroc.Ice.Current;

public class TemperatureSensorI extends SensorI implements TemperatureSensor {
    private double temperature = 0;

    @Override
    public void temperatureChanged(double temperature, Current current) throws TemperatureSensorException {
        // The thermostat accepts values only between [-20, 20]
        if (temperature < -20 || temperature > 20) {
            setCurrentTime(current);
            throw new TemperatureSensorException(time, "Invalid temperature value - thermostat accepts values only between [-20, 20].");
        }
        this.temperature = temperature;
    }

    @Override
    public double getTemperature(Current current) {
        return temperature;
    }
}
