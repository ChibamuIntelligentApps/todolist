package com.poop.todo.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import com.poop.todo.config.TestContext;
import com.poop.todo.config.WebAppContext;
import com.poop.todo.dto.TodoDTO;
import com.poop.todo.model.Todo;
import com.poop.todo.service.TodoService;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.swing.text.View;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes ={TestContext.class, WebAppContext.class})
@WebAppConfiguration
public class TodoControllerTest {
	
	private static final String TODO_ADD_JSP="/WEB-INF/jsp/todo/add.jsp";
	@Resource
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	@Autowired
	private TodoService todoServiceMock;
	
	@Before
	public void setUp(){
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void showAddToForm_ShouldCreateFormObjectAndRenderAddTodoForm() throws Exception {
		mockMvc.perform(get("/todo/add"))
				.andExpect(status().isOk())
				.andExpect(view().name(TodoController.VIEW_TODO_ADD))
				.andExpect(forwardedUrl(TODO_ADD_JSP))
				.andExpect(model().attribute(TodoController.MODEL_ATTRIBUTE_TODO, hasProperty("id",nullValue())))
				.andExpect(model().attribute(TodoController.MODEL_ATTRIBUTE_TODO, hasProperty("description",isEmptyOrNullString())))
				.andExpect(model().attribute(TodoController.MODEL_ATTRIBUTE_TODO, hasProperty("title", isEmptyOrNullString())));
		verifyZeroInteractions(todoServiceMock);
	}
	
	@Test
	public void add_EmptyTodoEntry_ShouldRenderFormViewAndReturnValidationErrorForTitle() throws Exception {
		mockMvc.perform(post("/todo/add")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.sessionAttr(TodoController.MODEL_ATTRIBUTE_TODO, new TodoDTO())
				)
			.andExpect(status().isOk())
			.andExpect(view().name(TodoController.VIEW_TODO_ADD))
			.andExpect(forwardedUrl(TODO_ADD_JSP))
			.andExpect(model().attributeHasFieldErrors(TodoController.MODEL_ATTRIBUTE_TODO, "title"));
	}
}
