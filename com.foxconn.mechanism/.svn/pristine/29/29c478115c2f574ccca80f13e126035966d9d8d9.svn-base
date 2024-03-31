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
/**
 * @author MW00442
 *
 */
public class BoxMatrix implements MatrixInterfance{
	
	private ExcelBean bean;
	
	public BoxMatrix(){}
	
	public BoxMatrix(ExcelBean bean){
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
		if(StrUtil.isBlank(this.bean.getLength()) || StrUtil.isBlank(this.bean.getWidth())) {
			bean.setCost(null);
			return;
		}
		double price = NumberUtil.div(NumberUtil.mul(this.bean.getLength(),this.bean.getWidth()).doubleValue() , 1000000D);
		BigDecimal total = NumberUtil.mul(String.valueOf(price),this.bean.getQty());
		BigDecimal costDecimal = NumberUtil.mul(total.toString(),this.bean.getCostFactor());
		this.bean.setCost(NumberUtil.roundStr(costDecimal.doubleValue(), 6));
	}
	
	

}
