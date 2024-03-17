package edu.school42.fixme.broker.validation;

import edu.school42.fixme.broker.Broker;
import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.dto.FixMessageDto;
import edu.school42.fixme.common.dto.MessageType;
import edu.school42.fixme.common.exception.FixMessageValidationException;
import edu.school42.fixme.common.options.BrokerOptions;
import edu.school42.fixme.common.util.FixMessageUtil;
import edu.school42.fixme.common.validation.FixMessageValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class BrokerValidation extends FixMessageValidator {

	public BrokerValidation(FixMessageMapper mapper) {
		super(mapper);
	}

	@Override
	public FixMessageDto validate(String message) throws FixMessageValidationException {
		validateNecessary(message);
		validateTags(message, FixMessageUtil.BROKER_TAGS);
		validateTagsAndSeparators(message, FixMessageUtil.BROKER_TAGS);

		Map<Integer, Object> fixMessageAsMap = new HashMap<>();
		Arrays.stream(message.split("\\|")).forEach(section -> {
			String[] splitSection = section.split("=");
			if (splitSection.length != 2) {
				throw new FixMessageValidationException("Wrong amount of arguments");
			}
			fixMessageAsMap.put(Integer.parseInt(splitSection[0]), splitSection[1]);
		});

		FixMessageDto dto = new FixMessageDto();
		dto.setId(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
		dto.setOrderId(UUID.randomUUID().toString());
		dto.setType(MessageType.PLACE_ORDER);
		dto.setTargetId(Long.parseLong((String) fixMessageAsMap.get(FixMessageUtil.MARKET)));
		dto.setSendersId(Broker.ID);
		try {
			dto.setSide(BrokerOptions.valueOf((String) fixMessageAsMap.get(FixMessageUtil.SIDE)));
		} catch (IllegalArgumentException e) {
			throw new FixMessageValidationException("Validation error");
		}
		dto.setInstrument((String) fixMessageAsMap.get(FixMessageUtil.INSTRUMENT));
		var quantity = Integer.parseInt((String) fixMessageAsMap.get(FixMessageUtil.QUANTITY));
		var price = Double.parseDouble((String) fixMessageAsMap.get(FixMessageUtil.PRICE));
		if (quantity < 0 || price < 0D) {
			throw new FixMessageValidationException("Validation error");
		}
		dto.setQuantity(quantity);
		dto.setMarket(dto.getTargetId());
		dto.setPrice(price);
		dto.countBodyLength();
		dto.countChecksum();
		return dto;
	}

}
