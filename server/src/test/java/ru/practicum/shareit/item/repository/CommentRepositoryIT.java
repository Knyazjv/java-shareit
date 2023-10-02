package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryIT {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user1 = new User(1L, "user", "mail@mail.ru");
    User user2 = new User(2L, "name", "ya@mail.ru");
    User user3 = new User(3L, "3name3", "yandex@mail.ru");
    Item item1 = new Item(1L, user1, "1iTem1", "1description1", true, null);
    Item item2 = new Item(2L, user1, "3name2", "2description2ITEm", true, null);
    Comment comment1 = new Comment(1L, "comment", user1, item2, LocalDateTime.now());
    Comment comment2 = new Comment(2L, "2comment2", user3, item2, LocalDateTime.now());
    Comment comment3 = new Comment(3L, "2comment2", user3, item1, LocalDateTime.now());

    @BeforeEach
    void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        itemRepository.save(item2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
    }

    @Test
    void findAllByItem_IdInTest() {
        List<Comment> comments = commentRepository.findAllByItem_IdIn(List.of(2L));
        assertEquals(comments.size(), 2);
        equalsComment(comment1,comments.get(0));
        equalsComment(comment2,comments.get(1));
    }

    @Test
    void findAllByItem_IdTest() {
        List<Comment> comments = commentRepository.findAllByItem_Id(1L);
        assertEquals(comments.size(), 1);
        equalsComment(comment3,comments.get(0));
    }

    void equalsComment(Comment comment, Comment otherComment) {
        assertEquals(comment.getId(), otherComment.getId());
        assertEquals(comment.getText(), otherComment.getText());
        assertEquals(comment.getUser().getId(), otherComment.getUser().getId());
        assertEquals(comment.getItem().getId(), otherComment.getItem().getId());
        assertEquals(comment.getCreated(), otherComment.getCreated());
    }
}