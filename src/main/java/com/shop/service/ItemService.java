package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImageDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImage;
import com.shop.repository.ItemImageRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId) {
        List<ItemImage> itemImageList = itemImageRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImageDto> itemImageDtoList = new ArrayList<>();
        for (ItemImage itemImage : itemImageList) {
            ItemImageDto itemImageDto = ItemImageDto.of(itemImage);
            itemImageDtoList.add(itemImageDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImageDtoList(itemImageDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImageFileList) throws Exception {

        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        List<Long> itemImageIds = itemFormDto.getItemImageIds();

        for (int i = 0; i < itemImageFileList.size(); i++) {
            itemImageService.updateItemImage(itemImageIds.get(i), itemImageFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }
}
