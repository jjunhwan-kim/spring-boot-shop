package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Getter
@Setter
@Table(name= "item_image")
@Entity
public class ItemImage extends BaseEntity {

    @Id
    @Column(name = "item_image_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imageName;

    private String originalImageName;

    private String imageUrl;

    private String representativeImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void setItem(Item item) {
        this.item = item;
        item.getItemImages().add(this);
    }

    public void updateItemImage(String originalImageName, String imageName, String imageUrl) {
        this.originalImageName = originalImageName;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }
}
