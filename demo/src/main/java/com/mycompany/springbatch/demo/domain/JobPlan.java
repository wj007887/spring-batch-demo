package com.mycompany.springbatch.demo.domain;

public class JobPlan {

	public int id;
	public int jobPlanId;
	public String jobPlanDesc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobPlanId() {
		return jobPlanId;
	}

	public void setJobPlanId(int jobPlanId) {
		this.jobPlanId = jobPlanId;
	}

	public String getJobPlanDesc() {
		return jobPlanDesc;
	}

	public void setJobPlanDesc(String jobPlanDesc) {
		this.jobPlanDesc = jobPlanDesc;
	}

}
