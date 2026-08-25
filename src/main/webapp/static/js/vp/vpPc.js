$(function() {
	// 팝업 호출 클릭시
	$('#openPageBtn').click(function() {
		fnOpenPage();
	});
	
	// Iframe 팝업 호출 클릭시
	$('#openPageIframe').click(function() {
		fnOpenPageIframe();
	});
	
	fnMakeServiceList('#svcCode');
});

function fnOpenPage() {
	const svcCode = $('#svcCode').val();
	
	const errMsg = new StringBuffer();
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const w = 840;
	const h = 610;
	
	const screenLeft = window.screenLeft !== undefined ? window.screenLeft : screen.left;
	const screenTop = window.screenTop !== undefined ? window.screenTop : screen.top;
	
	const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
	const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
	
	const left = ((width / 2) - (w /2)) + screenLeft;
	const top = ((height / 2) - (h /2)) + screenTop;
	
	window.open('../../html/vp/vpPcPopup.html?svcCode=' + svcCode, '_pc', 'width=' + w + ', height=' + h + ', left=' + left + ', top=' + top);
}

function fnOpenPageIframe() {
	const svcCode = $('#svcCode').val();
	
	const errMsg = new StringBuffer();
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	const authPopup = $('<iframe src="' + '../../html/vp/vpPcPopup.html?svcCode=' + svcCode + '" sandbox="' + 'allow-same-origin allow-scripts allow-forms allow-popups allow-modals' + '"></iframe>');

	$('#iframePopup').html('');
	$('#iframePopup').css('display', 'flex');
	$('#iframePopup').append(authPopup);
}

window.addEventListener("message", (e) => {
	if (e.data.action == 'close') {
		$('#iframePopup').html();
		$('#iframePopup').css('display', 'none');
	}
});
