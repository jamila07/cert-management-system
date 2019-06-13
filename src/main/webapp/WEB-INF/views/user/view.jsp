<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/cert/plugin.jsp"%>
<%@ include file="/WEB-INF/views/common/top.jsp"%>

	<br><br> 사용자 가입 승인 체계 만들기 
	<br> 최고 어드민만 이 메뉴를 볼 수 있고 메뉴 intercepter를 통해 접근제어 설정
	<h2>사용자 목록</h2>
	<table id="userTable" border=1>
		<tr>
			<th>번호</th>
			<th>이름</th>
			<th>가입날짜</th>
			<th>부서</th>
			<th>직급</th>
			<th>상태값</th>
		</tr>
	</table>

<script>
$(function(){
	$.ajax({ 
		url: '/user',
		type: 'GET',
		data: {
			"page":10
		},
		success: function(list) {  
			
			$.each(list.data, function(i){
				$("#userTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
						"<td>" + list.data[i].id + "</td>" +
						"<td>" + list.data[i].name + "</td>" +
						"<td>" + list.data[i].adddate + "</td>" +
						"<td>" + list.data[i].departteam + "</td>" +
						"<td>" + list.data[i].joblevel + "</td>" +
						"<td>" + list.data[i].state + "</td>" +
					"</tr>");
			});
		}
	});
});

</script>

<%@ include file="/WEB-INF/views/common/bottom.jsp"%>