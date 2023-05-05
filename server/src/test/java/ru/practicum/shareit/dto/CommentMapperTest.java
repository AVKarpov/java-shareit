package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;

class CommentMapperTest {

    @Test
    void toCommentDtoTest() {
        User author = User.builder()
                .name("Test author name")
                .email("test@test.com")
                .build();
        Comment comment = Comment.builder()
                .author(author)
                .text("Test comment")
                .created(LocalDateTime.of(2022, 10, 22, 10,0, 5))
                .build();
        CommentResponseDto commentResponseDto = toCommentDto(comment);
        assertEquals(comment.getId(), commentResponseDto.getId());
        assertEquals(comment.getText(), commentResponseDto.getText());
        assertEquals(comment.getAuthor().getName(), commentResponseDto.getAuthorName());
        assertEquals(comment.getCreated(), commentResponseDto.getCreated());
    }

    @Test
    void commentEqualsTest() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.of(2022, 10, 22, 10,0, 5))
                .build();
        Comment comment2 = Comment.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.of(2022, 10, 22, 10,0, 5))
                .build();
        assertTrue(comment1.equals(comment2));
    }

}
