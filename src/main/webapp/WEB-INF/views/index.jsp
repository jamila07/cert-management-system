<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ include file="/WEB-INF/views/plugin.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Page</title>
	<style type="text/css">
 		.box{
		  background:white;
		  width:300px;
		  border-radius:6px;
		  margin: 0 auto 0 auto;
		  padding:0px 0px 70px 0px;
		  border: #4CAF50 4px solid; 
		}
	</style>
</head>
<body>
<div class="box">
	<div style="text-align: center;">
		<h3>인증서 관리 시스템</h3>
	</div> 
	<input type="hidden" name="loginToken" id="loginToken" value="<%= session.getId() %>">
	<form id="loginForm" name="loginForm" method="post" action="login">
		<input type="hidden" name="sha256Pw"  id="sha256Pw" value=""/>
		<div style="padding:20px; margin-top:10px; text-align: center;">
			ID <input type="text" id="loginId" name="loginId" onkeypress="return enterLogin(event)" /><br>
			PW <input type="password" id="loginPw" name="loginPw" onkeypress="return enterLogin(event)" />
			<br> 
			<a href="javascript:;" onclick="login('loginForm')">
				<span>로그인</span>
			</a>&nbsp;&nbsp; 
			<a href="javascript:;" onclick="popOpen()">
				<span>회원가입</span>
			</a>
		</div>
	</form>
</div>

<div class="pop-layer" id="registerPop">
	<div>
		<p style="line-height:25px; color:#666;">
			<form id="registerForm" name="registerForm" method="post" action="/user">
				아이디: <input type="text" name="userId" id="userId" />
				<a href="javascript:;" onclick="">중복검사</a><br>
				비밀번호: <input type="password" name="password" id="password" /> <br>
				이름: <input type="text" name="name" id="name"/> <br> 
				부서/팀: <input type="text" name="departTeam" id="departTeam" /> <br>
				직위/직무: <input type="text" name="jobLevel" id="jobLevel"/> <br>
				이메일: <input type="text" name="eMail" id="eMail"/> <br>
				<input type="radio" name="groupCreator" value="true"/> 그룹생성
				<input type="radio" name="groupCreator" value="false"/> 그룹가입
				<input type="radio" name="groupCreator" value="null"/> 지정안함
				<div id='create_group' style="display: none;">
					그룹이름 : <input type="text" name="groupName" id="groupName" /><br>
					솔루션 : <input type="text" name="solutionName" id="solutionName" /><br>
					설명 : <input type="text" name="groupDescription" id="groupDescription" />
				</div>
			
				<div id='join_group' style="display: none;">
					검색 : <input type="text" name="searchGroupList" id="searchGroupList"/><br>
					선택 그룹 : <input type="text" name="groupId" id="groupId" readonly />
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
				</div>
			</form>
		</p> 
	</div>
	<div style="">
		<a href="javascript:;" onclick="register('registerForm')">회원 가입</a>
		<a href="javascript:;" onclick="popClose()">close</a>	
	</div>
</div>
<div id="fade" class="black_overlay"></div> 
</body>

<script>
var navi = 5;
	
	$(function(){
		$("#loginId").focus();
		
		$('input[type=radio][name=groupCreator]').change(function() {
			if ( this.value == 'false' ) {
				showGroupList();
			} else if ( this.value =='true' ) {
				createGroup();
			} else if ( this.value == 'null' ) {
				nonGroup();
			}
		});
		
		$("#groupTable").delegate("tr.trC", "click", function(e) {
			groupId = $(this).attr('value');
			$("#groupId").val( groupId );
		});
		
		$( "#userId" ).keyup(function() {
			signUpIdPressed();
		});
	});
	
	function enterLogin(e) {
	    if (e.keyCode == 13) {
	    	login('loginForm');
	    }
	}
	
	function signUpIdPressed() {
		sendData = {
			"oper":"chkOverlapUser"
		}
		userId = $("#userId").val();
		
		$.ajax({
			url: '/user/' + userId,
			data: JSON.stringify(sendData),
			dataType: 'json',
			type: 'POST',
			success: function( data ) {
				console.log(data.data);
				if ( data.data == true ) $("#userId").css("background-color", "#8be28b");
				else $("#userId").css("background-color", "#ff4d4d");
			}
		});
	}
	
	function login( form ) {
		if ( $("#loginId").val() == "" || $("#loginPw").val() == "" ) {
			alert( "아이디 패스워드 공백임" );
			return;
		}
		
		$("#sha256Pw").val( SHA256( $("#loginToken").val() + SHA256( $("#loginPw").val() ) ) );
		$("#loginPw").val("");
		
		submitData( form );
	}
	
	function popOpen() {
		$("#registerPop").css("display", "block");
		$("#fade").css("display", "block");
	}
	
	function popClose() {
		$("#registerPop").css("display", "none");
		$("#fade").css("display", "none");
		cleanPopData();
	}
	 
	function register( form ) {
		$("#searchGroupList").prop('disabled', true);
		
		submitData( form );
		popClose();
		
	}
	
	function cleanPopData() {
		$("#userId").val("");
		$("#password").val("");
		$("#name").val("");
		$("#departTeam").val("");
		$("#jobLevel").val("");
		$("#eMail").val("");
	}
	
	function createGroup() {
		$("#join_group").css("display", "none");
	
		$("#searchGroupList").val("");
		$("#groupId").val("");
		
		$("#groupName").prop('disabled', false);
		$("#solutionName").prop('disabled', false);
		$("#groupDescription").prop('disabled', false);
		
		$("#searchGroupList").prop('disabled', true);
		$("#groupId").prop('disabled', true);
		
		$("#create_group").css("display", "block");
	}
	
	function showGroupList() {
		$("#create_group").css("display", "none");
		
		$("#groupName").val("");
		$("#solutionName").val("");
		$("#groupDescription").val("");
		
		$("#searchGroupList").prop('disabled', false);
		$("#groupId").prop('disabled', false);
		
		$("#groupName").prop('disabled', true);
		$("#solutionName").prop('disabled', true);
		$("#groupDescription").prop('disabled', true);
		
		$("#join_group").css("display", "block");
		
		showGroupInfo(1);
	} 
	
	function nonGroup() {
		$("#create_group").css("display", "none");
		$("#join_group").css("display", "none");
		
		$("#groupName").val("");
		$("#solutionName").val("");
		$("#groupDescription").val("");
		$("#searchGroupList").val("");
		$("#groupId").val("");
		
		$("#groupName").prop('disabled', true);
		$("#solutionName").prop('disabled', true);
		$("#groupDescription").prop('disabled', true);
		$("#searchGroupList").prop('disabled', true);
		$("#groupId").prop('disabled', true);
	}
	function showGroupInfo( page ) {
		var sendData = {
				"page": page,
				"perPageNum" : navi
		};
		
		$.ajax({
			url: '/showGroupInfo.do',
			type: 'POST',
			data: JSON.stringify(sendData),
			dataType: 'json',
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
					
				printPaging(list.pageMaker, "showGroupInfo", "groupPagination" );
			}
		});
	}
</script>

</html>
