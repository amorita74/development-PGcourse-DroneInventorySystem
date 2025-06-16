package com.digitalojt.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.digitalojt.web.entity.StockList;

@Repository
public interface StockListInfoRepository extends JpaRepository<StockList, Integer>,
JpaSpecificationExecutor<StockList> {
	
	/**	
	 * 論理フラグが 0 の部品在庫情報を取得（ID 昇順）
	 */

	List<StockList> findByDeleteFlagOrderByStockIdAsc(int deleteFlag);

	List<StockList> findByCategoryIdAndStockNameAndAmountValue(int categoryId, String stockName,int stockAmount);
	
	/**	
	 * 在庫部品名に合致する部品在庫情報を取得
	 * @param categoryName
	 * @return
	 */	
	
	StockList getByStockName(String stockName);

	
	// START -- 障害ID:006対応：更新時、名称重複登録チェック処理を追加 -- 
	boolean existsByStockNameAndStockIdNot(String stockName, Integer stockId);
	// END -- 障害ID:006対応 -- 
}
