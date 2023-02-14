/*
 * Copyright (c) 2022 Red Hat Developer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.parodos.workflow.registry;

import com.redhat.parodos.workflow.definition.entity.WorkFlowTaskDefinitionEntity;
import com.redhat.parodos.workflows.definition.WorkFlowCheckerDefinition;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.Before;
import com.redhat.parodos.workflows.common.enums.WorkFlowType;
import com.redhat.parodos.workflows.definition.WorkFlowDefinition;
import com.redhat.parodos.workflows.definition.task.WorkFlowTaskDefinition;
import com.redhat.parodos.workflows.workflow.WorkFlow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.parodos.workflow.definition.repository.WorkFlowDefinitionRepository;
import com.redhat.parodos.workflow.definition.repository.WorkFlowTaskDefinitionRepository;
import com.redhat.parodos.workflow.definition.entity.WorkFlowDefinitionEntity;

import static org.junit.Assert.*;

public class BeanWorkFlowRegistryImplTest {
    private  WorkFlowDefinitionRepository wfDefinitionRepo;
    private WorkFlowTaskDefinitionRepository wfTaskDefinitionRepo;
    private List<WorkFlowDefinition> wfDefinitions;
    private List<WorkFlowTaskDefinition> wfTaskDefinitions;
    private List<WorkFlow> wfExecutions;
    void setMockRepositories() {
        this.wfDefinitionRepo = Mockito.mock(WorkFlowDefinitionRepository.class);
        this.wfTaskDefinitionRepo = Mockito.mock(WorkFlowTaskDefinitionRepository.class);
    }

    void setUpDefinitions() {
        this.wfDefinitions = new ArrayList<WorkFlowDefinition>();
        this.wfTaskDefinitions = new ArrayList<WorkFlowTaskDefinition>();
        this.wfExecutions = new ArrayList<WorkFlow>();
    }

    @Before
    public void initEach() {
        this.setUpDefinitions();
        this.setMockRepositories();
    }
    @Test
    public void NoWorkFlowsAdded() {
        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(
                this.wfDefinitions,
                this.wfTaskDefinitions,
                this.wfExecutions,
                this.wfDefinitionRepo,
                this.wfTaskDefinitionRepo,
                new ObjectMapper());

        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        Mockito.verify(this.wfTaskDefinitionRepo, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(this.wfTaskDefinitionRepo, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void SimpleWorkloadAdded() {

        // given
        WorkFlowDefinition wfDefinition = getSampleWFDefinition("test");
        this.wfDefinitions.add(wfDefinition);
        WorkFlowDefinitionEntity wfEntity = getWFDefEntityFrom( wfDefinition);
        Mockito.when(this.wfDefinitionRepo.save(Mockito.any())).thenReturn(wfEntity);

        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(
                this.wfDefinitions,
                this.wfTaskDefinitions,
                this.wfExecutions,
                this.wfDefinitionRepo,
                this.wfTaskDefinitionRepo,
                new ObjectMapper());

        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        Mockito.verify(this.wfDefinitionRepo, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(this.wfTaskDefinitionRepo, Mockito.times(0)).save(Mockito.any());

        assertNotNull(beanWorkFlowRegistry.getWorkFlowDefinitionById(wfEntity.getId()));
        assertNull(beanWorkFlowRegistry.getWorkFlowExecutionByName("test"));
    }


    @Test
    public void ComplicatedWorkloadAdded() {
        // given
        WorkFlowDefinition wfDefinition = getSampleWFDefinition("test");
        wfDefinition.setTasks(Arrays.asList(
                WorkFlowTaskDefinition.builder().name("test").build()));
        WorkFlowDefinitionEntity wfEntity = getWFDefEntityFrom( wfDefinition);
        this.wfDefinitions.add(wfDefinition);


        // Mockito.when(workFlowDefinitionRepository.save(Mockito.any())).thenReturn(oo);

        WorkFlowTaskDefinitionEntity workFlowTask = WorkFlowTaskDefinitionEntity
                .builder()
                .name("test")
                .description("test")
                .build();
        workFlowTask.setId(UUID.randomUUID());

        // Mocks
        Mockito.when(this.wfDefinitionRepo.save(Mockito.any())).thenReturn(wfEntity);
        Mockito.when(this.wfTaskDefinitionRepo.save(Mockito.any())).thenReturn(workFlowTask);
        Mockito.when(this.wfTaskDefinitionRepo.findByWorkFlowDefinitionEntity(Mockito.any())).thenReturn(
                Arrays.asList(workFlowTask));

        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(
                this.wfDefinitions,
                this.wfTaskDefinitions,
                this.wfExecutions,
                this.wfDefinitionRepo,
                this.wfTaskDefinitionRepo,
                new ObjectMapper());
        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        WorkFlowDefinition workFlowDefinition = beanWorkFlowRegistry.getWorkFlowDefinitionById(wfEntity.getId());
        assertEquals(workFlowDefinition.getName(), "test");
        assertEquals(workFlowDefinition.getTasks().size(), 1);
    }


    WorkFlowDefinition getSampleWFDefinition(String name) {

        return WorkFlowDefinition.builder()
                .name(name)
                .description("Failed description")
                .author("alice")
                .type(WorkFlowType.ASSESSMENT)
                .tasks(new ArrayList<WorkFlowTaskDefinition>())
                .build();
    }
        WorkFlowDefinitionEntity getWFDefEntityFrom(WorkFlowDefinition wk) {
            WorkFlowDefinitionEntity wkDefinitionEntity =  WorkFlowDefinitionEntity.builder()
                    .name(wk.getName())
                    .description(wk.getDescription())
                    .type(wk.getType().name())
                    .author(wk.getAuthor())
                    .createDate(wk.getCreatedDate())
                    .modifyDate(wk.getCreatedDate())
                    .build();
            wkDefinitionEntity.setId(UUID.randomUUID());
            return wkDefinitionEntity;
        }

    WorkFlowTaskDefinition getSampleWFTaskDefinition(String name,WorkFlowDefinition wfDefinition) {
        return WorkFlowTaskDefinition.builder()
                .name(name)
                .description("test")
                .workFlowDefinition(wfDefinition)
                .build();
    }

    WorkFlowTaskDefinitionEntity getWFTaskDefEntityFrom(WorkFlowTaskDefinition wfTask) {
        WorkFlowTaskDefinitionEntity wfTaskEntity = WorkFlowTaskDefinitionEntity
                .builder()
                .name(wfTask.getName())
                .description(wfTask.getDescription())
                .build();
        wfTaskEntity.setId(UUID.randomUUID());
        return wfTaskEntity;
    }

    @Test
    public void TestWorkflowTaskDefinitions() {
        // given
        WorkFlowDefinition wfDefinition = getSampleWFDefinition("test");
        WorkFlowDefinitionEntity wfEntity = getWFDefEntityFrom( wfDefinition);
        Mockito.when(this.wfDefinitionRepo.findFirstByName(Mockito.any())).thenReturn(wfEntity);


        WorkFlowTaskDefinition wfTask = getSampleWFTaskDefinition("test-task",  wfDefinition);
        wfTask.setWorkFlowCheckerDefinition(WorkFlowCheckerDefinition.builder().nextWorkFlowDefinition( wfDefinition).build());
        this.wfTaskDefinitions.add(wfTask);

        WorkFlowTaskDefinitionEntity wfTaskEntity = getWFTaskDefEntityFrom(wfTask);
        Mockito.when(this.wfTaskDefinitionRepo.findFirstByName(Mockito.any())).thenReturn(wfTaskEntity);

        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(
                this.wfDefinitions,
                this.wfTaskDefinitions,
                this.wfExecutions,
                this.wfDefinitionRepo,
                this.wfTaskDefinitionRepo,
                new ObjectMapper());
        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        Mockito.verify(this.wfTaskDefinitionRepo, Mockito.times(1)).save(Mockito.any());
    }
}
