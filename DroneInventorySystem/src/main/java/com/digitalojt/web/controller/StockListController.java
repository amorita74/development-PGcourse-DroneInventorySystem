package com.digitalojt.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

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
    private final MessageSource messageSource;	
	
	/**
	 * 初期表示
	 *@param model Modelオブジェクト
	 *@return String(path)
	 */
    
	@GetMapping(UrlConsts.STOCK_LIST)
	public String index(@RequestParam(required = false) Integer deleteFlag, Model model) {
		//return UrlConsts.STOCK_LIST_INDEX;

	//ログ取得
	logStart(LogMessage.HTTP_GET);
    logEnd(LogMessage.HTTP_GET);
    
    // Thymeleafが th:object="${stockListSearchForm}" で参照できるように、
    // 明示的に新しい StockListSearchForm オブジェクトをモデルに追加。
    model.addAttribute("stockListSearchForm", new StockListSearchForm());

    
    // 共通処理呼び出し
    setCommonModel(model);
    
    
	//部品在庫一覧情報  取得処理（部品在庫一覧管理画面に表示するデータを取得）
	List<StockList> searchResults = service.getStockListData();
	
			//System.out.println("取得件数 = " + searchResults.size()); //デバッグログ
	
	//画面表示用に商品情報をセット
	model.addAttribute(ModelAttributeContents.STOCK_LIST,searchResults);		
       
    		//System.out.println("stockList: " + searchResults); // デバッグ
    
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
	public String search(StockListSearchForm form, Model model, RedirectAttributes redirectAttributes) {

	    System.out.println("検索処理実行");	    
	    System.out.println("部品在庫一覧_検索 受け取ったフォームの内容: " + form);
	    
	    model.addAttribute("stockListSearchForm", form);
	    
	    // 共通処理呼び出し
	    setCommonModel(model);
	    
	    List<StockList> results = service.search(
	            form.getCategoryId(),
	            form.getStockName(),
	            form.getStockAmount(),
	            form.getComparisonType(), // eq / ge / le
	            form.getDeleteFlag()

	        );
	        model.addAttribute(ModelAttributeContents.STOCK_LIST, results);
	 	 
	    return UrlConsts.STOCK_LIST_INDEX;
	}
	
	// 部品在庫一覧 登録画面を表示する
	@GetMapping(UrlConsts.STOCK_LIST_REGISTER)
	public String viewRegister(Model model,PartsCategoryForm form) {
		
		//ログ取得
		logStart(LogMessage.HTTP_GET);
	    logEnd(LogMessage.HTTP_GET);
		
		System.out.println("部品在庫一覧 登録画面表示");
		
		//カテゴリー一覧を取得する 削除フラグ:0
		List<CategoryInfo> categoryList = categoryService.getCategoryInfoData();
		model.addAttribute(ModelAttributeContents.CATEGORY_LIST,categoryList);		

		//在庫センター情報を取得する
		List<CenterInfo> centerList = centerService.getCenterInfoData();
		model.addAttribute(ModelAttributeContents.CENTER_INFO_LIST,centerList);		
	    
	    model.addAttribute("partsInfoForm", new PartsInfoForm());

        return "redirect:" + UrlConsts.STOCK_LIST_REGISTER;  // 部品在庫一覧画面にリダイレクト
		
	}
	
	// 登録処理
	@PostMapping(UrlConsts.STOCK_LIST_REGISTER)
	public String register(Model model, @Valid PartsInfoForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		
		//ログ取得
		logStart(LogMessage.HTTP_GET);
	    logEnd(LogMessage.HTTP_GET);

		System.out.println("部品在庫一覧 登録処理実行");

		// 入力時のバリデーションチェック
		if(bindingResult.hasErrors()) {

			String errorMessage = getValidationErrorMessage(bindingResult, redirectAttributes);
			System.out.println("errorMsg: " + errorMessage);
			
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
				getValidationErrorMessage(bindingResult, redirectAttributes));
		
			// 部品在庫一覧画面にリダイレクト ERROR_MSGを渡す
			return "redirect:" + UrlConsts.STOCK_LIST_REGISTER; //部品在庫一覧 登録画面にリダイレクト
		
		}
		
		// 登録データに重複あればエラーメッセージを返す
	    try {
	    	
			// ここでフォームの内容を出力
		    System.out.println("部品在庫一覧_登録 受け取ったフォームの内容: " + form);

	    	service.registerStockList(form);  // 登録処理

	        //正常処理メッセージ
	        redirectAttributes.addFlashAttribute(ModelAttributeContents.SUCCESS_MSG,
	        		messageSource.getMessage(ErrorMessage.SUCCESS_REGISTERPARTS_MESSAGE,
	        		new Object[]{form.getStockName()}, 		// プレースホルダーにカテゴリ名を渡す
	        		Locale.getDefault()));
				
	        return "redirect:" + UrlConsts.STOCK_LIST; //部品在庫一覧 画面にリダイレクト

	    } catch (DataIntegrityViolationException e) {
	        // 重複があればここに到達
	        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());  // フラッシュスコープにエラーメッセージを追加
	        return "redirect:" + UrlConsts.STOCK_LIST_REGISTER; // 部品在庫一覧 画面にリダイレクト
	    } catch (InvalidInputException e) {
	        // 無効な入力の場合
	        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
	        return "redirect:" + UrlConsts.STOCK_LIST_REGISTER;
	    }
				

	}	
	
	/**	
	 *	部品在庫一覧 更新/削除画面表示
	 * 	
	 *	@param categoryId 部品カテゴリーID（更新/削除対象）
     *  @param model Modelオブジェクト
	 *	@return String(Viewの名前：部品在庫一覧画面)
	
	*/

	/**	
	 *	部品在庫一覧 更新処理
	 * 	
     *  @param model Modelオブジェクト
	 *	@return String(Viewの名前：部品在庫一覧画面)
	
	*/
		
	// 更新/削除画面を表示する
	@GetMapping(UrlConsts.STOCK_LIST_UPDATE + "/{stockId}")
	public String viewUpdate(Model model, @PathVariable("stockId") Integer stockId, PartsInfoForm form) {

		//ログ取得
		logStart(LogMessage.HTTP_GET);
	    logEnd(LogMessage.HTTP_GET);

	    System.out.println("部品在庫一覧 更新/削除画面表示 (stockId: " + stockId + ")");

	    StockList stockList = service.getStockListData(stockId); // サービス層の既存メソッドを使用

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

	        model.addAttribute("partsInfoForm", form);

			// ここでフォームの内容を出力
		    System.out.println("部品在庫一覧_更新受取 受け取ったフォームの内容: " + form);

	        return UrlConsts.STOCK_LIST_UPDATE;
	        
	    } else {
	    	
	        model.addAttribute("errorMsg", "指定された部品在庫情報が見つかりませんでした。");
	        return "redirect:" + UrlConsts.STOCK_LIST_INDEX;

	    }

	}
	
	//更新処理
	@PatchMapping(UrlConsts.STOCK_LIST_UPDATE)
	public String update(Model model, @Valid PartsInfoForm form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
				
		//ログ取得
		logStart(LogMessage.HTTP_GET);
	    logEnd(LogMessage.HTTP_GET);
		
		System.out.println("部品在庫一覧 更新処理実行");

		// 入力値のバリデーションチェック
		if(bindingResult.hasErrors()) {
			
			// バリデーションエラーメッセージ取得をredirectAttributesに追加
			redirectAttributes.addFlashAttribute(ModelAttributeContents.ERROR_MSG,
					getValidationErrorMessage(bindingResult, redirectAttributes));

			// フォームデータも渡す
		    redirectAttributes.addFlashAttribute("partsInfoForm", form);

			// 部品在庫一覧 更新/削除画面にリダイレクト
			return "redirect:" + UrlConsts.STOCK_LIST_REGISTER;
 		}
		
		// デバッグ：ここでフォームの内容を出力
	    System.out.println("部品在庫一覧_更新渡し 渡したフォームの内容: " + form);
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
		
        return "redirect:" + UrlConsts.STOCK_LIST;  // 部品在庫一覧画面にリダイレクト

	}
	
	
	//バリデーションエラーメッセージを取得する
	private String getValidationErrorMessage(BindingResult bindingResult, RedirectAttributes redirectAttributes) {

	    StringBuilder errorMessage = new StringBuilder();

	    // フィールドごとのエラーメッセージを取得し、リストに格納
	    bindingResult.getFieldErrors().forEach(error -> {
	        errorMessage.append(error.getDefaultMessage()).append("<br>"); // メッセージを改行で区切って追加 (HTML表示を考慮)
	    });

	    // グローバルエラーメッセージを取得
	    bindingResult.getGlobalErrors().forEach(error -> {
	        errorMessage.append(error.getDefaultMessage()).append("<br>");
	    });

	    return errorMessage.toString();
	    
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
	
}
