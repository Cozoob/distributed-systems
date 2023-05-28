import grpc
import grpc_requests
import grpc_reflection
from google.protobuf.descriptor_pool import DescriptorPool
from grpc_reflection.v1alpha.proto_reflection_descriptor_database import ProtoReflectionDescriptorDatabase

"""

* to list all services on server:
  grpcurl -plaintext localhost:8080 list

* to list all methods on server:
  grpcurl -plaintext localhost:8080 list calculator.AdvancedCalculator

* execute method on service running on server (it should work...)
  grpcurl -plaintext -d '{"optype": "MIN", "args": [1,2,0,4]}' localhost:8080 calculator.AdvancedCalculator/ComplexOperation

"""

services = []

def get_services():
    for serv in ref_db.get_services():
        if serv != "grpc.reflection.v1alpha.ServerReflection":
            services.append(serv)

def print_services():
    print("========AVAILABLE SERVICES========")

    for num, serv in enumerate(services):
        print(f"{num}. {serv.split('.')[-1]}")

    print("==================================")

def show_help():
    print("==================================")
    print("============  HELP  ==============")
    print("==================================")
    print("h -> Show this help")
    print("q -> Quit")
    print("Type in service number to see list of methods!")


def list_methods(name):
    serv = desc.FindServiceByName(name)
    print("==================================")
    print(f"\nMethods of {serv.name.split('.')[-1]}:")

    for num, method in enumerate(serv.methods):
        method_name = method.full_name.split('.')[-1]
        print(f"{num}. {method_name}")

        method_desc = desc.FindMethodByName(method.full_name)
        print("\n Its inputs:")
        for inp in method_desc.input_type.fields:
            print(f" * {inp.full_name.split('.')[-1]}")
        print()

    print("==================================")

def send_example_requests():
    print("!!Example for Advanced Calculator!!")
    args = [5, 3, 6, 8, 2, 7]
    operations = ["SUM", "AVG", "MIN", "MAX"]

    print(f"args {args}")
    for op in operations:
        res = client.request(
            "calculator.AdvancedCalculator",
            "ComplexOperation",
            {
                "optype": op,
                "args": args
            }
        )

        print(f"Operation {op}: result -> {res}")

if __name__ == '__main__':
    address = 'localhost:8080'
    channel = grpc.insecure_channel(address)
    ref_db = ProtoReflectionDescriptorDatabase(channel)
    desc = DescriptorPool(ref_db)

    client = grpc_requests.Client.get_by_endpoint(address)

    get_services()
    print_services()
    show_help()

    while True:
        try:
            user_input = input(">")
            if user_input == "h":
                show_help()
            elif user_input == "q":
                break
            else:
                print(user_input)
                list_methods(services[int(user_input)])
        except (KeyError, IndexError, ValueError, TypeError) as e:
            print("Unrecognized command...")
            print(e)

    send_example_requests()
