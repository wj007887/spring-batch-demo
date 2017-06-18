package com.mycompany.springbatch.demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class MasterPartitioner implements Partitioner {
	
	private static Logger logger = Logger.getLogger(MasterPartitioner.class);

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		logger.info("begin partition...");
		logger.debug("gridSize:"+gridSize);
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		for (int i = 0; i < gridSize; i++) {
			ExecutionContext execution = new ExecutionContext();
			execution.putInt("node", i);
			result.put("node"+i, execution);
		}
		logger.info("end partition...");
		return result;
	}

}
