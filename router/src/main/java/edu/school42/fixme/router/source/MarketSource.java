package edu.school42.fixme.router.source;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MarketSource extends Source {

	public MarketSource(AtomicReference<Socket> socket) {
		super(socket);
		setId();
		this.type = edu.school42.fixme.common.model.Source.MARKET;
	}

	private void setId() {
		int generatedId = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
		this.id = generatedId % 2 == 0 ? generatedId + 1 : generatedId;
	}
}
