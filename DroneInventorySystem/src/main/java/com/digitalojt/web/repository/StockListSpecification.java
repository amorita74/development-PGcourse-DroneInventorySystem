package com.digitalojt.web.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.digitalojt.web.consts.AmountValueComparisonType;
import com.digitalojt.web.consts.StockListFields;
import com.digitalojt.web.entity.StockList;

/**
 * StockList に対する検索条件（Specification）を定義するクラス。
 */
public class StockListSpecification {

    /**
     * カテゴリIDで完全一致検索する Specification。
     * 
     * @param categoryId 検索対象のカテゴリID（null許容）
     * @return Specification（条件が null の場合は無視）
     */
    public static Specification<StockList> hasCategoryId(final Integer categoryId) {
    	
        return new Specification<StockList>() {
        	
            @Override
            public Predicate toPredicate(Root<StockList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            	
                // categoryId が null の場合は条件を追加しない（null を返す）
                if (categoryId == null) {
                	
                    return null;
                    
                }
                
                return cb.equal(root.get(StockListFields.CATEGORY_ID), categoryId);
                
            }
        };
    }

    /**
     * 商品名で部分一致検索する Specification。
     *
     * @param stockName 検索キーワード（null/空文字は無視）
     * @return Specification
     */
    public static Specification<StockList> nameContains(final String stockName) {
    	
        return new Specification<StockList>() {
        	
            @Override
            public Predicate toPredicate(Root<StockList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            	
                if (stockName == null || stockName.isEmpty()) {
                	
                    return null;
                }
                
                return cb.like(root.get(StockListFields.STOCK_NAME), "%" + stockName + "%");
            }
        };
    }

    /**
     * 在庫数に対する条件検索（=, >=, <=）を行う Specification。
     *
     * @param amount 在庫数
     * @param comparisonType 比較種別（eq=等しい, ge=以上, le=以下）
     * @return Specification（条件が不正な場合は null）
     */
    public static Specification<StockList> amountMatches(final Integer amount, final String comparisonType) {
    	
        return new Specification<StockList>() {
        	
        	//org.springframework.data.jpa.domain.Specification<T> インターフェース
            @Override
            //検索条件として必要な値がそろっていないなら、その条件は使わないでスルーする
            public Predicate toPredicate(Root<StockList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            	
                if (amount == null || comparisonType == null) {
                    return null;
                    
                }
                
                switch (comparisonType) {
                
                    case AmountValueComparisonType.EQUAL ://個数とamountValueの値が等しい
                        return cb.equal(root.get(StockListFields.AMOUNT_VALUE), amount);
                        
                    case AmountValueComparisonType.GREATHER_THAN://個数よりamountValueの値が大きい
                        return cb.greaterThanOrEqualTo(root.get(StockListFields.AMOUNT_VALUE), amount);
                        
                    case AmountValueComparisonType.LESS_THAN://個数よりamountValueの値が小さい
                        return cb.lessThanOrEqualTo(root.get(StockListFields.AMOUNT_VALUE), amount);
                        
                    default:
                        // 想定外の比較タイプは無視
                    	
                        return null;
                }
            }
        };
    }
}
