<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/cert/plugin.jsp"%>
<%@ include file="/WEB-INF/views/common/top.jsp"%>

	<h2>초기 Root인증서 생성(once)</h2>
	<a href="javascript:;" onclick="registerRootCa()">
		<span>생성</span>
	</a> 
	<h2>회원 가입 요청</h2>
	<table id="userApplyTable" border=1>
		<tr>
			<th>번호</th>
			<th>이름</th>
			<th>팀</th>
			<th>직급</th>
			<th>요청분류</th>
			<th>요청날짜</th>
			<th>선택</th>
		</tr>
	</table>
	<div id='userApplyPagination'></div>
	
	<h2>그룹 생성 요청</h2>
	<table id="groupApplyTable" border=1>
		<tr>
			<th>번호</th>
			<th>그룹이름</th>
			<th>신청일</th>
			<th>신청자ID</th>
			<th>솔루션이름</th>
			<th>선택</th>
		</tr>
	</table>
	<div id='groupApplyPagination'></div>
			
	<h2>그룹 솔루션 추가 요청</h2>
	<table id="groupSolutionApplyTable" border=1>
		<tr>
			<th>번호</th>
			<th>그룹이름</th>
			<th>신청일</th>
			<th>신청자ID</th>
			<th>솔루션이름</th>
			<th>선택</th>
		</tr>
	</table>
	<div id='groupSolutionApplyPagination'></div>
	
	<h2>유저 리스트</h2>
	<h2>인증서 리스트</h2>

<script>
var navi = 10;

$(function(){
	goAppliedUserList(1);
	goAppliedGroupList(1);
	goAppliedGroupSolutionList(1);
});

function goAppliedUserList( page ) {
	var sendData = {
		"page": page
	};
	
	$.ajax({
		url: '/admin/appliedUserList.do',
		type: 'POST',
		data: JSON.stringify(sendData),
		dataType: 'json',
		success: function(list) { 
			var i =0;
			var j =0;
			var navi2 = list.data.length;
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#userApplyTable tr").length <= (i + 1)) {
						$("#userApplyTable").append("<tr class='trC' value='" + list.data[i].seqid + "'>" +
								"<td>" + list.data[i].seqid + "</td>" +
								"<td>" + list.data[i].name + "</td>" +
								"<td>" + list.data[i].departteam + "</td>" +
								"<td>" + list.data[i].joblevel + "</td>" +
								"<td>" + list.data[i].reqtype + "</td>" +
								"<td>" + list.data[i].adddate + "</td>" + 
								"<td><a href=javascript:; onclick=registerAppliedUser('" + list.data[i].seqid + "')>승인</a></td>" + 
							"</tr>");
					} else {  
						$("#userApplyTable").find('td').eq(j++).text( list.data[i].seqid );
						$("#userApplyTable").find('td').eq(j++).text( list.data[i].name );
						$("#userApplyTable").find('td').eq(j++).text( list.data[i].departteam );
						$("#userApplyTable").find('td').eq(j++).text( list.data[i].joblevel );
						$("#userApplyTable").find('td').eq(j++).text( list.data[i].reqtype );
						$("#userApplyTable").find('td').eq(j++).text( list.data[i].adddate );
						j++;
					}
				} else if ( i == navi2 && (i+1) == $("#userApplyTable tr").length ) {
					break; 
				} else {
					$('#userApplyTable > tbody:last > tr:last').remove();
				}
			}
				
			printPaging(list.pageMaker, "goAppliedUserList", "userApplyPagination" );
		}
	});
}

function goAppliedGroupSolutionList( page ) {
	var sendData = {
			"page": page
		};
		
	$.ajax({
		url: '/admin/appliedGroupSolutionList.do',
		type: 'POST',
		data: JSON.stringify(sendData),
		dataType: 'json',
		success: function(list) { 
			var i =0;
			var j =0;
			var navi2 = list.data.length;
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#groupSolutionApplyTable tr").length <= (i + 1)) {
						$("#groupSolutionApplyTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
								"<td>" + list.data[i].id + "</td>" +
								"<td>" + list.data[i].name + "</td>" +
								"<td>" + list.data[i].createdate + "</td>" +
								"<td>" + list.data[i].creator + "</td>" +
								"<td>" + list.data[i].solutionname + "</td>" +
								"<td><a href=javascript:; onclick=registerAppliedSolution('" + list.data[i].id + "')>승인</a></td>" + 
							"</tr>");
					} else {  
						$("#groupSolutionApplyTable").find('td').eq(j++).text( list.data[i].id );
						$("#groupSolutionApplyTable").find('td').eq(j++).text( list.data[i].name );
						$("#groupSolutionApplyTable").find('td').eq(j++).text( list.data[i].createdate );
						$("#groupSolutionApplyTable").find('td').eq(j++).text( list.data[i].creator );
						$("#groupSolutionApplyTable").find('td').eq(j++).text( list.data[i].solutionname );
						j++;
					}
				} else if ( i == navi2 && (i+1) == $("#groupSolutionApplyTable tr").length ) {
					break; 
				} else {
					$('#groupSolutionApplyTable > tbody:last > tr:last').remove();
				}
			}
				
			printPaging(list.pageMaker, "goAppliedGroupSolutionList", "groupSolutionApplyPagination" );
		}
	});
}


function goAppliedGroupList( page ) {
	var sendData = {
			"page": page
		};
		
	$.ajax({
		url: '/admin/appliedGroupList.do',
		type: 'POST',
		data: JSON.stringify(sendData),
		dataType: 'json',
		success: function(list) { 
			var i =0;
			var j =0;
			var navi2 = list.data.length;
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#groupApplyTable tr").length <= (i + 1)) {
						$("#groupApplyTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
								"<td>" + list.data[i].id + "</td>" +
								"<td>" + list.data[i].name + "</td>" +
								"<td>" + list.data[i].createdate + "</td>" +
								"<td>" + list.data[i].creator + "</td>" +
								"<td>" + list.data[i].solutionname + "</td>" +
								"<td><a href=javascript:; onclick=registerAppliedGroup('" + list.data[i].id + "')>승인</a></td>" + 
							"</tr>");
					} else {  
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].id );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].name );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].createdate );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].creator );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].solutionname );
						j++;
					}
				} else if ( i == navi2 && (i+1) == $("#groupApplyTable tr").length ) {
					break; 
				} else {
					$('#groupApplyTable > tbody:last > tr:last').remove();
				}
			}
				
			printPaging(list.pageMaker, "goAppliedGroupList", "groupApplyPagination" );
		}
	});
}

function registerAppliedUser( id ) {
	$.ajax({
		url: '/admin/appliedUser/' + id,
		type: 'POST',
		success: function(list) {
			reloadTable( $("#userApplyTable tr").length, "userApplyTable", "goAppliedUserList" );
			alert( '등록 성공');
		},
		error: function (xhr, ajaxOptions, thrownError) {
			reloadTable( $("#userApplyTable tr").length, "userApplyTable", "goAppliedUserList" );
			alert(xhr.status);
		}
	});
}

function registerAppliedGroup( id ) {
	$.ajax({
		url: '/admin/appliedGroup/' + id,
		type: 'POST',
		success: function(list) {
			reloadTable( $("#groupApplyTable tr").length, "groupApplyTable", "goAppliedGroupList" );
			alert( '등록 성공');
		},
		error: function (xhr, ajaxOptions, thrownError) {
			reloadTable( $("#groupApplyTable tr").length, "groupApplyTable", "goAppliedGroupList" );
			alert(xhr.status);
		}
	});
}

function registerAppliedSolution( id ) {
	$.ajax({
		url: '/admin/appliedSolution/' + id,
		type: 'POST',
		success: function(list) {
			reloadTable( $("#groupSolutionApplyTable tr").length, "groupSolutionApplyTable", "goAppliedGroupSolutionList" );
			alert( '등록 성공');
		},
		error: function (xhr, ajaxOptions, thrownError) {
			reloadTable( $("#groupSolutionApplyTable tr").length, "groupSolutionApplyTable", "goAppliedGroupSolutionList" );
			alert(xhr.status);
		}
	});	
}

function registerRootCa() {
	$.ajax({
		url: '/admin/registerRootCa.do',
		type: 'POST',
		success: function( data ) {
			alert( '등록 성공');
		},
		error: function (xhr, ajaxOptions, thrownError) {
			alert(xhr.status);
		}
	});
}

function reloadAll() {
	reloadTable( $("#userApplyTable tr").length, "userApplyTable", "goAppliedUserList" );
	reloadTable( $("#groupApplyTable tr").length, "groupApplyTable", "goAppliedGroupList" );
	reloadTable( $("#groupSolutionApplyTable tr").length, "groupSolutionApplyTable", "goAppliedGroupSolutionList" );
}

</script>
	
<%@ include file="/WEB-INF/views/common/bottom.jsp"%>