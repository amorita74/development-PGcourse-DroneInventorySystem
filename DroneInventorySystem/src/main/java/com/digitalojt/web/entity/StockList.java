package com.digitalojt.web.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_info")
public class StockList {

	/**
	 * 部品在庫ID
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id", nullable = false)
	private int stockId;

	/**
	 * 部品カテゴリーID
	 */
	@Column(name = "category_id")
	private int categoryId;

	/**
	 * 部品名
	 */
	@Column(name = "name")
	private String stockName;

	/**
	 * センターID（保管場所）
	 */
	@Column(name = "center_id")
	private int centerId;


	/**
	 * 説明
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 個数
	 */
	@Column(name = "amount")
	private int amountValue;
	
	/**
	 * 論理削除フラグ (0:未削除, 1:削除済)
	 */
    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;


	/*		
	作成日付		
	 */
	private Timestamp createDate;

	/*			
	更新日付		
	 */
	private Timestamp updateDate;
	

}
