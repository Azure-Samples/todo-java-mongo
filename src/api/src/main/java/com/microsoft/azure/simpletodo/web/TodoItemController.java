package com.microsoft.azure.simpletodo.web;

import com.microsoft.azure.simpletodo.model.TodoItem;
import com.microsoft.azure.simpletodo.model.TodoList;
import com.microsoft.azure.simpletodo.model.TodoState;
import com.microsoft.azure.simpletodo.repository.TodoItemRepository;
import com.microsoft.azure.simpletodo.repository.TodoListRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class TodoItemController {

    private final TodoListRepository todoListRepository;

    private final TodoItemRepository todoItemRepository;

    public TodoItemController(TodoListRepository todoListRepository, TodoItemRepository todoItemRepository) {
        this.todoListRepository = todoListRepository;
        this.todoItemRepository = todoItemRepository;
    }

    // FIXME The Swagger configuration is wrong, this a list of Todo items, not a list of Todo Lists
    @ApiOperation(value = "Gets Todo items within the specified list", nickname = "listsListIdItemsGet", notes = "", response = TodoList.class, responseContainer = "List", tags = {"Items",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "An array of Todo items", response = TodoList.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Todo list not found")})
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/lists/{listId}/items",
            produces = {"application/json"}
    )
    public ResponseEntity<List<TodoItem>> listsListIdItemsGet(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The max number of items to returns in a result") @Valid @RequestParam(value = "top", required = false, defaultValue = "20") BigDecimal top,
            @ApiParam(value = "The number of items to skip within the results") @Valid @RequestParam(value = "skip", required = false, defaultValue = "0") BigDecimal skip
    ) {
        Optional<TodoList> todoList = todoListRepository.findById(listId);
        if (todoList.isPresent()) {
            return ResponseEntity.ok(todoItemRepository.findTodoItemsByTodoList(listId, PageRequest.of(skip.multiply(top).intValue(), top.intValue())));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /lists/{listId}/items : Creates a new Todo item within a list
     *
     * @param listId   The Todo list unique identifier (required)
     * @param todoItem The Todo Item (optional)
     * @return A Todo item result (status code 201)
     * or Todo list not found (status code 404)
     */
    @ApiOperation(value = "Creates a new Todo item within a list", nickname = "listsListIdItemsPost", notes = "", response = TodoItem.class, tags = {"Items",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "A Todo item result", response = TodoItem.class),
            @ApiResponse(code = 404, message = "Todo list not found")})
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/lists/{listId}/items",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<TodoItem> listsListIdItemsPost(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo Item") @Valid @RequestBody(required = false) TodoItem todoItem) {

        Optional<TodoList> optionalTodoList = todoListRepository.findById(listId);
        if (optionalTodoList.isPresent()) {
            todoItem.setListId(listId);
            TodoItem savedTodoItem = todoItemRepository.save(todoItem);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedTodoItem.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /lists/{listId}/items/{itemId} : Gets a Todo item by unique identifier
     *
     * @param listId The Todo list unique identifier (required)
     * @param itemId The Todo item unique identifier (required)
     * @return A Todo item result (status code 200)
     * or Todo list or item not found (status code 404)
     */
    // FIXME the itemID is the unique identifier from the Todo List
    @ApiOperation(value = "Gets a Todo item by unique identifier", nickname = "listsListIdItemsItemIdGet", notes = "", response = TodoItem.class, tags = {"Items",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A Todo item result", response = TodoItem.class),
            @ApiResponse(code = 404, message = "Todo list or item not found")})
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/lists/{listId}/items/{itemId}",
            produces = {"application/json"}
    )
    public ResponseEntity<TodoItem> listsListIdItemsItemIdGet(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo item unique identifier", required = true) @PathVariable("itemId") String itemId) {

        return getTodoItem(listId, itemId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * PUT /lists/{listId}/items/{itemId} : Updates a Todo item by unique identifier
     *
     * @param listId   The Todo list unique identifier (required)
     * @param itemId   The Todo list unique identifier (required)
     * @param todoItem The Todo Item (optional)
     * @return A Todo item result (status code 200)
     * or Todo item is invalid (status code 400)
     * or Todo list or item not found (status code 404)
     */
    @ApiOperation(value = "Updates a Todo item by unique identifier", nickname = "listsListIdItemsItemIdPut", notes = "", response = TodoItem.class, tags = {"Items",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A Todo item result", response = TodoItem.class),
            @ApiResponse(code = 400, message = "Todo item is invalid"),
            @ApiResponse(code = 404, message = "Todo list or item not found")})
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/lists/{listId}/items/{itemId}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<TodoItem> listsListIdItemsItemIdPut(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("itemId") String itemId,
            @ApiParam(value = "The Todo Item") @Valid @RequestBody(required = false) TodoItem todoItem) {

        return getTodoItem(listId, itemId).map(t -> {
            todoItemRepository.save(todoItem);
            return ResponseEntity.ok(todoItem);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /lists/{listId}/items/{itemId} : Deletes a Todo item by unique identifier
     *
     * @param listId The Todo list unique identifier (required)
     * @param itemId The Todo list unique identifier (required)
     * @return A Todo item result (status code 204)
     * or Todo list or item not found (status code 404)
     */
    @ApiOperation(value = "Deletes a Todo item by unique identifier", nickname = "listsListIdItemsItemIdDelete", notes = "", response = TodoItem.class, tags = {"Items",})
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "A Todo item result", response = TodoItem.class),
            @ApiResponse(code = 404, message = "Todo list or item not found")})
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/lists/{listId}/items/{itemId}",
            produces = {"application/json"}
    )
    public ResponseEntity<TodoItem> listsListIdItemsItemIdDelete(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("itemId") String itemId
    ) {
        Optional<TodoItem> todoItem = getTodoItem(listId, itemId);
        if (todoItem.isPresent()) {
            todoItemRepository.deleteById(itemId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /lists/{listId}/items/state/{state} : Gets a list of Todo items of a specific state
     *
     * @param listId The Todo list unique identifier (required)
     * @param state  The Todo item state (required)
     * @param top    The max number of items to returns in a result (optional)
     * @param skip   The number of items to skip within the results (optional)
     * @return An array of Todo items (status code 200)
     * or Todo list or item not found (status code 404)
     */
    @ApiOperation(value = "Gets a list of Todo items of a specific state", nickname = "listsListIdItemsStateStateGet", notes = "", response = TodoItem.class, responseContainer = "List", tags = {"Items",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "An array of Todo items", response = TodoItem.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Todo list or item not found")})
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/lists/{listId}/items/state/{state}",
            produces = {"application/json"}
    )
    public ResponseEntity<List<TodoItem>> listsListIdItemsStateStateGet(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo item state", required = true, allowableValues = "todo, inprogress, done") @PathVariable("state") TodoState state,
            @ApiParam(value = "The max number of items to returns in a result") @Valid @RequestParam(value = "top", required = false, defaultValue = "20") BigDecimal top,
            @ApiParam(value = "The number of items to skip within the results") @Valid @RequestParam(value = "skip", required = false, defaultValue = "0") BigDecimal skip
    ) {

        return ResponseEntity.ok(
                todoItemRepository
                        .findTodoItemsByTodoListAndState(listId, state.name(), PageRequest.of(skip.multiply(top).intValue(), top.intValue())));
    }

    /**
     * PUT /lists/{listId}/items/state/{state} : Changes the state of the specified list items
     *
     * @param listId The Todo list unique identifier (required)
     * @param state The Todo item state (required)
     * @param requestBody  (optional)
     * @return Todo items updated (status code 204)
     *         or Update request is invalid (status code 400)
     */
    @ApiOperation(value = "Changes the state of the specified list items", nickname = "listsListIdItemsStateStatePut", notes = "", tags={ "Items", })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Todo items updated"),
            @ApiResponse(code = 400, message = "Update request is invalid") })
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/lists/{listId}/items/state/{state}",
            consumes = { "application/json" }
    )
    public ResponseEntity<Void> listsListIdItemsStateStatePut(
            @ApiParam(value = "The Todo list unique identifier", required = true) @PathVariable("listId") String listId,
            @ApiParam(value = "The Todo item state", required = true, allowableValues = "todo, inprogress, done") @PathVariable("state") TodoState state,
            @ApiParam(value = "" )   @Valid @RequestBody(required = false) List<String> requestBody) {

        for (TodoItem todoItem : todoItemRepository.findTodoItemsByTodoList(listId, Pageable.unpaged())) {
            todoItem.state(state);
            todoItemRepository.save(todoItem);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Optional<TodoItem> getTodoItem(String listId, String itemId) {
        Optional<TodoList> optionalTodoList = todoListRepository.findById(listId);
        if (optionalTodoList.isEmpty()) {
            return Optional.empty();
        }
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(itemId);
        if (optionalTodoItem.isPresent()) {
            TodoItem todoItem = optionalTodoItem.get();
            if (todoItem.getListId().equals(listId)) {
                return Optional.of(todoItem);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
