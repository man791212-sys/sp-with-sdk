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
// CA 목록
let caList;
// 거래코드
let trxcode;
// QR 카운터
let qrCounter = 180;
// QR 카운터 단위
let qrCounterInterval;
// QR 카운터 시작시간
let countStart;

$(function() {
	$('#layout').css('display', 'block');
	$('#pushAuth').css('display', 'none');
	
	// PUSH 버튼 클릭시
	$('#push').click(function() {
		fnClickPush();
	});
	
	// PUSH 엔터키 입력시
	$('#push').keypress(function(e) {
		if (e.key == 'Enter') {
			fnClickPush();
		}
	});
	
	// QR 버튼 클릭시
	$('#qr').click(function() {
		fnClickQr();
	});
	
	// QR 엔터키 입력시
	$('#qr').keypress(function(e) {
		if (e.key == 'Enter') {
			fnClickQr();
		}
	});
	
	// PUSH 이름, 전화번호 변경시
	$('#name, #telno').change(function() {
		fnReqPushActive();
	});
	
	// 전체동의 클릭시
	$('#allAgree').change(function() {
		const checked = $(this).prop('checked');
		const isQr = $('#qr.selected').length == 1;
		
		if (isQr) {
			if (checked) {
				fnReqQr();
			} else {
				fnCancelQrCounter();
			}
		}
		
		$('.agree-list input').prop('checked', checked);
		
		fnReqPushActive();
	});
	
	// 동의 클릭시
	$('.agree dd > ul > li input').change(function() {
		if ($(this).prop('checked')) {
			let flag = true;
			
			$('.agree dd > ul > li input').each(function() {
				if (!$(this).prop('checked')) {
					flag = false;
				}
			});
			
			if (flag) {
				$('#allAgree').trigger('click');
			} else {
				$('#allAgree').prop('checked', flag);
			}
		} else {
			$('#allAgree').prop('checked', false);
		}
		
		fnReqPushActive();
	});
	
	// 보기 버튼 클릭시
	$('.agree-list .btn').click(function() {
		$('.agree-popup .policy').css('display', 'none');
		$('.agree-popup').css('display', 'flex');
		
		const id = $(this).siblings('input').attr('id').replace('Yn', '');
		
		$('#' + id).css('display', 'block');
		$('#' + id + 'Title').focus();
	});
	
	// 동의팝업 닫기 버튼 클릭시
	$('.agree-popup .policy .btn-area button').click(function() {
		$('.agree-popup').css('display', 'none');
		
		const id = $(this).attr('id').replace('Close', 'Yn');
		
		$('#' + id).parent().find('button').focus();
	});
	
	// push 인증 요청 버튼 클릭시
	$('#btnReqPush').click(function() {
		if ($(this).hasClass('active')) {
			fnReqPush();
		}
	});
	
	// pushAuth 인증 완료 버튼 클릭시
	$('#pushAuthCmplBtn').click(function() {
		if ($(this).hasClass('active')) {
			fnChkAuthCmpl('push');
		}
	});
	
	// pushAuth 닫기 버튼 클릭시
	$('#pushAuthCloseBtn').click(function() {
		$('#layout').css('display', 'block');
		$('#pushAuth').css('display', 'none');
	});
	
	// 인증팝업 닫기 버튼 클릭시
	$('#btnCloseMidAuth').click(function() {
		fnCloseMidAuth();
	});
	
	// 인증 완료 버튼 클릭시
	$('#btnChkAuthCmpl').click(function() {
		if ($(this).hasClass('active')) {
			fnChkAuthCmpl('qr');
		}
	});
	
	fnGetParamater();
	
	fnMakeCaList();
});

// 파라미터 추출 및 설정
function fnGetParamater() {
	const url = window.location.search.replace('?', '');
	const params = url.split('&');
	
	for (let i in params) {
		if (params[i].includes('svcCode')) {
			svcCode = params[i].split('=')[1];
		}
	}
}

// PUSH 버튼 클릭
function fnClickPush() {
	$('#tpAgreeYn').parent().show();
	
	$('#caCover').attr('class', 'cover-hide');
	
	$('#' + appCode).trigger('click');
	
	$('.ca-list li').attr('tabindex', "0");
	
	$('#qr').removeClass('selected');
	$('#push').addClass('selected');
	
	$('#push').attr('aria-selected', true);
	$('#qr').attr('aria-selected', false);
	
	$('#tabPush').css('display', 'block');
	$('#tabQr').css('display', 'none');
	
	$('#btnReqPush').css('display', 'block');
	$('#btnChkAuthCmpl').css('display', 'none');
	
	$('.tab-menu h2').css('display', 'inline');
	
	$('.ca-layout .info-text').text('※ 모바일 신분증을 발급받은 앱 선택');
}

// QR 버튼 클릭
function fnClickQr() {
	$('#tpAgree').attr('checked', false);
	$('#tpAgreeYn').parent().hide();
	
	$('#caCover').attr('class', 'cover');
	
	$('.ca-list li').removeClass('selected');
	$('.ca-list li').attr('tabindex', "-1");
	
	$('#push').removeClass('selected');
	$('#qr').addClass('selected');
	
	$('#push').attr('aria-selected', false);
	$('#qr').attr('aria-selected', true);
	
	$('#tabPush').css('display', 'none');
	$('#tabQr').css('display', 'flex');
	
	$('#btnReqPush').css('display', 'none');
	$('#btnChkAuthCmpl').css('display', 'block');
	
	$('.tab-menu h2').css('display', 'none');
	
	$('.ca-layout .info-text').text('※ 모바일 신분증을 발급받은 앱으로 QR을 촬영하여 인증하세요.');
	
	if ($('#allAgree').prop('checked') && $('#qrCounter').text() == '180') {
		fnReqQr();
	} else {
		$('.qr-info').focus();
	}
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
	const caEl = '#caList';
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
		
		$(caEl).append('<li id="' + ca.appCode + '" aria-selected="false" tabindex="0"><span class="img-box"><img src="' + ca.appIcon + '" alt="' + ca.appName + ' BI"></span><span>' + ca.appName + '</span></li>');
	}
	
	$('.ca-list li').click(function() {
		fnSelectCa(this);
	});
	
	$('.ca-list li').keypress(function(e) {
		if (e.key == 'Enter') {
			fnSelectCa(this);
		}
	});
	
	$('.ca-list li:first-child').trigger('click');
}

// CA 선택
function fnSelectCa(obj) {
	$('.ca-list li').removeClass('selected');
	$(obj).addClass('selected');
	
	$('.ca-list li').attr('aria-selected', false);
	$(obj).attr('aria-selected', true);
	
	$('#selectedIcon').attr({'src': $('.ca-list li.selected img').attr('src'), 'alt': $('.ca-list li.selected img').attr('alt')});
	
	appCode = obj.id;
}

// PUSH 요청 활성화
function fnReqPushActive() {
	let flag = false;
	
	const selectMenu = $('.tab-menu li.selected').attr('id');
	
	if (selectMenu == 'push') {
		if ($('#name').val() && $('#telno').val() && $('#allAgree').prop('checked')) {
			flag = true;
		}
	} else if ($('#allAgree').prop('checked')) {
		flag = true;
	}
	
	if (flag) {
		$('#btnReqPush').addClass('active');
		
		$('#btnReqPush').focus();
	} else {
		$('#btnReqPush').removeClass('active');
	}
}

// PUSH 요청
function fnReqPush() {
	const ifType = 'PUSH';
	const mode = 'direct';
	const name = $('#name').val();
	const telno = $('#telno').val();
	
	const errMsg = new StringBuffer();
	
	if (appCode == '') {
		errMsg.append('앱을 선택해주세요.');
	}
	
	if (name.trim() == '') {
		errMsg.append('이름을 입력해주세요.');
	}
	
	if (telno.trim() == '') {
		errMsg.append('전화번호를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const telnoRegex = /^01[016789]-?\d{3,4}-?\d{4}$/;
	
	if (telno != '' && !telnoRegex.test(telno)) {
		alert('전화번호 형식이 맞지 않습니다.');
		
		$('#telno').val('');
		$('#telno').focus();
		
		return false;
	}

	const param = {
		  url: contextPath + '/web/push/start'
		, dataType: 'json'
		, data: JSON.stringify({
			  'ifType': ifType
			, 'mode': mode
			, 'svcCode': svcCode
			, 'appCode': appCode
			, 'name': name
			, 'telno': telno
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				trxcode = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				$('#layout').css('display', 'none');
				$('#pushAuth').css('display', 'block');
				
				$('#pushAuth').focus();
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

// QR 요청
function fnReqQr() {
	qrCounter = 180;
	
	$('#tabQr > div').css('display', 'none');
	$('.qr-wait').css('display', 'block');
	
	$('.qr-wait').focus();
	
	const ifType = 'QR-MPM';
	const mode = 'direct';
	
	trxcode = '';
	
	const param = {
		  url: contextPath + '/web/qr/start'
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
			
			$('#tabQr > div').css('display', 'none');
			
			if (data.result) {
				$('#qrCodeArea').empty();
				
				trxcode = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				const qrCodeArea = document.getElementById('qrCodeArea');
				const width = 200;
				const heigth = 200;
				
				new QRCode(qrCodeArea, {
					  width: width
					, height: heigth
					, text: resultData.m200Base64
				});
				
				$('#btnChkAuthCmpl').addClass('active');
				
				countStart = Date.now();
				
				qrCounterInterval = setInterval(fnQrCounter, 1000);
				
				$('.qr-success').css('display', 'flex');
				
				$('.qr-success').focus();
			} else {
				alert(resultData.errmsg);
				
				$('.qr-fail').css('display', 'block');
				
				$('.qr-fail').focus();
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			$('#tabQr > div').css('display', 'none');
			$('.qr-fail').css('display', 'block');
			
			$('.qr-fail').focus();
			
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// QR Counter
function fnQrCounter() {
	qrCounter = 180 - Math.round((Date.now() - countStart)/1000);
	
	if (qrCounter <= 0) {
		fnCancelQrCounter();
	} else {
		$('#qrCounter').text(qrCounter);
	}
}

// QR Counter Cancel
function fnCancelQrCounter() {
	qrCounter = 0;
		
	clearInterval(qrCounterInterval);
	
	$('#qrCodeArea').empty();
	$('#qrCodeArea').attr('title', '');
	$('#btnChkAuthCmpl').removeClass('active');
	
	$('#qrCodeArea').append('<button id="qrReReqBtn" type="button">재시도</button>').click(function() {
		fnReqQr();
	});
	
	$('#qrCounter').text(qrCounter);
	
	$('#tabQr > div').css('display', 'none');
	$('.qr-info').css('display', 'block');
	
	$('.qr-info').focus();
}

// 인증 완료 확인
function fnChkAuthCmpl(div) {
	const param = {
		  url: contextPath + '/web/auth/cmpl'
		, dataType: 'json'
		, data: JSON.stringify({
			  'trxcode': trxcode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			const resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				if (div == 'push') {
					$('#layout').css('display', 'block');
					$('#pushAuth').css('display', 'none');
				}
				
				alert('인증 완료되었습니다.');
				
				$('#btnCloseMidAuth').trigger('click');
			} else if (resultData.errcode == '10201') {
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
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// 모바일 신분증 팝업 닫기
function fnCloseMidAuth() {
	if (window.self == window.top) {
		window.close();
	} else {
		window.parent.postMessage({'action': 'close'});
	}
}
