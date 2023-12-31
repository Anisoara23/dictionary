package com.example.dictionary.application.batch.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class WordInfo {

    @Pattern(regexp = "^[a-zA-Z]+$",
            message = "Word must not be empty and must contain only letters")
    private String name;

    @NotEmpty
    private String category;

    @NotEmpty
    private String definition;

    private String example;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public String toString() {
        return "WordInfo{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", definition='" + definition + '\'' +
                ", example='" + example + '\'' +
                '}';
    }
}
