package com.redhat.parodos.examples.move2kube.task;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import com.redhat.parodos.examples.ocponboarding.task.dto.notification.NotificationRequest;
import com.redhat.parodos.examples.utils.RestUtils;
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

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

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
		if (!sendNotification(userID, workspaceID, projectID)) {
			log.error("Cannot notify user about the transformation status");
			return new DefaultWorkReport(WorkStatus.FAILED, workContext,
					new RuntimeException("Cannot notify user about the transformation status"));
		}
		return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);
	}

	private boolean sendNotification(String userID, String workspaceID, String projectID) {
		URI baseURI = URI.create(this.client.getBasePath());
		URI projectURI = baseURI.resolve(String.format("workspaces/%s/projects/%s", workspaceID, projectID));
		String url = projectURI.toString();
		String message = String
				.format("You need to complete some information for your transformation in the following url %s", url);

		// @TODO userID is the ID, but we need the username, so hardcode it here for now.
		NotificationRequest request = NotificationRequest.builder().usernames(List.of("test"))
				.subject("Complete the transformation steps").body(message).build();

		HttpEntity<NotificationRequest> notificationRequestHttpEntity = RestUtils.getRequestWithHeaders(request, "test",
				"test");

		ResponseEntity<String> response = RestUtils.executePost("http://localhost:8082/api/v1/messages",
				notificationRequestHttpEntity);
		return response.getStatusCode().is2xxSuccessful();
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
