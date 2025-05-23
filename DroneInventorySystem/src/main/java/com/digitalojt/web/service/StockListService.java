package com.digitalojt.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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
import com.digitalojt.web.entity.StockList;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.PartsInfoForm;
import com.digitalojt.web.repository.StockListInfoRepository;
import com.digitalojt.web.repository.StockListSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockListService {
	
	// MessageSourceの依存性注入（DI）
	@Autowired
	private MessageSource messageSource;

	private final StockListInfoRepository repository;

	/**	
	 * 論理フラグが 0 の部品カテゴリー情報を取得（ID 昇順）
	 * @return
	 */
	public List<StockList> getStockListData() {
		
		return repository.findByDeleteFlagOrderByStockIdAsc(DeleteFlagConsts.ACTIVE);
		
	}
	
	/**	
	 * 部品在庫IDに合致する部品カテゴリー情報を取得
	 * @param categoryId
	 * @return
     */	

	public StockList getStockListData(int stockId) {

		return repository.findById(stockId).get();
		
	}
	
	//検索処理パターンを振り分ける
	public List<StockList> search(Integer categoryId, String stockName, Integer stockAmount, String comparisonType, Integer deleteFlag) {

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
	
		// 存在する場合は、重複登録例外をスロー
		StockList entity = repository.getByStockName(form.getStockName());
		if(entity != null) {
			throw new DataIntegrityViolationException(
					//DATA_DUPLICATE_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
					messageSource.getMessage(ErrorMessage.DATA_DUPLICATE_ERROR_MESSAGE, null, Locale.getDefault()));
		}
		
		// 部品在庫情報テーブルのIDは、自動採番であるため、IDのセットは行わない。
		// また、フォームからIDが送られてきた場合は不正な操作の可能性があるため、登録処理を行わないようにする。
		if(form.getStockId() != null) {
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

		// stockIdはnullであること
		System.out.println("registerEntity = " + registerEntity);
		System.out.println("stockId = " + registerEntity.getStockId());
		System.out.println("createDate = " + registerEntity.getCreateDate());
		System.out.println("updateDate = " + registerEntity.getUpdateDate());
		System.out.println("deleteFlag = " + registerEntity.getDeleteFlag());
		
		//DB登録処理実施
		repository.save(registerEntity);
	}
	
	
	/**
	 * 部品在庫情報を更新する
	 * 
	 * @pram form
	 */	
	
	public void updateStockList(@Valid PartsInfoForm form) {
		
		// 存在しない場合は例外をスロー
		StockList entity = repository.findById(form.getStockId())
				.orElseThrow(() -> new EntityNotFoundException(
						//ININVALID_UPDATE_ERROR_MESSAGEをErrorMessageクラスに登録する必要がある
						messageSource.getMessage(ErrorMessage.INVALID_UPDATE_ERROR_MESSAGE, null, Locale.getDefault())));

		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		entity.setUpdateDate(currentTimestamp);
		
		// 削除か更新かで処理を分ける
		
	if(form.getDeleteFlag()) {

		// 削除の場合
		entity.setDeleteFlag(DeleteFlagConsts.DELETED);
		
	} else {
		
		// 更新の場合
		entity.setStockName(form.getStockName());
		entity.setDeleteFlag(DeleteFlagConsts.ACTIVE);
		
	}

	// stockIdとupDateはnullではないこと
	System.out.println("registerEntity = " + entity);
	System.out.println("stockId = " + entity.getStockId());
	System.out.println("createDate = " + entity.getCreateDate());
	System.out.println("updateDate = " + entity.getUpdateDate());
	System.out.println("deleteFlag = " + entity.getDeleteFlag());

	//DB更新/削除処理実施
	repository.save(entity);
	
	}
	

}
