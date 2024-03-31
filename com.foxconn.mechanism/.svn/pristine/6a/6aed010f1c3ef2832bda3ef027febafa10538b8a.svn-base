package com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory;

import java.math.BigDecimal;

import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 天地蓋計算方式
 * @author MW00442
 *
 */
public class CoversMatrix implements MatrixInterfance{
	private ExcelBean bean;
	
	public CoversMatrix(){}
	
	public CoversMatrix(ExcelBean bean){
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
		if(StrUtil.isBlank(this.bean.getLength()) || StrUtil.isBlank(this.bean.getWidth()) ||  StrUtil.isBlank(this.bean.getHeight())) {
			bean.setCost(null);
			return;
		}
		BigDecimal num1 = NumberUtil.add(this.bean.getHeight() , this.bean.getLength());
		BigDecimal num2 = NumberUtil.add(this.bean.getHeight() , this.bean.getWidth());
		double price = NumberUtil.div(NumberUtil.mul(num1,num2).doubleValue(), 1000000D);
		BigDecimal total = NumberUtil.mul(String.valueOf(price),this.bean.getQty());
		BigDecimal costDecimal = NumberUtil.mul(total.toString(),this.bean.getCostFactor());
		this.bean.setCost(NumberUtil.roundStr(costDecimal.doubleValue(), 6));
	}
	
	

}
