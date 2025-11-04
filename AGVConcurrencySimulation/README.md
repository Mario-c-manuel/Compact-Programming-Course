====================================================
             AGV CONCURRENCY SIMULATION
====================================================

OVERVIEW:
This project simulates multiple Automated Guided Vehicles
(AGVs) performing tasks concurrently in a storage system.
It demonstrates:
 - Task assignment to multiple AGVs
 - Concurrent execution using Java threads
 - Logging of AGV activities

The system allows users to start tasks for AGVs, track
their progress, and view activity logs by AGV and date.

----------------------------------------------------
FEATURES IMPLEMENTED:
----------------------------------------------------

1. AGV TASK MANAGEMENT (AGV.java)
   - Start and complete tasks independently
   - Each AGV runs on a separate thread
   - Logs task start, completion, and status updates

2. TASK ASSIGNMENT (TaskManager.java)
   - Assign tasks to available AGVs
   - Handles concurrency and avoids conflicts
   - Supports multiple tasks per AGV

3. DAILY LOGGING (DailyLogger.java)
   - Logs stored in /logs/AGV/ folders
   - Each log file named <AGVName>_YYYY-MM-DD.log
   - Supports interactive reading of log files

----------------------------------------------------
EXAMPLE USAGE:
----------------------------------------------------
 - Start AGV1 and AGV2
 - Assign Task 101 to AGV1, Task 102 to AGV2
 - Monitor console for task progress and logs
 - Open AGV logs by entering AGV name and date
 - Complete tasks and review system behavior

----------------------------------------------------
SAMPLE LOG ENTRY:
----------------------------------------------------
[09:15:30] AGV1 started Task #101
[09:16:05] AGV2 started Task #102
[09:17:10] AGV1 completed Task #101
[09:18:00] AGV2 completed Task #102
[09:18:10] AGV1 ready for next task

----------------------------------------------------
HOW TO RUN:
----------------------------------------------------
1. Open the project in Eclipse.
2. Run the Main class.
3. Follow console prompts to:
   - Start AGVs
   - Assign tasks
   - View AGV activity logs

----------------------------------------------------
NOTES / OPTIONAL FEATURES:
----------------------------------------------------
- Only task execution and logging are implemented.
  Task cancellation, rescheduling, or advanced
  conflict resolution are not included.
- Thread safety is managed within TaskManager for
  simple concurrency scenarios.
- Date input is expected in YYYY-MM-DD format. Invalid
  dates may not be validated.

----------------------------------------------------
DEPENDENCIES / REQUIREMENTS:
----------------------------------------------------
- Java JDK 11 or higher
- Eclipse IDE (any recent version)
- No external libraries required

----------------------------------------------------
PROJECT STRUCTURE:
----------------------------------------------------
AGVConcurrencySimulation/
│
├─ src/
│   ├─ AGV.java
│   ├─ TaskManager.java
│   ├─ DailyLogger.java
│   └─ Main.java
│
└─ logs/
    └─ AGV/
====================================================
