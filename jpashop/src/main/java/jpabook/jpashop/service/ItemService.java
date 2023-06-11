package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회작업이 더 많기 떄문에 전체적으로 readOnly = true 를 걸어주고, save 메소드에만 readOnly = false 처리
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional // readOnly = false 처리 (조회작업이 아니기 떄문)
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
       return itemRepository.findOne(itemId);
    }
}
