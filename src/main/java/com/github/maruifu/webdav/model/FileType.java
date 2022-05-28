package com.github.maruifu.webdav.model;


import lombok.Data;

/**
 * 用于描述文件类型
 */
public enum  FileType {
    folder("drive#folder"), file("drive#file");
    
    FileType(final String description) {
        this.description=description;
    }
    
    private final String description;
    
    public String getDescription(){
        return this.description;
    }
    
    public static void main(String[] args) {
        System.out.println(FileType.file.getDescription());
    }
}
