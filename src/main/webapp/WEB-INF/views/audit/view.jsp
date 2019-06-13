<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/cert/plugin.jsp"%>
<%@ include file="/WEB-INF/views/common/top.jsp"%>

	<h2>웹 감사로그 목록</h2>
	<table id="webAuditTable" border=1>
		<tr>
			<th>번호</th>
			<th>사용자 아이디</th>
			<th>날짜</th>
			<th>URL</th>
			<th>파라메터</th>
			<th>IP</th>
		</tr>
	</table>
	<div id='webAuditPagination'></div>
	
	<table id="certAuditTable" border=1>
		<tr>
			<th>번호</th>
			<th>사용자 아이디</th>
			<th>날짜</th>
			<th>요청파람</th>
			<th>IP</th>
	</table>
	<div id='certAuditPagination'></div>

<script> 
var navi = 10;

$(function(){
	goWebAuditList(1);
	goCertAuditList(1);
});

function goWebAuditList( page ) {
	$.ajax({ 
		url: '/audit/web',
		type: 'GET',
		data: {
			"page":page
		},
		success: function(list) { 
			var i =0;
			var j =0;
			var navi2 = list.data.length;
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#webAuditTable tr").length <= (i + 1)) {
						$("#webAuditTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
								"<td>" + list.data[i].id + "</td>" +
								"<td>" + list.data[i].userid + "</td>" +
								"<td>" + list.data[i].date + "</td>" +
								"<td>" + list.data[i].url + "</td>" +
								"<td>" + list.data[i].param + "</td>" +
								"<td>" + list.data[i].clientip + "</td>" +
							"</tr>");
					} else { 
						$("#webAuditTable").find('td').eq(j++).text( list.data[i].id );
						$("#webAuditTable").find('td').eq(j++).text( list.data[i].userid );
						$("#webAuditTable").find('td').eq(j++).text( list.data[i].date );
						$("#webAuditTable").find('td').eq(j++).text( list.data[i].url );
						$("#webAuditTable").find('td').eq(j++).text( list.data[i].param );
			 			$("#webAuditTable").find('td').eq(j++).text( list.data[i].clientip );
					}
				} else if ( i == navi2 && (i+1) == $("#webAuditTable tr").length ) {
					break;
				} else {
					$('#webAuditTable > tbody:last > tr:last').remove();
				}

			}
				
			printPaging(list.pageMaker, "goWebAuditList", "webAuditPagination" );
		}
	});
}

function goCertAuditList( page ) {
	$.ajax({ 
		url: '/audit/cert',
		type: 'GET',
		data: {
			"page":page
		},
		success: function(list) { 
			var i =0;
			var j =0;
			var navi2 = list.data.length
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#certAuditTable tr").length <= (i + 1)) {
						$("#certAuditTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
								"<td>" + list.data[i].id + "</td>" +
								"<td>" + list.data[i].userid + "</td>" +
								"<td>" + list.data[i].date + "</td>" +
								"<td>" + list.data[i].requestparam + "</td>" +
								"<td>" + list.data[i].clientip + "</td>" +
							"</tr>");
					} else {  
						$("#certAuditTable").find('td').eq(j++).text( list.data[i].id );
						$("#certAuditTable").find('td').eq(j++).text( list.data[i].userid );
						$("#certAuditTable").find('td').eq(j++).text( list.data[i].date );
						$("#certAuditTable").find('td').eq(j++).text( list.data[i].requestparam );
			 			$("#certAuditTable").find('td').eq(j++).text( list.data[i].clientip );
					}
				} else if ( i == navi2 && (i+1) == $("#certAuditTable tr").length ) {					break;
				} else {
					$('#certAuditTable > tbody:last > tr:last').remove();
				}

			}
				
			printPaging(list.pageMaker, "goCertAuditList", "certAuditPagination" );
		}
	});
}

</script>

<%@ include file="/WEB-INF/views/common/bottom.jsp"%>