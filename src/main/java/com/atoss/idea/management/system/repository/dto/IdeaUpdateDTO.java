package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.repository.entity.Status;

import java.util.List;

public class IdeaUpdateDTO {
    private String title;
    private Status status;
    private List<Category> categoryList;
    private Image image;
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
