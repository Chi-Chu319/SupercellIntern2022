This project uses the latest java 19 and maven.

Please download java from: https://www.oracle.com/java/technologies/downloads/
## Test
The tests are implemented with junit. They can be run with command
```aidl
mvn test -Dtest="Ex1Test"
mvn test -Dtest="Ex2Test"
```

The benchmark program is also implemented wit junit. It measures the performance boost on different amount of parallelism, user amount, action per user amount.

The test cases are generated randomly with CaseGenerator.

The performance boost will be printed to the console.

To run the benchmark test for problem 2
```aidl
 mvn test -Dtest="BenchmarkTest"
```
The benchmark test can also be adjusted very easily by modifying the configuration. Please see detail from the test file - BenchmarkTest.java.

## Build

There are two entry point in this project, namely NotificationProcessorCli.java for problem 1 and StateProcessorCli.java for problem 2.

By default, it is set to package the NotificationProcessorCli.java as the entry point. To run the StateProcessorCli.java, please change the content in pom.xml to
```aidl
<configuration>
    <archive>
        <manifest>
            <mainClass>fi.intern.supercell.clis.StateProcessorCli</mainClass>
        </manifest>
    </archive>
</configuration>
```

To build the package, please run (skip tests)
```aidl
mvn -Dmaven.test.skip=true clean package
```
To execute the problem 1 solver, run
```aidl
java -jar target/supercellIntern-1.0-SNAPSHOT.jar -i TESTFILE1
```
To execute the problem 2 solver, run
```aidl
java -jar target/supercellIntern-1.0-SNAPSHOT.jar -i TESTFILE2
```
To execute the problem 2 solver and enable concurrency, run
```aidl
java -jar target/supercellIntern-1.0-SNAPSHOT.jar -i TESTFILE2 -c
```

The output are available from the stdout

## Thoughts and notes on the implementation
### Problem 1
The first solution came into my mind is using graph. And indeed, I solved it with graph.

I have implemented an adj list graph using linked lists. And the vertices (users) are stored in an array list with a map as a symbol table to map username -> index on the array list.

Here is the first assumption: there is no duplicated username. This prevents users from updating other users' data, as commonly in games, no duplicated username is allowed. With this assumption, we could use the username directly as identifier.

If this assumption is relaxed. UUID is a good solution.

<br>
With the array list for user array, we could achieve O(1) for adding user and O(1) for random access. The second assumption is, users are not allowed to delete their account. Otherwise, the runtime could be O(n) for a delete operation (n is the number of users).

However, with this assumption relaxed, a disabled properties can be added to user (check when iterating through friends).

<br>
Using linked list as the friend list could bring us O(1) for adding friends and O(degree(user)) for friend deletion, assuming we don't allow user to have friends more than 50. In this sense, this application is still performant.

Another thought I have had is using adj array -> a 2D array for users. But it could bring us O(n^2) memory and adding element to array is always O(n). So this is not really applicable.

<br>
The value update of a user is done with a map of tuples. The tuple contains the value and the timestamp. Each time a value is updated, it checks the current timestamp and the stored timestamp. If the update is outdated, it is ditched.

### Problem 2
To achieve high concurrency in this application, the lock striping technique is adopted.

Lock striping is a technique where the locking occurs on several buckets or stripes. Here is the bucket is user.

The lock is locked on each user. So all the update for one user is sequential but for different user, the updates are in parallel.

And for user creation, instead of using arraylist, they are put directly into the map. So np data race for the array index.

This eliminates the data race case where multiple thread are trying to create, perform action on a user. Here, we assume the user amount is smaller than the bucket size. With this technique, we can achieve rather significant boost on performance, e.g. 5x faster on an 8 core processor (available from the benchmark test).

Even though this works very well with the given and random test cases, in real life, a more complicated (and performant) solution can be implemented, e.g. synchronized value update, friends operation, so the actions on a single user can also run in parallel.
(I really like to know how supercell does it :))

Through the benchmark program, on average, there is 1.9 boost for 2 cores, 3x boost for 4 cores, 5x boost for cores. 