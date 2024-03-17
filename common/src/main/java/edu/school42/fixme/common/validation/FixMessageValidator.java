package edu.school42.fixme.common.validation;

import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.dto.FixMessageDto;
import edu.school42.fixme.common.exception.FixMessageValidationException;
import edu.school42.fixme.common.util.FixMessageUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class FixMessageValidator {

	private final FixMessageMapper mapper;

	public abstract FixMessageDto validate(String message) throws FixMessageValidationException;

	protected void validateNecessary(String message) throws FixMessageValidationException {
		if (message.isBlank() && !message.startsWith(String.valueOf(FixMessageUtil.ID).concat("="))) {
			throw new FixMessageValidationException("Validation error");
		}
	}

	protected void validateTags(String message, List<Integer> tags) throws FixMessageValidationException {
		long count = countTags(message, tags);
		if (count != tags.size()) {
			throw new FixMessageValidationException("Validation error");
		}
	}

	protected void validateTagsAndSeparators(String message, List<Integer> tags) throws FixMessageValidationException {
		long countTags = countTags(message, tags);
		long countKv = countSeparators(message, "=");
		long countSections = countSeparators(message, "|");

		if (countTags * 2 != countKv + countSections) {
			throw new FixMessageValidationException("Validation error");
		}
	}

	private long countTags(String message, List<Integer> tags) {
		return tags.stream()
				.filter(tag -> message.contains("|".concat(String.valueOf(tag)).concat("="))
						|| message.contains(String.valueOf(tag).concat("=")))
				.count();
	}

	private long countSeparators(String message, String separator) {
		return message.chars().reduce(0, (sub, curr) -> {
			if (curr == separator.charAt(0)) {
				++sub;
			}
			return sub;
		});
	}
}
