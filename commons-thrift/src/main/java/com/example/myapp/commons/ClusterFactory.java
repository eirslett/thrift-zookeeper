package com.example.myapp.commons;

import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.zookeeper.ServerSet;
import com.twitter.common.zookeeper.ServerSetImpl;
import com.twitter.common.zookeeper.ZooKeeperClient;
import com.twitter.finagle.Client;
import com.twitter.finagle.Group;
import com.twitter.finagle.builder.Server;
import com.twitter.finagle.zookeeper.ZkResolver;
import com.twitter.finagle.zookeeper.ZookeeperServerSetCluster;
import scala.collection.JavaConversions;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

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

    public static ZookeeperServerSetCluster getCluster(String clusterName) {

        ServerSet serverSet = new ServerSetImpl(getZooKeeperClient(), getPath(clusterName));
        return new ZookeeperServerSetCluster(serverSet);
    }

    public static ServerSet getServerSet(String groupName) {
        return new ServerSetImpl(getZooKeeperClient(), getPath(groupName));
    }

    public static Group<SocketAddress> getGroup(String groupName) {
        return new ZkResolver().resolve("127.0.0.1:2181!"+getPath(groupName)).get();
    }

    public static void reportServerUpAndRunning(Server server, String clusterName) {
        getCluster(clusterName).join(server.localAddress(), new scala.collection.immutable.HashMap());
    }

    public static Set<SocketAddress> getOnlineServers(String clusterName) {
        return JavaConversions.asJavaSet(getGroup(clusterName).members());
    }

    private static String getPath(String clusterName) {
        return "/myapp/services/"+clusterName;
    }

    private static ZooKeeperClient zooKeeperClient;
    private static ZooKeeperClient getZooKeeperClient() {
        if (zooKeeperClient == null) {
            zooKeeperClient = new ZooKeeperClient(sessionTimeout, nodes);
        }
        return zooKeeperClient;
    }
}
