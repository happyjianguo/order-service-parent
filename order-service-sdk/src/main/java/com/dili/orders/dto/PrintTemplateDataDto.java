package com.dili.orders.dto;

public class PrintTemplateDataDto<T> {

	public PrintTemplateDataDto() {
	}

	public PrintTemplateDataDto(String template, T data) {
		super();
		this.template = template;
		this.data = data;
	}

	private String template;
	private T data;

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
