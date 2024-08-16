# TGF Traffic Generator Function

TGF (Traffic Generator Function) is a sophisticated simulator designed to mimic real-world data, voice, and SMS transactions.
The system fetches MSISDNs from Hazelcast, generates random consumption data, and efficiently sends these transactions
to the Charging Function (CHF) using Akka.

TGF is built with multi-threading capabilities, allowing it to run multiple Java threads simultaneously for enhanced performance.
It also includes a console-based user interface, providing a set of commands to monitor and control traffic flow effectively.
For logging, TGF utilizes Log4j, ensuring that all activities and transactions are accurately logged for later analysis.

This project serves as an invaluable tool for testing and simulating telecom transaction environments,
offering a robust platform for developers and testers to assess system performance under various conditions.


## TGF Commands:

| Command           | Description                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `start`           | Starts all threads managed by `threadManager`.                              |
| `stop`            | Stops all threads managed by `threadManager`.                               |
| `terminate`       | Terminates the command processing loop.                                     |
| `setDelay`        | Updates the delay for all transaction types.                                |
| `setDelayVoice`   | Updates the delay specifically for voice transactions.                      |
| `setDelayData`    | Updates the delay specifically for data transactions.                       |
| `setDelaySms`     | Updates the delay specifically for SMS transactions.                        |
| `setTps`          | Sets the delay for all transaction types based on the Transactions Per Second (TPS). |
| `setTpsVoice`     | Sets the delay for voice transactions based on TPS.                         |
| `setTpsData`      | Sets the delay for data transactions based on TPS.                          |
| `setTpsSms`       | Sets the delay for SMS transactions based on TPS.                           |
| `printDelay`      | Prints the current delay settings for all transaction types.                |
| `printTps`        | Prints the current TPS settings for all transaction types.                  |
| `printStats`      | Prints the statistics collected by `statsManager`.                          |
| `resetStats`      | Resets all collected statistics in `statsManager`.                          |
| `updateMsisdn`    | Updates the list of MSISDNs from Hazelcast.                                 |
| `testRandom`      | Prints a random transaction to test values.                                 |
| `default`         | Prints an error message for unrecognized commands.                          |
