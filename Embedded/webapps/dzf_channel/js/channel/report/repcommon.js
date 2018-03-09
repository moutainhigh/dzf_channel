function initQryCommbox(){
	changeArea();
	changeProvince();
	initArea({"qtype" :2});
	initProvince({"qtype" :2});
	initManager({"qtype" :2});
}

function changeArea(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :2};
			if(!isEmpty(n)){
				queryData={'aname' : n,"qtype" :2};
				$('#ovince').combobox('setValue',null);
				$('#uid').combobox('setValue',null);
			}
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

function changeProvince(){
	 $("#ovince").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :2};
			if(!isEmpty(n)){
				queryData={'aname' : $("#aname").combobox('getValue'),'ovince':n,"qtype" :2};
				$('#uid').combobox('setValue',null);
			}
			initManager(queryData);
		}
	});
}

function initArea(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initProvince(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryProvince.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#ovince').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initManager(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryTrainer.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#uid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}
