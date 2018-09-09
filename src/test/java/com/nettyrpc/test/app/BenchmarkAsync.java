package com.nettyrpc.test.app;

import com.nettyrpc.client.RPCFuture;
import com.nettyrpc.client.RpcClient;
import com.nettyrpc.client.proxy.IAsyncObjectProxy;
import com.nettyrpc.registry.ServiceDiscovery;
import com.nettyrpc.test.client.HelloService;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkAsync {

	private static final Logger logger = LoggerFactory.getLogger(BenchmarkAsync.class);

	public static void main(String[] args) throws InterruptedException {

		ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
		final RpcClient rpcClient = new RpcClient(serviceDiscovery);
		int threadNum = 10;
		final int requestNum = 20;
		Thread[] threads = new Thread[threadNum];
		long startTime = System.currentTimeMillis();
		// benchmark for async call
		for (int i = 0; i < threadNum; ++i) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					
					for (int i = 0; i < requestNum; i++) {
						try {
							IAsyncObjectProxy client = RpcClient.createAsync(HelloService.class);
							RPCFuture helloFuture = client.call("hello", Integer.toString(i));
							String result = (String) helloFuture.get(3000, TimeUnit.MILLISECONDS);
							if (result.equals("Hello! " + i)) {
								logger.info("Hello! {}", i);
							} else {
								logger.info("error = {}", result);
							}
						} catch (Exception e) {
							System.out.println(e);
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