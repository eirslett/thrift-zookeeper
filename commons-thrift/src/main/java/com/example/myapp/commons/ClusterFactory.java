package com.example.myapp.commons;

import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.zookeeper.ServerSet;
import com.twitter.common.zookeeper.ServerSetImpl;
import com.twitter.common.zookeeper.ZooKeeperClient;
import com.twitter.finagle.builder.Server;
import com.twitter.finagle.zookeeper.ZookeeperServerSetCluster;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class ClusterFactory {
    static Amount<Integer,Time> sessionTimeout = Amount.of(15, Time.SECONDS);
    static List<InetSocketAddress> nodes = Arrays.asList(
            // Use a cluster of nodes...
            // new InetSocketAddress("zk1.myapp.com", 2181),
            // new InetSocketAddress("zk2.myapp.com", 2181),
            // new InetSocketAddress("zk3.myapp.com", 2181),
            // new InetSocketAddress("zk4.myapp.com", 2181),
            // new InetSocketAddress("zk5.myapp.com", 2181),

            // ...or use local ZooKeeper node
            new InetSocketAddress("localhost", 2181)
    );

    public static ZookeeperServerSetCluster getForService(String clusterName) {
        ServerSet serverSet = new ServerSetImpl(getZooKeeperClient(), "/myapp/services/"+clusterName);
        return new ZookeeperServerSetCluster(serverSet);
    }

    public static void reportServerUpAndRunning(Server server, String clusterName) {
        getForService(clusterName).join(server.localAddress(), new scala.collection.immutable.HashMap());
    }

    private static ZooKeeperClient zooKeeperClient;
    private static ZooKeeperClient getZooKeeperClient() {
        if (zooKeeperClient == null) {
            zooKeeperClient = new ZooKeeperClient(sessionTimeout, nodes);
        }
        return zooKeeperClient;
    }
}
