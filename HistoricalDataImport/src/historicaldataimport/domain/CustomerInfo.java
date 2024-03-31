package historicaldataimport.domain;

public class CustomerInfo {

	private String customerId;//客户ID
	private String customerName;//客户名
	private int resultsCount;//查询结果的数量
	
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
	public int getResultsCount() {
		return resultsCount;
	}
	public void setResultsCount(int resultsCount) {
		this.resultsCount = resultsCount;
	}
	
	@Override
	public String toString() {
		return "CustomerInfo [customerId=" + customerId + ", customerName=" + customerName + ", resultsCount="
				+ resultsCount + "]";
	}
		
}
