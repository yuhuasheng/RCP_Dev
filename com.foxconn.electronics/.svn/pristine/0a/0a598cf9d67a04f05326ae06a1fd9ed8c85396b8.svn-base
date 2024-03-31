package com.foxconn.electronics.matrixbom.export.domain;

import java.lang.reflect.Field;

import com.foxconn.electronics.util.TCPropName;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class DTLenovoPSUBean {
	
	@TCPropName(cell = 0)
	private Integer item;
	
	@TCPropName(value = "d9_Chassis", cell = 1)
	private String chassis;
	
	@TCPropName(value = "d9_Rating", cell = 8)
	private String rating;
	
	@TCPropName(value = "d9_Type", cell = 9)
	private String type;
	
	@TCPropName(value = "d9_CustomerPN", cell = 10)
	private String customerPN;
	
	@TCPropName(value = "d9_EnglishDescription", cell = 11)
	private String enDescription;
	
	@TCPropName(value = "d9_ManufacturerID", cell = 12)
	private String manufacturerID;
	
	@TCPropName(value = "item_id", cell = 15)
	private String foxconnPN;
	
	@TCPropName(value = "d9_ManufacturerPN", cell = 16)
	private String manufacturerPN;
	
	@TCPropName(value = "d9_MeetTCO90", cell = 18)
	private String meetTCO90;

	
	public DTLenovoPSUBean(TCComponentBOMLine bomLine, int index) {
		try {
			tcPropMapping(this, bomLine);
			this.setItem(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public String getChassis() {
		return chassis;
	}

	public void setChassis(String chassis) {
		this.chassis = chassis;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCustomerPN() {
		return customerPN;
	}

	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}

	public String getEnDescription() {
		return enDescription;
	}

	public void setEnDescription(String enDescription) {
		this.enDescription = enDescription;
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getFoxconnPN() {
		return foxconnPN;
	}

	public void setFoxconnPN(String foxconnPN) {
		this.foxconnPN = foxconnPN;
	}

	public String getManufacturerPN() {
		return manufacturerPN;
	}

	public void setManufacturerPN(String manufacturerPN) {
		this.manufacturerPN = manufacturerPN;
	}

	public String getMeetTCO90() {
		return meetTCO90;
	}

	public void setMeetTCO90(String meetTCO90) {
		this.meetTCO90 = meetTCO90;
	}

	@Override
	public String toString() {
		return "DTLenovoPSUBean [chassis=" + chassis + ", rating=" + rating + ", type=" + type + ", customerPN="
				+ customerPN + ", enDescription=" + enDescription + ", manufacturerID=" + manufacturerID
				+ ", foxconnPN=" + foxconnPN + ", manufacturerPN=" + manufacturerPN + ", meetTCO90=" + meetTCO90 + "]";
	}
	
	
	public <T> T tcPropMapping(T bean, TCComponentBOMLine tcbomLine) throws TCException, IllegalArgumentException, IllegalAccessException {
		if (bean != null && tcbomLine != null) {
			TCComponentItemRevision itemRev = tcbomLine.getItemRevision();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
				if (tcPropName != null) {
					String tcAttrName = tcPropName.value();
					if (!tcAttrName.isEmpty()) {
						Object value = "";
						if (tcAttrName.startsWith("bl")) {
							value = tcbomLine.getProperty(tcAttrName);
						} else {
							value = itemRev.getProperty(tcAttrName);
						}
						
						if (fields[i].getType() == Integer.class) {
							if (value.equals("") || value == null) {
								value = null;
							} else {
								value = Integer.parseInt((String) value);
							}
						}
						fields[i].set(bean, value);
					}
				}
			}
		}
		return bean;
	}
}
