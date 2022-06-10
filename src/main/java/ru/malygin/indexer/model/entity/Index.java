package ru.malygin.indexer.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("_index")
public class Index implements BaseEntity {
    @Id
    private Long id;
    private Long siteId;
    private Long appUserId;
    private Double rank;
    private String pagePath;
    private String word;

    @Override
    public boolean hasRequiredField() {
        //  @formatter:off
        return siteId != null
                && appUserId != null
                && rank != null
                && pagePath != null
                && word != null;
        //  @formatter:on
    }
}
