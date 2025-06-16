package com.digitalojt.web.consts;

/**
 * エラーメッセージ定数クラス
 * 
 * @author dotlife
 *
 */
public class ErrorMessage {
	
	// ログイン情報の入力に誤りがあった場合に、出力するエラーメッセージのID
	public static final String  LOGIN_WRONG_INPUT = "login.wrongInput";

	// データが空の場合のエラーメッセージ
	public static final String DATA_EMPTY_ERROR_MESSAGE = "data.empty";
	
	// すべての項目が空の場合のエラーメッセージ
	public static final String ALL_FIELDS_EMPTY_ERROR_MESSAGE = "allField.empty";

	// 空文字検索に関するエラーメッセージ
	public static final String UNEXPECTED_INPUT_ERROR_MESSAGE = "unexpected.input";

	// 不正な文字列を使用した検索に関するエラーメッセージ
	public static final String INVALID_INPUT_ERROR_MESSAGE = "{invalid.input}";

	// 文字超過に関するエラーメッセージ
	public static final String CENTER_NAME_LENGTH_ERROR_MESSAGE = "centerName.length.wrongInput";
	
	// 空欄の場合のエラーメッセージキー
	public static final String CATEGORY_NAME_REQUIRED = "category.name.required";
	
	// 禁止文字チェック（{ } ; = $ & ）が含まれている場合のエラーメッセージキー
	public static final String CATEGORY_NAME_FORBIDDEN = "category.name.forbidden";
	
	// 文字数が制限を超えた場合のエラーメッセージキー
	public static final String CATEGORY_NAME_INVALID_LENGTH = "category.name.length";
	
	// 操作履歴画面の操作時刻に関するエラーメッセージ
	public static final String OPERATION_DATE_FIELD_ERROR_MESSAGE = "operationLog.operationDateField.empty";

	//登録データの登録に成功した場合のメッセージ
	public static final String SUCCESS_REGISTER_MESSAGE = "register.success";
	public static final String SUCCESS_REGISTERPARTS_MESSAGE = "registerParts.success";
	
	//登録データが重複している場合のエラーメッセージ
	public static final String DATA_DUPLICATE_ERROR_MESSAGE = "data.duplicate";
	
	//不正な登録を検知した場合のエラーメッセージ
	public static final String INVALID_REGISTRATION_ERROR_MESSAGE = "invalid.registration";

	//更新に成功した場合のメッセージ
	public static final String SUCCESS_UPDATE_MESSAGE = "update.success";
	public static final String SUCCESS_UPDATEPARTS_MESSAGE = "updateParts.success";
		
	//更新に失敗した場合のエラーメッセージ
	public static final String INVALID_UPDATE_ERROR_MESSAGE = "invalid.update";
	
	//更新対象が見つからなかった場合のエラーメッセージ
	public static final String NOT_FOUND_UPDATE_ERROR_MESSAGE = "notfound.update";

	//検索対象が見つからなかった場合のエラーメッセージ
	public static final String NOT_FOUND_SEARCH_ERROR_MESSAGE = "data.empty";
	
	//検索時の予期せぬエラーメッセージ
	public static final String UNEXPECTED_SEARCH_ERROR_MESSAGE = "unexpectedError.search";
	
	//部品在庫登録時の空欄を検知した際のエラーメッセージ
	public static final String STOCKPARTS_NAME_REQUIRED = "{stockParts.name.required}";
	public static final String STOCKPARTS_INPUT_NAME_ERROR_MESSAGE = "{stockParts.name.invalid.input}";
	public static final String STOCKPARTS_INPUT_DESCRIPTION_ERROR_MESSAGE = "{stockParts.description.invalid.input}";
	public static final String STOCKPARTS_NAME_INVALID_LENGTH = "{stockParts.name.length.wrongInput}";
	public static final String STOCKPARTS_DESCRIPTION_INVALID_LENGTH = "{stockParts.description.length.wrongInput}";

}
