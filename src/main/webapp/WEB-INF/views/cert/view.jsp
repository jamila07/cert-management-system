<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/cert/plugin.jsp"%>
<%@ include file="/WEB-INF/views/common/top.jsp"%>
	<h2>인증서 발급</h2>
	<form name="certForm" id="certForm" action="/cert/register.do" method="post">
		<input type="hidden" name="type" value="2"/>
		그룹 : <select name="groupName" id="groupName">
			<!-- AJAX Load -->
			<option value=""></option>
		</select><br>
		솔루션 : <select name="groupSolutionName" id="groupSolutionName" onclick="ifNotSelectGroup()">
			<!-- if select group -->
			<option value=""></option>
		</select><br>
		현장 이름 : <input type="text" name="citeName" /> <br>
		도(시): <input type="text" name="citeLocality" /> <br>
		시(구): <input type="text" name="citeProvince" />	 <br>
		현장 도메인 : <input type="text" name="citeDomain" /> <br>
		설명 : <input type="text" name="description" /> <br>
		<a href="javascript:;" onclick="submitData('certForm')">
			<span>고고</span>
		</a>
		
	</form>
	<h2>인증서 검증</h2>
		<div class="fileDrop"></div>
		<div class="uploadedList"></div>
		
	<h2>인증서 목록</h2>
	<table id="certTable" border=1>
		<tr>
			<th>번호</th>
			<th>DN</th>
			<th>유효기간 시작</th>
			<th>유효기간 끝</th>
			<th>발급자</th>
			<th>신청자</th>
			<th>ouType</th>
			<th>다운로드</th>
		</tr>
	</table>

<style>
	.fileDrop {
		width: 50%;
		height:200px;
		border:1px dotted blue;
	}
	
	small {
		margin-left:3px;
		font-weight: bold;
		color: gray;
	}
</style>
<script>
$(function(){
	$(".fileDrop").on("dragenter dragover", function(evenet){
		event.preventDefault();
	});
	
	$(".fileDrop").on("drop", function(evenet){
		event.preventDefault();
		
		var files = event.dataTransfer.files;
		
		var file = files[0];
		
		var b64File;
		getBase64(file).then(function(data){
			if ( data.indexOf( 'data:application/x-x509-ca-cert;base64,' ) == 0 ) {
				data = data.substring( 39 );
				
				var sendData = {
					"data":data	
				};
				$.ajax({
					url:'/cert/upload', 
					data:JSON.stringify( sendData ),
					type:'POST',
					dataType:'json', 
					success: function(data) {
						alert(data.status);
					},
					error: function(data) {
						alert("실패 : " + data.responseJSON.errMsg );
					}
				});
			} else {
				alert("인증서 아닌거 올리지마셈");
			}	
		});
	});
	
	$.ajax({ 
		url: '/cert/showList.do',
		type: 'POST',
		dataType: 'json', 
		success: function(list) {  
			
			$.each(list.data, function(i){
				$("#certTable").append("<tr class='trC' value='" + list.data[i].id + "'>" +
						"<td>" + list.data[i].id + "</td>" +
						"<td>" + list.data[i].subjectdn + "</td>" +
						"<td>" + list.data[i].startdate + "</td>" +
						"<td>" + list.data[i].enddate + "</td>" +
						"<td>" + list.data[i].issuer + "</td>" +
						"<td>" + list.data[i].subject + "</td>" +
						"<td>" + list.data[i].outype + "</td>" +
// 						"<td><a href=/cert/download/'" + list.data[i].id + "' >다운로드</a></td>" + 
						"<td>" +
							"<a href=javascript:; onclick=certBinDownload('" + list.data[i].id + "')>인증서(bin)</a> &nbsp;" +
							"<a href=javascript:; onclick=certPemDownload('" + list.data[i].id + "')>인증서(pem)</a> &nbsp;" +
							"<a href=javascript:; onclick=pkcs8KeyDownload('" + list.data[i].id + "')>키(p8,bin)</a>" +
							"<a href=javascript:; onclick=pkcs8PemDownload('" + list.data[i].id + "')>키(p8,pem)</a>" +
						"</td>" +
					"</tr>");
			});
		}
	});
	
	/* Operation List AJAX */
	var groupList;
	$.ajax({
		url:'/group/showJoinedGroup.do',
		type: 'POST',
		success:function(data) {
			groupList = data.data;
			for ( var id in groupList) {
				for ( var name in groupList[id] ) {
					$("#groupName").append($('<option>',{
						value: id,
						text: name
					}));
				} 
			}
		}
	});
	
	$('#groupName').change(function() { 
		$('#groupSolutionName').empty();
		$('#groupSolutionName').val(""); 
		for ( var id in groupList ) { 
			if ( id == $('#groupName').val() ) {
				for ( var name in groupList[id] ) {
					for ( var i=0; i<groupList[id][name].length; i++ ) {
						$("#groupSolutionName").append($('<option>',{
							value: groupList[id][name][i],
							text: groupList[id][name][i]
						}));		
					}
				}	
			}
		}  
	});
});

function certBinDownload( id ) {
   
	$.ajax({ 
		url: '/cert/certBinDownload/' + id,
		type: 'POST',   
		success: function (data) {
			console.log(data);
			b64ToFileSave("cert.cer", data.fileB64);
	    }    
	});    
}

function certPemDownload( id ) {
	   
	$.ajax({ 
		url: '/cert/certPemDownload/' + id,
		type: 'POST',   
		success: function (data) {
			console.log(data);
			b64ToFileSave("cert.cer", data.fileB64);
	    }    
	});    
}

function pkcs8KeyDownload( id ) {
	   
	$.ajax({ 
		url: '/cert/pkcs8KeyDownload/' + id,
		type: 'POST',   
		success: function (data) {
			console.log(data);
			b64ToFileSave("cert_pri.key", data.fileB64);
	    }    
	});    
}

function pkcs8PemDownload( id ) {
	   
	$.ajax({ 
		url: '/cert/pkcs8PemDownload/' + id,
		type: 'POST',   
		success: function (data) {
			console.log(data);
			b64ToFileSave("cert_pri.pem", data.fileB64);
	    }    
	});    
}

function ifNotSelectGroup() {
	if ( !$('#groupSolutionName').val() ) 
		alert('그룹을 먼저 선택하세요.'); 
}

function logout() { 
	$.ajax({
		url : '/logout.do',
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

<%@ include file="/WEB-INF/views/common/bottom.jsp"%>