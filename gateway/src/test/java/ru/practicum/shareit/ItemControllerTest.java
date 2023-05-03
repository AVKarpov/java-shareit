package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class ItemControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void searchWithWrongParam() throws Exception {
        String text = "description";

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "-1")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
