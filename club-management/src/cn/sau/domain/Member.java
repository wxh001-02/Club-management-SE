package cn.sau.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Member {
    private Long id;
    private Long clubId;
    private Long userId;
    private Integer status;
    private LocalDate joinDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String username;
    private String clubName;
}
