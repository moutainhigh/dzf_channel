var contextPath = DZF.contextPath;

$(function() {
	initQry();
	lineQry();//线状图初始化并查询数据
	initListen();
});

/**
 * 初始化
 */
function initQry(){
	initPeriod("#bperiod");
	initPeriod("#eperiod");
	//线状图 季度查询条件初始化
	$('#byear').combobox('setValue',$('#year').val());
	$('#eyear').combobox('setValue',$('#year').val());
	$('#bjd').combobox('setValue',$('#bjdv').val());
	$('#ejd').combobox('setValue',$('#ejdv').val());
	//线状图 年度查询条件初始化
	$('#bqyear').combobox('setValue',$('#year').val());
	$('#eqyear').combobox('setValue',$('#year').val());
}

/**
 * 线状图监听事件
 */
function initListen(){
	$("#month").show();
	$("#quarter").hide();
	$("#qyear").hide();
	$("#qrytype").combobox({
		onChange : function(n, o) {
			if(o == "" || o == null){
				return;
			}
			$("#month").hide();
			$("#quarter").hide();
			$("#qyear").hide();
			if(n == 1){
				$("#month").show();
			}else if(n == 2){
				$("#quarter").show();
			}else if(n == 3){
				$("#qyear").show();
			}
			lineQry();
		}
	});
}


/**
 * 线状图查询
 */
function lineQry(){
	var qrytype = $('#qrytype').combobox('getValue');
	var bperiod = "";
	var eperiod = "";
	if(qrytype == 1){
		bperiod = $('#bperiod').combobox('getValue');
		eperiod = $('#eperiod').combobox('getValue');
	}else if(qrytype == 2){
		var byear = $('#byear').combobox('getValue');
		var eyear = $('#eyear').combobox('getValue');
		bperiod = byear + "-" +$('#bjd').combobox('getValue');
		eperiod = eyear + "-" +$('#ejd').combobox('getValue');
	}else if(qrytype == 3){
		bperiod = $('#bqyear').combobox('getValue');
		eperiod = $('#eqyear').combobox('getValue');
	}
	$.ajax({
		type : 'POST',
		url : DZF.contextPath + '/report/channelData!query.action',
		dataType : "json",
		data : {
			qtype : qrytype,
			bperiod : bperiod,
			eperiod : eperiod,
		},
		async : false,
		success : function(result){
			if(result.success){
				var row = result.rows;
				if(row != null){
					initBarChart(row);
				}
			}else{
				Public.tips({
					content : result.msg,
					type : 2,
				});
			}
		}
	});
}


/**
 * 柱状图初始化
 */
function initBarChart(row){
	var myChart = echarts.init(document.getElementById('main'));
	var option = {
	    color: ['#5b9bd5', '#ed7d31'],

	    tooltip: {
	        trigger: 'axis',
	        axisPointer: { // 坐标轴指示器，坐标轴触发有效
	            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
	        }
	    },
	    legend: {
	        data: ['预付款扣款金额', '返点扣款金额'],
	        right: '90',
	    },
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    toolbox: {
	        feature: {
	            saveAsImage: {}
	        }
	    },
	    xAxis: [{
	        type: 'category',
	        data: row.sdate,
	    }],
	    yAxis: [{
	        type: 'value'
	    }],
	    series: [{
	        name: '预付款扣款金额',
	        type: 'bar',
	        data: row.fir,
	    },
	    {
	        name: '返点扣款金额',
	        type: 'bar',
	        stack: '广告',
	        data: row.sec,
	    }]
	};
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}