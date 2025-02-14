package com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory;

import java.math.BigDecimal;

import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 *  EPE膜、PE膜計算公式
 * @author MW00442
 *
 */
public class MembraneBagMatrix implements MatrixInterfance{
	private ExcelBean bean;
	
	public MembraneBagMatrix(){}
	
	public MembraneBagMatrix(ExcelBean bean){
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
		if(StrUtil.isBlank(this.bean.getLength()) || StrUtil.isBlank(this.bean.getWidth()) ||  StrUtil.isBlank(this.bean.getThickness())) {
			bean.setCost(null);
			return;
		}
		BigDecimal num1 = NumberUtil.mul(this.bean.getLength(),this.bean.getWidth());
		BigDecimal num2 = NumberUtil.mul(num1.toString(),this.bean.getThickness());
		double price = NumberUtil.div(NumberUtil.mul(num2.toString(),this.bean.getDensity()).doubleValue() * 2 , 1000000000D);
		BigDecimal total = NumberUtil.mul(String.valueOf(price),this.bean.getQty());
		BigDecimal costDecimal = NumberUtil.mul(total.toString(),this.bean.getCostFactor());
		this.bean.setCost(NumberUtil.roundStr(costDecimal.doubleValue(), 6));
	}
	
	

}
