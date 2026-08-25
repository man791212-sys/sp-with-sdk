/**
 * Util
 * util.js
 */

$(function() {
	// Image to Hex 변환 버튼 클릭시
	$('[id^=func]').click(function() {
		fnUiSet(this);
	});
	
	// Image to Hex 변환 버튼 클릭시
	$('#imageToHexBtn').click(function() {
		fnImageToHex();
	});

	// Hex to Image 변환 버튼 클릭시
	$('#hexToImageBtn').click(function() {
		fnHexToImage();
	});
	
	// String to Hex 변환 버튼 클릭시
	$('#stringToHexBtn').click(function() {
		fnStringToHex();
	});

	// Hex to String 변환 버튼 클릭시
	$('#hexToStringBtn').click(function() {
		fnHexToString();
	});
	
	// Image to Base64 변환 버튼 클릭시
	$('#imageToBase64Btn').click(function() {
		fnImageToBase64();
	});

	// Base64 to Image 변환 버튼 클릭시
	$('#base64ToImageBtn').click(function() {
		fnBase64ToImage();
	});
	
	// Json to Base64 변환 버튼 클릭시
	$('#jsonToBase64Btn').click(function() {
		fnJsonToBase64();
	});

	// Base64 to Json 변환 버튼 클릭시
	$('#base64ToJsonBtn').click(function() {
		fnBase64ToJson();
	});
	
	// String to RSA 변환 버튼 클릭시
	$('#rsaEncryptBtn').click(function() {
		fnRsaEncrypt();
	});
	
	// RSA to String 변환 버튼 클릭시
	$('#rsaDecryptBtn').click(function() {
		fnRsaDecrypt();
	});
	
	// JSON 정렬 버튼 클릭시
	$('#prettyJsonBtn').click(function() {
		fnPrettyJson();
	});
});

// UI 설정
function fnUiSet(obj) {
	$('[id^=func]').removeClass('selected');
	
	$(obj).addClass('selected');
	
	$('form').hide();
	
	switch(obj.id) {
		case 'funcImageHax' : 
			$('#imageToHexForm').show();
			$('#hexToImageForm').show();
			
			break;
		case 'funcStringHax' : 
			$('#stringToHexForm').show();
			$('#hexToStringForm').show();
			
			break;
		case 'funcImageBase64' : 
			$('#imageToBase64Form').show();
			$('#base64ToImageForm').show();
			
			break;
		case 'funcJsonBase64' : 
			$('#jsonToBase64Form').show();
			$('#base64ToJsonForm').show();
			
			break;
		case 'funcRsa' : 
			$('#rsaEncryptForm').show();
			$('#rsaDecryptForm').show();
			
			break;
		case 'funcJson' : 
			$('#prettyJsonForm').show();
			
			break;
	}
}

// Image to Hex 변환
function fnImageToHex() {
	const errMsg = new StringBuffer();
	
	const files = $('#imageToHex1')[0].files;
	
	if (files.length == 0) {
		errMsg.append('파일을 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const reader = new FileReader();
	
	reader.readAsArrayBuffer(files[0]);
	
	reader.onloadend = (e) => {
		if (e.target.readyState == FileReader.DONE) {
			const b = e.target.result;
			const u = new Uint8Array(b);
			const hs = Array.from(u, function(a) {
				return ('0' + (a & 0xff).toString(16)).slice(-2);
			}).join('');
			
			$('#imageToHex2').text(hs);
		}
	}
}

// Hex to Image 변환
function fnHexToImage() {
	const errMsg = new StringBuffer();
	
	const hex = $('#hexToImage1').val();
	
	if (hex == '') {
		errMsg.append('파일을 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	if (hex.replace(/[^A-Fa-f0-9]/g, '').length % 2) {
		alert('자리수가 맞지 않습니다.');
		
		return false;
	}
	
	const b = new Array();
	
	for (let i = 0; i < hex.length / 2; i++) {
		const h = hex.substr(i * 2, 2);
		
		b[i] = parseInt(h, 16);
	}
	
	const ba = new Uint8Array(b);
	
	$('#hexToImage2').css('display', 'block');
	$('#hexToImage2').attr('src', URL.createObjectURL(new Blob([ba], {'type': 'application/octet-stream'})));
}

// String to Hex 변환
function fnStringToHex() {
	const errMsg = new StringBuffer();
	
	const str = $('#stringToHex1').val();
	
	if (str == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const b = new TextEncoder().encode(str);
	const u = new Uint8Array(b);
	const hs = Array.from(u, function(a) {
		return ('0' + (a & 0xff).toString(16)).slice(-2);
	}).join('');
	
	$('#stringToHex2').val(hs);
}

// Hex to String 변환
function fnHexToString() {
	const errMsg = new StringBuffer();
	
	const hex = $('#hexToString1').val();
	
	if (hex == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	if (hex.replace(/[^A-Fa-f0-9]/g, '').length % 2) {
		alert('자리수가 맞지 않습니다.');
		
		return false;
	}
	
	const b = new Array();
	
	for (let i = 0; i < hex.length / 2; i++) {
		const h = hex.substr(i * 2, 2);
		
		b[i] = parseInt(h, 16);
	}
	
	const ba = new Uint8Array(b);
	
	const str = new TextDecoder().decode(ba);
	
	$('#hexToString2').val(str);
}

// Image to Base64 변환
function fnImageToBase64() {
	const errMsg = new StringBuffer();
	
	const files = $('#imageToBase641')[0].files;
	
	if (files.length == 0) {
		errMsg.append('파일을 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const reader = new FileReader();
	
	reader.readAsDataURL(files[0]);
	
	reader.onload = () => {
		const base64 = reader.result;
		const base64Unsafe = base64.replace(/\+/g, '-').replace(/\//g, '_').split(',');
		
		if (base64Unsafe.length == 2) {
			$('#imageToBase642').text(base64Unsafe[1]);
		} else {
			alert('이미지 변환 실패!');
		}
	}
}

// Base64 to Image 변환
function fnBase64ToImage() {
	const errMsg = new StringBuffer();
	
	const base64 = $('#base64ToImage1').val();
	
	if (base64 == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const base64Unsafe = base64.replace(/\-/g, '+').replace(/\_/g, '/');
	
	$('#base64ToImage2').css('display', 'block');
	$('#base64ToImage2').attr('src', 'data:application/octet-stream;base64,' + base64Unsafe);
}

// Json to Base64 변환
function fnJsonToBase64() {
	const errMsg = new StringBuffer();
	
	const json = $('#jsonToBase641').val();
	
	if (json == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const base64 = Base64.encode(json);
	
	$('#jsonToBase642').val(base64.replace(/\+/g, '-').replace(/\//g, '_'));
}

// Base64 to Json 변환
function fnBase64ToJson() {
	const errMsg = new StringBuffer();
	
	const base64 = $('#base64ToJson1').val();
	
	if ($('#base64ToJson1').val() == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const json = Base64.decode(base64).replace(/\\u003d/g, '=');
	
	$('#base64ToJson2').val(json);
}

// String to RSA 변환
function fnRsaEncrypt() {
	const rsaEncrypt1 = $('#rsaEncrypt1').val();
	const decryptTargetDid = $('#decryptTargetDid').val();
	const isOaep = $('#isOaep').val();
	
	const errMsg = new StringBuffer();
	
	if (rsaEncrypt1.trim() == '') {
		errMsg.append('암호화 할 데이터를 입력해주세요.');
	}
	
	if (decryptTargetDid.trim() == '') {
		errMsg.append('복호화 대상 DID를 입력해주세요.');
	}
	
	if (isOaep.trim() == '') {
		errMsg.append('OAEP 적용여부를 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}

	const param = {
		  url: contextPath + '/web/rsa/encrypt'
		, dataType: 'json'
		, data: JSON.stringify({
			'data': rsaEncrypt1, 'decryptTargetDid': decryptTargetDid, 'isOaep': isOaep
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				$('#rsaEncrypt2').text(data.data);
			} else {
				const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// RSA to String 변환
function fnRsaDecrypt() {
	const rsaDecrypt1 = $('#rsaDecrypt1').val();
	const isOaep = $('#isOaep').val();
	
	const errMsg = new StringBuffer();
	
	if (rsaDecrypt1.trim() == '') {
		errMsg.append('복호화 할 데이터를 입력해주세요.');
	}
	
	if (isOaep.trim() == '') {
		errMsg.append('OAEP 적용여부를 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}

	const param = {
		  url: contextPath + '/web/rsa/decrypt'
		, dataType: 'json'
		, data: JSON.stringify({
			'data': rsaDecrypt1, 'isOaep': isOaep
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				$('#rsaDecrypt2').text(data.data);
			} else {
				const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// JSON 정렬
function fnPrettyJson() {
	const prettyJson1 = $('#prettyJson1').val();
	
	const errMsg = new StringBuffer();
	
	if (prettyJson1.trim() == '') {
		errMsg.append('JSON 정렬 데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	let result = '';
	
	try {
		result = JSON.stringify(JSON.parse(prettyJson1), null, '\t');
	} catch {
		alert('JSON 파싱오류가 발생하였습니다.');
		
		return false;
	}
	
	$('#prettyJson2').val(result);
}
