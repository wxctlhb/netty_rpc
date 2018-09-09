package com.nettyrpc.test.util;

import com.nettyrpc.protocol.JsonUtil;
import com.nettyrpc.protocol.RpcRequest;
import com.nettyrpc.protocol.RpcResponse;
import com.nettyrpc.protocol.SerializationUtil;
import com.nettyrpc.test.client.Person;
import com.nettyrpc.test.server.HelloServiceImpl;

import java.util.UUID;

public class JsonTest {

	public static void main(String[] args) {

		RpcResponse response = new RpcResponse();
		response.setRequestId(UUID.randomUUID().toString());
		response.setError("Error msg");
		System.out.println(response.getRequestId());
		byte[] data1 = JsonUtil.serialize(response);
		System.out.println("Json byte length: " + data1.length);
		byte[] datas2 = SerializationUtil.serialize(response);
		System.out.println("Protobuf byte length: " + datas2.length);
		RpcResponse resp = (RpcResponse) JsonUtil.deserialize(data1, RpcResponse.class);
		System.out.println(resp.getRequestId());

		RpcRequest request = new RpcRequest();
		request.setClassName(HelloServiceImpl.class.getName());
		request.setMethodName(HelloServiceImpl.class.getDeclaredMethods()[0].getName());
		Person person = new Person("lu", "xiaoxun");
		request.setParameters(new Object[] { person });
		request.setRequestId(UUID.randomUUID().toString());
		System.out.println(request.getRequestId());
		byte[] data11 = JsonUtil.serialize(request);
		System.out.println("Json byte length: " + data11.length);
		byte[] datas22 = SerializationUtil.serialize(request);
		System.out.println("Protobuf byte length: " + datas22.length);
		RpcRequest req = (RpcRequest) JsonUtil.deserialize(data11, RpcRequest.class);
		System.out.println(req.getRequestId());
	}
}