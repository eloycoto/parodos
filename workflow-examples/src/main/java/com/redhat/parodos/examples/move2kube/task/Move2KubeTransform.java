package com.redhat.parodos.examples.move2kube.task;

import java.io.IOException;

import com.redhat.parodos.workflow.utils.WorkContextUtils;
import com.redhat.parodos.workflows.work.DefaultWorkReport;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import dev.parodos.move2kube.ApiException;
import dev.parodos.move2kube.api.PlanApi;
import dev.parodos.move2kube.api.ProjectOutputsApi;
import dev.parodos.move2kube.client.model.GetPlan200Response;
import dev.parodos.move2kube.client.model.StartTransformation202Response;
import dev.parodos.move2kube.client.model.StartTransformationRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Move2KubeTransform extends Move2KubeBase {

	private String plan;

	public Move2KubeTransform(String server) {
		super();
		this.setClient(server);
	}

	public WorkReport execute(WorkContext workContext) {
		String workspaceID = (String) workContext.get(getWorkspaceContextKey());
		String projectID = (String) workContext.get(getProjectContextKey());
		log.error("---> Executing transform here");
		if (!isPlanCreated(workspaceID, projectID)) {
			String errorMessage = "Plan for workspace '" + workspaceID + "' and project '" + projectID
					+ "' is not created";
			log.error(errorMessage);
			return new DefaultWorkReport(WorkStatus.FAILED, workContext, new IllegalArgumentException(errorMessage));
		}

		try {
			String transformID = transform(workspaceID, projectID);
			workContext.put(getTransformContextKey(), transformID);
		}
		catch (IllegalArgumentException | IOException e) {
			log.error("transform failed Illegal: {}", e.getMessage());
			return new DefaultWorkReport(WorkStatus.FAILED, workContext, e);
		}
		catch (ApiException e) {
			log.error("transform failed APIExecptrion: {}", e.getMessage());
			return new DefaultWorkReport(WorkStatus.FAILED, workContext, e);
		}

		String userID = String.valueOf(WorkContextUtils.getUserId(workContext));
		sendNotification(userID, workspaceID, projectID);
		return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);
	}

	private void sendNotification(String userID, String workspaceID, String projectID) {
		final StringBuilder url = new StringBuilder();
		url.append(getBasePath());
		url.append("workspaces/");
		url.append(workspaceID);
		url.append("/projects/");
		url.append(projectID);

		log.error("URL --->", url.toString());
	}

	private String transform(String workspaceID, String projectID)
			throws IllegalArgumentException, ApiException, IOException {
		ProjectOutputsApi outputs = new ProjectOutputsApi(client);
		StartTransformation202Response response = outputs.startTransformation(workspaceID, projectID,
				StartTransformationRequest.fromJson(plan));
		if (response == null) {
			throw new IllegalArgumentException("Cannot start transformation");
		}
		return response.getId();
	}

	private boolean isPlanCreated(String workspaceID, String projectID) {
		PlanApi planApi = new PlanApi(client);
		try {
			GetPlan200Response response = planApi.getPlan(workspaceID, projectID);
			if (response == null) {
				log.error("Plan is null");
				return false;

			}
			this.plan = response.toJson();
		}
		catch (Exception e) {
			log.error("Plan execption here {}", e.getMessage());
			return false;
		}
		return true;
	}

}
