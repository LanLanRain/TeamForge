package com.rainsoul.teamforge.importuser;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

/**
 * TableListener类，实现了ReadListener接口，用于监听UserInfoTable的读取操作。
 */
@Slf4j
public class TableListener implements ReadListener<UserInfoTable> {

    /**
     * 当读取到UserInfoTable数据时被调用。
     *
     * @param userInfoTable 用户信息表，包含读取到的用户信息。
     * @param analysisContext 分析上下文，提供了分析过程中的上下文信息。
     */
    @Override
    public void invoke(UserInfoTable userInfoTable, AnalysisContext analysisContext) {
        System.out.println("userInfoTable = " + userInfoTable);
    }

    /**
     * 在所有UserInfoTable数据都解析完成之后调用。
     *
     * @param analysisContext 分析上下文，提供了分析过程中的上下文信息。
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("已经解析完成");
    }
}
