package edu.school42.fixme.market.socket;

import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.dto.FixMessageDto;
import edu.school42.fixme.common.model.Source;
import edu.school42.fixme.market.Market;
import edu.school42.fixme.market.handling.BuyHandling;
import edu.school42.fixme.market.handling.SellHandling;
import edu.school42.fixme.market.model.FixMessageEntity;
import edu.school42.fixme.market.repository.FixMessagesRepository;
import edu.school42.fixme.market.service.FixMessagesService;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class MarketSocket {

	private final int port;

	private final FixMessageMapper mapper = new FixMessageMapper();
	private final FixMessagesService fixMessagesService = new FixMessagesService(new FixMessagesRepository());
	private static final String HOST = "localhost";

	public MarketSocket(int port) {
		this.port = port;
	}

	public void start() {
		try (
				Socket socket = new Socket(HOST, port);
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))
		) {
			log.info("received :: {}", br.readLine());

			while (true) {
				log.info("Available instruments: ");
				Market.INSTRUMENTS.forEach(
						i -> log.info("{} - {}", i.getInstrument(), i.getQuantity())
				);
				String line = br.readLine();
				if (Objects.isNull(line)) {
					break ;
				}
				log.info("received :: {}", line);
				if (!line.contains("35=C|") && !line.contains("35=V|")) {
					updateBrokerEntity(line);
					FixMessageDto dto = mapper.toDto(line);
					String incomingMessage = switch (dto.getSide()) {
						case BUY -> new BuyHandling().handle(dto, mapper);
						case SELL -> new SellHandling().handle(dto, mapper);
					};
					updateMarketEntity(incomingMessage);
					pw.println(incomingMessage);
					log.info("sent :: {}", incomingMessage);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}


	private void updateBrokerEntity(String line) {
		FixMessageEntity requestEntity = new FixMessageEntity();
		requestEntity.setBody(line);
		requestEntity.setSource(Source.BROKER);
		requestEntity.setStatus(FixMessageEntity.Status.REQUEST);
		fixMessagesService.update(requestEntity);
	}


	private void updateMarketEntity(String incomingMessage) {
		FixMessageEntity responseEntity = new FixMessageEntity();
		responseEntity.setBody(incomingMessage);
		responseEntity.setSource(Source.MARKET);
		responseEntity.setStatus(FixMessageEntity.Status.RESPONSE);
		fixMessagesService.update(responseEntity);
	}
}
