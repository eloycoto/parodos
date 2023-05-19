package com.redhat.parodos.examples.move2kube.task;

import java.io.File;
import java.util.List;

import com.redhat.parodos.workflow.task.enums.WorkFlowTaskOutput;
import com.redhat.parodos.workflows.work.DefaultWorkReport;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import dev.parodos.move2kube.ApiClient;
import dev.parodos.move2kube.ApiException;
import dev.parodos.move2kube.api.PlanApi;
import dev.parodos.move2kube.api.ProjectInputsApi;
import dev.parodos.move2kube.client.model.GetPlan200Response;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.*;

//
// aasda
// asdasd
@Slf4j
public class Move2KubePlan extends Move2KubeBase {

	public Move2KubePlan(String server) {
		super();
		this.setClient(server);
	}

	public WorkReport execute(WorkContext workContext) {
		PlanApi planApi = new PlanApi(client);
		String workspaceID = (String) workContext.get(getWorkspaceContextKey());
		String projectID = (String) workContext.get(getProjectContextKey());
		log.error("ProjectID:: {}", projectID);
		addSourceCode(workspaceID, projectID, (String) workContext.get("gitArchivePath"));
		try {
			planApi.startPlanning(workspaceID, projectID);

			for (int i = 1; i <= 10; ++i) {
				GetPlan200Response plan = planApi.getPlan(workspaceID, projectID);
				if (plan == null) {
					try {
						sleep(i * 1000);
					}
					catch (Exception e) {
						continue;
					}
					continue;
				}
				log.error("Plan is here in the {} attempt", i);
				break;
			}
		}
		catch (ApiException e) {
			log.error("Cannot execute plan, error: {}", e.getMessage());
			return new DefaultWorkReport(WorkStatus.FAILED, workContext);
		}
		return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);
	}

	private void addSourceCode(String workspaceID, String projectID, String ZIPPath) {
		log.error("*****************************************************************************");
		log.error("*****************************************************************************");
		log.error("Add source code on path {}", ZIPPath);
		log.error("*****************************************************************************");
		log.error("*****************************************************************************");
		File file = new File(ZIPPath);
		ApiClient clientFormData = client;
		clientFormData.addDefaultHeader("Content-Type", "multipart/form-data");
		ProjectInputsApi projectInputsApi = new ProjectInputsApi(clientFormData);
		try {
			projectInputsApi.createProjectInput(workspaceID, projectID, "sources", "foo", "bar", file);
		}
		catch (Exception e) {
			log.error("cannot append source code! {}", e.getMessage());
		}
	}

	@Override
	public List<WorkFlowTaskOutput> getWorkFlowTaskOutputs() {
		return List.of(WorkFlowTaskOutput.HTTP2XX, WorkFlowTaskOutput.OTHER);
	}

}
