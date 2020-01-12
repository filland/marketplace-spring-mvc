//package com.trade.controller;
//
//import com.trade.model.User;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Ignore;
//import org.testng.annotations.Test;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
////@WebAppConfiguration
////@ContextConfiguration(locations = { "classpath:spring-web-servlet.xml" })
//public class LoginControllerTest
////        extends AbstractTestNGSpringContextTests
//{
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    private MockHttpServletResponse mockHttpServletResponse;
//    private MockHttpServletRequest mockHttpServletRequest;
//
//    private MockMvc mockMvc;
//
//    @BeforeMethod
//    public void setUp() {
//
//        MockitoAnnotations.initMocks(this);
//
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//
//    }
//
//    @AfterMethod
//    public void tearDown() {
//
//        mockMvc = null;
//
//    }
//
//    @Test
//    public void testGetLoginPage() throws Exception {
//
//        mockMvc
//                .perform(get("/login"))
//                .andExpect(status().is(200))
//                .andExpect(content().contentType(MediaType.TEXT_HTML))
//                .andExpect(content().encoding("UTF-8"));
//
//    }
//
//    @Test
//    @Ignore("because it does not work")
//    public void testLogin() throws Exception {
//
//        String user = "user";
//        String password = "password";
//
//
//        User userFromDB = new User();
//        userFromDB.setName(user);
//        userFromDB.setPassword(password);
//
////        when(jdbcTemplate.queryForObject(
////                "select * from user where username = ?",
////                new UserDaoImpMySQL.UserRowMapper(),
////                user
////        )).thenReturn(userFromDB);
////
////        when(userDaoImpMySQL.findByUsername(user)).thenReturn(userFromDB);
////        when(userService.findByUsername(user)).thenReturn(userFromDB);
////
////        when(authorizationService.login(user, password, mockHttpServletResponse))
////                .thenReturn(true);
//
//        mockMvc
//                .perform(post("/login")
//                        .param("username", user)
//                        .param("password", password))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.TEXT_HTML))
//                .andExpect(content().encoding("UTF-8"));
//
//    }
//
//    @Test
//    public void testLogout() {
//    }
//}