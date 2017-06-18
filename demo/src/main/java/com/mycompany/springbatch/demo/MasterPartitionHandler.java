package com.mycompany.springbatch.demo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.StepExecutionSplitter;
import org.springframework.batch.integration.partition.StepExecutionRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.mycompany.springbatch.demo.message.JdbcMessage;

public class MasterPartitionHandler implements PartitionHandler, InitializingBean {

	private static Log logger = LogFactory.getLog(MasterPartitionHandler.class);

	private int gridSize = 1;

	private JdbcMessage messagingGateway;

	private String stepName;

	private long pollInterval = 10000;

	private JobExplorer jobExplorer;

	private boolean pollRepositoryForResults = false;

	private long timeout = -1;

	private DataSource dataSource;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(stepName, "A step name must be provided for the remote workers.");
		Assert.state(messagingGateway != null, "The MessagingOperations must be set");

		pollRepositoryForResults = !(dataSource == null && jobExplorer == null);

		if (pollRepositoryForResults) {
			logger.debug("MessageChannelPartitionHandler is configured to poll the job repository for slave results");
		}

		if (dataSource != null && jobExplorer == null) {
			JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
			jobExplorerFactoryBean.setDataSource(dataSource);
			jobExplorerFactoryBean.afterPropertiesSet();
			jobExplorer = jobExplorerFactoryBean.getObject();
		}
	}

	/**
	 * When using job repository polling, the time limit to wait.
	 *
	 * @param timeout
	 *            millisconds to wait, defaults to -1 (no timeout).
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * {@link org.springframework.batch.core.explore.JobExplorer} to use to
	 * query the job repository. Either this or a {@link javax.sql.DataSource}
	 * is required when using job repository polling.
	 *
	 * @param jobExplorer
	 *            {@link org.springframework.batch.core.explore.JobExplorer} to
	 *            use for lookups
	 */
	public void setJobExplorer(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}

	/**
	 * {@link javax.sql.DataSource} pointing to the job repository
	 *
	 * @param dataSource
	 *            {@link javax.sql.DataSource} that points to the job
	 *            repository's store
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setMessagingGateway(JdbcMessage messagingGateway) {
		this.messagingGateway = messagingGateway;
	}

	/**
	 * Passed to the {@link StepExecutionSplitter} in the
	 * {@link #handle(StepExecutionSplitter, StepExecution)} method, instructing
	 * it how many {@link StepExecution} instances are required, ideally. The
	 * {@link StepExecutionSplitter} is allowed to ignore the grid size in the
	 * case of a restart, since the input data partitions must be preserved.
	 *
	 * @param gridSize
	 *            the number of step executions that will be created
	 */
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	/**
	 * The name of the {@link Step} that will be used to execute the partitioned
	 * {@link StepExecution}. This is a regular Spring Batch step, with all the
	 * business logic required to complete an execution based on the input
	 * parameters in its {@link StepExecution} context. The name will be
	 * translated into a {@link Step} instance by the remote worker.
	 *
	 * @param stepName
	 *            the name of the {@link Step} instance to execute business
	 *            logic
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	/**
	 * Sends {@link StepExecutionRequest} objects to the request channel of the
	 * {@link MessagingTemplate}, and then receives the result back as a list of
	 * {@link StepExecution} on a reply channel. Use the
	 * {@link #aggregate(List)} method as an aggregator of the individual remote
	 * replies. The receive timeout needs to be set realistically in the
	 * {@link MessagingTemplate} <b>and</b> the aggregator, so that there is a
	 * good chance of all work being done.
	 *
	 * @see PartitionHandler#handle(StepExecutionSplitter, StepExecution)
	 */
	public Collection<StepExecution> handle(StepExecutionSplitter stepExecutionSplitter, final StepExecution masterStepExecution)
			throws Exception {

		final Set<StepExecution> split = stepExecutionSplitter.split(masterStepExecution, gridSize);

		if (CollectionUtils.isEmpty(split)) {
			return null;
		}

		for (StepExecution stepExecution : split) {
			StepExecutionRequest request = new StepExecutionRequest(stepName, stepExecution.getJobExecutionId(), stepExecution.getId());
			if (logger.isDebugEnabled()) {
				logger.debug("Sending request: " + request);
			}
			messagingGateway.send(request);
		}
		
		return (Collection<StepExecution>) messagingGateway.receive();
	}

//	private Map<StepExecutionRequest> createMessage(int sequenceNumber, int sequenceSize, StepExecutionRequest stepExecutionRequest) {
//		Map<String,StepExecutionRequest> stepExecMap = new HashMap<K, V>
//		return MessageBuilder.withPayload(stepExecutionRequest).setSequenceNumber(sequenceNumber).setSequenceSize(sequenceSize)
//				.setCorrelationId(stepExecutionRequest.getJobExecutionId() + ":" + stepExecutionRequest.getStepName())
//				.setReplyChannel(replyChannel).build();
//	}

}
