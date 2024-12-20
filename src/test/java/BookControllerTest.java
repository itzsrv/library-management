import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.itzsrv.controller.BookController;
import com.itzsrv.model.Book;
import com.itzsrv.repository.BookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    Book Book1 = new Book("Atomic Habits", "someone", "how to build better habits",4,1 );
    Book Book2 = new Book("Thinking Fast and Slow", "", "how to create good mental maps",4,2 );
    Book Book3 = new Book("Grokking Algorithms", "someone", "learn algorithms the fun way",3,3 );

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getAllRecords_success() throws Exception {
        List<Book> books = new ArrayList<>(Arrays.asList(Book1, Book2, Book3));

        Mockito.when(bookRepository.findAll()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/library-api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].name", is("Thinking Fast and Slow")));
    }

    @Test
    public void getBookById_success() throws Exception {
        Mockito.when(bookRepository.findById(Book1.getId())).thenReturn(java.util.Optional.of(Book1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/library-api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Atomic Habits")));
    }

    @Test
    public void createBook_success() throws Exception {
        Book book = Book.builder()
                .name("Let's C")
                .id(4)
                .author("Yashwant Kanetkar")
                .summary("learn procedural programming with c")
                .rating(4)
                .build();

        Mockito.when(bookRepository.save(book)).thenReturn(book);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/library-api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(book));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Let's C")));
    }

    @Test
    public void updateBook_success() throws Exception {
        Book updatedBook = Book.builder()
                .name("Let's C++")
                .id(1)
                .author("Yashwant Kanetkar")
                .summary("learn object oriented programming with c++")
                .rating(2)
                .build();

        Mockito.when(bookRepository.findById(Book1.getId())).thenReturn(Optional.of(Book1));
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/library-api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(updatedBook));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Let's C++")));
    }

    @Test
    public void deleteBookById_success() throws Exception {

        Mockito.when(bookRepository.findById(Book2.getId())).thenReturn(java.util.Optional.of(Book2));
        //Mockito.when(bookRepository.delete(book)).thenReturn(??) #Not needed

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/library-api/books/2"))
                        //.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify the method was called on the repository (optional)
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(Book2.getId());
    }

}
