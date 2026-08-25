$(function() {
	// 팝업 호출 클릭시
	$('#openPageBtn').click(function() {
		fnOpenPage();
	});
	
	fnMakeServiceList('#svcCode');
});

function fnOpenPage() {
	const svcCode = $('#svcCode').val();
	const appLinkDiv = $('#appLinkDiv').val();
	
	const errMsg = new StringBuffer();
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return false;
	}
	
	window.open('../../html/vp/vpMobilePopup.html?svcCode=' + svcCode + '&appLinkDiv=' + appLinkDiv, '_mobile');
}