package com.redhat.parodos.examples.move2kube;

import java.util.concurrent.Executors;

import com.redhat.parodos.examples.move2kube.task.Move2KubeTask;
import com.redhat.parodos.tasks.git.GitArchiveTask;
import com.redhat.parodos.tasks.git.GitCloneTask;
import com.redhat.parodos.workflow.annotation.Infrastructure;
import com.redhat.parodos.workflow.consts.WorkFlowConstants;
import com.redhat.parodos.workflows.workflow.ParallelFlow;
import com.redhat.parodos.workflows.workflow.SequentialFlow;
import com.redhat.parodos.workflows.workflow.WorkFlow;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class move2kubeWorkFlowConfiguration {

	@Bean
	GitCloneTask gitCloneTask() {
		return new GitCloneTask();
	}

	@Bean
	GitArchiveTask gitArchiveTask() {
		return new GitArchiveTask();
	}

	@Bean
	Move2KubeTask move2KubeTask() {
		Move2KubeTask move2KubeTask = new Move2KubeTask("http://localhost:8081/api/v1");
		return move2KubeTask;
	}

	@Bean(name = "move2KubeProject")
	@Infrastructure
	WorkFlow move2KubeProject(@Qualifier("move2KubeTask") Move2KubeTask move2KubeTask) {
		return SequentialFlow.Builder.aNewSequentialFlow().named("move2KubeProject").execute(move2KubeTask).build();
	}

	@Bean(name = "getSources")
	@Infrastructure
	WorkFlow getSources(@Qualifier("gitCloneTask") GitCloneTask gitCloneTask,
			@Qualifier("gitArchiveTask") GitArchiveTask gitArchiveTask) {
		return SequentialFlow.Builder.aNewSequentialFlow().named("getSources").execute(gitCloneTask)
				.then(gitArchiveTask).build();
	}

	@Bean(name = "move2KubeWorkFlow" + WorkFlowConstants.INFRASTRUCTURE_WORKFLOW)
	@Infrastructure
	WorkFlow move2kubeWorkflow(@Qualifier("getSources") WorkFlow getSources,
			@Qualifier("move2KubeProject") WorkFlow move2KubeProject) {
		return ParallelFlow.Builder.aNewParallelFlow()
				.named("move2KubeWorkFlow" + WorkFlowConstants.INFRASTRUCTURE_WORKFLOW)
				.execute(move2KubeProject, getSources).with(Executors.newFixedThreadPool(2)).build();
	}

}
