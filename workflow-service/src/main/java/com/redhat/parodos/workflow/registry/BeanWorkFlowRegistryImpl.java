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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.parodos.workflow.definition.entity.WorkFlowCheckerDefinitionEntity;
import com.redhat.parodos.workflow.definition.entity.WorkFlowCheckerDefinitionPK;
import com.redhat.parodos.workflow.definition.entity.WorkFlowDefinitionEntity;
import com.redhat.parodos.workflow.definition.entity.WorkFlowTaskDefinitionEntity;
import com.redhat.parodos.workflow.definition.repository.WorkFlowDefinitionRepository;
import com.redhat.parodos.workflow.definition.repository.WorkFlowTaskDefinitionRepository;
import com.redhat.parodos.workflows.definition.WorkFlowDefinition;
import com.redhat.parodos.workflows.definition.task.WorkFlowTaskDefinition;
import com.redhat.parodos.workflows.workflow.WorkFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * An implementation of the WorkflowRegistry that loads all Bean definitions of
 * type WorkFlow into a list
 *
 * @author Luke Shannon (Github: lshannon)
 * @author Annel Ketcha (Github: anludke)
 */

@Slf4j
@Component
public class BeanWorkFlowRegistryImpl implements WorkFlowRegistry<String> {
    // Spring will populate this through classpath scanning when the Context starts
    // up
    private static String underscoreChar = "_";
    private final List<WorkFlowDefinition> workFlowDefinitions;
    private final List<WorkFlowTaskDefinition> workFlowTaskDefinitions;
    private final List<WorkFlow> workFlowExecutions;
    private final WorkFlowDefinitionRepository workFlowDefinitionRepository;
    private final WorkFlowTaskDefinitionRepository workFlowTaskDefinitionRepository;
    private final ObjectMapper objectMapper;

    // WorkFlow Maps with db id and entities
    private final Map<UUID, WorkFlowDefinition> workFlowDefinitionIdMap = new HashMap<>();
    private final Map<String, WorkFlow> workFlowExecutionNameMap = new HashMap<>();
    // WorkFlow Task Maps with db id and entities
    private final Map<String, WorkFlowTaskDefinitionEntity> workFlowDefinitionTaskNameMap = new HashMap<>();
    private final Map<String, UUID> workFlowDefinitionTaskIdMap = new HashMap<>();

    public BeanWorkFlowRegistryImpl(List<WorkFlowDefinition> workFlowDefinitions,
            List<WorkFlowTaskDefinition> workFlowTaskDefinitions,
            List<WorkFlow> workFlowExecutions,
            WorkFlowDefinitionRepository workFlowDefinitionRepository,
            WorkFlowTaskDefinitionRepository workFlowTaskDefinitionRepository,
            ObjectMapper objectMapper) {
        this.workFlowDefinitions = workFlowDefinitions;
        this.workFlowTaskDefinitions = workFlowTaskDefinitions;
        this.workFlowExecutions = workFlowExecutions;
        this.workFlowDefinitionRepository = workFlowDefinitionRepository;
        this.workFlowTaskDefinitionRepository = workFlowTaskDefinitionRepository;
        this.objectMapper = objectMapper;

        if (workFlowDefinitions == null) {
            log.error(
                    "No workflows definitions were registered. Initializing an empty collection of workflows so the application can start");
            workFlowDefinitions = new ArrayList<>();
        }

        if (workFlowExecutions == null) {
            log.error(
                    "No workflows executions were registered. Initializing an empty collection of workflows so the application can start");
            workFlowExecutions = new ArrayList<>();
        }

        // persist workflow from beans
        // TODO: refine into services
        this.workFlowDefinitions.forEach(wd -> {

            WorkFlowDefinitionEntity workFlowDefinitionEntity = workFlowDefinitionRepository
                    .save(WorkFlowDefinitionEntity.builder()
                            .name(wd.getName())
                            .description(wd.getDescription())
                            .type(wd.getType().name())
                            .author(wd.getAuthor())
                            .createDate(wd.getCreatedDate())
                            .modifyDate(wd.getCreatedDate())
                            .build());

            wd.getTasks().forEach(wdt -> {
                try {
                    log.info("Phase 1--->");
                    WorkFlowTaskDefinitionEntity taskEntity = WorkFlowTaskDefinitionEntity.builder()
                            .name(wdt.getName())
                            .description(wdt.getDescription())
                            .createDate(wdt.getCreateDate())
                            .modifyDate(wdt.getModifyDate())
                            .parameters(objectMapper.writeValueAsString(wdt.getParameters()))
                            .outputs(objectMapper.writeValueAsString(wdt.getOutputs()))
                            .workFlowDefinitionEntity(workFlowDefinitionEntity)
                            .build();

                    log.info("Phase 2IIII---> {} {}", workFlowDefinitionEntity.getId(), "------");
                    WorkFlowTaskDefinitionEntity workFlowTaskDefinitionEntity = workFlowTaskDefinitionRepository
                            .save(taskEntity);

                    log.info("Phase 3--->");
                    workFlowDefinitionTaskIdMap.put(wdt.getName(), workFlowTaskDefinitionEntity.getId());

                    workFlowDefinitionTaskNameMap.put(String.format("%s%s%s", workFlowDefinitionEntity.getName(),
                            underscoreChar,
                            workFlowTaskDefinitionEntity.getName()),
                            workFlowTaskDefinitionEntity);

                    log.info("Phase 5--->");
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

            List<WorkFlowTaskDefinitionEntity> workFlowTaskDefinitionEntityList = workFlowTaskDefinitionRepository
                    .findByWorkFlowDefinitionEntity(workFlowDefinitionEntity);

            workFlowTaskDefinitionEntityList.forEach(workFlowTaskDefinitionEntity -> {
                log.info("Second loop phase 1");
                WorkFlowTaskDefinition wtd = wd.getTasks().stream()
                        .filter(t -> t.getName().equalsIgnoreCase(workFlowTaskDefinitionEntity.getName())).findFirst()
                        .get();
                if (wtd.getPreviousTask() != null) {
                    workFlowTaskDefinitionEntity
                            .setPreviousTask(workFlowDefinitionTaskIdMap.get(wtd.getPreviousTask().getName()));
                }

                if (wtd.getNextTask() != null) {
                    workFlowTaskDefinitionEntity
                            .setNextTask(workFlowDefinitionTaskIdMap.get(wtd.getNextTask().getName()));
                }
                // log.info("Second Loop phase 2 ---", workFlowTaskDefinitionEntity.getId());
                workFlowTaskDefinitionRepository.save(workFlowTaskDefinitionEntity);
            });
            log.info("Second loop final --",workFlowDefinitionEntity.getId(), "Final");
            workFlowDefinitionIdMap.put(workFlowDefinitionEntity.getId(), wd);
        });

        workFlowTaskDefinitions.stream()
                .filter(workFlowTaskDefinition -> workFlowTaskDefinition.getWorkFlowCheckerDefinition() != null)
                .forEach(wtd -> {
                    log.info("Phase1--- TD");
                    WorkFlowTaskDefinitionEntity workFlowTaskDefinitionEntity = workFlowTaskDefinitionRepository
                            .findFirstByName(wtd.getName());
                    workFlowTaskDefinitionEntity.setWorkFlowCheckerDefinitionEntity(
                            Optional.ofNullable(wtd.getWorkFlowCheckerDefinition())
                                    .map(wdtChecker -> WorkFlowCheckerDefinitionEntity.builder()
                                            .id(WorkFlowCheckerDefinitionPK.builder()
                                                    .workFlowCheckerId(workFlowDefinitionRepository
                                                            .findFirstByName(wdtChecker.getName()).getId())
                                                    .taskId(workFlowTaskDefinitionEntity.getId())
                                                    .build())
                                            .task(workFlowTaskDefinitionEntity)
                                            .checkWorkFlow(
                                                    workFlowDefinitionRepository.findFirstByName(wdtChecker.getName()))
                                            .nextWorkFlow(workFlowDefinitionRepository
                                                    .findFirstByName(wdtChecker.getNextWorkFlowDefinition().getName()))
                                            .cronExpression(wdtChecker.getCronExpression())
                                            .build())
                                    .orElse(null));
                    workFlowTaskDefinitionRepository.save(workFlowTaskDefinitionEntity);
                });

        this.workFlowExecutions.forEach(we -> workFlowExecutionNameMap.put(we.getName(), we));

        log.info(">> Detected {} WorkFlow definitions from the Bean Registry", workFlowDefinitions.size());
        log.info(">> Detected {} WorkFlow executions from the Bean Registry", workFlowExecutions.size());
    }

    @Override
    public WorkFlow getWorkFlowExecutionByName(String workFlowName) {
        return workFlowExecutionNameMap.get(workFlowName);
    }

    @Override
    public WorkFlowDefinition getWorkFlowDefinitionById(UUID workFlowId) {
        return workFlowDefinitionIdMap.get(workFlowId);
    }

    @Override
    public UUID getWorkFlowTaskDefinitionId(String workFlowName, String workFlowTaskName) {
        return UUID.fromString(workFlowDefinitionTaskNameMap.get(String.format("%s%s%s",
                workFlowName,
                underscoreChar,
                workFlowTaskName)).getId().toString());
    }
}
