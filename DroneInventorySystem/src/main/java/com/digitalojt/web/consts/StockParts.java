package com.digitalojt.web.consts;

/**
 * 登録部品情報に関するEnum
 * 
 * @author akira morita
 *
 */

public enum StockParts {

	NAME("名称"),
	DESCRIPTION("説明"),
	AMOUNTS("個数");

	private final String stockPartsItem;

	StockParts(String name) {
		this.stockPartsItem = name;
	}

	public String getName() {
		return stockPartsItem;
	}
}