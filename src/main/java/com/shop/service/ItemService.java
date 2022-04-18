package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImage;
import com.shop.repository.ItemImageRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImageService itemImageService;
    private final ItemImageRepository itemImageRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImageFileList) throws Exception {

        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        for (int i = 0; i < itemImageFileList.size(); i++) {
            ItemImage itemImage = new ItemImage();
            itemImage.setItem(item);
            if (i == 0) {
                itemImage.setRepresentativeImageYesOrNo("Y");
            } else {
                itemImage.setRepresentativeImageYesOrNo("N");
            }
            itemImageService.saveItemImage(itemImage, itemImageFileList.get(i));
        }

        return item.getId();
    }
}
