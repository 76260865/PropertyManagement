package com.jason.property.model;

public class Invoice {
	private String number;
	private String status;
	private String orderAmount;
	private String amount;
	private String perAmount;
	private String accountAmount;
	private String payType;
	private String payDate;
	private String payId;
	private String employeeName;
	private String notes;
	private String name;
	private String roomId;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPerAmount() {
		return perAmount;
	}

	public void setPerAmount(String perAmount) {
		this.perAmount = perAmount;
	}

	public String getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(String accountAmount) {
		this.accountAmount = accountAmount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Invoice() {

	}
	

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public Invoice(String number, String status, String orderAmount,
			String amount, String perAmount, String accountAmount,
			String payType, String payDate, String employeeName, String notes) {
		super();
		this.number = number;
		this.status = status;
		this.orderAmount = orderAmount;
		this.amount = amount;
		this.perAmount = perAmount;
		this.accountAmount = accountAmount;
		this.payType = payType;
		this.payDate = payDate;
		this.employeeName = employeeName;
		this.notes = notes;
	}

}
