var contextPath = DZF.contextPath;

$(function() {
	initQry();
	lineQry();//线状图初始化并查询数据
	chartQry();//柱状图初始化并查询数据
	initListen();

});

/**
 * 初始化
 */
function initQry(){
	initPeriod("#bperiod");
	initPeriod("#eperiod");
	//柱状图 月度查询条件初始化
	$('#tyear').combobox('setValue',$('#year').val());
	$('#tbmonth').combobox('setValue',$('#mth').val());
	$('#temonth').combobox('setValue',$('#mth').val());
	
	//线状图 季度查询条件初始化
	$('#byear').combobox('setValue',$('#year').val());
	$('#eyear').combobox('setValue',$('#year').val());
	$('#bjd').combobox('setValue',$('#bjdv').val());
	$('#ejd').combobox('setValue',$('#ejdv').val());
	
	//柱状图 季度查询条件初始化
	$('#tbjd').combobox('setValue',$('#bjdv').val());
	$('#tejd').combobox('setValue',$('#ejdv').val());
	
	//线状图 年度查询条件初始化
	$('#bqyear').combobox('setValue',$('#year').val());
	$('#eqyear').combobox('setValue',$('#year').val());
	
}

/**
 * 监听事件初始化
 */
function initListen(){
	initLineListen();
	initChartListen();
}

/**
 * 线状图监听事件
 */
function initLineListen(){
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
 * 柱状图监听事件
 */
function initChartListen(){
	$("#tmonth").show();
	$("#tseason").hide();
	$("#tqrytype").combobox({
		onChange : function(n, o) {
			if(o == "" || o == null){
				return;
			}
			$("#tmonth").hide();
			$("#tseason").hide();
			if(n == 1){
				$("#tmonth").show();
			}else if(n == 2){
				$("#tseason").show();
			}
			chartQry();
		}
	});
	
	$("#tshowtype").combobox({
		onChange : function(n, o) {
			if(o == "" || o == null){
				return;
			}
			chartQry();
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
		url : DZF.contextPath + '/report/achievementrep!queryLine.action',
		dataType : "json",
		data : {
			qtype : qrytype,
			bperiod : bperiod,
			eperiod : eperiod,
			corptype : 2,//节点类型  1：渠道总业绩分析；2：大区业绩分析； 3：省业绩分析；
		},
		async : false,
		success : function(result){
			if(result.success){
				var row = result.rows;
				if(row != null){
					initLineChart(row);
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
 * 线状图初始化
 */
function initLineChart(row){
	// 基于准备好的dom，初始化echarts实例
	var myChart = echarts.init(document.getElementById('main'));
	var option = {
		    color: ['#5b9bd5', '#ed7d31'],
		    title: {
		        text: '业绩环比(%)'
		        /* subtext: '副标题'*/
		    },
		    tooltip: {
		        trigger: 'axis'
		    },
		    legend: {
		        data: ['扣款金额增长率', '合同金额增长率'],
		        right: '200',
		    },

		    toolbox: {
		        show: true,
		        feature: {
		            saveAsImage: {}
		        }
		    },
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
//		        data: ['2018-01', '2018-02', '2018-03', '2018-04', '2018-05', '2018-06']
		        data: row.sdate,
		    },
		    yAxis: {
		        type: 'value',
		        axisLabel: {
		            formatter: '{value} '
		        }
		    },
		    series: [{
		        name: '扣款金额增长率',
		        type: 'line',
//		        data: [11, 11, 15, 13, 12, 13],
		        data: row.fir,
		    },
		    {
		        name: '合同金额增长率',
		        type: 'line',
//		        data: [11, 11, 2, 5, 3, 2],
		        data: row.sec,
		    }]
		};
	
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}

/**
 * 柱状图查询
 */
function chartQry(){
	var qrytype = $('#tqrytype').combobox('getValue');
	var tshowtype = $('#tshowtype').combobox('getValue');
	var bperiod = "";
	var eperiod = "";
	var year = $('#tyear').combobox('getValue');
	if(qrytype == 1){
		bperiod = year + "-" + $('#tbmonth').combobox('getValue');
		eperiod = year + "-" + $('#temonth').combobox('getValue');
	}else if(qrytype == 2){
		bperiod = year + "-" + $('#tbjd').combobox('getValue');
		eperiod = year + "-" + $('#tejd').combobox('getValue');
	}
	$.ajax({
		type : 'POST',
		url : DZF.contextPath + '/report/achievementrep!queryChart.action',
		dataType : "json",
		data : {
			qtype : qrytype,
			bperiod : bperiod,
			eperiod : eperiod,
			year : year,
			iptype : tshowtype,
			corptype : 2,//节点类型  1：渠道总业绩分析；2：大区业绩分析； 3：省业绩分析；
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
	var myChart = echarts.init(document.getElementById('man'));
	var option = {
	    color: ['#5b9bd5', '#ed7d31'],

	    tooltip: {
	        trigger: 'axis',
	        axisPointer: { // 坐标轴指示器，坐标轴触发有效
	            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
	        }
	    },
	    legend: {
	        data: ['往期增长率', '本期增长率'],
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
//	        data: ['2018-01', '2018-02', '2018-03', '2018-04', '2018-05', '2018-06']
	        data: row.sdate,
	    }],
	    yAxis: [{
	        type: 'value'
	    }],
	    series: [{
	        name: '往期增长率',
	        type: 'bar',
//	        data: [320, 332, 301, 334, 390, 330]
	        data: row.fir,
	    },
	    {
	        name: '本期增长率',
	        type: 'bar',
	        stack: '广告',
//	        data: [120, 132, 101, 134, 90, 230]
	        data: row.sec,
	    }]
	};
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}