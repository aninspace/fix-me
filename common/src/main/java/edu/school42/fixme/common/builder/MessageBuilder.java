package edu.school42.fixme.common.builder;

import edu.school42.fixme.common.dto.FixMessageDto;
import edu.school42.fixme.common.dto.MessageType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageBuilder {

	private int msgId = 0;

	private static MessageBuilder MESSAGE_BUILDER;

	public static MessageBuilder getInstance() {
		if (MESSAGE_BUILDER == null) {
			MESSAGE_BUILDER = new MessageBuilder();
		}
		return MESSAGE_BUILDER;
	}

	public FixMessageDto confirmationOfIdMessage(long sourceId, long routerId) {
		FixMessageDto dto = new FixMessageDto();
		dto.setId(++msgId);
		dto.setType(MessageType.ID_CONFIRMATION);
		dto.setOrderId(String.valueOf(routerId));
		dto.setSendersId(routerId);
		dto.setTargetId(sourceId);
		dto.setSourceId((int) sourceId);

		dto.countBodyLength();
		dto.countChecksum();
		return dto;
	}

	public FixMessageDto validationErrorMessage(long sourceId, String orderId, long routerId) {
		FixMessageDto dto = new FixMessageDto();
		dto.setId(++msgId);
		dto.setType(MessageType.VALIDATION_ERROR);
		dto.setOrderId(orderId);
		dto.setSendersId(routerId);
		dto.setTargetId(sourceId);

		dto.countBodyLength();
		dto.countChecksum();
		return dto;
	}
}
