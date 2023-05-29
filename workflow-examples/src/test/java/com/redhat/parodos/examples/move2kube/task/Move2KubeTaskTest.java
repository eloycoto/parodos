package com.redhat.parodos.examples.move2kube.task;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.redhat.parodos.workflow.utils.WorkContextUtils;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import dev.parodos.move2kube.ApiException;
import dev.parodos.move2kube.api.ProjectInputsApi;
import dev.parodos.move2kube.api.ProjectsApi;
import dev.parodos.move2kube.api.WorkspacesApi;
import dev.parodos.move2kube.client.model.CreateProject201Response;
import dev.parodos.move2kube.client.model.ProjectInputsValue;
import dev.parodos.move2kube.client.model.Workspace;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public class Move2KubeTaskTest {

	Move2KubeTask task;

	private WorkspacesApi workspacesApi;

	private ProjectsApi projectsApi;

	private ProjectInputsApi projectInputsApi;

	private static String move2KubeWorkspaceIDCtxKey = "move2KubeWorkspaceID";

	private static String move2KubeProjectIDCtxKey = "move2KubeProjectID";

	@Before
	public void BeforeEach() {
		workspacesApi = mock(WorkspacesApi.class);
		projectsApi = mock(ProjectsApi.class);
		projectInputsApi = mock(ProjectInputsApi.class);

		task = new Move2KubeTask("http://localhost", workspacesApi, projectsApi, projectInputsApi);
		log.error("Move2KubeTask BeforeEach");
	}

	@After
	public void AfterEach() {
		log.error("Move2KubeTask AfterEach");
	}

	@Test
	public void testValidExecution() {
		// given
		WorkContext workContext = getSampleWorkContext();
		Workspace workspace = getSampleWorkspace("test");

		Map<String, ProjectInputsValue> inputs = new HashMap<String, ProjectInputsValue>();
		inputs.put("test1", getSampleProjectInput("test1"));
		inputs.put("test2", getSampleProjectInput("test2"));
		workspace.setInputs(inputs);

		assertDoesNotThrow(() -> {
			when(workspacesApi.getWorkspaces()).thenReturn(List.of(workspace));
			when(projectsApi.createProject(any(), any())).thenReturn(getSampleProject("test"));
		});

		// when
		WorkReport report = task.execute(workContext);

		// then
		assertThat(report.getError()).isNull();
		assertThat(report.getStatus()).isEqualTo(WorkStatus.COMPLETED);
		assertThat(report.getWorkContext().get(move2KubeWorkspaceIDCtxKey)).isNotNull();
		assertThat(report.getWorkContext().get(move2KubeProjectIDCtxKey)).isNotNull();

		assertDoesNotThrow(() -> {
			verify(workspacesApi, times(1)).getWorkspaces();
			verify(projectsApi, times(1)).createProject(any(), any());
			verify(projectInputsApi, times(2)).createProjectInput(any(), any(), any(), any(), any(), any());
		});
	}

	@Test
	public void testWithoutValidWorkspace() {
		// given
		WorkContext workContext = getSampleWorkContext();
		assertDoesNotThrow(() -> {
			when(workspacesApi.getWorkspaces()).thenReturn(Collections.emptyList());
			when(projectsApi.createProject(any(), any())).thenReturn(getSampleProject("test"));
		});

		// when
		WorkReport report = task.execute(workContext);

		// then
		assertThat(report.getError()).isNotNull();
		assertThat(report.getStatus()).isEqualTo(WorkStatus.FAILED);
		assertThat(report.getWorkContext().get(move2KubeWorkspaceIDCtxKey)).isNull();
		assertThat(report.getWorkContext().get(move2KubeProjectIDCtxKey)).isNull();

		assertDoesNotThrow(() -> {
			verify(workspacesApi, times(1)).getWorkspaces();
			verify(projectsApi, times(0)).createProject(any(), any());
		});
	}

	@Test
	public void testWithIssuesCreatingProject() {
		// given
		WorkContext workContext = getSampleWorkContext();
		assertDoesNotThrow(() -> {
			when(workspacesApi.getWorkspaces()).thenReturn(List.of(getSampleWorkspace("test")));
			when(projectsApi.createProject(any(), any())).thenThrow(ApiException.class);
		});

		// when
		WorkReport report = task.execute(workContext);

		// then
		assertThat(report.getError()).isNotNull();
		assertThat(report.getStatus()).isEqualTo(WorkStatus.FAILED);
		assertThat(report.getWorkContext().get(move2KubeWorkspaceIDCtxKey)).isNotNull();
		assertThat(report.getWorkContext().get(move2KubeProjectIDCtxKey)).isNull();

		assertDoesNotThrow(() -> {
			verify(workspacesApi, times(1)).getWorkspaces();
			verify(projectsApi, times(1)).createProject(any(), any());
		});
	}

	public Workspace getSampleWorkspace(String workspace) {
		var wrk = new Workspace();
		wrk.setId(UUID.randomUUID().toString());
		wrk.setName(workspace);
		return wrk;
	}

	public CreateProject201Response getSampleProject(String name) {
		CreateProject201Response project = new CreateProject201Response();
		project.setId(UUID.randomUUID().toString());
		return project;
	}

	public WorkContext getSampleWorkContext() {
		WorkContext workContext = new WorkContext();
		WorkContextUtils.setMainExecutionId(workContext, UUID.randomUUID());
		return workContext;
	}

	public ProjectInputsValue getSampleProjectInput(String name) {
		ProjectInputsValue projectInput = new ProjectInputsValue();
		projectInput.setName(name);
		projectInput.setDescription(name);
		projectInput.setId(UUID.randomUUID().toString());
		return projectInput;
	}

}
