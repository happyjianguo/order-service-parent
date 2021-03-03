package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;

import java.math.BigDecimal;

public class WeighingCollectionStatementDto extends WeighingStatement {
	// 结算单id
	private Long weighingStatementId;

	// 结算单code
	private String weighingStatementCode;

	// 过磅单id
	private Long weighingBillId;

	// 过磅单code
	private String weighingBillCode;

	// 商品名称
	private String goodsName;

	// 净重
	private BigDecimal netWeightY;

	// 件数 unit_amount
	private BigDecimal unitAmountY;
	// 货款

	private BigDecimal tradeAmountY;

	// 单价
	private BigDecimal unitPriceY;

	// 人工费
	private BigDecimal staffChargesY;

	// 包装费
	private BigDecimal packingChargesY;

	// 代收费
	private BigDecimal collectionChargesY;

	// 合计
	private BigDecimal totalAmountY;

	// 包装名称
	private String packingTypeName;

	public Long getWeighingStatementId() {
		return weighingStatementId;
	}

	public void setWeighingStatementId(Long weighingStatementId) {
		this.weighingStatementId = weighingStatementId;
	}

	public String getWeighingStatementCode() {
		return weighingStatementCode;
	}

	public void setWeighingStatementCode(String weighingStatementCode) {
		this.weighingStatementCode = weighingStatementCode;
	}

	@Override
	public Long getWeighingBillId() {
		return weighingBillId;
	}

	@Override
	public void setWeighingBillId(Long weighingBillId) {
		this.weighingBillId = weighingBillId;
	}

	public String getWeighingBillCode() {
		return weighingBillCode;
	}

	public void setWeighingBillCode(String weighingBillCode) {
		this.weighingBillCode = weighingBillCode;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public BigDecimal getNetWeightY() {
		return netWeightY;
	}

	public void setNetWeightY(BigDecimal netWeightY) {
		this.netWeightY = netWeightY;
	}

	public BigDecimal getUnitAmountY() {
		return unitAmountY;
	}

	public void setUnitAmountY(BigDecimal unitAmountY) {
		this.unitAmountY = unitAmountY;
	}

	public BigDecimal getUnitPriceY() {
		return unitPriceY;
	}

	public void setUnitPriceY(BigDecimal unitPriceY) {
		this.unitPriceY = unitPriceY;
	}

	public BigDecimal getStaffChargesY() {
		return staffChargesY;
	}

	public void setStaffChargesY(BigDecimal staffChargesY) {
		this.staffChargesY = staffChargesY;
	}

	public BigDecimal getPackingChargesY() {
		return packingChargesY;
	}

	public void setPackingChargesY(BigDecimal packingChargesY) {
		this.packingChargesY = packingChargesY;
	}

	public BigDecimal getCollectionChargesY() {
		return collectionChargesY;
	}

	public void setCollectionChargesY(BigDecimal collectionChargesY) {
		this.collectionChargesY = collectionChargesY;
	}

	public BigDecimal getTotalAmountY() {
		return totalAmountY;
	}

	public void setTotalAmountY(BigDecimal totalAmountY) {
		this.totalAmountY = totalAmountY;
	}

	public BigDecimal getTradeAmountY() {
		return tradeAmountY;
	}

	public void setTradeAmountY(BigDecimal tradeAmountY) {
		this.tradeAmountY = tradeAmountY;
	}

	public String getPackingTypeName() {
		return packingTypeName;
	}

	public void setPackingTypeName(String packingTypeName) {
		this.packingTypeName = packingTypeName;
	}
}
