package com.redhat.parodos.examples.move2kube.task;

import java.util.UUID;

import com.redhat.parodos.examples.utils.RestUtils;
import com.redhat.parodos.workflow.utils.WorkContextUtils;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import dev.parodos.move2kube.api.PlanApi;
import dev.parodos.move2kube.api.ProjectOutputsApi;
import dev.parodos.move2kube.client.model.GetPlan200Response;
import dev.parodos.move2kube.client.model.StartTransformation202Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Move2KubeTransformTest {

	Move2KubeTransform task;

	private static String move2KubeWorkspaceIDCtxKey = "move2KubeWorkspaceID";

	private static String move2KubeProjectIDCtxKey = "move2KubeProjectID";

	private PlanApi planApi;

	private ProjectOutputsApi projectOutputsApi;

	@Before
	public void setup() throws Exception {
		planApi = mock(PlanApi.class);
		projectOutputsApi = mock(ProjectOutputsApi.class);
		task = new Move2KubeTransform("http://localhost:8080", planApi, projectOutputsApi);
	}

	@Test
	public void testParameters() {
		assertThat(this.task.getWorkFlowTaskParameters().size()).isEqualTo(0);
	}

	@Test
	public void testValidExecution() {

		// given
		WorkContext context = getSampleWorkContext();
		GetPlan200Response response = new GetPlan200Response();
		StartTransformation202Response transformResponse = new StartTransformation202Response();
		transformResponse.setId("foo");

		assertDoesNotThrow(() -> {
			when(planApi.getPlan(any(), any())).thenReturn(new GetPlan200Response());
			when(projectOutputsApi.startTransformation(any(), any(), any())).thenReturn(transformResponse);
		});

		try (MockedStatic<RestUtils> mockedStatic = mockStatic(RestUtils.class)) {
			ResponseEntity<String> responseo = ResponseEntity.ok("ok");
			responseo.getStatusCode();

			mockedStatic.when((MockedStatic.Verification) RestUtils.executePost(any(), (HttpEntity<?>) any()))
					.thenReturn(ResponseEntity.ok("ok"));

			// when
			WorkReport report = this.task.execute(context);

			// then
			assertThat(report.getError()).isNull();
			assertThat(report.getStatus()).isEqualTo(WorkStatus.COMPLETED);
		}

		assertDoesNotThrow(() -> {
			verify(projectOutputsApi, times(1)).startTransformation(eq(move2KubeWorkspaceIDCtxKey),
					eq(move2KubeProjectIDCtxKey), any());
		});

	}

	public WorkContext getSampleWorkContext() {
		WorkContext workContext = new WorkContext();
		workContext.put(move2KubeProjectIDCtxKey, move2KubeProjectIDCtxKey);
		workContext.put(move2KubeWorkspaceIDCtxKey, move2KubeWorkspaceIDCtxKey);
		WorkContextUtils.setUserId(workContext, UUID.randomUUID());
		return workContext;
	}

}