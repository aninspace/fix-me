package edu.school42.fixme.router.process;

import edu.school42.fixme.common.converter.FixMessageMapper;
import edu.school42.fixme.common.builder.MessageBuilder;
import edu.school42.fixme.common.dto.FixMessageDto;
import edu.school42.fixme.router.Router;
import edu.school42.fixme.router.source.Source;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class MessageHandler implements Runnable {

	private static final int ROUTER_ID = 0;

	private final Source source;
	private final AtomicReference<Socket> socket;
	private final FixMessageMapper mapper;
	private final MessageBuilder messageBuilder;

	@Override
	public void run() {
		try (
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.get().getInputStream()));
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.get().getOutputStream()), true)
		) {
			String message = mapper.toFixString(messageBuilder.confirmationOfIdMessage(source.getId(), ROUTER_ID));
			pw.println(message);
			log.info("sent :: {}", message);

			while (true) {
				String line = br.readLine();
				if (Objects.isNull(line)) {
					break ;
				}
				log.info("received :: {}", line);
				FixMessageDto dto = mapper.toDto(line);
				if (validateByChecksum(dto)) {
					forwardMessage(dto, pw);
				} else {
					String errorMessage = mapper.toFixString(messageBuilder.validationErrorMessage(source.getId(), dto.getOrderId(), ROUTER_ID));
					pw.println(errorMessage);
					log.info("sent:: {}", errorMessage);
				}
			}
			socket.get().close();
			Router.ROUTING_TABLE.remove(source);
			log.info("removed {} with id :: {}", source.getType(), source.getId());
		} catch (Exception e) {
			try {
				socket.get().close();
			} catch (IOException ignored) {
			}
			throw new RuntimeException(e.getMessage());
		}
	}

	private boolean validateByChecksum(FixMessageDto dto) {
		String currChecksum = dto.getChecksum();
		dto.countChecksum();
		String newChecksum = dto.getChecksum();
		return currChecksum.equals(newChecksum);
	}

	private void forwardMessage(FixMessageDto dto, PrintWriter pw) {
		try {
			AtomicReference<Socket> targetSocket = switch (dto.getType()) {
				case PLACE_ORDER -> {
					Router.ROUTING_TABLE.replaceBrokerSourceId(source, dto.getSendersId());
					yield Router.ROUTING_TABLE.findSocket(dto.getTargetId(), edu.school42.fixme.common.model.Source.MARKET);
				}
				case HANDLE_ORDER -> Router.ROUTING_TABLE.findSocket(dto.getTargetId(), edu.school42.fixme.common.model.Source.BROKER);
				default -> throw new RuntimeException("unknown message type");
			};
			if (Objects.isNull(targetSocket)) {
				String message = mapper.toFixString(messageBuilder.validationErrorMessage(source.getId(), dto.getOrderId(), ROUTER_ID));
				pw.println(message);
				log.info("sent :: {}", message);
				return;
			}
			PrintWriter targetPw = new PrintWriter(targetSocket.get().getOutputStream(), true);
			String message = mapper.toFixString(dto);
			targetPw.println(message);
			log.info("sent :: {}", message);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
