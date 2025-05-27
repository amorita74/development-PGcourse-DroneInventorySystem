package com.digitalojt.web.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.digitalojt.web.consts.LogMessage;
import com.digitalojt.web.consts.UrlConsts;

@Service
public class ErrorService extends AbstractService {
	
    /**
     * エラーサービス
     * 
     * @param  エラーメッセージ
     * @return エラーページ（error.html）
     */
	@GetMapping(UrlConsts.ERROR)
    public String handleError(@ModelAttribute("errorMsg") String errorMsg) {  

        logException(LogMessage.HTTP_GET, errorMsg != null ? errorMsg : "不明なエラー");
		
		return UrlConsts.ERROR_VIEW;
	}

}
