package historicaldataimport.domain;

public class ExcelRowData {

	private int rowNumber;//行
	private String customerId;//客户ID
	private String customerName;//客户名
	private String seriesId;//系列ID
	private String seriesName;//系列名
	private String productLineId;//产品线ID
	private String productLineName;//产品线名
	private String projectId;//专案ID
	private String projectName;//专案名
	private String projectLevel;//专案等级
	private String projectPhase;//专案阶段
	
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
	public String getSeriesName() {
		return seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public String getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(String productLineId) {
		this.productLineId = productLineId;
	}
	public String getProductLineName() {
		return productLineName;
	}
	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectLevel() {
		return projectLevel;
	}
	public void setProjectLevel(String projectLevel) {
		this.projectLevel = projectLevel;
	}
	public String getProjectPhase() {
		return projectPhase;
	}
	public void setProjectPhase(String projectPhase) {
		this.projectPhase = projectPhase;
	}
	
	@Override
	public String toString() {
		return "ExcelRowData [rowNumber=" + rowNumber + ", customerId=" + customerId + ", customerName=" + customerName
				+ ", seriesId=" + seriesId + ", seriesName=" + seriesName + ", productLineId=" + productLineId
				+ ", productLineName=" + productLineName + ", projectId=" + projectId + ", projectName=" + projectName
				+ ", projectLevel=" + projectLevel + ", projectPhase=" + projectPhase + "]";
	}
	
}
