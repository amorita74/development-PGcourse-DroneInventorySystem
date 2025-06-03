package com.digitalojt.web.form;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.digitalojt.web.consts.ErrorMessage;

import lombok.Data;

/*
部品在庫一覧 画面の検索キーフォームクラス
*/

@Data
public class StockListSearchForm {

		
	//部品カテゴリーID	
	private Integer categoryId;
	
	// 部品在庫名
	@Size(max = 20, message = ErrorMessage.CATEGORY_NAME_INVALID_LENGTH)
	@Pattern(regexp = "^[^{}()'*;$&=]*$", message = ErrorMessage.INVALID_INPUT_ERROR_MESSAGE)		
    private String stockName;

    // 個数   
    private Integer stockAmount;

    // 個数オプション
    private String comparisonType;

    // 削除フラグ   
    private Integer deleteFlag;

}
