package com.poop.todo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poop.todo.dto.TodoDTO;
import com.poop.todo.exception.TodoNotFoundException;
import com.poop.todo.model.Todo;
import com.poop.todo.service.TodoService;

@Controller
public class TodoController {
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
	
	public TodoDTO findById(Long id) throws TodoNotFoundException{
		Todo found = todoService.findById(id);
		return createDTO(found);
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
