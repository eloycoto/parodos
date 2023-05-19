package com.redhat.parodos.examples.move2kube.task;

import java.net.URL;
import java.util.List;

import com.redhat.parodos.workflow.task.enums.WorkFlowTaskOutput;
import com.redhat.parodos.workflow.task.infrastructure.BaseInfrastructureWorkFlowTask;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import dev.parodos.move2kube.ApiClient;
import lombok.Getter;

public class Move2KubeBase extends BaseInfrastructureWorkFlowTask {

	@Getter
	protected static String workspaceContextKey = "move2KubeWorkspaceID";

	@Getter
	protected static String projectContextKey = "move2KubeProjectID";

	@Getter
	protected static String transformContextKey = "move2KubeTransformID";

	protected ApiClient client = null;

	protected void setClient(String server) {
		this.client = new ApiClient();
		this.client.setBasePath(server);
	}

	@Override
	public List<WorkFlowTaskOutput> getWorkFlowTaskOutputs() {
		return List.of();
	}

	@Override
	public WorkReport execute(WorkContext workContext) {
		return null;
	}

	public URL getBasePath() {
		try {
			URL url = new URL(client.getBasePath());
			return url;
		}
		catch (Exception e) {
			return null;
		}
	}

}
