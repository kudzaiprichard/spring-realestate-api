package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Bookmark;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@Data
@SuperBuilder
public class BookmarkResponse extends Bookmark {
    private Integer userId;
    private Integer propertyId;

    public BookmarkResponse(Bookmark bookmark) {
        BeanUtils.copyProperties(bookmark, this);
        init();
    }

    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
