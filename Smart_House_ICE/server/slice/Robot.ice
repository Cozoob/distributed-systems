
#ifndef ROBOT_ICE
#define ROBOT_ICE

module SmartHouse {

    struct TimeOfDay {
        short hour;
        short minute;
        short second;
    };

    exception OutOfEnergyException {
        TimeOfDay errorTime;
        string message = "Cannot perform movement - robot does not have enough energy.";
    };

    exception InvalidPositionException {
        TimeOfDay errorTime;
        string message = "Cannot set new position - invalid position value.";
    };

    exception RobotNotActiveException  {
        TimeOfDay errorTime;
        string message = "Cannot perform action - robot is not active.";
    };

    exception RobotAlreadyActiveException  {
        TimeOfDay errorTime;
        string message = "Cannot activate robot - robot is already active.";
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
        void move(Movement m);
        void setNewPosition(Position p);
        void turnOff();
        void turnOn();
    };
};

#endif