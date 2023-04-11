package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

    private CommentMapper() {

    }

    public static CommentResponseDto toCommentDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
