package edu.school42.fixme.broker;

import edu.school42.fixme.broker.socket.BrokerSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Broker {

	public static final long ID = System.currentTimeMillis();
	private static final int BROKER_PORT = 5002;


	public void start() {
		try {
			new BrokerSocket(BROKER_PORT).start();
		} catch	(RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}