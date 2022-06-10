package ru.malygin.indexer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page implements Serializable {

    private Long id;
    private Long siteId;
    private Long appUserId;
    private String path;
    private String content;
    private Integer code;
    private LocalDateTime createTime;

    public boolean notEmpty() {
        return siteId != null && appUserId != null && path != null && content != null && code != null && createTime != null;
    }
}
