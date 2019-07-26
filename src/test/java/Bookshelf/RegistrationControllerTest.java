package Bookshelf;

import Bookshelf.controller.RegistrationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-Test.properties")
@Sql(value = {"/create-user-before.sql", "/books-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/books-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class RegistrationControllerTest {
    Map<String, Object> model;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationController regController;

    @Test
    public void registration() throws Exception {
        this.mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addNewUser() throws Exception {
        this.mockMvc.perform(post("/registration")
                .param("username", "test3")
                .param("password", "test3").with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(formLogin().user("test3").password("test3"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void addExistingUser() throws Exception {
        this.mockMvc.perform(post("/registration")
                .param("username", "test")
                .param("password", "test").with(csrf()))
                .andDo(print())
                .andExpect(xpath("//*[@id='exist']").string("\n" +
                        "            USER exist!\n" +
                        "    "));
    }
}
