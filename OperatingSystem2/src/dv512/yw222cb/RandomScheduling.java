package dv512.yw222cb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 * File:	dv512.yw222cb.dv512.yw222cb.RandomScheduling.java
 * Course: 	20HT - Operating Systems - 1DV512
 * Author: 	Yibo Wang yw222cb
 * Date: 	April 2022
 */
// TODO: put this source code file into a new Java package with meaningful name (e.g., dv512.YourStudentID)!

// You can implement additional fields and methods in code below, but
// you are not allowed to rename or remove any of it!

// Additionally, please remember that you are not allowed to use any third-party libraries

public class RandomScheduling {

  // for cal time
  long start;
  long end;

  public RandomScheduling() {

  }
  // constructor


  public static class ScheduledProcess {

    int processId;
    int burstTime;
    int arriveTime;
    int waitTime;


    // The time that have been give to a process
    int alreadyProcessed;

    public ScheduledProcess(int processId, int burstTime, int arriveTime) {
      this.processId = processId;
      this.burstTime = burstTime;
      this.arriveTime = arriveTime;
      alreadyProcessed = 0;
      waitTime = 0;
    }

    // ... add further fields and methods, if necessary
    // if it still have to be processed, then add 1 to cpu time already process.
    public boolean stillNeedsToProcess() {
      if (alreadyProcessed == burstTime) {
        return false;
      } else {
        alreadyProcessed += 1;
        return true;
      }
    }

    public int getProcessId() {
      return processId;
    }

    public void setProcessId(int processId) {
      this.processId = processId;
    }

    public int getBurstTime() {
      return burstTime;
    }

    public void setBurstTime(int burstTime) {
      this.burstTime = burstTime;
    }

    public int getArrivalMoment() {
      return arriveTime;
    }

    public void setArrivalMoment(int arrivalMoment) {
      this.arriveTime = arrivalMoment;
    }

    public int getWaitTime() {
      return waitTime;
    }

    public void setWaitTime(int waitTime) {
      this.waitTime = waitTime;
    }

    public int getAlreadyProcessed() {
      return alreadyProcessed;
    }

    public void setAlreadyProcessed(int alreadyProcessed) {
      this.alreadyProcessed = alreadyProcessed;
    }
  }

  // Random number generator that must be used for the simulation
  Random rng;
  // this is defined as a completed process list
  List<ScheduledProcess> completedProcess = new ArrayList<>();
  int tick;
  int pid;
  boolean isIdle;
  List<ScheduledProcess> queue;
  ScheduledProcess process;

  //this is a method for non-preemptive.
  public void nonPreemptive(
      final int numProcesses,
      final int minBurstTime, final int maxBurstTime,
      final int maxArrivalsPerTick, final double probArrival) {
    while (completedProcess.size() != numProcesses) {
      //generate process
      generateProcessQueue(false,
          numProcesses,
          minBurstTime, maxBurstTime,
          maxArrivalsPerTick, probArrival);
      if (queue.size() == 0) {
        continue;
      }
      if (isIdle) { // pick a process if idle
        int p = this.rng.nextInt(queue.size());
        process = queue.get(p);
        // that is a way to swap 2 elements in a list  0 to p
        Collections.swap(queue, 0, p);
      }
      // Execution
      if (!process.stillNeedsToProcess()) { // dotn need to allocate

        completedProcess.add(process);
        queue.remove(0);
        isIdle = true; // CPU is free to recieve next process.
      } else {
        // re set
        isIdle = false;
      }
      // increase ever wait time for a process
      for (int i = 1; i < queue.size(); i++) {
        queue.get(i).waitTime++;
      }
      //add one time
      tick += 1;
    }
  }

  // this is a method for preemptive.
  public void Preemptive(final int timeQuantum,
      final int numProcesses,
      final int minBurstTime, final int maxBurstTime,
      final int maxArrivalsPerTick, final double probArrival) {

    while (completedProcess.size() != numProcesses) {
      // add some process to back
      int j;
      for (j = 1; j <= timeQuantum; j++) {
        for (int i = 0; i < maxArrivalsPerTick; i++) {
          if (this.rng.nextDouble() < probArrival && pid < numProcesses) {
            //generate the random burst time
            int burstTime = this.rng.nextInt((maxBurstTime - minBurstTime) + 1) + minBurstTime;
            //add
            queue.add(new ScheduledProcess(pid, burstTime, tick));
            pid += 1;
          }
        }

        // only idle and queue size no equal with 0 then will choose one.
        //if the back process size is zero, there is 0 process can be chosen.
        if (isIdle && queue.size() != 0) {
          int p = this.rng.nextInt(queue.size());
          process = queue.get(p);
          //when choose one the time quantan should be back to 1
          j = 1;
          // that is a way to swap 2 elements in a list  0 to p
          Collections.swap(queue, 0, p);
        }

        // add time. here we simulate how it works
        if (process.stillNeedsToProcess()) {
          // if need to add time, it means executing right now.
          isIdle = false;
        } else {
          // if it doesnt need to process, means done.
          completedProcess.add(process);
          queue.remove(process);
          isIdle = true;
          // if all done then jump to loop
          if (queue.size() == 0) {
            break;
          }
        }

        if (j == timeQuantum
            && !isIdle) {
          // put it to the end
          queue.add(process);
          queue.remove(0);
          isIdle = true; // reset it to be idle.
        }

        // calculate time
        for (int i = 1; i < queue.size(); i++) {
          queue.get(i).waitTime++;
        }
        // one loop one tick
        tick += 1;
      }
    }
  }

  // this is a method for generate process but it only implement non preemitve in this way
  public void generateProcessQueue(final boolean isPreemptive,
      final int numProcesses,
      final int minBurstTime, final int maxBurstTime,
      final int maxArrivalsPerTick, final double probArrival) {
    if (!isPreemptive) {
      for (int i = 0; i < maxArrivalsPerTick; i++) {
        if (this.rng.nextDouble() < probArrival
            && queue.size() + completedProcess.size() < numProcesses) {
          int burstTime = this.rng.nextInt((maxBurstTime - minBurstTime) + 1) + minBurstTime;
          queue.add(new ScheduledProcess(pid, burstTime, tick));
          pid += 1;
        }
      }
      if (isPreemptive) {
      }
    }
  }

  public RandomScheduling(long rngSeed) {
    this.rng = new Random(rngSeed);
  }


  public void init() {
    // TODO - remove any information from the previous simulation, if necessary
    // ini
    completedProcess = new ArrayList<>();
    tick = 0;
    pid = 0;
    isIdle = true;
    queue = new ArrayList<>();
    start = 0;
    end = 0;
    process = new ScheduledProcess(0, 0, 0);
  }

  // List containing completed Processes, had to place it here due to how printResults() method setup.

  public void runNewSimulation(final boolean isPreemptive, final int timeQuantum,
      final int numProcesses,
      final int minBurstTime, final int maxBurstTime,
      final int maxArrivalsPerTick, final double probArrival) {
    init();

    // TODO:
    // 1. Run a simulation as a loop, with one iteration considered as one unit of time (tick)
    // 2. The simulation should involve the provided number of processes "numProcesses"
    // 3. The simulation loop should finish after the all of the processes have completed
    // 4. On each tick, a new process might arrive with the given probability (chance)
    // 5. However, the max number of new processes per tick is limited
    // by the given value "maxArrivalsPerTick"
    // 6. The burst time of the new process is chosen randomly in the interval
    // between the values "minBurstTime" and "maxBurstTime" (inclusive)

    // 7. When the CPU is idle and no process is running, the scheduler
    // should pick one of the available processes *at random* and start its execution
    // 8. If the preemptive version of the scheduling algorithm is invoked, then it should
    // allow up to "timeQuantum" time units (loop iterations) to the process,
    // and then preempt the process (pause its execution and return it to the queue)

    // If necessary, add additional fields (and methods) to this class to
    // accomodate your solution

    // Note: for all of the random number generation purposes in this method,
    // use "this.rng" !

    if (!isPreemptive) {
      nonPreemptive(numProcesses,
          minBurstTime,
          maxBurstTime,
          maxArrivalsPerTick,
          probArrival);


    } else if (isPreemptive) {
      Preemptive(timeQuantum,
          numProcesses,
          minBurstTime, maxBurstTime,
          maxArrivalsPerTick, probArrival);


    }

  }

  public void printResults() {
    // TODO:
    // 1. For each process, print its ID, burst time, arrival time, and total waiting time
    // 2. Afterwards, print the complete execution time of the simulation
    // and the average process waiting time
    double average = 0;
    // for average wait time
    System.out.println("\tProcess ID\tBurst Time\tArrival Time\tWait Time\t\n");
    for (ScheduledProcess process : completedProcess) {
      System.out.print("   " + process.getProcessId() + "\t\t\t\t\t\t");
      System.out.print(process.getBurstTime() + "\t\t\t\t\t\t");
      System.out.print(process.getArrivalMoment() + "\t\t\t\t\t\t");
      System.out.print(process.getWaitTime());
      System.out.println();
      average += process.getWaitTime();
    }
    System.out.println("Average wait time: " + average / completedProcess.size());
    System.out.println("Total execution time: " + tick);

  }


  public static void main(String args[]) {
    // TODO: replace the seed value below with your birth date, e.g., "20001001"
    final long rngSeed = 20000904;

    // Do not modify the code below - instead, complete the implementation
    // of other methods!
    RandomScheduling scheduler = new RandomScheduling(rngSeed);

    final int numSimulations = 5;

    final int numProcesses = 10;
    final int minBurstTime = 2;
    final int maxBurstTime = 10;
    final int maxArrivalsPerTick = 2;
    final double probArrival = 0.75;

    final int timeQuantum = 2;

    boolean[] preemptionOptions = {false, true};

    for (boolean isPreemptive : preemptionOptions) {

      for (int i = 0; i < numSimulations; i++) {
        System.out.println("Running " + ((isPreemptive) ? "preemptive" : "non-preemptive")
            + " simulation #" + i);

        scheduler.runNewSimulation(
            isPreemptive, timeQuantum,
            numProcesses,
            minBurstTime, maxBurstTime,
            maxArrivalsPerTick, probArrival);

        System.out.println("Simulation results:"
            + "\n" + "----------------------");
        scheduler.printResults();

        System.out.println("\n");
      }
    }

  }

}
