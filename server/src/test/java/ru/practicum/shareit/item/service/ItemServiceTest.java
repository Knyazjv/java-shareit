package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.MappingBooking;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    UserService userService;
    CommentRepository commentRepository;
    ItemService itemService;

    MappingBooking mappingBooking = new MappingBooking();
    MappingComment mappingComment = new MappingComment();
    MappingItem mappingItem = new MappingItem(mappingBooking, mappingComment);

    User user1 = new User(1L, "user", "mail@mail.ru");
    User user2 = new User(2L, "name", "ya@mail.ru");
    Item item1 = new Item(1L, user1, "1iTem1", "1description1", true, null);
    Item item2 = new Item(2L, user2, "3name2", "2description2ITEm", true, null);
    ItemDto itemDto1 = new ItemDto(1L, "1iTem1", "1description1", true, null);
    ItemDto itemDto2 = new ItemDto(2L, "3name2", "2description2ITEm", true, null);
    ItemDto itemDto3 = new ItemDto(null, "name", null, true, null);
    Booking booking = new Booking();

    Long userId = 1L;
    Long itemId = 1L;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userService = mock(UserService.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, mappingItem,
                mappingComment, userService, commentRepository);
    }

    @Test
    void createItemTest() {
        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto response = itemService.createItem(itemDto1, userId);
        equalsItemsDto(itemDto1, response);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto1, userId));
        verify(userService, times(1)).getUserById(any());
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void updateItemTest() {
        when(itemRepository.save(any())).thenReturn(item1);
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        ItemDto response = itemService.updateItem(itemDto3, userId, itemId);
        equalsItemsDto(itemDto1, response);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_whenOwnerItemNotFound_thenNotFoundExceptionThrown() {
        long id = 2;
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto3, id, itemId));
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void updateItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto3, userId, itemId));
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto3, userId, itemId));
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void getItemInfoDtoByIdTest() {
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItem_Id(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItem_IdAndItem_User_Id(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        ItemInfoDto response = itemService.getItemInfoDtoById(itemId, userId);
        ItemInfoDto itemInfoDto = mappingItem.toItemInfoDto(item1, new ArrayList<>(), new ArrayList<>());
        equalsItemsInfoDto(itemInfoDto, response);
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void getItemInfoDtoById_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.getItemInfoDtoById(itemId, userId));
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void getItemsUserWithPaginationTest() {
        when(userService.getUserById(any())).thenReturn(user1);
        when(bookingRepository.findAllByItem_User_Id(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        when(itemRepository.findAllByUserId(any(),any())).thenReturn(List.of(item1, item2));
        when(commentRepository.findAllByItem_IdIn(any())).thenReturn(new ArrayList<>());

        List<ItemInfoDto> dtoList = List.of(mappingItem.toItemInfoDto(item1, new ArrayList<>(), new ArrayList<>()),
                mappingItem.toItemInfoDto(item2, new ArrayList<>(), new ArrayList<>()));

        List<ItemInfoDto> response = itemService.getItemsUserWithPagination(userId, PageRequest.of(0, 10));
       assertEquals(dtoList.size(), response.size());
        equalsItemsInfoDto(dtoList.get(0), response.get(0));
        equalsItemsInfoDto(dtoList.get(1), response.get(1));
        verify(itemRepository, times(1)).findAllByUserId(any(), any());
    }

    @Test
    void getItemsUserWithPagination_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService
                .getItemsUserWithPagination(userId, PageRequest.of(0, 10)));
        verify(itemRepository, times(0)).findAllByUserId(any(), any());
    }

    @Test
    void searchItemsWithPaginationTest() {
        List<Item> itemDtos = List.of(item1, item2);
        when(itemRepository.searchItems(any(), any())).thenReturn(itemDtos);

        List<ItemDto> response = itemService.searchItemsWithPagination("text", PageRequest.of(0, 10));
        equalsItemsDto(itemDto1, response.get(0));
        equalsItemsDto(itemDto2, response.get(1));
        verify(itemRepository, times(1)).searchItems(any(), any());
    }

    @Test
    void searchItemsWithPagination_whenTextIsEmpty_thenReturnedEmptyList() {
        List<ItemDto> response = itemService.searchItemsWithPagination("", PageRequest.of(0, 10));
        assertTrue(response.isEmpty());
        verify(itemRepository, times(0)).searchItems(any(), any());
    }

    @Test
    void createCommentTest() {
        Comment comment = new Comment(1L, "comment", user1, item2, LocalDateTime.now());
        CommentDtoResponse commentDtoResponse = mappingComment.toCommentDtoResponse(comment);

        when(userService.getUserById(any())).thenReturn(user1);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndStartBefore(any(),any(),any(),any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDtoResponse response = itemService.createComment(userId, itemId, new CommentDtoRequest("comment"));
        assertEquals(commentDtoResponse.getId(), response.getId());
        assertEquals(commentDtoResponse.getText(), response.getText());
        assertEquals(commentDtoResponse.getAuthorName(), response.getAuthorName());
        assertEquals(commentDtoResponse.getCreated(), response.getCreated());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void createComment_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenThrow(NotFoundException.class);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));

        assertThrows(NotFoundException.class, () -> itemService
                .createComment(userId, itemId, new CommentDtoRequest("comment")));
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemRepository.findById(any())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService
                .createComment(userId, itemId, new CommentDtoRequest("comment")));
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void createComment_whenBookingNotFound_thenCommentExceptionThrown() {
        when(userService.getUserById(any())).thenReturn(user1);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndStartBefore(any(),any(),any(),any()))
                .thenReturn(new ArrayList<>());

        assertThrows(CommentException.class, () -> itemService
                .createComment(userId, itemId, new CommentDtoRequest("comment")));
        verify(commentRepository, times(0)).save(any());
    }

    private void equalsItemsDto(ItemDto itemDto, ItemDto otherItemDto) {
        assertEquals(itemDto.getId(), otherItemDto.getId());
        assertEquals(itemDto.getName(), otherItemDto.getName());
        assertEquals(itemDto.getDescription(), otherItemDto.getDescription());
    }

    private void equalsItemsInfoDto(ItemInfoDto item, ItemInfoDto otherItem) {
        assertEquals(item.getId(), otherItem.getId());
        assertEquals(item.getName(), otherItem.getName());
        assertEquals(item.getDescription(), otherItem.getDescription());
        assertEquals(item.getAvailable(), otherItem.getAvailable());
    }
}