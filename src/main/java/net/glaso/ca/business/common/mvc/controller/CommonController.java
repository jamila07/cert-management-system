package net.glaso.ca.business.common.mvc.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import net.glaso.ca.business.common.domain.Criteria;
import net.glaso.ca.business.common.domain.PageMaker;

import javax.servlet.http.HttpServletResponse;

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

	public void sendRedirect(String alertMessage, String redirectUri, HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter printWriter = response.getWriter();

		String returnScript = new StringBuilder( "<script>alert(\'" )
				.append( alertMessage )
				.append( "\');" )
				.append( "location.replace(\'" )
				.append( redirectUri )
				.append( "\');</script>" ).toString();

		printWriter.println( returnScript );
		printWriter.flush();
		printWriter.close();
	}

	private PageMaker setPaging( Criteria cri ) {
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri( cri );

		return pageMaker;
	}
}
