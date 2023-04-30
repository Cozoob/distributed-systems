import time
import logging
import ray

# Init of ray

if ray.is_initialized:
    ray.shutdown()
ray.init(logging_level=logging.ERROR)

print('TASK 2')
print('IT WORKS! Wait patiently :)')

# Init of big dict with arrays

NUMBER = 5

large_dict = {key: val for key, val in zip(range(NUMBER), list(range(NUMBER)))}

obj_dict_ref = ray.put(large_dict)

def process_large_data(key, large_dict):
    # for key in large_list:
    #     large_dict[key] = key * large_dict[key] ** 2
    print("Processing...", key)
    time.sleep(2)
    return key * large_dict[key] ** 2


@ray.remote
def process_large_data_distributed(key, large_dict):
    return process_large_data(key, large_dict)

# Process the dicts and lists locally
start_time = time.time()
local_res = [process_large_data(key, large_dict) for key in large_dict.keys()]
end_time = time.time()
print(f'Processing of big arrays and dicts with local method. TIME: {end_time - start_time} seconds')

# Process the dicts and lists distributed
start_time = time.time()
dis_res = ray.get([process_large_data_distributed.remote(key, obj_dict_ref) for key in large_dict.keys()])
end_time = time.time()
print(f'Processing of big arrays and dicts with distributed method. TIME: {end_time - start_time} seconds')

# Shutdown of the ray
ray.shutdown()