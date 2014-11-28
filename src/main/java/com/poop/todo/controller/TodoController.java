package com.poop.todo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poop.todo.dto.TodoDTO;
import com.poop.todo.exception.TodoNotFoundException;
import com.poop.todo.model.Todo;
import com.poop.todo.service.TodoService;

@Controller
public class TodoController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TodoController.class);
	protected static final String MODEL_ATTRIBUTE_TODO = "todo";
	protected static final String VIEW_TODO_ADD = "todo/add";
	protected static final String REQUEST_MAPPING_TODO_VIEW = "/todo/{id}";
	protected static final String FLASH_MESSAGE_KEY_FEEDBACK = "feedbackMessage";
	protected static final String FEEDBACK_MESSAGE_KEY_TODO_ADDED="feedback.message.todo.added";
	protected static final String PARAMETER_TODO_ID = "id"; 

	private final TodoService todoService;

	private final MessageSource messageSource;

	@Autowired
	public TodoController(MessageSource msgSrc, TodoService tdService) {
		messageSource = msgSrc;
		todoService = tdService;
	}

	@RequestMapping(value = "/api/todo", method = RequestMethod.GET)
	@ResponseBody
	public List<TodoDTO> findAll() {
		List<Todo> models = todoService.findAll();
		return createDTOs(models);
	}

	@RequestMapping(value = "/api/todo/{id}", method = RequestMethod.GET)
	@ResponseBody
	public TodoDTO findById(@PathVariable("id") Long id)
			throws TodoNotFoundException {
		Todo found = todoService.findById(id);
		return createDTO(found);
	}

	@RequestMapping(value = "/todo/add", method = RequestMethod.GET)
	public String showAddTodoForm(Model model) {
		LOGGER.debug("Rendering add to-do entry form.");

		TodoDTO formObject = new TodoDTO();
		model.addAttribute(MODEL_ATTRIBUTE_TODO, formObject);
		return VIEW_TODO_ADD;
	}

	@RequestMapping(value = "/todo/add", method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute(MODEL_ATTRIBUTE_TODO) TodoDTO dto, BindingResult result, RedirectAttributes attributes) {
		LOGGER.debug("Adding a new to-do entry with information: {}", dto);
		
		if (result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			for(ObjectError err: errors){
				LOGGER.debug("ERROR="+err.toString());
			}	
			 LOGGER.debug("Add to-do form was submitted with binding errors. Rendering form view.");
			 return VIEW_TODO_ADD;
		} 
		Todo added = todoService.add(dto);
		LOGGER.debug("Added a todo entry with information {}", added);

		addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_TODO_ADDED, added.getTitle());
		attributes.addAttribute(PARAMETER_TODO_ID, added.getId());
		return createRedirectViewPath(REQUEST_MAPPING_TODO_VIEW);
	}

	private List<TodoDTO> createDTOs(List<Todo> models) {
		List<TodoDTO> dtos = new ArrayList<TodoDTO>();
		for (Todo todo : models) {
			dtos.add(createDTO(todo));
		}
		return dtos;
	}

	private TodoDTO createDTO(Todo model) {
		TodoDTO todoDto = new TodoDTO();
		todoDto.setId(model.getId());
		todoDto.setTitle(model.getTitle());
		todoDto.setDescription(model.getDescription());
		return todoDto;
	}
	
	private String getMessage(String messageCode, Object... messageParameters) {
		Locale current = LocaleContextHolder.getLocale();
		LOGGER.debug("Current locale is {}", current);
		return messageSource.getMessage(messageCode, messageParameters, current);
	}

	private void addFeedbackMessage(RedirectAttributes attributes, String messageCode, Object... messageParameters) {
		LOGGER.debug("Adding feedback message with code: {} and params: {}",messageCode, messageParameters);
		String localizedFeedbackMessage = getMessage(messageCode,messageParameters);
		LOGGER.debug("Localized message is: {}", localizedFeedbackMessage);
		attributes.addFlashAttribute(FLASH_MESSAGE_KEY_FEEDBACK, localizedFeedbackMessage);
	}

	private String createRedirectViewPath(String requestMapping) {
		StringBuilder redirectViewPath = new StringBuilder();
		redirectViewPath.append("redirect:");
		redirectViewPath.append(requestMapping);
		return redirectViewPath.toString();
	}

}
