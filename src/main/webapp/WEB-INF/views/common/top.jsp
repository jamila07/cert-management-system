<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ include file="/WEB-INF/views/common/plugin.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mini CA</title>
</head>
<body>

<div>
	<div class="dropdown">
		<button class="dropbtn">인증서</button>
		<div class="dropdown-content">
			<a href="/cert">인증서 현황</a>
		</div>
	</div><!-- 
 --><div class="dropdown">
		<button class="dropbtn">그룹</button>
		<div class="dropdown-content" >
			<a href="/group">그룹 현황</a> 
		</div>
	</div><!-- 
 --><div class="dropdown">
		<button class="dropbtn">관리자</button>
		<div class="dropdown-content" >
			<a href="/admin">신청 현황</a>
			<a href="/admin">인증서 현황</a>
			<a href="/admin">그룹 현황</a> 
			<a href="/audit">감사로그</a>
			<a href="/user/home.do">사용자 현황(권한X)</a>
		</div>
	</div>
	<div style="display: inline-block;">
		알람창//
	</div>
	<div style="display: inline-block;">
		로그인 유저 정보 확인 및 수정//
	</div>
	<div style="display: inline-block;">
		<a href="javascript:;" onclick="logout()">
			<span>로그아웃</span>
		</a> 
	</div>
	
</div>
<hr>

<script>
function logout() { 
	$.ajax({
		url : '/logout',
		success:function(data) {
			alert("로그아웃");
			window.location.href = data.redirect;
		}, 
		error: function (xhr, ajaxOptions, thrownError) {
			alert(xhr.status);
		}
	}); 
}
</script>