/**@Creator ： 刘兴
 * @描述 ： 会计工厂日历控件
 */

/**
 * 初始化日历控件
 * 
 * @param id
 *            日历控件的id，
 * @param date
 *            初始日期
 */
function initFctDate(id, date) {
	var originDate = date;
	if (!date) {
		originDate = new Date();
	}
	$(id).datebox("setValue", originDate);
	initFctDateBlur(id);
}

/**
 * 为日历控件绑定焦点丢失事件，
 * @param id
 */
function initFctDateBlur(id){
	$(id)
	.datebox("textbox")
	.blur(
			function() {
				setTimeout(
						function() {
							var enddate = $(id).datebox('getValue');
							if (enddate != ""
									&& !enddate
											.match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)) {
								$.messager.alert('提示',
										'日期格式不正确，请按"XXXX-XX-XX"的格式输入。');
								return;
							}
							if (enddate != ""
									&& new Date(enddate) == 'Invalid Date') {
								$.messager.alert('提示', '无效的日期。');
								return;
							}
						}, 500);
			});
}
/**
 * 初始化日期为当月的第一天
 * 
 * @param id
 */
function initFctBeginDate(id) {
	initFctDate(id, getFctFirstDayMonth());
}

/**
 * 初始化日期为当前日期
 * 
 * @param id
 */
function initFctCurDate(id) {
	initFctDate(id, getFctCurDay());
}

/**
 * 获取当前日期
 * 
 * @returns {格式：2016-05-17}
 */
function getFctCurDay() {
	var date = new Date();
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
			+ (date.getMonth() + 1);
	return date.getFullYear() + '-' + month + '-' + day;
}

/**
 * 获取当月第一天 
 * @returns {格式：2016-05-01}
 */
function getFctFirstDayMonth() {
	var date = new Date();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
			+ (date.getMonth() + 1);
	return date.getFullYear() + '-' + month + '-01';
}

/**
 * 获取当月第一天到当前日期的区间
 * @returns {格式：2016-05-01~2016-05-17}
 */
function initFctDateProid(){
	return getFctFirstDayMonth()+"~"+getFctCurDay();
}

/**
 * 返回两个日期之间的区间
 * @param beginid
 * @param endid
 * @returns {格式：2016-05-01~2016-05-17}
 */
function getFctDatePeorid(beginid,endid){
	return $(beginid).datebox("getValue") + " 至  "+ $(endid).datebox("getValue");
}