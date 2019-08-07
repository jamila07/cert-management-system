<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/cert/plugin.jsp"%>
<%@ include file="/WEB-INF/views/common/top.jsp"%>
<input type="hidden" id="userId"value="<%= session.getAttribute( "sessionId" ) %>" />
	<h2>인증서 발급</h2>
	<form name="certForm" id="certForm" action="/cert" method="post">
		<input type="hidden" name="type" value="2"/>
		그룹 : <select name="groupId" id="groupId">
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
		<a href="javascript:;" onclick="certFormSubmit('certForm')">
			<span>고고</span>
		</a>
		
	</form>
	<h2>인증서 검증</h2>
		<div class="fileDrop"></div>
		<div class="uploadedList"></div>
		
	<h2>인증서 목록</h2>
	<table id="certTable" border=1 style="cursor:pointer">
		<tr>
			<th>번호</th>
			<th>DN</th>
			<th>유효기간 시작</th>
			<th>유효기간 끝</th>
			<th>발급자</th>
			<th>신청자</th>
			<th>ouType</th>
		</tr>
	</table>
	
	<div class="pop-layer" id="viewPop" style="height: 80%;">
		<h2>CERTIFICATE INFO</h2>
		<hr>
		<div>
			<table id="certOneInfoTbl" border=1>
				<tr>
					<th>SubjectDn</th>
					<th>시작</th>
					<th>끝</th>
					<th>발급자</th>
					<th>신청자</th>
				</tr>
			</table>
		</div>
		<h2>EXPORT OPTION</h2>
		<hr>
		<div>
			<input type="hidden" id="certId4Managing" />
			<input type="radio" name="exportWhat" value="1" checked /> 키쌍
			<input type="radio" name="exportWhat" value="2"/> 개인키(p8)
			<input type="radio" name="exportWhat" value="3"/> 공개키
			<input type="radio" name="exportWhat" value="4" /> 인증서 체인(X.509)
			<br><br>
			<div id="keyPairDv">
				<input type="radio" name="keyPairExpType" value="1" checked >PKCS12
				<input type="radio" name="keyPairExpType" value="2"> PEM
				<input type="radio" name="keyPairExpType" value="3"> JKS <br>
				PIN : <input type="password" id="keyPairPin"><br>
				PIN 확인 : <input type="password" id="keyPairPinChk"><br>
				비밀번호 : <input type="password" id="keyPairPw"><br>
				비밀번호 확인 : <input type="password" id="keyPairPwChk"><br>
			</div>
			
			<div id="privateKeyDv" style="display:none;">
				비밀번호 : <input type="password" id="privateKeyPw"><br>
				비밀번호 확인 : <input type="password" id="privateKeyPwChk"><br>
				<input type="checkbox" name="privateKeyExpType">PEM 	
			</div>
			<div id="publicKeyDv" style="display:none;">
				<input type="checkbox" name="publicKeyExpType">PEM
			</div>
			<div id="certChainDv" style="display:none;">
				<input type="radio" name="certChainExpType" value="1" checked>HEAD
				<input type="radio" name="certChainExpType" value="2"> ENTIRE <br>
	
				<input type="checkbox" name="certOutputType" value="pem">PEM
			</div>
			<br>
			<a href="javascript:;" onclick="downloadCert()">
				<span>다운로드 </span>
			</a>
		</div>
		<br>
		<div style="text-align: center;">
			<a href="javascript:;" onclick="certManagingPopClose()" >
				<span >close the door</span>
			</a>
		</div>
	</div>
	<div id="fade" class="black_overlay"></div> 
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
					"data":data,
					"oper":"verify"
				};
				$.ajax({
					url:'/cert', 
					data:JSON.stringify( sendData ),
					type:'POST',
					contentType: "application/json",
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
		url: '/cert',
		type: 'GET',
		data: {
			"page":0
		},
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
					"</tr>");
			});
		}
	});
	
	/* Operation List AJAX */
	var groupList;
	$.ajax({
		url:'/user/' + $('#userId').val() +'/applied-groups',
		type: 'GET',
		success:function(data) {
			groupList = data.data;
			for ( var id in groupList) {
				for ( var name in groupList[id] ) {
					$("#groupId").append($('<option>',{
						value: id,
						text: name
					}));
				} 
			}
		}
	});
	
	$('#groupId').change(function() { 
		$('#groupSolutionName').empty();
		$('#groupSolutionName').val(""); 
		for ( var id in groupList ) { 
			if ( id == $('#groupId').val() ) {
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
	
	$('input[type=radio][name=exportWhat]').change(function() {
		none4Display();
		if ( this.value == 1 ) {
			showKeyPairInput();
		} else if ( this.value == 2 ) {
			showPrivateKeyInput();
		} else if ( this.value == 3 ) {
			showPublicKeyInput();
		} else if ( this.value == 4 ) {
			showCertChainInput();
		}
	});
	
	$('input[type=radio][name=certChainExpType]').change(function() {
		if ( this.value == '1' /*HEAD*/ ) {
			$('input[type=checkbox][name=certOutputType]').prop('disabled', false);
		} else if ( this.value =='2' /*ENTIRE*/ ) {
			$('input[type=checkbox][name=certOutputType]').prop("checked",true);
			$('input[type=checkbox][name=certOutputType]').prop('disabled', true);
		}
	});
	
	$('input[type=radio][name=keyPairExpType]').change(function() {
		if ( this.value == '1' || this.value == '3' ) {
			$('#keyPairPin').prop('disabled', false);
			$('#keyPairPinChk').prop('disabled', false);
		} else if ( this.value='2' ) {
			$('#keyPairPin').val("");
			$('#keyPairPinChk').val("");
			
			$('#keyPairPin').prop('disabled', true);
			$('#keyPairPinChk').prop('disabled', true);
		}
	});

	$(document).on("click", "#certTable tr", function(e) {
		if ( this.firstElementChild.textContent == "번호" ) return;
		certManagingPopOpen( this.firstElementChild.textContent );
	});

});

function certFormSubmit( form ) {
	var certFormExtJSON = {
		"oper":"register"
	};
	
	submitData( form, certFormExtJSON );
}

function ifNotSelectGroup() {
	if ( !$('#groupSolutionName').val() ) 
		alert('그룹을 먼저 선택하세요.'); 
}

function certManagingPopOpen( certId ) {
	$("#certId4Managing").val( certId );
	
	if ( $('#certOneInfoTbl tr').length > 1 ) {
		$('#certOneInfoTbl  > tbody:last > tr:last').remove();
	}
	
	loadCertInfoOne( certId );
	
	$("#fade").css("display", "block");
	$("#viewPop").css("display", "block");
}

function certManagingPopClose() {
	$("#viewPop").css("display", "none");
	$("#fade").css("display", "none");
}

function showKeyPairInput() {
	$('#keyPairDv').css("display","block");
	
}

function showPrivateKeyInput() {
	$('#privateKeyDv').css("display","block");
}

function showPublicKeyInput() {
	$('#publicKeyDv').css("display","block");
}

function showCertChainInput() {
	$('#certChainDv').css("display","block");
}

function none4Display() {
	$('#certChainDv').css("display","none");
	$('#publicKeyDv').css("display","none");
	$('#privateKeyDv').css("display","none");
	$('#keyPairDv').css("display","none");
}

function downloadCert() {
	var certId = $('#certId4Managing').val();
	var data;
	var fExt;
	
	if ( $('input[type=radio][name=exportWhat]:checked').val() == 1 ) {
			
		if ( $('#keyPairPw').val() != $('#keyPairPwChk').val() ) {
			alert( '키 비밀번호 다름' );
			return;
		}
		
		if ( $('#keyPairPin').val() != $('#keyPairPinChk').val() ) {
			alert( 'PIN 다름' );
			return;
		}
		
		data = {
			"exportWhat":$('input[type=radio][name=exportWhat]:checked').val(),				
			"exportType":$('input[type=radio][name=keyPairExpType]:checked').val(),
			"password": $('#keyPairPw').val(),
			"pin": $('#keyPairPin').val()
		}
		
		if ( $('input[type=radio][name=keyPairExpType]:checked').val() == 1 )
			fExt = "p12";
		else if ( $('input[type=radio][name=keyPairExpType]:checked').val() == 2 )
			fExt = "pem";
		else if ( $('input[type=radio][name=keyPairExpType]:checked').val() == 3 )
			fExt = "jks";
		
		console.log( data );
	 } else if ( $('input[type=radio][name=exportWhat]:checked').val() == 2 ) {
		if ( $('#privateKeyPw').val() != $('#privateKeyPwChk').val() ) {
			alert( '비밀번호 다름' );
			return;
		}
		
		data = {
			"exportWhat":$('input[type=radio][name=exportWhat]:checked').val(),				
			"isPem":$('input[type=checkbox][name=privateKeyExpType]').is(':checked'),
			"password": $('#privateKeyPw').val()
		}
		
		fExt = "pkcs8";
		
		console.log( data);
	 } else if ( $('input[type=radio][name=exportWhat]:checked').val() == 3 ) {
		data = {
			"exportWhat":$('input[type=radio][name=exportWhat]:checked').val(),
			"isPem":$('input[type=checkbox][name=publicKeyExpType]').is(':checked')
		} 
		
		fExt = "pub";
		
		console.log( data);
	 } else if ( $('input[type=radio][name=exportWhat]:checked').val() == 4 ) {
		 data = {
   		 	"exportWhat":$('input[type=radio][name=exportWhat]:checked').val(),
			"exportType":$('input[type=radio][name=certChainExpType]:checked').val(),
			"isPem":$('input[type=checkbox][name=certOutputType]').is(':checked')
		 }
		 
		 fExt = "cer";
		 
		 console.log( data);
	}
	
	data.oper = "download";
	
	$.ajax({
		url: "/cert/" + certId,
		type:'post',
		dataType: "JSON",
		contentType: "application/json",
		data: JSON.stringify( data ),
		success:function(data) {
			b64ToFileSave("user." + fExt, data.fileB64);
		},
		error:function( response ) {
			var r = jQuery.parseJSON(response.responseText);
			alert ( r );
		}
	});
}

function loadCertInfoOne( certId ) {

	for ( i=0; i<$("#certTable tr").length; i++ ) {
		if ( $("#certTable tr:eq( " + i + ")").find("td").eq(0).html() == certId ) {
			$("#certOneInfoTbl").append( "<tr>" + "<td>" + $("#certTable tr:eq( " + i + ")").find("td").eq(1).html() + "</td>" +
					"<td>" + $("#certTable tr:eq( " + i + ")").find("td").eq(2).html() + "</td>" +
			 		"<td>" + $("#certTable tr:eq( " + i + ")").find("td").eq(3).html() + "</td>" +
					"<td>" + $("#certTable tr:eq( " + i + ")").find("td").eq(4).html() + "</td>" +
					"<td>" + $("#certTable tr:eq( " + i + ")").find("td").eq(5).html() + "</td>" + "</tr>" 
			);
		}
	}
}

</script>

<%@ include file="/WEB-INF/views/common/bottom.jsp"%>