package com.poop.todo.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poop.todo.dto.TodoDTO;
import com.poop.todo.exception.TodoNotFoundException;
import com.poop.todo.model.Todo;
import com.poop.todo.service.TodoService;

@Controller
public class TodoController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TodoController.class);
	private static final String MODEL_ATTRIBUTE_TODO = "todo";
	private static final String VIEW_TODO_ADD = "todo/add";
	private final TodoService todoService;
	
	private final MessageSource messageSource;
	
	@Autowired
	public TodoController(MessageSource msgSrc, TodoService tdService){
		messageSource = msgSrc;
		todoService = tdService;
	}
	
	@RequestMapping(value = "/api/todo", method = RequestMethod.GET)
	@ResponseBody
	public List<TodoDTO> findAll(){
		List<Todo> models = todoService.findAll();
		return createDTOs(models);
	}
	@RequestMapping(value = "/api/todo/{id}", method = RequestMethod.GET)
	@ResponseBody
	public TodoDTO findById(@PathVariable("id") Long id) throws TodoNotFoundException{
		Todo found = todoService.findById(id);
		return createDTO(found);
	}
	
	@RequestMapping(value = "/todo/add", method = RequestMethod.GET)
	public void showAddTodoForm(Model model){
		LOGGER.debug("Rendering add to-do entry form.");
		
		 TodoDTO formObject = new TodoDTO();
		 model.addAttribute(MODEL_ATTRIBUTE_TODO, formObject);
		 return VIEW_TODO_ADD;
	}
	
	private List<TodoDTO> createDTOs(List<Todo> models){
		List<TodoDTO> dtos = new ArrayList<TodoDTO>();
		for(Todo todo : models){
			dtos.add(createDTO(todo));
		}
		return dtos;
	}
	private TodoDTO createDTO(Todo model){
		TodoDTO todoDto = new TodoDTO();
		todoDto.setId(model.getId());
		todoDto.setTitle(model.getTitle());
		todoDto.setDescription(model.getDescription());
		return todoDto;
	}
	
}
