package edu.school42.fixme.router.routing;

import edu.school42.fixme.router.source.Source;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class RoutingTable {

	private final List<Source> sources = new ArrayList<>();

	public void add(Source source) {
		sources.add(source);
		sources.sort(Comparator.comparingLong(Source::getId).reversed());
	}

	public void replaceBrokerSourceId(Source source, Long newId) {
		sources
				.stream()
				.filter(aSource -> aSource.equals(source))
				.findAny()
				.ifPresent(aSource -> aSource.setId(newId));
	}

	public void remove(Source source) {
		sources.remove(source);
	}

	public AtomicReference<Socket> findSocket(long id, edu.school42.fixme.common.model.Source type) {
		return sources
				.stream()
				.filter(source -> source.getType() == type && source.getId() == id)
				.findAny()
				.map(Source::getSocket)
				.orElse(null);
	}
}
