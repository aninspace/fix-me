package edu.school42.fixme.market.handling;

import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.dto.FixMessageDto;
import edu.school42.fixme.common.dto.MessageType;
import edu.school42.fixme.common.options.MarketOptions;
import edu.school42.fixme.market.Market;
import edu.school42.fixme.market.model.Instrument;
import java.util.concurrent.ThreadLocalRandom;

public class BuyHandling {

    protected FixMessageDto getReject(FixMessageDto responseDto, String message) {
        responseDto.setOrdStatus(MarketOptions.REJECTED);
        responseDto.setOrdRejReason(message);
        responseDto.countBodyLength();
        responseDto.countChecksum();
        return responseDto;
    }

    protected FixMessageDto createHeader(FixMessageDto dto) {
        FixMessageDto header = new FixMessageDto();
        header.setId(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 0));
        header.setOrderId(dto.getOrderId());
        header.setType(MessageType.HANDLE_ORDER);
        header.setSendersId(dto.getTargetId());
        header.setTargetId(dto.getSendersId());
        return header;
    }

    public String handle(FixMessageDto fixMessageDto, FixMessageMapper mapper) {
        FixMessageDto responseDto = createHeader(fixMessageDto);
        int instrumentIdx = Market.INSTRUMENTS.indexOf(new Instrument().setInstrument(fixMessageDto.getInstrument()));
        if (instrumentIdx == -1) {
            return mapper.toFixString(getReject(responseDto, "instrument not found"));
        }
        int quantity = Market.INSTRUMENTS.get(instrumentIdx).getQuantity() - fixMessageDto.getQuantity();
        if (quantity < 0) {
            return mapper.toFixString(getReject(responseDto, "not enough quantity"));
        }
        Market.INSTRUMENTS.get(instrumentIdx).setQuantity(quantity);
        Market.MONEY += fixMessageDto.getPrice() * quantity;

        responseDto.setOrdStatus(MarketOptions.EXECUTED);
        responseDto.countBodyLength();
        responseDto.countChecksum();
        return mapper.toFixString(responseDto);
    }
}
