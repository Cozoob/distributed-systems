import sys, Ice
import SmartHouse


with Ice.initialize(sys.argv) as communicator:
    base = communicator.stringToProxy("SimpleRobot:default -p 10000")
    robot = SmartHouse.RobotPrx.checkedCast(base)
    if not robot:
        raise RuntimeError("Invalid proxy")

    robot.isActive()
