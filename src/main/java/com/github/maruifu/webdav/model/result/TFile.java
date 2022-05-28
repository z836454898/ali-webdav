package com.github.maruifu.webdav.model.result;

import java.util.Date;

/**
 * 从云上获取的文件信息模板
 */
public class TFile {
    private Date created_time; //创建时间
    private String domain_id;
    private String drive_id;
    private String encrypt_mode;
    private String id;
    private Boolean hidden;
    private String name; //文件名称
    private String file_name;
    private String parent_file_id;
    private String starred;
    private String status;
    private String kind; //文件类型
    private Date modified_time; //修改时间
    private String web_content_link; //文件链接
    private Long size; //文件大小
    private String download_url;

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getDomain_id() {
        return domain_id;
    }

    public void setDomain_id(String domain_id) {
        this.domain_id = domain_id;
    }

    public String getDrive_id() {
        return drive_id;
    }

    public void setDrive_id(String drive_id) {
        this.drive_id = drive_id;
    }

    public String getEncrypt_mode() {
        return encrypt_mode;
    }

    public void setEncrypt_mode(String encrypt_mode) {
        this.encrypt_mode = encrypt_mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_file_id() {
        return parent_file_id;
    }

    public void setParent_file_id(String parent_file_id) {
        this.parent_file_id = parent_file_id;
    }

    public String getStarred() {
        return starred;
    }

    public void setStarred(String starred) {
        this.starred = starred;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Date getModified_time() {
        return modified_time;
    }

    public String getWeb_content_link() {
        return web_content_link;
    }

    public void setWeb_content_link(String web_content_link) {
        this.web_content_link = web_content_link;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setModified_time(Date modified_time) {
        this.modified_time = modified_time;
    }
}
