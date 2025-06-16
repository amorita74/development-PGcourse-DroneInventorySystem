package com.digitalojt.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalojt.web.consts.DeleteFlagConsts;
import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.entity.StockList;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.PartsInfoForm;
import com.digitalojt.web.repository.StockListInfoRepository;
import com.digitalojt.web.repository.StockListSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockListService extends AbstractService{
	
	// MessageSourceの依存性注入（DI）
	@Autowired
	private MessageSource messageSource;

	private final StockListInfoRepository repository;
	
	/**	
	 * 論理フラグが 0 の部品カテゴリー情報を取得（ID 昇順）
	 * @return
	 */
	public List<StockList> getStockListData() {

	//ログ取得開始
	logStart(LogMessage.HTTP_GET);
	//ログ取得終了
	logEnd(LogMessage.HTTP_GET);	
		
		return repository.findByDeleteFlagOrderByStockIdAsc(DeleteFlagConsts.ACTIVE);
		
	}
	
	/**	
	 * 部品在庫IDに合致する部品カテゴリー情報を取得
	 * @param categoryId
	 * @return
     */	

	public StockList getStockListDataWithId(int stockId) {

	//ログ取得開始
	logStart(LogMessage.HTTP_GET);
	//ログ取得終了
	logEnd(LogMessage.HTTP_GET);
		
		return repository.findById(stockId).get();
				
	}
	
	//検索処理パターンを振り分ける
	public List<StockList> search(Integer categoryId, String stockName, Integer stockAmount, String comparisonType, Integer deleteFlag) {

	//ログ取得開始
	logStart(LogMessage.HTTP_GET);
	//ログ取得終了
	logEnd(LogMessage.HTTP_GET);
		
	    Specification<StockList> spec = Specification
	            .where(StockListSpecification.hasCategoryId(categoryId))
	            .and(StockListSpecification.nameContains(stockName))
	            .and(StockListSpecification.amountMatches(stockAmount, comparisonType));

	        return repository.findAll(spec);
		
	}
	
		
	
	/*	
	 * 部品在庫情報を新規登録する
	 * @param form
	*/	

	@Transactional
	public void registerStockList(@Valid PartsInfoForm form) {
	
	//ログ取得開始
	logStart(LogMessage.HTTP_POST);
		
	try {

		// 存在する場合は、重複登録例外をスロー
		StockList entity = repository.getByStockName(form.getStockName());

		if(entity != null) {
			
			//エラーログ出力
			logException(LogMessage.HTTP_POST, ErrorMessage.DATA_DUPLICATE_ERROR_MESSAGE);

			throw new DataIntegrityViolationException(
					//DATA_DUPLICATE_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
					messageSource.getMessage(ErrorMessage.DATA_DUPLICATE_ERROR_MESSAGE, null, Locale.getDefault()));
			
		}
		
		// 部品在庫情報テーブルのIDは、自動採番であるため、IDのセットは行わない。
		// また、フォームからIDが送られてきた場合は不正な操作の可能性があるため、登録処理を行わないようにする。
		if(form.getStockId() != null) {
			
			//エラーログ出力
			logException(LogMessage.HTTP_POST, ErrorMessage.INVALID_REGISTRATION_ERROR_MESSAGE);

			throw new InvalidInputException(
					//INVALID_REGISTRATION_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
					messageSource.getMessage(ErrorMessage.INVALID_REGISTRATION_ERROR_MESSAGE, null, Locale.getDefault()));
			
		}
		
		StockList registerEntity = new StockList();
		registerEntity.setCategoryId(form.getCategoryId());
		registerEntity.setStockName(form.getStockName());
		registerEntity.setCenterId(form.getCenterId());
		registerEntity.setDescription(form.getStockDescription());
		registerEntity.setAmountValue(form.getStockAmounts());		
		registerEntity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		registerEntity.setCreateDate(currentTimestamp);
		registerEntity.setUpdateDate(currentTimestamp);
		
		//DB登録処理実施
		repository.save(registerEntity);


	} catch (Exception e) {
		
		//例外キャッチログ取得
		logError(LogMessage.HTTP_POST,e);
		
		//ロールバックされずにコミットされるリスクがあるので再スローして、呼び出し元に伝える
		throw e;

	} finally {
		
		//ログ取得終了
		logEnd(LogMessage.HTTP_POST);
		
	}

}
			
	/**
	 * 部品在庫情報を更新する
	 * 
	 * @pram form
	 */	
	
	@Transactional
	public void updateStockList(@Valid PartsInfoForm form) {

	//ログ取得開始
	logStart(LogMessage.HTTP_POST);

	try {
		Optional<StockList> optional = repository.findById(form.getStockId());
		
		 // 存在しない場合は例外をスロー
		if (!optional.isPresent()) {
			
		    // ログ出力
			logException(LogMessage.HTTP_POST, ErrorMessage.INVALID_UPDATE_ERROR_MESSAGE);

		    throw new EntityNotFoundException(
		        messageSource.getMessage(ErrorMessage.INVALID_UPDATE_ERROR_MESSAGE, null, Locale.getDefault())
		    );
		}
		
		// START -- 障害ID:006対応：更新時、名称重複登録チェック処理を追加 -- 
		// 重複チェック
		if (repository.existsByStockNameAndStockIdNot(form.getStockName(), form.getStockId())) {
		    throw new IllegalArgumentException(
		        messageSource.getMessage(ErrorMessage.DATA_DUPLICATE_ERROR_MESSAGE,
		            new Object[]{form.getStockName()}, Locale.getDefault()));
		}
		// END -- 障害ID:006対応 -- 
		
		 // 中身を取り出す
		 StockList entity = optional.get();
		
		 Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		 entity.setUpdateDate(currentTimestamp);
		
		// 削除か更新かで処理を分ける
	    if(form.getDeleteFlag()) {

	    	//ログ
	    	logStart(LogMessage.FLAG_DELETE);
	    	
	    	// 削除の場合
	    	entity.setDeleteFlag(DeleteFlagConsts.DELETED);
		
	    } else {
		
	    	//ログ
	    	logStart(LogMessage.FLAG_ACTIVE);

	    	// 更新の場合
	    	entity.setStockName(form.getStockName());
	    	
	    	//START -- 障害ID:0005対応：更新する項目を追加 --
	    	entity.setCategoryId(form.getCategoryId()); 		//カテゴリーID
	    	entity.setCenterId(form.getCenterId()); 			//センターID
	    	entity.setDescription(form.getStockDescription());	//説明
	    	entity.setAmountValue(form.getStockAmounts());		//個数
	    	//END -- 障害ID:0005対応 --

	    	entity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		
	    }
	    	
	    	//DB更新/削除処理実施
	    	repository.save(entity);

	}catch(Exception e) {
		
		//例外キャッチログ取得
		logError(LogMessage.HTTP_POST,e);
		
		//ロールバックされずにコミットされるリスクがあるので再スローして、呼び出し元に伝える
		throw e;

		
	}finally {
		
		//ログ取得終了	
		logEnd(LogMessage.HTTP_POST);
		
	}
	
}
	

}
