package com.nettyrpc.test.app;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nettyrpc.client.RpcClient;
import com.nettyrpc.registry.ServiceDiscovery;
import com.nettyrpc.test.client.HelloService;

public class Benchmark {

	private static final Logger logger = LoggerFactory.getLogger(Benchmark.class);

	public static void main(String[] args) throws InterruptedException {

		ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
		final RpcClient rpcClient = new RpcClient(serviceDiscovery);
		int threadNum = 10;
		final int requestNum = 100;
		Thread[] threads = new Thread[threadNum];
		long startTime = System.currentTimeMillis();
		// benchmark for sync call
		for (int i = 0; i < threadNum; ++i) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					
					for (int i = 0; i < requestNum; i++) {
						final HelloService syncClient = RpcClient.create(HelloService.class);
						String result = syncClient.hello(Integer.toString(i));
						if (result.equals("Hello! " + i)) {
							logger.info("Hello! {}", i);
						} else {
							logger.info("error = {}", result);
						}
					}
				}
			});
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		long timeCost = (System.currentTimeMillis() - startTime);
		logger.info("Sync call total-time-cost:{}ms, req/s={}", timeCost,
				new DecimalFormat("#.00").format(((double) (requestNum * threadNum)) / timeCost * 1000));
		rpcClient.stop();
	}
}