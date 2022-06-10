package ru.malygin.indexer.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("_stat")
public class Statistic implements BaseEntity {

    @Id
    private Long id;
    private Long siteId;
    private Long appUserId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer parsedPages;
    private Integer savedLemmas;
    private Integer createdIndexes;

    public Statistic clone() {
        //  @formatter:off
        Statistic crawlerStatistic;
        try {
            crawlerStatistic = (Statistic) super.clone();
        } catch (CloneNotSupportedException e) {
            crawlerStatistic = new Statistic(this.id,
                                             this.siteId,
                                             this.appUserId,
                                             this.startTime,
                                             this.endTime,
                                             this.parsedPages,
                                             this.savedLemmas,
                                             this.createdIndexes);
        }
        return crawlerStatistic;
        //  @formatter:on
    }

    @Override
    public boolean hasRequiredField() {
        //  @formatter:off
        return siteId != null
                && appUserId != null
                && startTime != null
                && endTime != null
                && parsedPages != null
                && savedLemmas != null
                && createdIndexes != null;
        //  @formatter:on
    }
}
