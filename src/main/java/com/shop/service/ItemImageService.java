package com.shop.service;

import com.shop.entity.ItemImage;
import com.shop.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemImageService {

    @Value("${itemImageLocation}")
    private String itemImageLocation;

    private final ItemImageRepository itemImageRepository;

    private final FileService fileService;

    public void saveItemImage(ItemImage itemImage, MultipartFile itemImageFile) throws Exception {
        String originalImageName = itemImageFile.getOriginalFilename();
        String imageName = "";
        String imageUrl = "";

        if (!StringUtils.isEmpty(originalImageName)) {
            imageName = fileService.uploadFile(itemImageLocation, originalImageName, itemImageFile.getBytes());
            imageUrl = "/images/item/" + imageName;
        }

        itemImage.updateItemImage(originalImageName, imageName, imageUrl);
        itemImageRepository.save(itemImage);
    }
}
