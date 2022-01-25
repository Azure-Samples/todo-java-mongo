package com.microsoft.azure.simpletodo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A task that needs to be completed
 */
@ApiModel(description = "A task that needs to be completed")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-01-24T15:32:56.631412+01:00[Europe/Paris]")
public class TodoItem {

    @Id
    @JsonProperty("id")
    private String id;

    @Indexed
    @JsonProperty("listId")
    private String listId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @Indexed
    @JsonProperty("state")
    private TodoState state;

    @JsonProperty("dueDate")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dueDate;

    @JsonProperty("completedDate")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime completedDate;

    public TodoItem id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     */
    @ApiModelProperty(value = "")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TodoItem listId(String listId) {
        this.listId = listId;
        return this;
    }

    /**
     * Get listId
     *
     * @return listId
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull
    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public TodoItem name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TodoItem description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoItem state(TodoState state) {
        this.state = state;
        return this;
    }

    /**
     * Get state
     *
     * @return state
     */
    @ApiModelProperty(value = "")
    @Valid
    public TodoState getState() {
        return state;
    }

    public void setState(TodoState state) {
        this.state = state;
    }

    public TodoItem dueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    /**
     * Get dueDate
     *
     * @return dueDate
     */
    @ApiModelProperty(value = "")
    @Valid
    public OffsetDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TodoItem completedDate(OffsetDateTime completedDate) {
        this.completedDate = completedDate;
        return this;
    }

    /**
     * Get completedDate
     *
     * @return completedDate
     */
    @ApiModelProperty(value = "")
    @Valid
    public OffsetDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(OffsetDateTime completedDate) {
        this.completedDate = completedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoItem todoItem = (TodoItem) o;
        return Objects.equals(this.id, todoItem.id) &&
                Objects.equals(this.listId, todoItem.listId) &&
                Objects.equals(this.name, todoItem.name) &&
                Objects.equals(this.description, todoItem.description) &&
                Objects.equals(this.state, todoItem.state) &&
                Objects.equals(this.dueDate, todoItem.dueDate) &&
                Objects.equals(this.completedDate, todoItem.completedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, listId, name, description, state, dueDate, completedDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TodoItem {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    listId: ").append(toIndentedString(listId)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    dueDate: ").append(toIndentedString(dueDate)).append("\n");
        sb.append("    completedDate: ").append(toIndentedString(completedDate)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

