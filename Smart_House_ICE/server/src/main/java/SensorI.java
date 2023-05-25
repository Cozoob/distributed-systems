import SmartHouse.TimeOfDay;
import com.zeroc.Ice.Current;

import java.time.LocalTime;

public class SensorI implements SmartHouse.Sensor {
    protected String name = null;
    protected String type = null;
    protected TimeOfDay time = new TimeOfDay();

    @Override
    public String getName(Current current) {
        if (this.name == null) {
            this.name = current.id.name;
        }
        return this.name;
    }

    @Override
    public String getType(Current current) {
        if (this.type == null) {
            this.type = current.id.category;
        }
        return this.type;
    }

    @Override
    public void setCurrentTime(Current current) {
        LocalTime localTime = LocalTime.now();
        time.hour = (short) localTime.getHour();
        time.minute = (short) localTime.getMinute();
        time.second = (short) localTime.getSecond();
    }
}
