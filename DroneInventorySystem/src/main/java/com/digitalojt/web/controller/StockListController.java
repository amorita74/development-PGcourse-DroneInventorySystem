package com.digitalojt.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.digitalojt.web.consts.ErrorMessage;
import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.ModelAttributeContents;
import com.digitalojt.web.consts.StockListFields;
import com.digitalojt.web.consts.UrlConsts;
import com.digitalojt.web.entity.CategoryInfo;
import com.digitalojt.web.entity.CenterInfo;
import com.digitalojt.web.entity.StockList;
import com.digitalojt.web.exception.InvalidInputException;
import com.digitalojt.web.form.PartsCategoryForm;
import com.digitalojt.web.form.PartsInfoForm;
import com.digitalojt.web.form.StockListSearchForm;
import com.digitalojt.web.service.CenterInfoService;
import com.digitalojt.web.service.PartsCategoryService;
import com.digitalojt.web.service.StockListService;

import lombok.RequiredArgsConstructor;

/**
 * 在庫一覧画面コントローラークラス
 * 
 * @author your name
 *
 */
@Controller
@RequiredArgsConstructor
public class StockListController extends AbstractController {

	// 部品カテゴリーサービス
	private final StockListService service;

	// Categoryのサービス
	private final PartsCategoryService categoryService;

	// Centerのサービス
	private final CenterInfoService centerService;
	
	
	 // メッセージソースを定義
	@Autowired
    private final MessageSource messageSource;	
	
	/**
	 * 初期表示
	 *@param model Modelオブジェクト
	 *@return String(path)
	 */
    
	@GetMapping(UrlConsts.STOCK_LIST)
	public String index(@RequestParam(required = false) Integer deleteFlag, Model model) {

	//ログ取得
	logStart(LogMessage.HTTP_GET);
    
    	// Thymeleafが th:object="${stockListSearchForm}" で参照できるように、
    	// 明示的に新しい StockListSearchForm オブジェクトをモデルに追加。
    	model.addAttribute(ModelAttributeContents.STOCK_LIST_SEARCH_FORM, new StockListSearchForm());
   
    	// 共通処理呼び出し
    	setCommonModel(model);
       
    	//部品在庫一覧情報  取得処理（部品在庫一覧管理画面に表示するデータを取得）
    	List<StockList> searchResults = service.getStockListData();
	    	
    	//画面表示用に商品情報をセット
    	model.addAttribute(ModelAttributeContents.STOCK_LIST,searchResults);		

	//ログ取得終了
    logEnd(LogMessage.HTTP_GET);
	
	return UrlConsts.STOCK_LIST_INDEX;

	}
		
	/**
	 * 	部品在庫一覧 登録画面表示
	 * @param model Modelオブジェクト
	 * @return String(Viewの名前:部品在庫一覧 登録画面)
	 */

	/**	
	 * 	入力フォームの情報を部品在庫一覧へ登録
	 * 			
	 * 	@param model Modelオブジェクト
	 * 	@param form フォームオブジェクト
	 * 	@param bindingResult バリデーション結果
	 * 	@param redirectAttributesリダイレクト時にフラッシュスコープでデータを渡すためのオブジェクト
	 * 	@param string リダイレクト先のURL（入力エラー時は登録画面,成功時は管理画面）
	 */
	
	//検索処理
	@GetMapping(UrlConsts.STOCK_LIST_SEARCH)
	public String search(@Valid StockListSearchForm form, BindingResult bindingResult, Model model) {
		
	//ログ取得
	logStart(LogMessage.HTTP_GET);
	
	//START -- 障害ID:002対応：バリデーションエラーチェックを追加 -- 
	if (bindingResult.hasErrors()) {
	    setCommonModel(model);
	    model.addAttribute(ModelAttributeContents.STOCK_LIST_SEARCH_FORM, form);
	    
        // エラーメッセージを手動で詰め替える（最初のエラーだけ例として表示）
        String error = bindingResult.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                //保険で入れておく
                .orElse(ErrorMessage.UNEXPECTED_SEARCH_ERROR_MESSAGE);

        model.addAttribute(ModelAttributeContents.ERROR_MSG , error);
        
	    return UrlConsts.STOCK_LIST_INDEX;
	}
	//END -- 障害ID:002対応： -- 
	    
	    model.addAttribute(ModelAttributeContents.STOCK_LIST_SEARCH_FORM, form);
	    
	    // 共通処理呼び出し
	    setCommonModel(model);
	    
	    List<StockList> results = service.search(
	            form.getCategoryId(),
	            form.getStockName(),
	            form.getStockAmount(),
	            form.getComparisonType(),
	            form.getDeleteFlag());
	    
	    
	    //START -- 障害ID:001対応：検索結果が0件だった時のメッセージ出力処理を実装 --
	    if (results.isEmpty()) {
	    	
	    	String message = messageSource.getMessage(ErrorMessage.NOT_FOUND_SEARCH_ERROR_MESSAGE, null, Locale.getDefault());
	        model.addAttribute(ModelAttributeContents.ERROR_MSG, message);

	    } else {

		    model.addAttribute(ModelAttributeContents.STOCK_LIST, results);

	    }
	    //END -- 障害ID:001対応 --

	 //ログ取得終了
	 logEnd(LogMessage.HTTP_GET);
        
	 return UrlConsts.STOCK_LIST_INDEX;
	 
	}
	
	// 部品在庫一覧 登録画面を表示する
	@GetMapping(UrlConsts.STOCK_LIST_REGISTER)
	public String viewRegister(Model model,PartsCategoryForm form) {
		
	//ログ取得
	logStart(LogMessage.HTTP_GET);
		
		//カテゴリー一覧を取得する 削除フラグ:0
		List<CategoryInfo> categoryList = categoryService.getCategoryInfoData();
		model.addAttribute(ModelAttributeContents.CATEGORY_LIST,categoryList);		

		//在庫センター情報を取得する
		List<CenterInfo> centerList = centerService.getCenterInfoData();
		model.addAttribute(ModelAttributeContents.CENTER_INFO_LIST,centerList);		
	    
	    model.addAttribute(ModelAttributeContents.STOCK_PARTS_FORM, new PartsInfoForm());

	 //ログ取得終了
	 logEnd(LogMessage.HTTP_GET);

	    // 修正：部品在庫一覧画面に返却 redirectだと無限ループ発生
	    return UrlConsts.STOCK_LIST_REGISTER; 
		
	}
	
	// 登録処理
	@PostMapping(UrlConsts.STOCK_LIST_REGISTER)
	public String register(Model model, @Valid PartsInfoForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
	//ログ取得
	logStart(LogMessage.HTTP_POST);

		// 入力時のバリデーションチェック
		if(bindingResult.hasErrors()) {

			//START -- 障害ID:003,004対応：バリデーションエラーメッセージを改修 -- 
		    // 共通メソッド呼び出し：バリデーションエラーメッセージ出力処理		
			String errorMessage = buildValidationErrorMessage(bindingResult);
			// エラーメッセージを画面に渡す
		    redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG, errorMessage);
		    //END -- 障害ID:003,004対応 -- 
			
			// ログ出力:バリデーションエラー
			logValidationError(LogMessage.HTTP_POST,errorMessage);	
		
		
			// 部品在庫一覧画面にリダイレクト ERROR_MSGを渡す
			return "redirect:" + UrlConsts.STOCK_LIST_REGISTER; //部品在庫一覧 登録画面にリダイレクト
		
		}
		
		// 登録データに重複あればエラーメッセージを返す
	    try {
	    	
	    	service.registerStockList(form);  // 登録処理

	        //正常処理メッセージ
	        redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG,
	        		messageSource.getMessage(ErrorMessage.SUCCESS_REGISTERPARTS_MESSAGE,
	        		new Object[]{form.getStockName()}, 		// プレースホルダーにカテゴリ名を渡す
	        		Locale.getDefault()));
				
	        
	        return "redirect:" + UrlConsts.STOCK_LIST; //部品在庫一覧 画面にリダイレクト

	    } catch (DataIntegrityViolationException e) {
	    	
	    	//例外処理 ログ取得
	    	logError(LogMessage.HTTP_POST, e);
	    		    	
	    	// 重複があればここに到達
	        redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG, e.getMessage());  // フラッシュスコープにエラーメッセージを追加

	        return "redirect:" + UrlConsts.STOCK_LIST_REGISTER; // 部品在庫一覧 画面にリダイレクト

	    } catch (InvalidInputException e) {
	    	
	    	//例外処理 ログ取得
	    	logError(LogMessage.HTTP_POST, e);
	    		    	
	    	// 無効な入力の場合
	        redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG, e.getMessage());

	        return "redirect:" + UrlConsts.STOCK_LIST_REGISTER;

	      //予期しない例外処理を追加  
	    } catch (Exception e) {
	    	
	    	//例外処理 ログ取得
	    	logError(LogMessage.HTTP_POST, e);
	    		    	
	    	redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
	    			messageSource.getMessage(ErrorMessage.INVALID_UPDATE_ERROR_MESSAGE,
	    			null, Locale.getDefault()));


	    	//エラーコントローラに渡す
	    	return "redirect:" + UrlConsts.ERROR;
	    	
	    }finally {

	   	 	//ログ取得終了
	   	 	logEnd(LogMessage.HTTP_POST);
	    	
	    }
				

	}	
	
	/**	
	 *	部品在庫一覧 更新/削除画面表示
	 * 	
	 *	@param categoryId 部品カテゴリーID（更新/削除対象）
     *  @param model Modelオブジェクト
	 *	@return String(Viewの名前：部品在庫一覧画面)
	
	*/		
	// 更新/削除画面を表示する
	@GetMapping(UrlConsts.STOCK_LIST_UPDATE_WITHID)
	public String viewUpdate(Model model, @PathVariable(StockListFields.STOCK_ID) Integer stockId, PartsInfoForm form,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

	//ログ取得
	logStart(LogMessage.HTTP_GET);

	    StockList stockList = service.getStockListDataWithId(stockId); // サービス層の既存メソッドを使用

		//カテゴリー一覧を取得する 削除フラグ:0
		List<CategoryInfo> categoryList = categoryService.getCategoryInfoData();
		model.addAttribute(ModelAttributeContents.CATEGORY_LIST,categoryList);		

		//在庫センター情報を取得する
		List<CenterInfo> centerList = centerService.getCenterInfoData();
		model.addAttribute(ModelAttributeContents.CENTER_INFO_LIST,centerList);		

	    //対象となる部品在庫データを取得
	    if (stockList != null) {
	        form.setStockId(stockList.getStockId());
	        form.setStockName(stockList.getStockName());	        
	        form.setCategoryId(stockList.getCategoryId());
	        form.setCenterId(stockList.getCenterId());	        
	        form.setStockDescription(stockList.getDescription());
	        form.setStockAmounts(stockList.getAmountValue());
	        form.setDeleteFlag(stockList.getDeleteFlag() == 1); // int の deleteFlag を boolean の form に設定

	        model.addAttribute(ModelAttributeContents.STOCK_PARTS_FORM, form);

	    	//ログ取得終了
	    	logEnd(LogMessage.HTTP_GET);

	        return UrlConsts.STOCK_LIST_UPDATE;
	        
	    } else {
	    	

			// 修正：エラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
					messageSource.getMessage(ErrorMessage.NOT_FOUND_UPDATE_ERROR_MESSAGE,
							null, Locale.getDefault()));
	    	
			//ログ取得終了
			logEnd(LogMessage.HTTP_GET);

	    	return "redirect:" + UrlConsts.STOCK_LIST_INDEX;

	    }
	        
	}
	
	/**	
	 *	部品在庫一覧 更新処理
	 * 	
     *  @param model Modelオブジェクト
	 *	@return String(Viewの名前：部品在庫一覧画面)
	
	*/		
	//更新処理
	@PatchMapping(UrlConsts.STOCK_LIST_UPDATE)
	public String update(Model model, @Valid PartsInfoForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
				
	//ログ取得
	logStart(LogMessage.HTTP_GET);
		
		// 入力値のバリデーションチェック
		if(bindingResult.hasErrors()) {
			
		  //START -- 障害ID:004対応：バリデーションエラーメッセージを改修 -- 
		    // 共通メソッド呼び出し：バリデーションエラーメッセージ出力処理		
			String errorMessage = buildValidationErrorMessage(bindingResult);			
			// エラーメッセージを画面に渡す
		    redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG, errorMessage);
		    //END -- 障害ID:0004対応 -- 

			// フォームデータも渡す
		    redirectAttributes.addFlashAttribute(ModelAttributeContents.STOCK_PARTS_FORM, form);

			// 部品在庫一覧 更新/削除画面にリダイレクト
			return "redirect:" + UrlConsts.STOCK_LIST_REGISTER;
												
 		}
		
		// START -- 障害ID:006対応：更新時、名称重複登録チェック処理を追加 -- 
		try {
		    service.updateStockList(form);
		} catch (IllegalArgumentException e) {
		    redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG, e.getMessage());
		    redirectAttributes.addFlashAttribute(ModelAttributeContents.STOCK_PARTS_FORM, form);
		    return "redirect:" + UrlConsts.STOCK_LIST_REGISTER;
		}
	    // END -- 障害ID:0006対応 -- 

		
		// 部品在庫情報を更新		
	    service.updateStockList(form);
	    
		//部品在庫一覧情報  取得処理（部品在庫一覧管理画面に表示するデータを取得）
		List<StockList> searchResults = service.getStockListData();
		//画面表示用に商品情報をセット
		model.addAttribute(ModelAttributeContents.STOCK_LIST,searchResults);		

		// 正常処理メッセージ
		// 更新成功メッセージをフラッシュスコープに設定		
 		redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG,
				messageSource.getMessage(ErrorMessage.SUCCESS_UPDATEPARTS_MESSAGE,
                new Object[]{form.getStockName()}, // プレースホルダーに部品在庫名を渡す
                Locale.getDefault()));		
		
 	//ログ取得終了
 	logEnd(LogMessage.HTTP_GET);

 	return "redirect:" + UrlConsts.STOCK_LIST;  // 部品在庫一覧画面にリダイレクト

	}
	
	
	//ヘルパーメソッドとして共通化
	private void setCommonModel(Model model) {
	    // カテゴリー
	    List<CategoryInfo> categoryList = categoryService.getCategoryInfoData();
	    model.addAttribute("categoryList", categoryList);
	    Map<Integer, String> categoryMap = categoryList.stream()
	        .collect(Collectors.toMap(CategoryInfo::getCategoryId, CategoryInfo::getCategoryName));
	    model.addAttribute("categoryMap", categoryMap);

	    // センター
	    List<CenterInfo> centerList = centerService.getCenterInfoData();
	    model.addAttribute("centerList", centerList);
	    Map<Integer, String> centerMap = centerList.stream()
	        .collect(Collectors.toMap(CenterInfo::getCenterId, CenterInfo::getCenterName));
	    model.addAttribute("centerMap", centerMap);
	}
	
	
	
    //START -- 障害ID:001,002,003対応：バリデーションエラーメッセージを改修 --
	  //共通メソッドとして以下を追加
	  // バリデーションエラーメッセージを出力するメソッド
    public static String buildValidationErrorMessage(BindingResult bindingResult) {
    	
    	// バリデーションエラーメッセージ取得をredirectAttributesに追加			

        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("<br>"));
    }
   //END -- 障害ID:001,002,003対応 -- 

	
}
