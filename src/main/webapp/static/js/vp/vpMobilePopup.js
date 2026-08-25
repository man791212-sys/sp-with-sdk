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

// 서비스코드
let svcCode;
// 앱코드
let appCode;
// 앱호출방식
let appLinkDiv;
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
// 거래코드
let trxcode;

$(function() {
	// 모바일 신분증 팝업 닫기 버튼 클릭시
	$('#btnCloseMidAuth').click(function() {
		fnCloseMidAuth();
	});
	
	// 보기 버튼 클릭시
	$('.agree-layout .body li').click(function() {
		$('.agree-popup .policy').css('display', 'none');
		$('.agree-popup').css('display', 'block');
		
		const id = this.id.replace('Policy', '');
		
		$('#' + id).css('display', 'block');
		$('#' + id + 'Title').focus();
	});
	
	// 동의 팝업 닫기 버튼 클릭시
	$('.agree-popup .policy .btn-area button').click(function() {
		$('.agree-popup').css('display', 'none');
		
		const id = $(this).attr('id').replace('Close', 'Policy');
		
		$('#' + id).find('a').focus();
	});
	
	// 동의 창 닫기 버튼 클릭시
	$('#btnCloseAgreeLayout').click(function() {
		$('.app-layout').css({'display': 'block', 'top': '0'});
		$('.agree-layout').css({'display': 'none', 'bottom': '-330px'});
		
		$('.app-layout .body li.selected').focus();
	});
	
	// 모두 동의하고 인증요청 버튼 클릭시
	$('#btnDoAgreeAuth').click(function() {
		fnReqAuth();
	});
	
	// 인증요청 창 닫기 버튼 클릭시
	$('#btnCloseAuthReq').click(function() {
		$('#btnCloseMidAuth').trigger('click');
	});
	
	// 인증완료 버튼 클릭시
	$('#btnChkAuthCompl').click(function() {
		fnChkAuthCmpl();
	});
	
	fnInit();
});

// 초기화 설정
function fnInit() {
	const url = window.location.search.replace('?', '');
	const params = url.split('&');
	
	for (let i in params) {
		if (params[i].includes('svcCode')) {
			svcCode = params[i].split('=')[1];
		}
		
		if (params[i].includes('appLinkDiv')) {
			appLinkDiv = params[i].split('=')[1];
		}
	}
	
	if (!svcCode) {
		alert('서비스코드를 입력해주세요');
		
		return false;
	}
	
	if (!appLinkDiv) {
		alert('앱호출방식을 입력해주세요');
		
		return false;
	}
	
	if (agent.includes('ANDROID')) {
		os = 'AOS';
	} else if (agent.includes('IPHONE') || agent.includes('IPAD') || agent.includes('IPOD')) {
		os = 'IOS';
	} else {
		os = 'ETC';
	}
	
	if (os == 'AOS') {
		if (appLinkDiv == '1') {
			$('.app-layout').css({'display': 'none', 'top': '100%'});
			$('.agree-layout').css({'display': 'block', 'bottom': '0'});
			$('#btnCloseAgreeLayout').css('display', 'none');
			
			urlScheme = 'mobileid';
			
			$('#termsAgreePolicy a').focus();
		} else {
			$('.app-layout').css({'display': 'block', 'top': '0'});
			$('.agree-layout').css('bottom', '-330px');
			
			fnMakeCaList();
		}
	} else if (os == 'IOS') {
		$('.app-layout').css({'display': 'block', 'top': '0'});
		$('.agree-layout').css({'display': 'none', 'bottom': '-330px'});
		
		fnMakeCaList();
	} else {
		alert('지원하지 않는 기기입니다.');
		
		window.close();
	}
	
	$('#contents').focus();
}

// CA 목록 생성
function fnMakeCaList() {
	const param = {
		  url: contextPath + '/mip/spInfo'
		, dataType: 'json'
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				caList = resultData.caList;
				
				fnMakeRandomCaList();
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
function fnMakeRandomCaList() {
	const caEl = '.app-layout .body ul';
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
		const hasAppLink = (os == 'AOS' && ((appLinkDiv == '1' && ca.appLinkAos) || (appLinkDiv == '2' && ca.appLink2Aos)))
						|| (os == 'IOS' && ((appLinkDiv == '1' && ca.appLinkIos) || (appLinkDiv == '2' && ca.appLink2Ios)));

		if (hasAppLink) {
			$(caEl).append('<li id="' + ca.appCode + '" role="button" tabindex="0"><a class="img-box"><img src="' + ca.appIcon + '" alt="' + ca.appName + ' BI"></a><span>' + ca.appName + '</span></li>');
		}
	}
	
	// 앱 클릭시
	$('.app-layout .body li').click(function() {
		fnSelectCa(this);
	});
	
	// 앱 클릭시
	$('.app-layout .body li').keypress(function(e) {
		if (e.key == 'Enter') {
			fnSelectCa(this);
		}
	});
}

// CA 선택
function fnSelectCa(obj) {
	$('.app-layout .body li').removeClass('selected');
	
	$(obj).addClass('selected');
	
	appCode = obj.id;
	
	$('.app-layout').css({'display': 'none', 'top': '100%'});
	$('.agree-layout').css({'display': 'block', 'bottom': '0'});
	
	for (const element of caList) {
		if (appCode == element.appCode) {
			if (appLinkDiv == '1') {
				if (os == 'AOS') {
					urlScheme = element.appLinkAos;
				} else if (os == 'IOS') {
					urlScheme = element.appLinkIos;
				}
			} else if (os == 'AOS') {
				urlScheme = element.appLinkAos;
				appLink = element.appLink2Aos;
			} else if (os == 'IOS') {
				appLink = element.appLink2Ios;
			}
			
			break;
		}
	}
}

// 모바일 신분증 팝업 닫기
function fnCloseMidAuth() {
	window.close();
}

// 인증 요청
function fnReqAuth() {
	const ifType = 'App2App';
	const mode = 'direct';
	
	trxcode = '';
	
	const param = {
		  url: contextPath + '/web/app2app/start'
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
				trxcode = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;

				if (appLinkDiv == '1') {
					document.location.href = urlScheme + '://verify?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '&clientScheme=';
				} else if (os == 'AOS') {
					document.location.href = 'intent://verify?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '#Intent;scheme=' + urlScheme + ';package=' + appLink + ';end';
				} else if (os == 'IOS') {
					document.location.href = appLink + '/verify.html?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '&clientScheme=';
				}
				
				$('.agree-layout').css('display', 'none');
				$('.auth-layout').css('display', 'block');
				
				$('.auth-layout').focus();
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

// 인증 완료 확인
function fnChkAuthCmpl() {
	const param = {
		  url: contextPath + '/web/auth/cmpl'
		, dataType: 'json'
		, data: JSON.stringify({
			  'trxcode': trxcode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				alert('인증 완료되었습니다.');
				
				$('#btnCloseMidAuth').trigger('click');
			} else {
				const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				if (resultData.errcode == '10201') {
					alert('인증 완료 후 시도하시기 바랍니다.');
				} else if (resultData.errcode == '10202') {
					alert('인증 시간이 만료되었습니다.');
					
					$('#btnCloseMidAuth').trigger('click');
				} else if (resultData.errcode == '19003') {
					alert('인증 오류가 발생 했습니다. 다시시도하시기 바랍니다.');
					
					$('#btnCloseMidAuth').trigger('click');
				} else {
					alert(resultData.errmsg);
				}
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}
