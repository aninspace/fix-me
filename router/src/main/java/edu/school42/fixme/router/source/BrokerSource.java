package edu.school42.fixme.router.source;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BrokerSource extends Source {

	public BrokerSource(AtomicReference<Socket> socket) {
		super(socket);
		generateUniqueId();
		this.type = edu.school42.fixme.common.model.Source.BROKER;
	}

	private void generateUniqueId() {
		int potentialId = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
		this.id = (potentialId % 2 == 1) ? potentialId + 1 : potentialId;
		if (this.id >= 1_000_000) {
			this.id -= 2;
		}
	}
}
