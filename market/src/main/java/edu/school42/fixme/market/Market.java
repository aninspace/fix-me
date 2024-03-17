package edu.school42.fixme.market;

import edu.school42.fixme.market.model.Instrument;
import edu.school42.fixme.market.socket.MarketSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Market {

	public static final List<Instrument> INSTRUMENTS = new ArrayList<>(List.of(
			new Instrument().setInstrument("A").setQuantity(ThreadLocalRandom.current().nextInt(100, 100000)),
			new Instrument().setInstrument("B").setQuantity(ThreadLocalRandom.current().nextInt(100, 100000)),
			new Instrument().setInstrument("C").setQuantity(ThreadLocalRandom.current().nextInt(100, 100000))));

	public static double MONEY = ThreadLocalRandom.current().nextDouble(1_000_00, Integer.MAX_VALUE);

	private static final int MARKET_PORT = 5001;


	public void start() {
		try {
			new MarketSocket(MARKET_PORT).start();
		} catch	(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}


}