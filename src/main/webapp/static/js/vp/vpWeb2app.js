$(function() {
	// M200 요청 버튼 클릭시
	$('#m200ReqBtn').click(function() {
		fnM200Req();
	});
	
	// App 호출 버튼 클릭시
	$('#appCallBtn').click(function() {
		fnAppCallBtn();
	});
	
	// 초기화 버튼 클릭시
	$('#resetBtn').click(function() {
		$('#form')[0].reset();
		
		fnResetTrxsts();
	});
	
	// 응답 상태 확인 버튼 클릭시
	$('#trxstsBtn').click(function() {
		fnGetTrxsts();
	});
	
	// 앱호출방식 변경시
	$('#appLinkDiv').parent().change(function() {
		fnChangeAppLinkDiv();
	});
	
	fnInit();
});

// 초기화 설정
function fnInit() {
	if (os == 'ETC') {
		alert('지원하지 않는 기기입니다.');
		
		return false;
	}
	
	if (os == 'AOS' && $('#appLinkDiv').val() == '1') {
		$('#appCode').parent().hide();
	} else {
		$('#appCode').parent().show();
	}
	
	fnMakeSpInfo('#svcCode', '#appCode', 'App2App');
}

// M200 요청
function fnM200Req() {
	const ifType = 'App2App';
	const mode = 'direct';
	const svcCode = $('#svcCode').val();
	
	const errMsg = new StringBuffer();
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	TRX_CODE = '';

	const param = {
		  url: contextPath + '/app2app/start'
		, dataType: 'json'
		, data: JSON.stringify({
			  'ifType': ifType
			, 'mode': mode
			, 'svcCode': svcCode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				TRX_CODE = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				for (let i=0; i<caList.length; i++) {
					if ($('#appCode').val() == caList[i].appCode) {
						if (os == 'AOS') {
							urlScheme = caList[i].appLinkAos;
							appLink = caList[i].appLink2Aos;
						} else if (os == 'IOS') {
							urlScheme = caList[i].appLinkIos;
							appLink = caList[i].appLink2Ios;
						}
						
						break;
					}
				}
				
				if ($('#appLinkDiv').val() == '1') {
					$('#reqDataArea').val(urlScheme + '://verify?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '&clientScheme=');
				} else {
					if (os == 'AOS') {
						$('#reqDataArea').val('intent://verify?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '#Intent;scheme=' + urlScheme + ';package=' + appLink + ';end');
					} else if (os == 'IOS') {
						$('#reqDataArea').val(appLink + '/verify.html?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '&clientScheme=');
					}
				}
				
				fnGetTrxsts();
			} else {
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// App 호출
function fnAppCallBtn() {
	document.location.href = $('#reqDataArea').val();
}

// 앱호출방식 변경
function fnChangeAppLinkDiv() {
	if (os == 'AOS' && $('#appLinkDiv').val() == '1') {
		$('#appCode').parent().hide();
	} else {
		$('#appCode').parent().show();
	}
}
