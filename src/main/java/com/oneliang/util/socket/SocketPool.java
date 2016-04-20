package com.oneliang.util.socket;

import java.io.IOException;
import java.net.Socket;

import com.oneliang.util.resource.ResourcePool;
import com.oneliang.util.resource.ResourcePoolException;

public class SocketPool extends ResourcePool<Socket> {

	protected void destroyResource(Socket resource) throws ResourcePoolException {
		try {
			resource.close();
		} catch (IOException e) {
			throw new ResourcePoolException(e);
		}
	}
}