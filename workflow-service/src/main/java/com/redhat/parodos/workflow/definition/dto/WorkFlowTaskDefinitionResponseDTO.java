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
package com.redhat.parodos.workflow.definition.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.redhat.parodos.workflows.common.enums.WorkFlowTaskOutput;
import com.redhat.parodos.workflows.definition.task.WorkFlowTaskParameter;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * response dto
 *
 * @author Luke Shannon (Github: lshannon)
 * @author Richard Wang (Github: richardw98)
 * @author Annel Ketcha (Github: anludke)
 */
@Data
@Builder
public class WorkFlowTaskDefinitionResponseDTO {
    private String id;

    private String name;

    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WorkFlowTaskParameter> parameters;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WorkFlowTaskOutput> outputs;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmbeddedTaskResponseDTO previousTask;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmbeddedTaskResponseDTO nextTask;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID workFlowChecker;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID nextWorkFlow;
}
