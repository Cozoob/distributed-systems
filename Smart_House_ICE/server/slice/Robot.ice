
#ifndef ROBOT_ICE
#define ROBOT_ICE

module SmartHouse {

    // Struct to collect time for all exceptions defined in the module

    struct TimeOfDay {
        short hour;
        short minute;
        short second;
    };

    // Robot

    exception OutOfEnergyException {
        TimeOfDay errorTime;
        optional(1) string message = "Cannot perform movement - robot does not have enough energy.";
    };

    exception InvalidPositionException {
        TimeOfDay errorTime;
        optional(2) string message = "Cannot set new position - invalid position value.";
    };

    exception RobotNotActiveException  {
        TimeOfDay errorTime;
        optional(3) string message = "Cannot perform action - robot is not active.";
    };

    exception RobotAlreadyActiveException  {
        TimeOfDay errorTime;
        optional(4) string message = "Cannot activate robot - robot is already active.";
    };

    enum Movement {
        UP,
        DOWN,
        LEFT,
        RIGHT
    };

    struct Position {
        float x;
        float y;
    };

    interface Robot {
        bool isActive();
        byte getEnergy();
        float getPositionX();
        float getPositionY();
        void move(Movement m) throws OutOfEnergyException, InvalidPositionException, RobotNotActiveException;
        void setNewPosition(Position p) throws InvalidPositionException, RobotNotActiveException;
        void turnOff();
        void turnOn();
    };

    // Sensors

    interface Sensor {
        string getName();
        string getType();
        void setCurrentTime();
    };

    // GasSensor

    exception GasSensorException {
        TimeOfDay errorTime;
        string reason;
    };

    interface GasSensor extends Sensor {
        void gasDetected(double level) throws GasSensorException;
        double getGasLevel();
    };

    // Temperature Sensor

    exception TemperatureSensorException {
        TimeOfDay errorTime;
        string reason;
    };

    interface TemperatureSensor extends Sensor {
        void temperatureChanged(double temperature) throws TemperatureSensorException;
        double getTemperature();
    };

    // Door Sensors

    exception DoorSensorException {
        TimeOfDay errorTime;
        string reason;
    };

    interface DoorSensor extends Sensor {
        bool isOpen();
        void doorOpened() throws DoorSensorException;
        void doorClosed() throws DoorSensorException;
    };

    interface NormalDoorSensor extends DoorSensor {
        bool isLocked();
        void lockDoor() throws DoorSensorException;
        void unlockDoor() throws DoorSensorException;
    };

    interface GarageDoorSensor extends DoorSensor {
        void openGarageDoor() throws DoorSensorException;
        void closeGarageDoor() throws DoorSensorException;
    };
};

#endif