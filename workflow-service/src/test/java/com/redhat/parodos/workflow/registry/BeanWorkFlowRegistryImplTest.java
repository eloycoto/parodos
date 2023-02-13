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
import org.mockito.Mock;
import org.mockito.Mockito;
// import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

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

    @Test
    public void faketest() {
        assertTrue(true);
        assertEquals(true, true);
    }

    // @Test
    public void NoWorkFlowsAdded() {
        // given
        List<WorkFlowDefinition> workFlowDefinitions = new ArrayList<WorkFlowDefinition>();
        List<WorkFlowTaskDefinition> workFlowTaskDefinitions = new ArrayList<WorkFlowTaskDefinition>();
        List<WorkFlow> workFlowExecutions = new ArrayList<WorkFlow>();
        ObjectMapper objectMapper = new ObjectMapper();

        WorkFlowDefinitionRepository workFlowDefinitionRepository = Mockito.mock(WorkFlowDefinitionRepository.class);
        WorkFlowTaskDefinitionRepository workFlowTaskDefinitionRepository = Mockito
                .mock(WorkFlowTaskDefinitionRepository.class);

        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(workFlowDefinitions,
                workFlowTaskDefinitions, workFlowExecutions, workFlowDefinitionRepository,
                workFlowTaskDefinitionRepository, objectMapper);
        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        Mockito.verify(workFlowDefinitionRepository, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(workFlowTaskDefinitionRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void SimpleWorkloadAdded() {
        // given
        WorkFlowDefinition wk = WorkFlowDefinition.builder()
                .name("test")
                .description("Failed description")
                .author("alice")
                .type(WorkFlowType.ASSESSMENT)
                .tasks(new ArrayList<WorkFlowTaskDefinition>())
                .build();
        List<WorkFlowDefinition> workFlowDefinitions = new ArrayList<WorkFlowDefinition>();
        workFlowDefinitions.add(wk);

        List<WorkFlowTaskDefinition> workFlowTaskDefinitions = new ArrayList<WorkFlowTaskDefinition>();
        List<WorkFlow> workFlowExecutions = new ArrayList<WorkFlow>();
        ObjectMapper objectMapper = new ObjectMapper();

        WorkFlowDefinitionRepository workFlowDefinitionRepository = Mockito.mock(WorkFlowDefinitionRepository.class);
        WorkFlowTaskDefinitionRepository workFlowTaskDefinitionRepository = Mockito
                .mock(WorkFlowTaskDefinitionRepository.class);
        WorkFlowDefinitionEntity oo = WorkFlowDefinitionEntity.builder()
                .name(wk.getName())
                .description(wk.getDescription())
                .type(wk.getType().name())
                .author(wk.getAuthor())
                .createDate(wk.getCreatedDate())
                .modifyDate(wk.getCreatedDate())
                .build();

        oo.setId(UUID.randomUUID());
        Mockito.when(workFlowDefinitionRepository.save(Mockito.any())).thenReturn(oo);

        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(workFlowDefinitions,
                workFlowTaskDefinitions, workFlowExecutions, workFlowDefinitionRepository,
                workFlowTaskDefinitionRepository, objectMapper);
        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        Mockito.verify(workFlowDefinitionRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(workFlowTaskDefinitionRepository, Mockito.times(0)).save(Mockito.any());

        assertNotNull(beanWorkFlowRegistry.getWorkFlowDefinitionById(oo.getId()));
        assertNull(beanWorkFlowRegistry.getWorkFlowExecutionByName("test")); // @TODO to check this thing here
    }


    @Test
    public void ComplicatedWorkloadAdded() {
        // given
        List<WorkFlowTaskDefinition> taskList  = new ArrayList<WorkFlowTaskDefinition>();
        WorkFlowTaskDefinition task1 = WorkFlowTaskDefinition.builder().name("test").build();

        taskList.add(task1);
        WorkFlowDefinition wk = WorkFlowDefinition.builder()
                .name("test")
                .description("Failed description")
                .author("alice")
                .type(WorkFlowType.ASSESSMENT)
                .tasks(taskList)
                .build();
        List<WorkFlowDefinition> workFlowDefinitions = new ArrayList<WorkFlowDefinition>();
        workFlowDefinitions.add(wk);

        List<WorkFlowTaskDefinition> workFlowTaskDefinitions = new ArrayList<WorkFlowTaskDefinition>();
        List<WorkFlow> workFlowExecutions = new ArrayList<WorkFlow>();
        ObjectMapper objectMapper = new ObjectMapper();

        WorkFlowDefinitionRepository workFlowDefinitionRepository = Mockito.mock(WorkFlowDefinitionRepository.class);
        WorkFlowTaskDefinitionRepository workFlowTaskDefinitionRepository = Mockito
                .mock(WorkFlowTaskDefinitionRepository.class);
        WorkFlowDefinitionEntity oo = WorkFlowDefinitionEntity.builder()
                .name(wk.getName())
                .description(wk.getDescription())
                .type(wk.getType().name())
                .author(wk.getAuthor())
                .createDate(wk.getCreatedDate())
                .modifyDate(wk.getCreatedDate())
                .build();

        oo.setId(UUID.randomUUID());
        Mockito.when(workFlowDefinitionRepository.save(Mockito.any())).thenReturn(oo);

        WorkFlowTaskDefinitionEntity workFlowTask = WorkFlowTaskDefinitionEntity.builder().name("test").description("test").build();
        workFlowTask.setId(UUID.randomUUID());
        // List<WorkFlowTaskDefinitionEntity> workFlowTaskDefinitionsEntity = new ArrayList<WorkFlowTaskDefinitionEntity>();
        // workFlowTaskDefinitionsEntity.add(workFlowTask);
        Mockito.when(workFlowTaskDefinitionRepository.save(Mockito.any())).thenReturn(workFlowTask);
        Mockito.when(workFlowTaskDefinitionRepository.findByWorkFlowDefinitionEntity(Mockito.any())).thenReturn(Arrays.asList(workFlowTask));

        // when
        BeanWorkFlowRegistryImpl beanWorkFlowRegistry = new BeanWorkFlowRegistryImpl(workFlowDefinitions,
                workFlowTaskDefinitions, workFlowExecutions, workFlowDefinitionRepository,
                workFlowTaskDefinitionRepository, objectMapper);
        // then
        assertNotEquals(beanWorkFlowRegistry, null);
        WorkFlowDefinition workFlowDefinition = beanWorkFlowRegistry.getWorkFlowDefinitionById(oo.getId());
        assertEquals(workFlowDefinition.getName(), "test");
        assertEquals(workFlowDefinition.getTasks().size(), 1);
        // Mockito.verify(workFlowDefinitionRepository, Mockito.times(2)).save(Mockito.any());
        // Mockito.verify(workFlowTaskDefinitionRepository, Mockito.times(1)).save(null);
    }
}
