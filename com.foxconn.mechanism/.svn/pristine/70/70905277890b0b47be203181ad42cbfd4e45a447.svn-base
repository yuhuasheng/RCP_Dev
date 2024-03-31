package com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory;

import java.math.BigDecimal;

import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 缓冲材类-EPE泡棉/EPS 泡棉/纸托 計算公式
 * @author MW00442
 *
 */
public class BufferMatrix implements MatrixInterfance{
	private ExcelBean bean;
	
	public BufferMatrix(){}
	
	public BufferMatrix(ExcelBean bean){
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
		if(StrUtil.isBlank(this.bean.getWeight())) {
			bean.setCost(null);
			return;
		}
		BigDecimal total = NumberUtil.mul(this.bean.getWeight(),this.bean.getQty());
		BigDecimal costDecimal = NumberUtil.mul(total.toString(),this.bean.getCostFactor());
		this.bean.setCost(NumberUtil.roundStr(costDecimal.doubleValue(), 6));
	}
	
	

}
