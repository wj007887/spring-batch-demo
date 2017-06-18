package com.mycompany.springbatch.demo;

import org.springframework.batch.integration.partition.StepExecutionRequest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.core.GenericMessagingTemplate;

public class MasterTemplate extends GenericMessagingTemplate{
	
	
	@Override
	public void send(Message<?> message) {
		super.send(message);
	}
	
	@Override
	public Message<?> receive(MessageChannel destination) {
		// TODO Auto-generated method stub
		return super.receive(destination);
	}
	
	private Message<StepExecutionRequest> createMessage(int sequenceNumber, int sequenceSize,
			StepExecutionRequest stepExecutionRequest, PollableChannel replyChannel) {
		return MessageBuilder.withPayload(stepExecutionRequest).setSequenceNumber(sequenceNumber)
				.setSequenceSize(sequenceSize)
				.setCorrelationId(stepExecutionRequest.getJobExecutionId() + ":" + stepExecutionRequest.getStepName())
				.setReplyChannel(replyChannel)
				.build();
	}

}
