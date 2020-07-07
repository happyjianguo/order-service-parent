package com.dili.order.dto;

/**
 * 费用项，标识买卖家的交易手续费
 * 
 * @author jiang
 *
 */
public class FeeDto {

	/**
	 * 金额
	 */
	private Long amount;

	/**
	 * 费用项类型
	 */
	private Integer type;

	/**
	 * 费用项类型名称
	 */
	private String typeName;

	/**
	 * 费用分买方费用（useFor=1）与卖方费用（useFor=2），且入账至商户收益账户
	 */
	private Integer useFor;

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getUseFor() {
		return useFor;
	}

	public void setUseFor(Integer useFor) {
		this.useFor = useFor;
	}
}
