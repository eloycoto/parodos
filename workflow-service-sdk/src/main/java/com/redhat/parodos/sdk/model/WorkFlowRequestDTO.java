/*
 * Parodos Workflow Service API
 * This is the API documentation for the Parodos Workflow Service. It provides operations to execute assessments to determine infrastructure options (tooling + environments). Also executes infrastructure task workflows to call downstream systems to stand-up an infrastructure option.
 *
 * The version of the OpenAPI document: v1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.redhat.parodos.sdk.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.redhat.parodos.sdk.model.ArgumentRequestDTO;
import com.redhat.parodos.sdk.model.WorkRequestDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.redhat.parodos.sdk.invoker.JSON;

/**
 * WorkFlowRequestDTO
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class WorkFlowRequestDTO {

	public static final String SERIALIZED_NAME_ARGUMENTS = "arguments";

	@SerializedName(SERIALIZED_NAME_ARGUMENTS)
	private List<ArgumentRequestDTO> arguments = new ArrayList<>();

	public static final String SERIALIZED_NAME_PROJECT_ID = "projectId";

	@SerializedName(SERIALIZED_NAME_PROJECT_ID)
	private String projectId;

	public static final String SERIALIZED_NAME_WORK_FLOW_NAME = "workFlowName";

	@SerializedName(SERIALIZED_NAME_WORK_FLOW_NAME)
	private String workFlowName;

	public static final String SERIALIZED_NAME_WORKS = "works";

	@SerializedName(SERIALIZED_NAME_WORKS)
	private List<WorkRequestDTO> works = new ArrayList<>();

	public WorkFlowRequestDTO() {
	}

	public WorkFlowRequestDTO arguments(List<ArgumentRequestDTO> arguments) {

		this.arguments = arguments;
		return this;
	}

	public WorkFlowRequestDTO addArgumentsItem(ArgumentRequestDTO argumentsItem) {
		if (this.arguments == null) {
			this.arguments = new ArrayList<>();
		}
		this.arguments.add(argumentsItem);
		return this;
	}

	/**
	 * Get arguments
	 * @return arguments
	 **/
	@javax.annotation.Nullable

	public List<ArgumentRequestDTO> getArguments() {
		return arguments;
	}

	public void setArguments(List<ArgumentRequestDTO> arguments) {
		this.arguments = arguments;
	}

	public WorkFlowRequestDTO projectId(String projectId) {

		this.projectId = projectId;
		return this;
	}

	/**
	 * Get projectId
	 * @return projectId
	 **/
	@javax.annotation.Nullable

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public WorkFlowRequestDTO workFlowName(String workFlowName) {

		this.workFlowName = workFlowName;
		return this;
	}

	/**
	 * Get workFlowName
	 * @return workFlowName
	 **/
	@javax.annotation.Nullable

	public String getWorkFlowName() {
		return workFlowName;
	}

	public void setWorkFlowName(String workFlowName) {
		this.workFlowName = workFlowName;
	}

	public WorkFlowRequestDTO works(List<WorkRequestDTO> works) {

		this.works = works;
		return this;
	}

	public WorkFlowRequestDTO addWorksItem(WorkRequestDTO worksItem) {
		if (this.works == null) {
			this.works = new ArrayList<>();
		}
		this.works.add(worksItem);
		return this;
	}

	/**
	 * Get works
	 * @return works
	 **/
	@javax.annotation.Nullable

	public List<WorkRequestDTO> getWorks() {
		return works;
	}

	public void setWorks(List<WorkRequestDTO> works) {
		this.works = works;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		WorkFlowRequestDTO workFlowRequestDTO = (WorkFlowRequestDTO) o;
		return Objects.equals(this.arguments, workFlowRequestDTO.arguments)
				&& Objects.equals(this.projectId, workFlowRequestDTO.projectId)
				&& Objects.equals(this.workFlowName, workFlowRequestDTO.workFlowName)
				&& Objects.equals(this.works, workFlowRequestDTO.works);
	}

	@Override
	public int hashCode() {
		return Objects.hash(arguments, projectId, workFlowName, works);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class WorkFlowRequestDTO {\n");
		sb.append("    arguments: ").append(toIndentedString(arguments)).append("\n");
		sb.append("    projectId: ").append(toIndentedString(projectId)).append("\n");
		sb.append("    workFlowName: ").append(toIndentedString(workFlowName)).append("\n");
		sb.append("    works: ").append(toIndentedString(works)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces (except the
	 * first line).
	 */
	private String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

	public static HashSet<String> openapiFields;

	public static HashSet<String> openapiRequiredFields;

	static {
		// a set of all properties/fields (JSON key names)
		openapiFields = new HashSet<String>();
		openapiFields.add("arguments");
		openapiFields.add("projectId");
		openapiFields.add("workFlowName");
		openapiFields.add("works");

		// a set of required properties/fields (JSON key names)
		openapiRequiredFields = new HashSet<String>();
	}

	/**
	 * Validates the JSON Object and throws an exception if issues found
	 * @param jsonObj JSON Object
	 * @throws IOException if the JSON Object is invalid with respect to
	 * WorkFlowRequestDTO
	 */
	public static void validateJsonObject(JsonObject jsonObj) throws IOException {
		if (jsonObj == null) {
			if (!WorkFlowRequestDTO.openapiRequiredFields.isEmpty()) { // has required
																		// fields but JSON
																		// object is null
				throw new IllegalArgumentException(String.format(
						"The required field(s) %s in WorkFlowRequestDTO is not found in the empty JSON string",
						WorkFlowRequestDTO.openapiRequiredFields.toString()));
			}
		}

		Set<Entry<String, JsonElement>> entries = jsonObj.entrySet();
		// check to see if the JSON string contains additional fields
		for (Entry<String, JsonElement> entry : entries) {
			if (!WorkFlowRequestDTO.openapiFields.contains(entry.getKey())) {
				throw new IllegalArgumentException(String.format(
						"The field `%s` in the JSON string is not defined in the `WorkFlowRequestDTO` properties. JSON: %s",
						entry.getKey(), jsonObj.toString()));
			}
		}
		if (jsonObj.get("arguments") != null && !jsonObj.get("arguments").isJsonNull()) {
			JsonArray jsonArrayarguments = jsonObj.getAsJsonArray("arguments");
			if (jsonArrayarguments != null) {
				// ensure the json data is an array
				if (!jsonObj.get("arguments").isJsonArray()) {
					throw new IllegalArgumentException(String.format(
							"Expected the field `arguments` to be an array in the JSON string but got `%s`",
							jsonObj.get("arguments").toString()));
				}

				// validate the optional field `arguments` (array)
				for (int i = 0; i < jsonArrayarguments.size(); i++) {
					ArgumentRequestDTO.validateJsonObject(jsonArrayarguments.get(i).getAsJsonObject());
				}
				;
			}
		}
		if ((jsonObj.get("projectId") != null && !jsonObj.get("projectId").isJsonNull())
				&& !jsonObj.get("projectId").isJsonPrimitive()) {
			throw new IllegalArgumentException(String.format(
					"Expected the field `projectId` to be a primitive type in the JSON string but got `%s`",
					jsonObj.get("projectId").toString()));
		}
		if ((jsonObj.get("workFlowName") != null && !jsonObj.get("workFlowName").isJsonNull())
				&& !jsonObj.get("workFlowName").isJsonPrimitive()) {
			throw new IllegalArgumentException(String.format(
					"Expected the field `workFlowName` to be a primitive type in the JSON string but got `%s`",
					jsonObj.get("workFlowName").toString()));
		}
		if (jsonObj.get("works") != null && !jsonObj.get("works").isJsonNull()) {
			JsonArray jsonArrayworks = jsonObj.getAsJsonArray("works");
			if (jsonArrayworks != null) {
				// ensure the json data is an array
				if (!jsonObj.get("works").isJsonArray()) {
					throw new IllegalArgumentException(
							String.format("Expected the field `works` to be an array in the JSON string but got `%s`",
									jsonObj.get("works").toString()));
				}

				// validate the optional field `works` (array)
				for (int i = 0; i < jsonArrayworks.size(); i++) {
					WorkRequestDTO.validateJsonObject(jsonArrayworks.get(i).getAsJsonObject());
				}
				;
			}
		}
	}

	public static class CustomTypeAdapterFactory implements TypeAdapterFactory {

		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			if (!WorkFlowRequestDTO.class.isAssignableFrom(type.getRawType())) {
				return null; // this class only serializes 'WorkFlowRequestDTO' and its
								// subtypes
			}
			final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
			final TypeAdapter<WorkFlowRequestDTO> thisAdapter = gson.getDelegateAdapter(this,
					TypeToken.get(WorkFlowRequestDTO.class));

			return (TypeAdapter<T>) new TypeAdapter<WorkFlowRequestDTO>() {
				@Override
				public void write(JsonWriter out, WorkFlowRequestDTO value) throws IOException {
					JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
					elementAdapter.write(out, obj);
				}

				@Override
				public WorkFlowRequestDTO read(JsonReader in) throws IOException {
					JsonObject jsonObj = elementAdapter.read(in).getAsJsonObject();
					validateJsonObject(jsonObj);
					return thisAdapter.fromJsonTree(jsonObj);
				}

			}.nullSafe();
		}

	}

	/**
	 * Create an instance of WorkFlowRequestDTO given an JSON string
	 * @param jsonString JSON string
	 * @return An instance of WorkFlowRequestDTO
	 * @throws IOException if the JSON string is invalid with respect to
	 * WorkFlowRequestDTO
	 */
	public static WorkFlowRequestDTO fromJson(String jsonString) throws IOException {
		return JSON.getGson().fromJson(jsonString, WorkFlowRequestDTO.class);
	}

	/**
	 * Convert an instance of WorkFlowRequestDTO to an JSON string
	 * @return JSON string
	 */
	public String toJson() {
		return JSON.getGson().toJson(this);
	}

}
