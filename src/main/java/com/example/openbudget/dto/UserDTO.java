package com.example.openbudget.dto;

import lombok.*;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private String botUserChatId;
    private String message;
}
