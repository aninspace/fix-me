package edu.school42.fixme.router.source;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@EqualsAndHashCode
@ToString
public abstract class Source {

	@Setter
	protected long id;
	protected edu.school42.fixme.common.model.Source type;

	protected final AtomicReference<Socket> socket;

	protected Source(AtomicReference<Socket> socket) {
		this.socket = socket;
	}
}
