package edu.school42.fixme.router.socket;

import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.builder.MessageBuilder;
import edu.school42.fixme.router.Router;
import edu.school42.fixme.router.process.MessageHandler;
import edu.school42.fixme.router.source.Source;
import edu.school42.fixme.router.source.BrokerSource;
import edu.school42.fixme.router.source.MarketSource;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Accessors(chain = true)
@RequiredArgsConstructor
public class RouterSocket implements Runnable {
	private final int port;
	private final FixMessageMapper mapper;
	private final MessageBuilder messageBuilder;

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port, 1000, Inet4Address.getByName("0.0.0.0"))) {
			log.info("server-socket started at port :: {}", serverSocket.getLocalPort());
			while (true) {
				AtomicReference<Socket> socket = new AtomicReference<>(serverSocket.accept());
				Source source = switch (socket.get().getLocalPort()) {
					case 5002 -> {
						log.info("connected new broker");
						yield new BrokerSource(socket);
					}
					case 5001 -> {
						log.info("connected new market");
						yield new MarketSource(socket);
					}
					default -> throw new RuntimeException(String.format("unknown port :: %d", socket.get().getPort()));
				};
				Router.ROUTING_TABLE.add(source);
				log.info("added {} with id :: {}", source.getType(), source.getId());
				Router.EXECUTOR_SERVICE.execute(new MessageHandler(source, socket, mapper, messageBuilder));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
