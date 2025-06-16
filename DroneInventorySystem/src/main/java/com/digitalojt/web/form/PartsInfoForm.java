package com.digitalojt.web.form;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.digitalojt.web.consts.ErrorMessage;

import lombok.Data;

/*
部品在庫一覧 登録画面のフォームクラス
*/

@Data
//@PartsCategoryFormValidator バリデーションつける
public class PartsInfoForm {

	/*		
	部品在庫ID	
	 */
	private Integer stockId;

	/*		
	部品カテゴリーID	
	 */
	private Integer categoryId;
	
	
	// 部品在庫名
	@NotBlank(message = ErrorMessage.STOCKPARTS_NAME_REQUIRED)
	@Size(max = 20, message = ErrorMessage.STOCKPARTS_NAME_INVALID_LENGTH)
	@Pattern(regexp = "^[^\\s{}()'*;$&=]*$", message = ErrorMessage.STOCKPARTS_INPUT_NAME_ERROR_MESSAGE)	
	private String stockName;

	/*		
	在庫センターID	
	 */
	private Integer centerId;

	// 部品説明
	@Size(max = 100, message = ErrorMessage.STOCKPARTS_DESCRIPTION_INVALID_LENGTH)
	@Pattern(regexp = "^[^\\s{}()'*;$&=]*$", message = ErrorMessage.STOCKPARTS_INPUT_DESCRIPTION_ERROR_MESSAGE)	
	private String stockDescription;
	

	/*		
	部品在庫数	
	 */
	private Integer stockAmounts;

	/*		
	部品在庫数オプション(以上or以下)	
	 */
	private String amountType;

	/*		
	作成日付		
	 */
	private Timestamp createDate;

	/*			
	更新日付		
	 */
	private Timestamp updateDate;	
	
	//削除フラグ
	private Boolean deleteFlag;

}
