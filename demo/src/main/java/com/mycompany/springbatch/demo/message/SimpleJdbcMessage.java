package com.mycompany.springbatch.demo.message;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.integration.partition.StepExecutionRequest;

import com.mycompany.springbatch.demo.Constants;

public class SimpleJdbcMessage implements JdbcMessage {
	
	private long receiveTimeout;
	
	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	@Override
	public void send(Object t) {
		// TODO 写计划任务表
		StepExecutionRequest request = (StepExecutionRequest) t;
	}

	@Override
	public Collection<StepExecution> receive() {
		// TODO 查询执行表记录得到远程分片任务执行结果信息
		Collection<StepExecution> stepColletion = new ArrayList<StepExecution>();
		for (int i = 0; i < 8; i++) {
			StepExecution execution = new StepExecution(Constants.STEP_NAME, null);
			stepColletion.add(execution);
		}
		return stepColletion;
	}

}
