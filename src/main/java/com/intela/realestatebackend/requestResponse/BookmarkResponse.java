package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Bookmark;
import com.intela.realestatebackend.models.property.Plan;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.awt.print.Book;

@Data
@SuperBuilder
public class BookmarkResponse extends Bookmark {
    private Integer userId;
    private Integer propertyId;
    public BookmarkResponse(Bookmark bookmark){
        BeanUtils.copyProperties(bookmark, this);
    }
    @PostConstruct
    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
