import logging
import time
import ray
import random
import math


# Init of ray

if ray.is_initialized:
    ray.shutdown()
ray.init(logging_level=logging.ERROR)

print('TASK 3')
print('IT WORKS! Wait patiently :)')

# Local method to calculate PI
def calc_pi(num_batches):
    in_circle = 0

    for _ in range(num_batches):
        a, b = random.random(), random.random()

        if a ** 2 + b ** 2 <= 1:
            in_circle += 1

    return 4 * (in_circle / num_batches)

# Distributed method to calculate PI
@ray.remote
def distributed_calc_pi(num_batches):
    return calc_pi(num_batches)


SIZE = 1000
num_batches = SIZE ** 2
rounds = 10

# Calculate PI using local method
start_time = time.time()
local_res = [calc_pi(num_batches) for _ in range(rounds)]
end_time = time.time()
print(f'Calculated value of the PI {local_res[0]} - local. TIME: {end_time - start_time} seconds')

# Calculate PI using distributed method
start_time = time.time()
local_res = ray.get([distributed_calc_pi.remote(num_batches) for _ in range(rounds)])
end_time = time.time()
print(f'Calculated value of the PI {local_res[0]} - distributed. TIME: {end_time - start_time} seconds')

# Ray Actor
# It keeps the state of the progress of calculating value of the PI
@ray.remote
class PIHandler:
    def __init__(self):
        self.pi_val = 0

    def get_pi(self):
        return self.pi_val

    def update_pi(self, pi_val):
        if abs(math.pi - pi_val) < abs(math.pi - self.pi_val):
            self.pi_val = pi_val

allowed_err = 0.0001

@ray.remote
def PI_worker(PI_handler):
    for num_batches in range(SIZE, SIZE * SIZE, 10 * SIZE):
        curr_pi_val = ray.get(PI_handler.get_pi.remote())

        if abs(math.pi - curr_pi_val) <= allowed_err:
            return ray.get(PI_handler.get_pi.remote())

        new_pi_val = calc_pi(num_batches)
        PI_handler.update_pi.remote(new_pi_val)

    return ray.get(PI_handler.get_pi.remote())

handler = PIHandler.remote()
rounds = 3

# Calculate PI using Actor and Worker
start_time = time.time()
local_res = ray.get([PI_worker.remote(handler) for _ in range(rounds)])
end_time = time.time()
print(f'Calculated value of the PI {local_res[0]} - actor & worker. TIME: {end_time - start_time} seconds')
