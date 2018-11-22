
/**
 * 编码校验（只能含有数字和字母）
 * @param value
 * @returns {Boolean}
 */
function isLetterAndNum(value) {
	var strExp = /^[A-Za-z0-9]+$/;
	if (strExp.test(value)) {
		return true;
	} else {
		return false;
	}
}