package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MappingComment {
    public Comment toComment(Long id, CommentDtoRequest commentDtoRequest,
                              User user, Item item, LocalDateTime dateTime) {
        return new Comment(id, commentDtoRequest.getText(), user, item, dateTime);
    }

    public CommentDtoResponse toCommentDtoResponse(Comment comment) {
        return new CommentDtoResponse(comment.getId(), comment.getText(),
                comment.getUser().getName(), comment.getCreated());
    }

    public List<CommentDtoResponse> toCommentDtoResponses(List<Comment> comments) {
        return comments.stream().map(this::toCommentDtoResponse).collect(Collectors.toList());
    }
}
