package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceImplIT {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getItemsUserWithPaginationTest() {
        Long userId = 1L;
        UserDto userDto = new UserDto(null, "user", "mail@mail.ru");
        ItemDto itemDto1 = new ItemDto(null, "1iTem1", "1description1", true, null);
        ItemDto itemDto2 = new ItemDto(null, "3name2", "2description2ITEm", true, null);
        userService.createUser(userDto);
        itemService.createItem(itemDto1, userId);
        itemService.createItem(itemDto2, userId);
        itemDto1.setId(userId);
        itemDto2.setId(2L);
        List<ItemInfoDto> itemInfoDtos = itemService
                .getItemsUserWithPagination(userId, PageRequest.of(0, 10));

        equalsItemsInfoDto(itemDto1, itemInfoDtos.get(0));
        equalsItemsInfoDto(itemDto2, itemInfoDtos.get(1));
    }

    private void equalsItemsInfoDto(ItemDto item, ItemInfoDto otherItem) {
        assertEquals(item.getId(), otherItem.getId());
        assertEquals(item.getName(), otherItem.getName());
        assertEquals(item.getDescription(), otherItem.getDescription());
        assertEquals(item.getAvailable(), otherItem.getAvailable());
    }
}