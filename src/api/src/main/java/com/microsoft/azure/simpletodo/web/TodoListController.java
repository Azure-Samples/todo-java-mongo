package com.microsoft.azure.simpletodo.web;

import com.microsoft.azure.simpletodo.model.TodoItem;
import com.microsoft.azure.simpletodo.model.TodoList;
import com.microsoft.azure.simpletodo.repository.TodoListRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

@RestController
public class TodoListController {

    private final TodoListRepository todoListRepository;

    public TodoListController(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    /**
     * GET /lists : Gets an array of Todo lists
     *
     * @param top  The max number of items to returns in a result (optional)
     * @param skip The number of items to skip within the results (optional)
     * @return A Todo list result (status code 200)
     */
    // FIXME The Swagger configuration is wrong, this needs to be an array of Todo Lists, not a Todo List
    @ApiOperation(value = "Gets an array of Todo lists", nickname = "listsGet", notes = "", response = TodoList.class, tags = {"Lists",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Todo list result", response = TodoList.class)})
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/lists",
            produces = {"application/json"}
    )
    public ResponseEntity<TodoList[]> listsGet(
            @ApiParam(value = "The max number of items to returns in a result") @Valid @RequestParam(value = "top", required = false, defaultValue = "20") BigDecimal top,
            @ApiParam(value = "The number of items to skip within the results") @Valid @RequestParam(value = "skip", required = false, defaultValue = "0") BigDecimal skip
    ) {
        return ResponseEntity.ok(todoListRepository.findAll(PageRequest.of(skip.multiply(top).intValue(), top.intValue())).toList().toArray(TodoList[]::new));
    }

    /**
     * POST /lists : Creates a new Todo list
     *
     * @param todoList The Todo List (optional)
     * @return A Todo list result (status code 201)
     * or Invalid request schema (status code 400)
     */
    @ApiOperation(value = "Creates a new Todo list", nickname = "listsPost", notes = "", response = TodoList.class, tags = {"Lists",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "A Todo list result", response = TodoList.class),
            @ApiResponse(code = 400, message = "Invalid request schema")})
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lists",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<TodoList> listsPost(@ApiParam(value = "The Todo List") @Valid @RequestBody(required = false) TodoList todoList) {
        try {
            TodoList savedTodoList = todoListRepository.save(todoList);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedTodoList.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /lists/{listId} : Gets a Todo list by unique identifier
     *
     * @param listId The Todo list unique identifier (required)
     * @return A Todo list result (status code 200)
     * or Todo list not found (status code 404)
     */

    @ApiOperation(value = "Gets a Todo list by unique identifier", nickname = "listsListIdGet", notes = "", response = TodoList.class, tags = {"Lists",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A Todo list result", response = TodoList.class),
            @ApiResponse(code = 404, message = "Todo list not found")})
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/lists/{listId}",
            produces = {"application/json"}
    )
    public ResponseEntity<TodoList> listsListIdGet(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId) {
        return todoListRepository.findById(listId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * PUT /lists/{listId} : Updates a Todo list by unique identifier
     *
     * @param listId   The Todo list unique identifier (required)
     * @param todoList The Todo List (optional)
     * @return A Todo list result (status code 200)
     * or Todo list is invalid (status code 400)
     */

    @ApiOperation(value = "Updates a Todo list by unique identifier", nickname = "listsListIdPut", notes = "", response = TodoList.class, tags = {"Lists",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A Todo list result", response = TodoList.class),
            @ApiResponse(code = 400, message = "Todo list is invalid")})
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/lists/{listId}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<TodoList> listsListIdPut(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo List") @Valid @RequestBody(required = false) TodoList todoList) {

        return todoListRepository
                .findById(listId)
                .map(t -> ResponseEntity.ok(todoListRepository.save(t)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * DELETE /lists/{listId} : Deletes a Todo list by unique identifier
     *
     * @param listId The Todo list unique identifier (required)
     * @return Todo list deleted successfully (status code 204)
     *         or Todo list not found (status code 404)
     */

    @ApiOperation(value = "Deletes a Todo list by unique identifier", nickname = "listsListIdDelete", notes = "", tags={ "Lists", })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Todo list deleted successfully"),
            @ApiResponse(code = 404, message = "Todo list not found") })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/lists/{listId}"
    )
    public ResponseEntity<Void> listsListIdDelete(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId
    ) {
        Optional<TodoList> todoList = todoListRepository.findById(listId);
        if (todoList.isPresent()) {
            todoListRepository.deleteById(listId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
