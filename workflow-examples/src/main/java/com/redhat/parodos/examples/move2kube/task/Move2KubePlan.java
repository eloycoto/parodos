package com.redhat.parodos.examples.move2kube.task;

import com.redhat.parodos.workflow.task.infrastructure.BaseInfrastructureWorkFlowTask;
import com.redhat.parodos.workflows.work.DefaultWorkReport;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;

public class Move2KubePlan extends BaseInfrastructureWorkFlowTask {

	public Move2KubePlan() {
		super();
	}

	public WorkReport execute(WorkContext workContext) {
		return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);
	}

}
