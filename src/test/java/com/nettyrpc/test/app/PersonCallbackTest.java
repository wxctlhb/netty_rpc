package com.nettyrpc.test.app;

import com.nettyrpc.client.AsyncRPCCallback;
import com.nettyrpc.client.RPCFuture;
import com.nettyrpc.client.RpcClient;
import com.nettyrpc.client.proxy.IAsyncObjectProxy;
import com.nettyrpc.registry.ServiceDiscovery;
import com.nettyrpc.test.client.PersonService;
import com.nettyrpc.test.client.Person;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonCallbackTest {

	private static final Logger logger = LoggerFactory.getLogger(BenchmarkAsync.class);

	public static void main(String[] args) {

		ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
		final RpcClient rpcClient = new RpcClient(serviceDiscovery);
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		try {
			IAsyncObjectProxy client = RpcClient.createAsync(PersonService.class);
			RPCFuture helloPersonFuture = client.call("getTestPerson", "xiaoming", 5);
			helloPersonFuture.addCallback(new AsyncRPCCallback() {

				@Override
				public void success(Object result) {

					List<Person> persons = (List<Person>) result;
					for (int i = 0; i < persons.size(); ++i) {
						logger.info(persons.get(i).toString());
					}
					countDownLatch.countDown();
				}

				@Override
				public void fail(Exception e) {
					logger.info(e.getMessage());
					countDownLatch.countDown();
				}
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		}
		rpcClient.stop();
		logger.info("End");
	}
}