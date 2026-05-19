package cn.sau.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Club {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private LocalDate createdDate;
    private Integer status;
    private Long presidentId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String presidentName;
}
