package com.rainsoul.teamforge.importuser;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserInfoTable {

    @ExcelProperty("学号")
    private String studentID;

    @ExcelProperty("昵称")
    private String username;
}
