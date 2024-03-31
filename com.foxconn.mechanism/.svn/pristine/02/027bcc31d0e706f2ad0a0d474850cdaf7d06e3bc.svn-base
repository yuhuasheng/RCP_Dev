package com.foxconn.mechanism.batchChangePhase.window;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public interface TransformPhaseService {

    // 初始化数据
    void initData();

    // 不合规的数据
    List<PhaseBean> getNotComplianceData();

    // 合规的数据
    List<PhaseBean> getComplianceData();

    // 执行转阶段
    void doTransformPhase(List<PhaseBean> data, Progress progress);

    // 图纸数据
    List<BlueprintBean> getBlueprintData();

    // 更新图纸标题栏阶段属性
    void doUpdatePhase();

    // 导出不符合的数据
    void exportNotComplianceData(List<PhaseBean> data, String savePath);

    // 导出转换阶段后的数据
    void exportAfterTransformPhaseData(List<PhaseBean> data, String savePath);

    // 导出图纸
    void exportBlueprint(List<BlueprintBean> data, String savePath);

    boolean checkModelType(); // 核对模型的类型
}
