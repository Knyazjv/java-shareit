package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.enumBooking.BookingStatus;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.MappingComment;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.MappingItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final MappingItem mappingItem;
    private final MappingComment mappingComment;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private static final String ITEM_NOT_FOUND = "Вещь не найдена, itemId: ";

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = userService.getUserById(userId);
        return mappingItem.toDto(itemRepository.save(mappingItem.toItem(null, user, itemDto)));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + itemId));
        if (!item.getUser().getId().equals(userId)) {
            throw new NotFoundException("У пользователя не найдена вещь, itemId: " + itemId);
        }
        Item newItem = mappingItem.toItem(itemId, user,
                applyUpdateToItemDto(itemDto, mappingItem.toDto(item)));
        return mappingItem.toDto(itemRepository.save(newItem));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemInfoDto getItemInfoDtoById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + itemId));
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<Booking> bookings = bookingRepository.findAllByItem_IdAndItem_User_Id(itemId, userId,
                Sort.by("start").descending());
        if (userId.equals(item.getUser().getId())) {
            return mappingItem.toItemInfoDto(item, bookings, comments);
        } else {
            return mappingItem.toItemInfoDto(item, new ArrayList<>(), comments);
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + itemId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemInfoDto> getItemsUser(Long userId) {
        userService.getUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByItem_User_Id(userId,
                                                    Sort.by("start").descending());
        List<Item> items = itemRepository.findAllByUserId(userId);
        List<Comment> comments = commentRepository.findAllByItem_IdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return itemRepository.findAllByUserId(userId).stream()
                .map(item -> mappingItem.toItemInfoDto(item, getBookingsByItemId(item.getId(), bookings),
                        getCommentsByItemId(item.getId(), comments)))
                .sorted(Comparator.comparing(ItemInfoDto::getId))
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text).stream()
                .map(mappingItem::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDtoResponse createComment(Long userId, Long itemId, CommentDtoRequest comment) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND + itemId));
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings = bookingRepository
                .findAllByBooker_IdAndItem_IdAndStatusAndStartBefore(userId,
                        itemId,
                        BookingStatus.APPROVED,
                        dateTime);
        if (bookings.isEmpty()) {
            throw new CommentException("Пользователь не брал вещь в аренду");
        }
        return mappingComment.toCommentDtoResponse(commentRepository
                .save(mappingComment.toComment(null, comment, user, item, LocalDateTime.now())));
    }

    private ItemDto applyUpdateToItemDto(ItemDto newItemDto, ItemDto itemDto) {
        newItemDto.setId(itemDto.getId());
        if (newItemDto.getName() == null) {
            newItemDto.setName(itemDto.getName());
        }
        if (newItemDto.getDescription() == null) {
            newItemDto.setDescription(itemDto.getDescription());
        }
        if (newItemDto.getAvailable() == null) {
            newItemDto.setAvailable(itemDto.getAvailable());
        }
        return newItemDto;
    }

    private List<Booking> getBookingsByItemId(Long itemId, List<Booking> bookings) {
         return bookings.stream()
                 .filter((booking -> booking.getItem().getId().equals(itemId)))
                 .collect(Collectors.toList());
    }

    private List<Comment> getCommentsByItemId(Long itemId, List<Comment> comments) {
        return comments.stream()
                .filter((comment -> comment.getItem().getId().equals(itemId)))
                .collect(Collectors.toList());
    }
}