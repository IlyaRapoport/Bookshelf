package Bookshelf;

import Bookshelf.controller.MainController;
import Bookshelf.domain.Books;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.PathVariable;
import Bookshelf.domain.User;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-Test.properties")
@Sql(value = {"/create-user-before.sql", "/books-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/books-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

@WithUserDetails("test")
public class MainControllerTest {
    Map<String, Object> model;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController controller;

    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='intro']/div").string("Hello, test!"));
    }

    @Test
    public void messageListTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(4));

    }

    @Test
    public void filterList() throws Exception {
        this.mockMvc.perform(post("/filter").param("filter", "test").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(2))
                .andExpect(xpath("//*[@data-id=1]").exists())
                .andExpect(xpath("//*[@data-id=3]").exists());
    }

    @Test
    public void filterListEmpty() throws Exception {
        this.mockMvc.perform(post("/filter").param("filter", "").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(4));
    }

    @Test
    public void getAddBook() throws Exception {
        this.mockMvc.perform(get("/add"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addBook() throws Exception {

        MockHttpServletRequestBuilder multipart = multipart("/ad")
                .file("file", "file".getBytes())
                .param("bookName", "bookName")
                .param("bookAuthor", "bookAuthor")
                .param("bookDescription", "bookDescription")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(5));
    }
    @Test
    public void addBookWithSelect() throws Exception {

        MockHttpServletRequestBuilder multipartWithSelect = multipart("/ad")
                .file("file", "file".getBytes())
                .param("bookName", "bookName")
                .param("bookAuthor", "")
                .param("bookAuthorSelect", "second")
                .param("bookDescription", "bookDescription")
                .with(csrf());
        this.mockMvc.perform(multipartWithSelect)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(5));
    }

    @Test
    public void deleteBook() throws Exception {

        this.mockMvc.perform(get("/books/4"))
                .andDo(print())
                .andExpect(authenticated());

        this.mockMvc.perform(post("/books/del").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/main"));

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(3));


    }
    @Test
    public void deleteBookWithComments() throws Exception {

        this.mockMvc.perform(get("/books/4"))
                .andDo(print())
                .andExpect(authenticated());

        this.mockMvc.perform(post("/books/comments").param("comments", "comments").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(get("/books/4"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='CommentsAuthor']/h0").exists())
                .andExpect(xpath("//*[@id='comments']/h0").exists());

        this.mockMvc.perform(post("/books/del").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/main"));

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='list']/a").nodeCount(3));


    }

    @Test
    public void addAmdDeleteComments() throws Exception {

        this.mockMvc.perform(get("/books/2"))
                .andDo(print())
                .andExpect(authenticated());

        this.mockMvc.perform(post("/books/comments").param("comments", "comments").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(get("/books/2"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='CommentsAuthor']/h0").exists())
                .andExpect(xpath("//*[@id='comments']/h0").exists());

        this.mockMvc.perform(get("/books/2"))
                .andDo(print())
                .andExpect(authenticated());

        this.mockMvc.perform(post("/books/delcom").param("bookId", "2").with(csrf()))
                .andDo(print())
                .andExpect(authenticated());

        this.mockMvc.perform(get("/books/2"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='CommentsAuthor']/h0").doesNotExist())
                .andExpect(xpath("//*[@id='comments']/h0").doesNotExist());
    }

    @Test
    public void editBook() throws Exception {

        this.mockMvc.perform(get("/books/2"))
                .andDo(print())
                .andExpect(authenticated());

        MockHttpServletRequestBuilder multipart = multipart("/books/upd")
                .file("file", "file".getBytes())
                .param("bookName", "bookName")
                .param("bookAuthor", "bookAuthor")
                .param("bookDescription", "bookDescription")
                .param("bookAuthorSelect", "bookAuthorSelect")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection());

    }
    @Test
    public void editBookWithSelect() throws Exception {

        this.mockMvc.perform(get("/books/2"))
                .andDo(print())
                .andExpect(authenticated());

        MockHttpServletRequestBuilder multipart = multipart("/books/upd")
                .file("file", "file".getBytes())
                .param("bookName", "bookName")
                .param("bookAuthor", "")
                .param("bookDescription", "bookDescription")
                .param("bookAuthorSelect", "second")
                .with(csrf());
        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection());

    }

}
