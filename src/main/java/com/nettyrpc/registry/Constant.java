package com.nettyrpc.registry;

/**
 * ZooKeeper constant
 */
public interface Constant {

	public int ZK_SESSION_TIMEOUT = 5000;

	public String ZK_REGISTRY_PATH = "/registry";

	public String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}