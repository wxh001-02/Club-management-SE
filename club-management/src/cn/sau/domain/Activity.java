package cn.sau.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Activity {
    private Long id;
    private Long clubId;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String checkInCode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String clubName;
}
