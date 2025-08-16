package com.ace.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum PredefinedFolders {

    IMAGES("images"),
    DOCUMENTS("documents"),
    ARCHIVES("archives"),
    SPREADSHEETS("spreadsheets");

    private final String type;

    PredefinedFolders(String type) {
        this.type = type;
    }

    public static List<String> getAllTypes() {
        return List.of(
                IMAGES.getType(),
                DOCUMENTS.getType(),
                ARCHIVES.getType(),
                SPREADSHEETS.getType());
    }
}
