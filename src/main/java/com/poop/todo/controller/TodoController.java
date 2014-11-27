package com.poop.todo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	protected static final String MODEL_ATTRIBUTE_TODO = "todo";
	protected static final String VIEW_TODO_ADD = "todo/add";
	protected static final String REQUEST_MAPPING_TODO_VIEW = "/todo/{id}";
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
	public String showAddTodoForm(Model model){
		LOGGER.debug("Rendering add to-do entry form.");
		
		 TodoDTO formObject = new TodoDTO();
		 model.addAttribute(MODEL_ATTRIBUTE_TODO, formObject);
		 return VIEW_TODO_ADD;
	}
	
	@RequestMapping(value="/todo/add", method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute (MODEL_ATTRIBUTE_TODO) TodoDTO dto){
		LOGGER.debug("Adding a new to-do entry with information: {}",dto);
		return createRedirectViewPath(REQUEST_MAPPING_TODO_VIEW);
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
	
	private String createRedirectViewPath(String requestMapping){
		StringBuilder redirectViewPath = new StringBuilder();
		redirectViewPath.append("redirect:");
		redirectViewPath.append(requestMapping);
		return redirectViewPath.toString();
	}
	
}
