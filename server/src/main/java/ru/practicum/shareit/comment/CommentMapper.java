package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());

        if (comment.getAuthor() != null) {
            commentDto.setAuthorName(comment.getAuthor().getName());
        }

        return commentDto;
    }

    public Comment toComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());

        return comment;
    }
}