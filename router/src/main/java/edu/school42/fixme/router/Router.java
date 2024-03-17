package edu.school42.fixme.router;

import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.builder.MessageBuilder;
import edu.school42.fixme.router.socket.RouterSocket;
import edu.school42.fixme.router.routing.RoutingTable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Slf4j
public class Router {
	public static final RoutingTable ROUTING_TABLE = new RoutingTable();
	public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);

	private static final int BROKER_PORT = 5002;
	private static final int MARKET_PORT = 5001;


	public void start() {
		try {
			FixMessageMapper mapper = new FixMessageMapper();
			MessageBuilder messageBuilder = MessageBuilder.getInstance();
			log.info("Router connected to database");

			EXECUTOR_SERVICE.submit(new RouterSocket(BROKER_PORT, mapper, messageBuilder));
			EXECUTOR_SERVICE.submit(new RouterSocket(MARKET_PORT, mapper, messageBuilder));

			EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch	(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}