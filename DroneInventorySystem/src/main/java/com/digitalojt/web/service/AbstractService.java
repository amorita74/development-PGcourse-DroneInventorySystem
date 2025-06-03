package com.digitalojt.web.service;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalojt.web.consts.LogMessage;

/**
 * 抽象サービス
 * ※部品在庫一覧画面のログ取得に使用する
 * ※抽象コントローラ：abstructContorollerと中身は同じ
 *  （setFlashErrorMsgは未使用のため除去）
 */

@Service
public abstract class AbstractService {
	

	// ロガーを共通部品として定義
	private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

	@Autowired
	private HttpServletRequest request; // リクエスト情報を取得

	/**
	 * 現在のメソッド名を取得
	 * @return メソッド名
	 */
	private String getMethodName() {
		return StackWalker.getInstance()
				.walk(frames -> frames.skip(2).findFirst().map(StackWalker.StackFrame::getMethodName))
				.orElse("UnknownMethod");
	}

	/**
	 * 処理開始のログ
	 * 
	 * @param logger ロガーオブジェクト
	 * @param action アクション名
	 */
	protected void logStart(String action) {
		logger.info(String.format(LogMessage.ACCESS_LOG, request.getRequestURI()));
		logger.info(String.format(LogMessage.APP_LOG, action, getMethodName(), LogMessage.PROCESS_START));
	}

	/**
	 * 処理終了のログ
	 * 
	 * @param logger ロガーオブジェクト
	 * @param action アクション名
	 */
	protected void logEnd(String action) {
		logger.info(String.format(LogMessage.APP_LOG, action, getMethodName(), LogMessage.PROCESS_END));
	}

	/**
	 * エラーログ
	 * 
	 * @param logger ロガーオブジェクト
	 * @param action アクション名
	 * @param e 例外オブジェクト
	 */
	protected void logError(String action, Exception e) {
		logger.error(String.format(LogMessage.ERROR_LOG, action, getMethodName(), e));
	}

	/**
	 * クリティカルエラーログ
	 * 
	 * @param logger ロガーオブジェクト
	 * @param action アクション名
	 * @param errorMsg エラーメッセージ
	 */
	protected void logException(String action, String errorMsg) {
		logger.error(String.format(LogMessage.ERROR_LOG, action, getMethodName(), String.valueOf(errorMsg)));
	}

	/**
	 * バリデーションエラーログ
	 * 
	 * @param logger ロガーオブジェクト
	 * @param action アクション名
	 * @param errorMsg エラーメッセージ
	 */
	protected void logValidationError(String action, String errorMsg) {
		logger.error(String.format(LogMessage.ERROR_LOG, action, getMethodName(), errorMsg));
	}


}
