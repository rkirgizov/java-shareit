package ru.practicum.shareit.server.item.dto.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.Comment;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, User booker, Item item) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(booker);
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

}
