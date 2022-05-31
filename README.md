# MultiThread
#RandomScheduling

1. Implement a program in Java that satisfies functional requirements listed below (also see the additional requirements for your code below this list):
   - The scheduling algorithm must start at time moment (tick) 0 and work in a loop (with a step of 1 tick) until all of the processes are completed.
   - The total number of processes is limited to a specific number. The processes do not arrive at predetermined time moments, however — instead, at each time moment one or several new processes may arrive with a given probability. Maximum number of new processes arriving at a given time moment is also limited with a separate parameter, though.
   - The total burst time for each new process is determined randomly (using a uniform distribution) between the given minimum and maximum burst time.
   - The next process to be assigned to the CPU is selected by the scheduling algorithm *randomly* (using a uniform distribution) among the currently available (and yet not completed) processes. 
   - The scheduling algorithm should support two behaviors: non-preemptive and preemptive (using the provided time quantum value).
   - The data about the total waiting time for each process should be stored.
   - After each simulation is complete, the information about each process (ID, burst time, arrival time, and total waiting time) should be printed. Additionally, the complete execution time of the simulation and the average process waiting time should be printed.
   - The implementation should run several simulations, with both non-preemptive and preemptive versions of the scheduling algorithm. See *RandomScheduler.java* for further details.