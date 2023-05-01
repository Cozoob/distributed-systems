import sys, Ice
import SmartHouse

Robots = {}

def show_help_header():
    print("--- AVAILABLE COMMANDS ---")
    print("Command -> Description")
    print("----------------------------------------")
    print("q -> exit the program/chosen devices")
    print("h -> show help")

def get_help():
    show_help_header()
    print("robots -> List of available robots")
    print("doors -> List of available doors")
    print("valves -> List of available valves")

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
    # TODO


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


with Ice.initialize(sys.argv) as communicator:
    # Robots
    base1 = communicator.stringToProxy("robot/robot1:tcp -h 127.0.0.1 -p 1000")
    base2 = communicator.stringToProxy("robot/robot2:tcp -h 127.0.0.1 -p 1000")
    robot1 = SmartHouse.RobotPrx.checkedCast(base1)
    robot2 = SmartHouse.RobotPrx.checkedCast(base2)
    Robots["r1"] = robot1
    Robots["r2"] = robot2



    # Wywolanie zmian dla obiektow
    user_input = ""
    print("Provide command form the list below to execute the action\n")
    get_help()
    while user_input != "q":
        user_input = input(">")
        if user_input == "q":
            break
        elif user_input == "h":
            get_help()
        elif user_input == "robots":
            process_robot_list()
        else:
            print("Unrecognized command... Write \'h\' if you need help.")

    print("Client disconnected.")
