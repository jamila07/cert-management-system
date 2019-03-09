package com.dreamsecurity.ca.business.common.mvc.controller;

import java.util.List;
import java.util.Map;

import com.dreamsecurity.ca.business.common.domain.Criteria;
import com.dreamsecurity.ca.business.common.domain.PageMaker;

public class CommonController {
	public Map<String, Object>  commonListing( Map<String, Object> entities, List<Map<String, Object>> list, int listCnt, PageMaker pageMaker ) {
		
		entities.put( "data", list );
		pageMaker.setTotalCount( listCnt );
		
		entities.put( "pageMaker", pageMaker );
		
		return entities;
	}
	 
	public PageMaker setPaging( int page, int pagePerNum ) {
		Criteria cri = new Criteria();
		cri.setPage( page );
		cri.setPerPageNum( pagePerNum );
		
		return setPaging( cri );
	}
	
	public PageMaker setPaging( int page ) {
		Criteria cri = new Criteria();
		cri.setPage( page );
	
		return setPaging( cri );
	}
	
	private PageMaker setPaging( Criteria cri ) {
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri( cri );
		
		return pageMaker;
	}
}
