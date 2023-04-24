import random
import time
import logging
import ray

# Init of ray

if ray.is_initialized:
    ray.shutdown()
ray.init(logging_level=logging.ERROR)

print('TASK 1')
print('IT WORKS! Wait patiently :)')

# local bubble sort method

def bubble_sort(arr):
    n = len(arr)

    # if the array is already sorted, it doesn't need
    # to go through the entire process
    is_sorted = True
    for i in range(n - 2):
        if arr[i] > arr[i + 1]:
            is_sorted = False
            break

    if is_sorted:
        return arr

    swapped = False
    # Traverse through all array elements
    for i in range(n - 1):
        for j in range(0, n - i - 1):
            # Swap if the element found is greater
            # than the next element
            if arr[j] > arr[j + 1]:
                swapped = True
                arr[j], arr[j + 1] = arr[j + 1], arr[j]

        if not swapped:
            # if we haven't needed to make a single swap, we
            # can just exit the main loop.
            return arr

    return arr

# wrapper for distributed bubble_sort
@ray.remote
def bubble_sort_distributed(arr):
    return bubble_sort(arr)

# Init of 2 arrays of arrays
size = 4000
loops = 10

arrs1 = [[random.randint(0, size) for _ in range(size)] for _ in range(loops)]
arrs2 = [[random.randint(0, size) for _ in range(size)] for _ in range(loops)]

# Sorting the array with local bubble sort method
start_time = time.time()
local_res = [bubble_sort(arr) for arr in arrs1]
end_time = time.time()
print(f'Sorting of {loops} random arrays with local bubble sort method. TIME: {end_time - start_time} seconds')


# Sorting the array with distributed bubble sort method
start_time = time.time()
dis_res = ray.get([bubble_sort_distributed.remote(arr) for arr in arrs2])
end_time = time.time()
print(f'Sorting of {loops} random arrays with distributed bubble sort method. TIME: {end_time - start_time} seconds')

# Shutdown the ray
ray.shutdown()