=== Thrift-Zookeeper example ===
This example used Finagle, a library developed at Twitter.
It contains:
- api: A Thrift API that defines a FooService
- commons-thrift: Common logic used for service discovery
- server: A Thrift server providing the FooService
- client: A consumer of the service

To run:
- mvn clean install
- Run a local ZooKeeper instance
- Start as many instances of MyAppThriftServer as you want
- Run the client (MyAppThriftClient)

Example output:
Got hey, this is a response from port=3705
Got hey, this is a response from port=3705
Got hey, this is a response from port=5071
Got hey, this is a response from port=5071
Got hey, this is a response from port=2381
Got hey, this is a response from port=3705
...

Monitoring
- Start a server instance
- Check out http://localhost:XXXX/stats.txt (XXXX is the port number for Ostrich admin)

What are the good parts?
- Service discovery
- Finagle handles load balancing, so it connects to one of the servers ZooKeeper has registered.
- It also handles connection pooling, retries, timeouts, statistics, backpressure
- It can be used to create Scala-native Thrift clients/servers
- It's written to be asynchronous
- No thrift binary required for building
- Throw in 10 lines of code and there's monitoring, with Ostrich:
https://github.com/twitter/ostrich
- Throw in 10 lines of code and there's distributed tracing, with Zipkin:
- https://github.com/twitter/zipkin