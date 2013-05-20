import com.example.myapp.commons.ClusterFactory;
import com.example.myapp.commons.FinagleClientConfig;
import com.example.myapp.commons.FinagleThriftClientFactory;
import com.example.myapp.thrift.Foo;
import com.example.myapp.thrift.FooService;
import com.twitter.finagle.Service;
import com.twitter.finagle.Thrift;
import com.twitter.finagle.builder.Cluster;
import com.twitter.finagle.stats.InMemoryStatsReceiver;
import com.twitter.finagle.thrift.ThriftClient;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.util.Await;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.net.SocketAddress;
import java.util.Set;

public class MyAppThriftClient {
    public static void main(String[] args) {
        Cluster cluster = ClusterFactory.getCluster("FooService");

        // Querying for a list of online servers is not necessary for Finagle,
        // but can be used for vanilla thrift servers.
        Set<SocketAddress> onlineServers = ClusterFactory.getOnlineServers("FooService");
        System.out.println("Online servers: "+onlineServers.toString());
/*
        Service<ThriftClientRequest, byte[]> service =
                ClientBuilder.safeBuild(ClientBuilder.get()
                        .cluster(cluster) // this is where service discovery happens
                        .name("FooService client")
                        .codec(ThriftClientFramedCodec.get())
                        .timeout(Duration.apply(2, TimeUnit.SECONDS))
                        .retries(4)
                        .hostConnectionLimit(1)
                        // .logger(Logger.getLogger("ROOT"))
                );

        FooService.FutureIface client = new FooService.FinagledClient(
                service,
                new TBinaryProtocol.Factory(),
                "FooService",
                new InMemoryStatsReceiver()
        );*/


        Service<ThriftClientRequest, byte[]> service = Thrift.newClient(ClusterFactory.getGroup("FooService")).toService();
        FooService.FutureIface client = new FooService.FinagledClient(service, new TBinaryProtocol.Factory(), "FooService", new InMemoryStatsReceiver());
/*

        FooService.FutureIface client = FinagleThriftClientFactory.createThriftClient(
                FooService.FinagledClient.class,
                "FooService",
                new FinagleClientConfig(2000, 5, 10)
        );
*/

        // Do some stuff
        for (int i = 0; i < 20; i++) {
            // Call .get() on the future to wait for it to return a value
            try {
                Foo foo = Await.result(client.giveMeSomeFoo(i));
                System.out.println(foo.getBazz());
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            String msg = client.giveMeSomeFoo(i).map(
                    Foo foo -> { return "Got "+foo.getBazz(); }
            ).get();
            System.out.println(msg);
            */
            // (or use functional programming so you don't block the thread)
        }
    }
}