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
package com.redhat.parodos.workflow.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.parodos.workflow.task.enums.WorkFlowTaskOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redhat.parodos.workflow.task.parameter.WorkFlowTaskParameter;
import com.redhat.parodos.workflow.task.parameter.WorkFlowTaskParameterType;
import com.redhat.parodos.workflows.work.Work;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Basic Contract for Work in the Infrastructure Service
 *
 * @author Luke Shannon (Github: lshannon)
 *
 */
public interface WorkFlowTask extends Work {

	/**
	 * Parameters required for the Task to execute. These are generally obtained from
	 * the @see WorkContext. The @see BaseWorkFlowTask has a method to simplify getting
	 * these values
	 * @return List of @see WorkFlowTaskParameter that need to be obtained from the @see
	 * WorkContext
	 */
	@NonNull
	default List<WorkFlowTaskParameter> getWorkFlowTaskParameters() {
		return Collections.emptyList();
	}

	/**
	 * The expected Output/result of the Task.
	 * @return List of @see WorkFlowTaskOutput
	 */
	@NonNull
	default List<WorkFlowTaskOutput> getWorkFlowTaskOutputs() {
		return Collections.emptyList();

	}

	default String GetAsJsonSchema() {
		HashMap<String, Map<String, String>> result = new HashMap<>();
		for (WorkFlowTaskParameter workFlowTaskParameter : this.getWorkFlowTaskParameters()) {
			Map<String, String> properties = Map.ofEntries(
					Map.entry("required", String.format("%s", !workFlowTaskParameter.isOptional())),
					Map.entry("description", workFlowTaskParameter.getDescription())
					);
			workFlowTaskParameter.getJsonSchemaOptions().forEach(properties::put);

			switch (workFlowTaskParameter.getType()) {
				case PASSWORD:
					properties.put("type", "string");
					properties.put("format", "password");
					break;
				case TEXT:
					properties.put("type", "string");
					properties.put("format", "text");
					break;
				case EMAIL:
					properties.put("type", "string");
					properties.put("format", "email");
					break;
				case NUMBER:
					properties.put("type", "number");
					break;
				case URL:
					properties.put("type", "string");
					properties.put("format", "url");
					break;
				case DATE:
					properties.put("type", "string");
					properties.put("format", "date");
					break;
				default:
					break;
			}
			result.put(workFlowTaskParameter.getKey(), properties);


		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(result);
		} catch (Exception e) {
			return "{}";
		}
	}


}
