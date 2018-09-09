package com.nettyrpc.test.app;

import com.nettyrpc.client.RPCFuture;
import com.nettyrpc.client.RpcClient;
import com.nettyrpc.client.proxy.IAsyncObjectProxy;
import com.nettyrpc.test.client.PersonService;
import com.nettyrpc.test.client.HelloService;
import com.nettyrpc.test.client.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest {

	@Autowired
	private RpcClient rpcClient;

	@Test
	public void helloTest1() {

		HelloService helloService = RpcClient.create(HelloService.class);
		String result = helloService.hello("World");
		Assert.assertEquals("Hello! World", result);
	}

	@Test
	public void helloTest2() {

		HelloService helloService = RpcClient.create(HelloService.class);
		Person person = new Person("Yong", "Huang");
		String result = helloService.hello(person);
		Assert.assertEquals("Hello! Yong Huang", result);
	}

	@Test
	public void helloPersonTest() {

		PersonService personService = RpcClient.create(PersonService.class);
		List<Person> persons = personService.getTestPerson("xiaoming", 5);
		List<Person> expectedPersons = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
		}
		assertThat(persons, equalTo(expectedPersons));
		for (int i = 0; i < persons.size(); ++i) {
			System.out.println(persons.get(i));
		}
	}

	@Test
	public void helloFutureTest1() throws ExecutionException, InterruptedException {

		IAsyncObjectProxy helloService = RpcClient.createAsync(HelloService.class);
		RPCFuture result = helloService.call("hello", "World");
		Assert.assertEquals("Hello! World", result.get());
	}

	@Test
	public void helloFutureTest2() throws ExecutionException, InterruptedException {

		IAsyncObjectProxy helloService = RpcClient.createAsync(HelloService.class);
		Person person = new Person("Yong", "Huang");
		RPCFuture result = helloService.call("hello", person);
		Assert.assertEquals("Hello! Yong Huang", result.get());
	}

	@Test
	public void helloPersonFutureTest1() throws ExecutionException, InterruptedException {

		IAsyncObjectProxy helloPersonService = RpcClient.createAsync(PersonService.class);
		RPCFuture result = helloPersonService.call("getTestPerson", "xiaoming", 5);
		List<Person> persons = (List<Person>) result.get();
		List<Person> expectedPersons = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
		}
		assertThat(persons, equalTo(expectedPersons));
		for (int i = 0; i < 5; ++i) {
			System.out.println(persons.get(i));
		}
	}

	@After
	public void setTear() {

		if (rpcClient != null) {
			rpcClient.stop();
		}
	}
}