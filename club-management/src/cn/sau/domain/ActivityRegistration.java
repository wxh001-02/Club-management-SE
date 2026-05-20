package cn.sau.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityRegistration {
    private Long id;
    private Long activityId;
    private Long userId;
    private LocalDateTime registerTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String activityTitle;
}
