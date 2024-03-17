package edu.school42.fixme.market.service;

import edu.school42.fixme.market.model.FixMessageEntity;
import edu.school42.fixme.market.repository.FixMessagesRepository;
import lombok.RequiredArgsConstructor;

public class FixMessagesService {

	private final FixMessagesRepository fixMessagesRepository;

	public FixMessagesService(FixMessagesRepository fixMessagesRepository) {
		this.fixMessagesRepository = fixMessagesRepository;
	}

	public void update(FixMessageEntity entity) {
		fixMessagesRepository.update(entity);
	}

}
