<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/cert/plugin.jsp"%>
<%@ include file="/WEB-INF/views/common/top.jsp"%>

	<h2>그룹생성</h2>
	<form name="groupForm" id="groupForm" action="/group" method="post">
		그룹이름 : <input type="text" name="name"/> <br>
		그룹 대체 이름 : <input type="text" name="altName"/> <br>
		솔루션 이름 : <input type="text" name="groupSolutionName" /><br>
		설명 : <input type="text" name="description" /> <br>
		<a href="javascript:;" onclick="submitData('groupForm')">
			<span>고고</span>
		</a>
	</form>
	
	<h2>그룹 목록</h2>
	<table id="groupTable" border=1>
		<tr>
			<th>번호</th>
			<th>그룹이름</th>
			<th>그룹별명</th>
			<th>그룹생성일</th>
			<th>그룹생성자</th>
			<th>설명</th>
		</tr>
	</table>
	<div id='groupPagination'></div>
	
	<div class="pop-layer" id="viewPop">
		<p style="line-height:25px; color:#666;">
			<h2>솔루션 추가 신청</h2>
					<input type="text" name="solutionName" />
					<input type="hidden" name="groupId" /><!--  init value when pop open -->
					<a href="javascript:;" onclick="applyGroupSolution()">
						<span>고고</span>
					</a>
			<h2> 그룹원 가입 신청 </h2>
				<table id="groupApplyTable" border=1>
					<tr>
						<th>아이디</th>
						<th>이름</th>
						<th>신청일자</th>
						<th>부서/팀</th>
						<th>직위/직무</th>
						<th>승인</th>
					</tr>
				</table>
				<div id='groupApplyPagination'></div>
			<h2> 그룹원 </h2>
			<table id="userGroupTable" border=1>
				<tr>	
					<th>유저아이디</th>
					<th>가입일</th>
					<th>권한</th>
				</tr>
			</table>
			<a href="javascript:;" onclick="addUserToGroup()">가입</a>
			<a href="javascript:;" onclick="removeUserToGroup()">탈퇴</a>
			<a href="javascript:;" onclick="popClose()">close</a>
		</p>
	</div>
<div id="fade" class="black_overlay"></div> 

<script>
var groupId;
var navi = 10;

$(function(){
	goGroupList(1);
	
	$("#groupTable").delegate("tr.trC", "click", function(e) {
		groupId = $(this).attr('value');
		popOpen();
		showUserGroup();
	});
});

function goGroupList( page ) {
	
	$.ajax({
		url: '/group',
		type: 'GET',
		data: {
			"page": page
		},
		success: function(list) {
			var i =0;
			var j =0;
			var navi2 = list.data.length;
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#groupTable tr").length <= (i + 1)) {
						$("#groupTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
								"<td>" + list.data[i].id + "</td>" +
								"<td>" + list.data[i].name + "</td>" +
								"<td>" + list.data[i].altname + "</td>" +
								"<td>" + list.data[i].createdate + "</td>" +
								"<td>" + list.data[i].creator + "</td>" + 
								"<td>" + list.data[i].description + "</td>" +
							"</tr>");
					} else { 
						$("#groupTable").find('td').eq(j++).text( list.data[i].id );
						$("#groupTable").find('td').eq(j++).text( list.data[i].name );
						$("#groupTable").find('td').eq(j++).text( list.data[i].altname );
						$("#groupTable").find('td').eq(j++).text( list.data[i].createdate );
						$("#groupTable").find('td').eq(j++).text( list.data[i].creator );
						$("#groupTable").find('td').eq(j++).text( list.data[i].description );
					}
				} else if ( i == navi2 && (i+1) == $("#groupTable tr").length ) {
					break;
				} else {
					$('#groupTable > tbody:last > tr:last').remove();
				}
			}
				
			printPaging(list.pageMaker, "goGroupList", "groupPagination" );
		}
	});

}

function goGroupApplyList( page ) {

	$.ajax({
		url: '/group/' + groupId + '/user',
		type: 'GET',
		data: {
			"page":page,
			"oper":"requested"
		},
		success: function(list) {
			var i =0;
			var j =0;
			var navi2 = list.data.length;
			
			for ( i=0; i<navi; i++ ) {
				if ( i < navi2 ) {
					if ( $("#groupApplyTable tr").length <= (i + 1)) {
						$("#groupApplyTable").append("<tr class='trC' value='" + list.data[i].userid + "'>" +
								"<td>" + list.data[i].userid + "</td>" +
								"<td>" + list.data[i].username + "</td>" +
								"<td>" + list.data[i].joindate + "</td>" +
								"<td>" + list.data[i].departteam + "</td>" +
								"<td>" + list.data[i].joblevel + "</td>" +
								"<td><a href=javascript:; onclick=approveAppliedUser('" + list.data[i].userid + "')>승인</a></td>" +
							"</tr>");
					} else { 
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].userid );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].username );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].joindate );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].departteam );
						$("#groupApplyTable").find('td').eq(j++).text( list.data[i].joblevel );
						j++;
					}
				} else if ( i == navi2 && (i+1) == $("#groupApplyTable tr").length ) {
					break;
				} else {
					$('#groupApplyTable > tbody:last > tr:last').remove();
				}
			}
				
			printPaging(list.pageMaker, "goGroupApplyList", "groupApplyPagination" );
		}
	});
}

function applyGroupSolution() {
	var sendData = {
		"solutionName": $("input[name='solutionName']").val()
	}
	
	$.ajax({
		url: '/group/' + groupId + '/solution',
		type: 'POST',
		dataType: 'json',
		contentType: 'application/json',
		data: JSON.stringify(sendData),
		success: function(list) {
			alert("성공");
			removePopData();
			showUserGroup();
		}
	});
}

function approveAppliedUser( id ) {
	
	$.ajax({
		url: '/group/' + groupId + '/user/' + id,
		type: 'POST',
		success: function(list) {
			reloadTable( $("#groupApplyTable tr").length, "groupApplyTable", "goGroupApplyList" );
			alert( '등록 성공');
		},
		error: function (xhr, ajaxOptions, thrownError) {
			reloadTable( $("#groupApplyTable tr").length, "groupApplyTable", "goGroupApplyList" );
			alert(xhr.status);
		}
	});
}

function popOpen() {
	$("#viewPop").css("display", "block");
	$("#fade").css("display", "block");
	goGroupApplyList( 1 );
	
	$("input[name='groupId']").val( groupId );
}

function popClose() {
	removePopData();
	$("#viewPop").css("display", "none");
	$("#fade").css("display", "none");
	groupId = 0;
}

function removePopData() {
	$("#userGroupTable").find("tr:gt(0)").remove();
}

function addUserToGroup() {
	$.ajax({
		url: '/group/' + groupId + '/user',
		type: 'POST',
		contentType: 'application/json',
		success: function(list) {
			alert("성공");
			removePopData();
			showUserGroup();
		}
	});
}

function removeUserToGroup() {
	$.ajax({
		url: '/group/' + groupId + '/user/' + userId,
		type: 'DELETE',
		dataType: 'json',
		success: function(list) {
			alert("성공");
			removePopData();
			showUserGroup();
		}
	});
} 

function showUserGroup() {
	var page = 10;
	$.ajax({
		url: '/group/' + groupId + '/user',
		type: 'GET',
		data: {
			"page":page,
			"oper":"registered"
		},
		success: function(list) {
			
			$.each(list.data, function(i){
				$("#userGroupTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
						"<td>" + list.data[i].userId + "</td>" +
						"<td>" + list.data[i].joinDate + "</td>" +
						"<td>" +list.data[i].userAuthority + "</td>" +
					"</tr>");
			});
		}
	});
}
</script>

<%@ include file="/WEB-INF/views/common/bottom.jsp"%>