package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemService itemService;

    @Test
    public void 상품_저장() throws Exception {
        //given
        Album album = new Album();
        album.setName("albumName");

        //when
        itemService.saveItem(album);

        //then
        assertEquals(album, itemService.findOne(album.getId()));
     }

     @Test(expected = NotEnoughStockException.class)
     public void 상품_재고_변경() throws Exception {
         //given
         Album album = new Album();
         album.setStockQuantity(1);

         //when
         album.removeStock(2);

         //then
         fail("테스트 실패");
      }

}