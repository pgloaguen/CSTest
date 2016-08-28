# Client/Server protocol exercise

## My solution

To limit the number of data sent via the network, I made the choice to use a custom binary format
and also to only send the fields which have been updated. Thanks to it I reduced the number of data sent by 75%.

To limit the cpu usage I stored the OS name, the pid and the resolution of the screen.
I also kept a reference to the factorial of ids. All of this to avoid to recompute those values each time a new Metric is generated

## Structure

**MetricFactory**: generates the metrics and also serializes and unserializes Metric objects.

**MetricSerializer**: serializes and unserializes Metric objects.

**Metric**: contains all metrics data at a given time.

**Producer**: sends Metric object via the socket by a random interval from 1 to 100 ms.

**Consumer**: listens the opened socket and writes data in the "cache" file

## Possible enhancement

The way the metrics are registered in the file "cache" is not efficient because we open/close for each Metric object we saved.


## How to compile
```
./gradlew assemble
```

## How to start tests
```
./gradlew test
```

## How to start a Consumer (server)
```
java -jar build/libs/ClientServerProtocolExercise-1.0-SNAPSHOT.jar -c 2000 // 200O or whatever the port you want
```


## How to start a Producer (client)
```
java -jar build/libs/ClientServerProtocolExercise-1.0-SNAPSHOT.jar -p 2000
```