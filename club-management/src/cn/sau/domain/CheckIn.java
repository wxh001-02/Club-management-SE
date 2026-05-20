package cn.sau.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckIn {
    private Long id;
    private Long activityId;
    private Long userId;
    private LocalDateTime checkInTime;
    private LocalDateTime createTime;

    private String username;
    private String activityTitle;
}
