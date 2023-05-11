package com.redhat.parodos.examples.move2kube.task;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// 
// @Slf4j
// public class Move2KubeTask extends BaseInfrastructureWorkFlowTask {
// 
// // private ApiClient apiClient;
// //
// // private WorkspacesApi workspace;
// 
// // public Move2KubeTask() {
// // super();
// // // this.apiClient = new ApiClient();
// // // this.apiClient.setBasePath("http://localhost:8080/api/v1");
// // // this.workspace = new WorkspacesApi(this.apiClient);
// // }
// 
// // @Override
// // public List<WorkParameter> getWorkFlowTaskParameters() {
// // return List
// // .of(WorkParameter.builder().key("Workspace").description("The workspace to be used
// // in transformation")
// // .optional(false).type(WorkParameterType.TEXT).build());
// // }
// 
// @Override
// public List<WorkFlowTaskOutput> getWorkFlowTaskOutputs() {
// return List.of(WorkFlowTaskOutput.HTTP2XX, WorkFlowTaskOutput.OTHER);
// }
// 
// @Override
// public WorkReport execute(WorkContext workContext) {
// return new DefaultWorkReport(WorkStatus.FAILED, workContext);
// }
// 
// }
import com.redhat.parodos.workflow.task.enums.WorkFlowTaskOutput;
import com.redhat.parodos.workflow.task.infrastructure.BaseInfrastructureWorkFlowTask;
import com.redhat.parodos.workflows.work.DefaultWorkReport;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import dev.parodos.move2kube.ApiClient;
import dev.parodos.move2kube.ApiException;
import dev.parodos.move2kube.api.ProjectInputsApi;
import dev.parodos.move2kube.api.ProjectsApi;
import dev.parodos.move2kube.api.WorkspacesApi;
import dev.parodos.move2kube.client.model.CreateProject201Response;
import dev.parodos.move2kube.client.model.Project;
import dev.parodos.move2kube.client.model.ProjectInputsValue;
import dev.parodos.move2kube.client.model.Workspace;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Move2KubeTask extends BaseInfrastructureWorkFlowTask {

	private ApiClient client = null;

	public Move2KubeTask(String server) {
		super();
		this.client = new ApiClient();
		this.client.setBasePath(server);
	}

	/**
	 * Executed by the InfrastructureTask engine as part of the Workflow
	 */
	public WorkReport execute(WorkContext workContext) {
		log.error("**********************************************");
		log.error("**********************************************");
		log.error("**********************************************");
		log.error("Init Move2Kube Project initialization!");
		String workspaceID = null;
		Map<String, ProjectInputsValue> workspaceInputs = null;
		try {
			Optional<Workspace> workspace = setWorkspace();
			if (workspace.isEmpty()) {
				return new DefaultWorkReport(WorkStatus.FAILED, workContext);
			}
			workspaceID = workspace.get().getId();
			workspaceInputs = workspace.get().getInputs();
			workContext.put("move2KubeWorkspaceID", workspaceID);
		}
		catch (ApiException e) {
			log.error("Cannot get the workspace, error: {}", e.getMessage());
		}
		log.error("WorkspaceID -->{}", workspaceID);
		log.error("Context --> {}", workContext);

		log.error("-----------------------------------------------------------");
		log.error("-----------------------------------------------------------");
		log.error("-----------------------------------------------------------");
		setProject(workspaceID, workspaceInputs, UUID.randomUUID().toString());
		log.error("**********************************************");
		log.error("**********************************************");
		log.error("**********************************************");
		return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);
	}

	@Override
	public List<WorkFlowTaskOutput> getWorkFlowTaskOutputs() {
		return List.of(WorkFlowTaskOutput.HTTP2XX, WorkFlowTaskOutput.OTHER);
	}

	private Optional<Workspace> setWorkspace() throws ApiException {
		WorkspacesApi workspace = new WorkspacesApi(this.client);
		return workspace.getWorkspaces().stream().findFirst();
	}

	private void setProject(String workspaceId, Map<String, ProjectInputsValue> inputs, String workflowId) {
		ProjectsApi projectsApi = new ProjectsApi(client);
		Project project = new Project();
		project.setName("WorkFlowID: " + workflowId);
		project.description("Project for workflow: " + workflowId);
		try {
			CreateProject201Response res = projectsApi.createProject(workspaceId, project);
			log.error("Setting the id as :{}", res.getId());
			project.setId(res.getId());
		}

		catch (Exception e) {
			return;
		}
		if (inputs == null) {
			return;
		}

		ApiClient clientFormData = client;
		clientFormData.addDefaultHeader("Content-Type", "multipart/form-data");
		ProjectInputsApi projectInputsApi = new ProjectInputsApi(clientFormData);

		inputs.forEach((k, v) -> {
			try {
				projectInputsApi.createProjectInput(workspaceId, project.getId(), "reference", v.getId(),
						v.getDescription(), null);
			}
			catch (Exception e) {
				log.error("Error creating project input: {}", e.getMessage());
			}
		});
	}

}
