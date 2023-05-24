package com.redhat.parodos.examples.move2kube.task;

import com.redhat.parodos.workflow.utils.WorkContextUtils;
import com.redhat.parodos.workflows.work.WorkStatus;
import dev.parodos.move2kube.client.model.CreateProject201Response;
import dev.parodos.move2kube.client.model.Project;
import dev.parodos.move2kube.client.model.Workspace;

import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import dev.parodos.move2kube.api.ProjectsApi;
import dev.parodos.move2kube.api.WorkspacesApi;
import dev.parodos.move2kube.client.model.Workspace;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.parodos.move2kube.ApiClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Move2KubeTaskTest  {

    Move2KubeTask task;
    private WorkspacesApi workspacesApi;

    private ProjectsApi projectsApi;

    @Before
    public void BeforeEach() {
        workspacesApi = Mockito.mock(WorkspacesApi.class);
        projectsApi = Mockito.mock(ProjectsApi.class);

        task = new Move2KubeTask("http://localhost", workspacesApi, projectsApi);
        log.error("Move2KubeTask BeforeEach");
    }

    @After
    public void AfterEach() {
        log.error("Move2KubeTask AfterEach");
    }
    @Test
    public void testExecution() {
        // given
        WorkContext workContext = getSampleWorkContext();
        assertDoesNotThrow(() -> {
                    Mockito.when(workspacesApi.getWorkspaces()).thenReturn(List.of(getSampleWorkspace("test")));
                    Mockito.when(projectsApi.createProject(Mockito.any(), Mockito.any())).thenReturn(getSampleProject("test"));
        });

        // when
        WorkReport report = task.execute(workContext);

        // then
        assertNull(report.getError());
        assertEquals(report.getStatus(), WorkStatus.COMPLETED);
        assertNotNull(report.getWorkContext().get("move2KubeWorkspaceID"));
        assertNotNull(report.getWorkContext().get("move2KubeProjectID"));

        assertDoesNotThrow(() -> {
            Mockito.verify(workspacesApi, Mockito.times(1)).getWorkspaces();
            Mockito.verify(projectsApi, Mockito.times(1)).createProject(Mockito.any(), Mockito.any());
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
//    public Project getSampleProject(String name) {
//        Project project = new Project();
//        project.setId(UUID.randomUUID().toString());
//        project.setName(name);
//        return project;
//    }

    public WorkContext getSampleWorkContext() {
        WorkContext workContext = new WorkContext();
        WorkContextUtils.setMainExecutionId(workContext, UUID.randomUUID());
        return workContext;
    }

}