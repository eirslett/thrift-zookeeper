package com.example.myapp.commons;

import com.twitter.finagle.Filter;
import com.twitter.finagle.Service;
import com.twitter.finagle.SimpleFilter;
import com.twitter.finagle.Thrift;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.service.TimeoutFilter;
import com.twitter.finagle.stats.InMemoryStatsReceiver;
import com.twitter.finagle.stats.StatsReceiver;
import com.twitter.finagle.thrift.ThriftClientFramedCodec;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.finagle.util.DefaultTimer;
import com.twitter.util.Duration;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class FinagleThriftClientFactory {
    public static <T> T createThriftClient(
            Class<T> type,
            String name,
            FinagleClientConfig config
    ) {
        try {
            TimeoutFilter<ThriftClientRequest, byte[]> timeoutFilter = timeoutFilter(config.getTimeout());

            Service<ThriftClientRequest, byte[]> service = Thrift.newClient(ClusterFactory.getGroup("FooService")).toService();
            Service<ThriftClientRequest, byte[]> composed = timeoutFilter.andThen(service); //  timeoutFilter.andThen(service);
            /*
            Service<ThriftClientRequest, byte[]> composed =
                    ClientBuilder.safeBuild(ClientBuilder.get()
                            .cluster(ClusterFactory.getCluster(name))
                            .name(name)
                            .codec(ThriftClientFramedCodec.get())
                            .timeout(Duration.apply(config.getTimeout(), TimeUnit.MILLISECONDS))
                            .retries(config.getRetries())
                            .hostConnectionLimit(config.getHostConnectionLimit())
                    );
*/

            return (T)type.getConstructor(
                    Service.class,
                    TProtocolFactory.class,
                    String.class,
                    StatsReceiver.class
            ).newInstance(
                    composed,
                    new TBinaryProtocol.Factory(),
                    name,
                    new InMemoryStatsReceiver()
            );
        } catch (InstantiationException |
                IllegalAccessException |
                InvocationTargetException |
                NoSuchMethodException e) {
            throw new ClientCreationException("Could not create Thrift client '"+name+"'", e);
        }
    }

    public static TimeoutFilter<ThriftClientRequest, byte[]> timeoutFilter(int ms) {
        return new TimeoutFilter<>(Duration.apply(ms, TimeUnit.MILLISECONDS), DefaultTimer.twitter());
    }

    public static <T> T createThriftClient(
            Class<T> type,
            String name,
            FinagleClientConfigProvider configProvider
    ) {
        return createThriftClient(type, name, configProvider.getConfig(name));
    }

    public static <T> T createThriftClient(
            Class<T> type,
            String name,
            int timeout,
            int retries,
            int hostConnectionLimit
    ) {
        return createThriftClient(type, name, new FinagleClientConfig(timeout, retries, hostConnectionLimit));
    }
}
