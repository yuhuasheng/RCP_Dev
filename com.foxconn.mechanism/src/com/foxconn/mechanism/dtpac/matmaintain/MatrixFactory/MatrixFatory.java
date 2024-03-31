package com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory;

import java.math.BigDecimal;

import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;
import com.foxconn.tcutils.util.CommonTools;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class MatrixFatory {
	
	
	public static void calculateCost(ExcelBean bean) {
		if(StrUtil.isNotBlank(bean.getCost()) && StrUtil.isBlank(bean.getCostFactor())) { // 费用不为空并且成本因子为空
			if(bean.getQty().contains("*")) {
				BigDecimal totalCostDecimal = new BigDecimal(1);
				String[] split = bean.getQty().split("\\*");
				for (int i = 1; i < split.length; i++) {
					String str = CommonTools.replaceBlank(split[i]);
					if (CommonTools.isNotEmpty(str)) {
						BigDecimal bigDecimal = new BigDecimal(str);
						totalCostDecimal = totalCostDecimal.multiply(bigDecimal);
					}
				}
				
				String totalQty =  totalCostDecimal.stripTrailingZeros().toPlainString();
				bean.setQty(totalQty);
				
				bean.setCost(NumberUtil.roundStr(NumberUtil.mul(bean.getQty(),bean.getCost()).doubleValue(), 6));
			}
			return;
		}
		try {
			switch (bean.getMatType()) {
			case "紙箱類":
				if("套箱".equals(bean.getMatName())) {
					switch (bean.getType()) {
					case "RSC箱":
						new RscBoxMatrix(bean).calculateCost();
						break;
					case "HSC箱":
						new HscBoxMatrix(bean).calculateCost();
						break;
					case "圍板":
						new CoamingMatrix(bean).calculateCost();
						break;
					case "天地蓋":
						new CoversMatrix(bean).calculateCost();
						break;
					case "紙刀卡":
					case "紙隔板":
						new BoxMatrix(bean).calculateCost();
						break;
					case "EPE隔板":
						new PartitionMatrix(bean).calculateCost();
						break;
					default:
						break;
					}
				}else {
					new RscBoxMatrix(bean).calculateCost();
				}
				break;
			case "袋類":
				if("靜電屏蔽袋".equals(bean.getMatName())) {
					new ShieldingBagMatrix(bean).calculateCost();
				}else {
					new MembraneBagMatrix(bean).calculateCost();
				}
				break;
			case "附件盒類":
				new BoxMatrix(bean).calculateCost();
				break;
			case "緩衝材類":
				new BufferMatrix(bean).calculateCost();
				break;
			case "打包類":
				switch (bean.getMatName()) {
				case "紙隔板":
					new BoxMatrix(bean).calculateCost();
					break;
				case "紙護角":
					new PaperCornerMatrix(bean).calculateCost();
					break;
				default:
					bean.setCost(NumberUtil.roundStr(NumberUtil.mul(bean.getQty(),bean.getCostFactor()).doubleValue(), 6));
					break;
				}
				break;
			case "標籤類":
				new BoxMatrix(bean).calculateCost();
				break;
			case "Manual類":
				bean.setCost(NumberUtil.roundStr(NumberUtil.mul(bean.getQty(),bean.getCostFactor()).doubleValue(), 6));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			bean.setCost(null);
		}
		
	}

}
