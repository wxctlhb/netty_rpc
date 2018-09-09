package com.nettyrpc.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.nettyrpc.client.ConnectManage;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscovery {

	private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private volatile List<String> dataList = new ArrayList<>();

	private String registryAddress;
	private ZooKeeper zookeeper;

	public ServiceDiscovery(String registryAddress) {

		this.registryAddress = registryAddress;
		zookeeper = this.connectServer();
		if (zookeeper != null) {
			this.watchNode(zookeeper);
		}
	}

	private ZooKeeper connectServer() {

		ZooKeeper zooKeeper = null;
		try {
			zooKeeper = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {

				@Override
				public void process(WatchedEvent event) {

					if (event.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (IOException | InterruptedException e) {
			logger.error("", e);
		}
		return zooKeeper;
	}

	private void watchNode(final ZooKeeper zk) {

		try {
			List<String> nodeLists = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						watchNode(zk);
					}
				}
			});
			List<String> dataList = new ArrayList<>();
			for (String node : nodeLists) {
				byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
				dataList.add(new String(bytes));
			}
			System.out.println(dataList);
			logger.debug("node data: {}", dataList);
			this.dataList = dataList;
			logger.debug("Service discovery triggered updating connected server node.");
			updateConnectedServer();
		} catch (KeeperException | InterruptedException e) {
			logger.error("", e);
		}
	}

	private void updateConnectedServer() {

		ConnectManage.getInstance().updateConnectedServer(this.dataList);
	}

	public void stop() {
		if (zookeeper != null) {
			try {
				zookeeper.close();
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}
	}
}