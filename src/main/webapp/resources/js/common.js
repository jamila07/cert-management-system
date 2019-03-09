// form Object To JSON
$.fn.serializeObject = function() {
	"use strict"
	var result = {}
	var extend = function(i, element) {
		var node = result[element.name]
		if ("undefined" !== typeof node && node !== null) {
			if ($.isArray(node)) {
				node.push(element.value)
		} else {
				result[element.name] = [ node, element.value ]
				}
		} else {
			result[element.name] = element.value
		}
	}
	$.each(this.serializeArray(), extend)
	return result
}

//submit To ajax Post
function submitData( form ) {
	var defer= $.Deferred();
	var post_url = $("#"+form).attr("action"); //get form action url
	var request_method = $("#"+form).attr("method"); //get form GET/POST method
	var form_data = JSON.stringify( $("#"+form).serializeObject() ); //Encode form elements for submission
	
	$.ajax({
		url : post_url,
		type: request_method,
		data : form_data,
		dataType : 'json',
		contentType: 'application/json',
		success:function(data) {
			alert("성공");
			defer.resolve("true");
			window.location.href = data.redirect;
		}, 
		error: function (xhr, ajaxOptions, thrownError) {
			alert(xhr.status);
		}
	});
	
	return defer.promise();
}

//paging
function printPaging( pageMaker, funcName, pageNationId ) {
	var str = "";
	
	if ( pageMaker.prev ) {
		str += "<a href='javascript:;' onclick='" + funcName +'(' + (pageMaker.startPage - 1 ) + ')' + "'> << </a>";
	}
	
	for ( var i=pageMaker.startPage, len=pageMaker.endPage; i<=len; i++ ) {
		if ( pageMaker.cri.page == i ) {
			str +=  i + " ";
		} else {
			str += "<a href='javascript:;' onclick='" + funcName +'(' + i + ')' + "'>"  + i + "</a> ";
		}
	} 
	
	if ( pageMaker.next ) {
		
		str += "<a href='javascript:;' onclick='" + funcName +'(' + (pageMaker.endPage + 1 ) + ')' + "'> >> </a>";
	}
	
	$('#' + pageNationId ).html( str );
}
 
//reload table
function reloadTable( tableLength, tableName, funcName ) {
	var deleteTableRow = "#" + tableName + " > tbody:last > tr:last"	

	for ( var i=0; i<tableLength - 1; i++ ) {
		$(deleteTableRow).remove();
	}
	
	window[funcName]( 1 );
}

// 간단한 Get 방식 파일 다운로드 
function fileDownload( uri, fileName ) {
	var a = document.createElement('A');
	a.href = uri;
	a.download = fileName;
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
}

// Base64 방식 파일 다운로드
function b64ToFileSave(fileName, b64) {
	var binaryString = window.atob(b64);
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
   
    for (var i = 0; i < binaryLen; i++) {
       var ascii = binaryString.charCodeAt(i);
       bytes[i] = ascii; 
    }
    
    var blob = new Blob([bytes], {type: "application/octet-stream"});
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
}

function getBase64(file) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = () => resolve(reader.result);
		reader.onerror = error => reject(error);
	});
}