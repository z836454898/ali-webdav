package com.github.maruifu.webdav.store;

import com.github.maruifu.webdav.model.result.TFile;
import com.github.maruifu.webdav.model.result.UploadPreResult;
import com.github.maruifu.webdav.model.FileType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 虚拟文件（用于上传时，列表展示）
 */
@Service
public class VirtualTFileService {
    private final Map<String, Map<String, TFile>> virtualTFileMap = new ConcurrentHashMap<>();

    /**
     * 创建文件
     */
    public void createTFile(String parentId, UploadPreResult uploadPreResult) {
        Map<String, TFile> tFileMap = virtualTFileMap.computeIfAbsent(parentId, s -> new ConcurrentHashMap<>());
        tFileMap.put(uploadPreResult.getFile_id(), convert(uploadPreResult));
    }

    public void updateLength(String parentId, String fileId, long length) {
        Map<String, TFile> tFileMap = virtualTFileMap.get(parentId);
        if (tFileMap == null) {
            return;
        }
        TFile tFile = tFileMap.get(fileId);
        if (tFile == null) {
            return;
        }
        tFile.setSize(tFile.getSize() + length);
        tFile.setModified_time(new Date());
    }

    public void remove(String parentId, String fileId) {
        Map<String, TFile> tFileMap = virtualTFileMap.get(parentId);
        if (tFileMap == null) {
            return;
        }
        tFileMap.remove(fileId);
    }

    public Collection<TFile> list(String parentId) {
        Map<String, TFile> tFileMap = virtualTFileMap.get(parentId);
        if (tFileMap == null) {
            return Collections.emptyList();
        }
        return tFileMap.values();
    }

    private TFile convert(UploadPreResult uploadPreResult) {
        TFile tFile = new TFile();
        tFile.setCreated_time(new Date());
        tFile.setId(uploadPreResult.getFile_id());
        tFile.setName(uploadPreResult.getFile_name());
        tFile.setKind(FileType.file.getDescription());
        tFile.setModified_time(new Date());
        tFile.setSize(0L);
        return tFile;
    }
}
