import sys, Ice
import SmartHouse

Robots = {}
GasSensors = {}
TemperatureSensors = {}
NormalDoorsSensors = {}
GarageDoorsSensors = {}

def show_help_header():
    print("\n")
    print("----------AVAILABLE COMMANDS------------")
    print("Command -> Description")
    print("----------------------------------------")
    print("q -> exit the program/chosen path")
    print("h -> show help")

def get_help():
    show_help_header()
    print("robots -> List of available robots")
    print("doors -> List of available doors")
    print("sensors -> List of available sensors")

def get_help_for_robots_list():
    show_help_header()
    for rob in Robots.keys():
        print(f"{rob} -> Choose {rob} to modify")

def get_help_for_robot():
    show_help_header()
    print("isActive -> Show activation state of robot")
    print("getEnergy -> Show energy of robot")
    print("getPositionX -> Show position of robot in the X axis")
    print("getPositionY -> Show position of robot in the Y axis")
    print("On -> Turn on the robot")
    print("Off -> Turn off the robot")
    print("Move -> Move the robot.")
    print("setNewPosition -> Set new position for the robot.")


def process_robot_list():
    get_help_for_robots_list()
    while True:
        user_input = input("robots>")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help_for_robots_list()
        elif user_input in Robots.keys():
            process_robot(user_input)
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

def process_robot(robot):
    get_help_for_robot()
    while True:
        user_input = input(f"robots/{robot}>")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help_for_robot()
        elif user_input == "isActive":
            print(Robots[robot].isActive())
        elif user_input == "getEnergy":
            print(Robots[robot].getEnergy())
        elif user_input == "getPositionX":
            print(Robots[robot].getPositionX())
        elif user_input == "getPositionY":
            print(Robots[robot].getPositionY())
        elif user_input == "On":
            Robots[robot].turnOn()
        elif user_input == "Off":
            Robots[robot].turnOff()
        elif user_input == "Move":
            print("Provide direction: 0 - UP, 1 - DOWN, 2 - LEFT, 3 - RIGHT.")
            user_input = input(f"robots/{robot}/move>")

            try:
                m = SmartHouse.Movement.valueOf(int(user_input))
                if not m:
                    print("You have provided wrong direction...")
                else:
                    Robots[robot].move(m)
                    print("Robot moved!")
            except ValueError:
                print("You have provided wrong direction...")
            except (SmartHouse.OutOfEnergyException, SmartHouse.InvalidPositionException, SmartHouse.RobotNotActiveException) as e:
                print(f"{e.errorTime.hour}:{e.errorTime.minute}:{e.errorTime.second} | {e.message}")

        elif user_input == "setNewPosition":
            print("Provide new position coordinators for the robot.")

            try:
                x = int(input(f"robots/{robot}/position.x>"))
                y = int(input(f"robots/{robot}/position.y>"))
                if not x or not y:
                    print("You have provided wrong direction...")
                else:
                    p = SmartHouse.Position(x, y)
                    Robots[robot].setNewPosition(p)
                    print("Robot has new position set!")
            except ValueError:
                print("You have provided wrong direction...")
            except (SmartHouse.RobotNotActiveException, SmartHouse.InvalidPositionException) as e:
                print(f"{e.errorTime.hour}:{e.errorTime.minute}:{e.errorTime.second} | {e.message}")
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

def get_help_for_sensor():
    show_help_header()
    print("name -> Show name of sensor")
    print("type -> Show type of sensor")

def get_help_for_doors():
    show_help_header()
    print("** Normal doors Sensors **")
    for rob in NormalDoorsSensors.keys():
        print(f"{rob} -> Choose {rob} to modify")
    print("** Garage doors Sensors **")
    for rob in GarageDoorsSensors.keys():
        print(f"{rob} -> Choose {rob} to modify")

def process_doors_list():
    get_help_for_doors()
    while True:
        user_input = input("doors>")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help_for_robots_list()
        # elif user_input in Robots.keys():
        #     process_robot(user_input)
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

def get_help_for_sensors():
    show_help_header()
    print("** Gas Sensors **")
    for rob in GasSensors.keys():
        print(f"{rob} -> Choose {rob} to modify")
    print("** Temperature Sensors **")
    for rob in TemperatureSensors.keys():
        print(f"{rob} -> Choose {rob} to modify")

def process_sensors_list():
    get_help_for_sensors()
    while True:
        user_input = input("sensors>")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help_for_sensors()
        elif user_input in GasSensors.keys():
            process_gas_sensor(user_input)
        elif user_input in TemperatureSensors.keys():
            process_temperature_sensor(user_input)
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

def get_help_for_gas_sensor():
    get_help_for_sensor()
    print("detectGas -> Check if the gas has been detected.")
    print("gasLevel -> Show current gas level.")

def process_gas_sensor(gas_sensor_id):
    get_help_for_gas_sensor()
    while True:
        user_input = input(f"sensors/{gas_sensor_id}>")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help_for_gas_sensor()
        elif user_input == "name":
            print(GasSensors[gas_sensor_id].getName())
        elif user_input == "type":
            print(GasSensors[gas_sensor_id].getType())
        elif user_input == "detectGas":
            print("Provide new level of gas.")
            try:
                user_input = int(input(f"sensors/{gas_sensor_id}/detectGas>"))
                if not user_input:
                    print("You have provided wrong value...")
                else:
                    GasSensors[gas_sensor_id].gasDetected(user_input)
                    print("Gas detection has not detected deadly gas...")
            except ValueError:
                print("You have provided wrong value...")
            except SmartHouse.GasSensorException as e:
                print(f"{e.errorTime.hour}:{e.errorTime.minute}:{e.errorTime.second} | {e.reason}")

        elif user_input == "gasLevel":
            print(GasSensors[gas_sensor_id].getGasLevel())
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

def get_help_for_temperature_sensor():
    get_help_for_sensor()
    print("change -> Change temperature.")
    print("temp -> Show current temperature.")

def process_temperature_sensor(temperature_sensor_id):
    get_help_for_temperature_sensor()
    while True:
        user_input = input(f"sensors/{temperature_sensor_id}>")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help_for_temperature_sensor()
        elif user_input == "name":
            print(TemperatureSensors[temperature_sensor_id].getName())
        elif user_input == "type":
            print(TemperatureSensors[temperature_sensor_id].getType())
        elif user_input == "change":
            print("Provide new temperature.")
            try:
                user_input = int(input(f"sensors/{temperature_sensor_id}/changeTemperature>"))
                if not user_input:
                    print("You have provided wrong value...")
                else:
                    TemperatureSensors[temperature_sensor_id].temperatureChanged(user_input)
                    print("Temperature has been changed...")
            except ValueError:
                print("You have provided wrong value...")
            except SmartHouse.TemperatureSensorException as e:
                print(f"{e.errorTime.hour}:{e.errorTime.minute}:{e.errorTime.second} | {e.reason}")

        elif user_input == "temp":
            print(TemperatureSensors[temperature_sensor_id].getTemperature())
        else:
            print("Unrecognized command... Write \'h\' if you need help.")


with Ice.initialize(sys.argv) as communicator:
    # Robots
    base1 = communicator.stringToProxy("robot/robot1:tcp -h 127.0.0.1 -p 1000")
    base2 = communicator.stringToProxy("robot/robot2:tcp -h 127.0.0.1 -p 1000")
    robot1 = SmartHouse.RobotPrx.checkedCast(base1)
    robot2 = SmartHouse.RobotPrx.checkedCast(base2)
    Robots["r1"] = robot1
    Robots["r2"] = robot2

    # Gas Sensors
    base3 = communicator.stringToProxy("gasSensor/gasSensor1:tcp -h 127.0.0.1 -p 1000")
    gasSensor = SmartHouse.GasSensorPrx.checkedCast(base3)
    GasSensors["gs1"] = gasSensor

    # Temperature Sensors
    base4 = communicator.stringToProxy("temperatureSensor/temperatureSensor1:tcp -h 127.0.0.1 -p 1000")
    temperatureSensor1 = SmartHouse.TemperatureSensorPrx.checkedCast(base4)
    TemperatureSensors["ts1"] = temperatureSensor1


    # Normal doors sensors
    base5 = communicator.stringToProxy("normalDoorSensor/normalDoorSensor1:tcp -h 127.0.0.1 -p 1000")
    normalDoorSensor1 = SmartHouse.NormalDoorSensorPrx.checkedCast(base5)
    NormalDoorsSensors["nds1"] = normalDoorSensor1


    # Garage doors sensors
    base6 = communicator.stringToProxy("garageDoorSensor/garageDoorSensor1:tcp -h 127.0.0.1 -p 1000")
    garageDoorSensor1 = SmartHouse.GarageDoorSensorPrx.checkedCast(base6)
    GarageDoorsSensors["gds1"] = garageDoorSensor1


    # Wywolanie zmian dla obiektow
    user_input = ""
    print("Provide command form the list below to execute the action")
    get_help()
    while user_input != "q":
        user_input = input(">")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help()
        elif user_input == "robots":
            process_robot_list()
        elif user_input == "doors":
            process_doors_list()
        elif user_input == "sensors":
            process_sensors_list()
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

    print("Client disconnected.")
