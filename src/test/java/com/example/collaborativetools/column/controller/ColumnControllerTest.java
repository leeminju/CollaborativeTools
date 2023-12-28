package com.example.collaborativetools.column.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.column.dto.request.ColumnCreateRequest;
import com.example.collaborativetools.column.dto.response.ColumnResponse;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.service.ColumnService;
import com.example.collaborativetools.config.MockSecurityFilter;
import com.example.collaborativetools.global.config.WebSecurityConfig;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import com.example.collaborativetools.user.entitiy.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("컬럼 API 컨트롤러 테스트")
@WebMvcTest(controllers = ColumnApiController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	})
class ColumnControllerTest {
	private MockMvc mvc;
	private Principal mockPrincipal;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ColumnService columnService;

	private static final long BOARD_ID = 1L;

	@BeforeEach
	public void setUp() {
		mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity(new MockSecurityFilter()))
			.build();
	}

	private void mockUserSetup() {
		// Mock 테스트 유져 생성
		User user = getUser(1L);
		UserDetailsImpl testUserDetails = new UserDetailsImpl(user);
		mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
	}

	private static User getUser(Long id) {
		User user = new User("testUser", "testPassword");
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}

	@Test
	void 컬럼_생성_테스트() throws Exception {
		//Given
		this.mockUserSetup();
		Long columnId = 1L;

		ColumnCreateRequest request = new ColumnCreateRequest(BOARD_ID, "테스트", 1);
		Columns column = getColumn(request, columnId);

		given(columnService.createColumn(any(), any())).willReturn(
			ColumnResponse.from(column)
		);

		//When
		ResultActions actions = mvc.perform(
			post("/api/columns")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isCreated());
	}

	private static Columns getColumn(ColumnCreateRequest request, Long columnId) {
		Columns column = Columns.create(
			request.getTitle(),
			request.getSequence(),
			getBoard(request.getBoardId())
		);
		ReflectionTestUtils.setField(column, "id", columnId);
		return column;
	}

	private static Board getBoard(Long boardId) {
		return new Board(
			boardId,
			"title",
			"desc",
			null,
			null,
			null
		);
	}

}