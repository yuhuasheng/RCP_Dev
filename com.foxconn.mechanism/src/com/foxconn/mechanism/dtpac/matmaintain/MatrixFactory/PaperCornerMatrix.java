package com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory;

import java.math.BigDecimal;

import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 *   纸刀卡、纸隔板、盒子、内衬、纸卡、纸隔板、标签計算公式
 * @author MW00442
 *
 */
public class PaperCornerMatrix implements MatrixInterfance{
	private ExcelBean bean;
	
	public PaperCornerMatrix(){}
	
	public PaperCornerMatrix(ExcelBean bean){
		this.bean = bean;
	}
	
	public ExcelBean getBean() {
		return bean;
	}

	public void setBean(ExcelBean bean) {
		this.bean = bean;
	}
	@Override
	public void calculateCost() {
		if(StrUtil.isBlank(this.bean.getLength())) {
			bean.setCost(null);
			return;
		}
		double price = NumberUtil.div(Double.parseDouble(this.bean.getLength()) , 1000D);
		BigDecimal total = NumberUtil.mul(String.valueOf(price),this.bean.getQty());
		BigDecimal costDecimal = NumberUtil.mul(total.toString(),this.bean.getCostFactor());
		this.bean.setCost(NumberUtil.roundStr(costDecimal.doubleValue(), 6));
	}
	
	

}
