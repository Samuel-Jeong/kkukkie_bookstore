package dev.jmagni.model.item.base;

import dev.jmagni.model.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Item extends BaseEntity {

    @Id
    //@GeneratedValue
    private String id;

    private String name;

    private ItemType itemType;

    private Integer price;

    private Integer count;

    public Item(String id, String name, ItemType itemType, Integer price, Integer count) {
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        this.price = price;
        this.count = count;
    }

    public boolean isNew() {
        return getCreateDateTime() == null;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

