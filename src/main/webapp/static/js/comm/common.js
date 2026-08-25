/**
 * 공통 script
 */
const contextPath = '';

//StringBuffer 설정
class StringBuffer {
    constructor() {
        this.buffer = new Array();
    }
    
    append(str) {
        this.buffer[this.buffer.length] = str;
    }
    
    toString(s) {
        return this.buffer.join((s || ''));
    }
}

// 장치구분
const agent = navigator.userAgent.toUpperCase();
// OS
let os;
// CA 목록
let caList;
// URL SCHEME
let urlScheme;
// 앱링크(Intent-packcage or Universal Link)
let appLink;

if (agent.includes('ANDROID')) {
	os = 'AOS';
} else if (agent.includes('IPHONE') || agent.includes('IPAD') || agent.includes('IPOD')) {
	os = 'IOS';
} else {
	os = 'ETC';
}

// SP정보 생성
function fnMakeSpInfo(servceEl, caEl, ifType) {
	const param = {
		  url: contextPath + '/mip/spInfo'
		, dataType: 'json'
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				if (servceEl) {
					for (let i=0; i<resultData.serviceList.length; i++) {
						if (resultData.serviceList[i].presentType != 3) {
							$(servceEl).append('<option value="' + resultData.serviceList[i].svcCode + '">' + resultData.serviceList[i].serviceName + '</option>');
						}
					}
				}
				
				if (caEl) {
					caList = resultData.caList;
					
					fnMakeRandomCaList(caEl, caList, ifType);
				}
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

// CA 목록 랜덤 생성
function fnMakeRandomCaList(caEl, caList, ifType) {
	const nums = new Array();
	const rannums = new Array();

	for (let i = 0; i < caList.length; i++) {
		nums[i] = i;
	}
	
	for (let i = caList.length; i > 0; i--) {
		const r = Math.floor(Math.random() * i);
		
		rannums.push(nums[r]);
		
		nums.splice(r, 1);
	}
	
	for (let i=0; i<caList.length; i++) {
		const ca = caList[rannums[i]];
		
		if (ifType == 'PUSH') {
			$(caEl).append('<option value="' + ca.appCode + '">' + ca.appName + '</option>');
		} else {
			if (os == 'AOS') {
				$(caEl).append('<option value="' + ca.appCode + '">' + ca.appName + '</option>');
			} else if (os == 'IOS' && ca.appLinkIos) {
				$(caEl).append('<option value="' + ca.appCode + '">' + ca.appName + '</option>');
			}
		}
	}
	
	// 앱 클릭시
	$(caEl).change(function() {
		for (let i=0; i<caList.length; i++) {
			if (appCode == caList[i].appCode) {
				if (os == 'AOS') {
					urlScheme = caList[i].appLinkAos;
				} else if (os == 'IOS') {
					urlScheme = caList[i].appLinkIos;
				}
				
				break;
			}
		}
	});
}

// 서비스 목록 생성
function fnMakeServiceList(servceEl) {
	const param = {
		  url: contextPath + '/mip/spInfo'
		, dataType: 'json'
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				if (servceEl) {
					for (let i=0; i<resultData.serviceList.length; i++) {
						if (resultData.serviceList[i].presentType != 3) {
							$(servceEl).append('<option value="' + resultData.serviceList[i].svcCode + '">' + resultData.serviceList[i].serviceName + '</option>');
						}
					}
				}
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

let TRX_CODE = '';  // 거래코드

// 거래상태 조회
function fnGetTrxsts() {
	const trxcode = TRX_CODE;
	
	const errMsg = new StringBuffer();

	if ((trxcode || '') == '') {
		errMsg.append('거래코드가 없습니다.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const param = {
		  url: contextPath + '/mip/trxsts'
		, dataType: 'json'
		, data: JSON.stringify({'data': Base64.encode(JSON.stringify({'trxcode': trxcode}))})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				const trxStsCodeVal = {
					  '0001': '서비스 요청'
					, '0002': 'profile 요청'
					, '0003': '검증 요청'
					, '0004': '검증 완료'
					, '0005': '검증 오류'
				};
				
				$('#trxcodeTag').text(resultData.trxcode);
				$('#trxStsCodeTag').text(trxStsCodeVal[resultData.trxStsCode] + '(' + resultData.trxStsCode + ')');
				$('#vpVerifyResultTag').text(resultData.vpVerifyResult);
				$('#regDtTag').text(resultData.regDt);
				$('#profileSendDtTag').text(resultData.profileSendDt);
				$('#vpReceptDtTag').text(resultData.vpReceptDt);
				$('#imgSendDtTag').text(resultData.imgSendDt);
				$('#udtDtTag').text(resultData.udtDt || resultData.regDt);
				
				const vp = resultData.vp;
				
				if (vp) {
					$('#vpTag').text('보기');
					
					$('#vpArea').val(vp);
					
					$('#vpTag').click(function() {
						if ($(this).text() == '보기') {
							$('#vpArea').show();
						
							$(this).text('닫기');
						} else {
							$('#vpArea').hide();
						
							$(this).text('보기');
						}
					});
				} else {
					$('#vpTag').text('');
					$('#vpArea').val('');
					$('#vpArea').hide();
				}
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

// 초기화
function fnResetTrxsts() {
	TRX_CODE = '';
	
	$('#reqDataArea').val('');
	
	$('#trxcodeTag').text('');
	$('#trxStsCodeTag').text('');
	$('#vpVerifyResultTag').text('');
	$('#regDtTag').text('');
	$('#profileSendDtTag').text('');
	$('#vpReceptDtTag').text('');
	$('#imgSendDtTag').text('');
	$('#udtDtTag').text('');
	
	$('#vpArea').val('');
}
