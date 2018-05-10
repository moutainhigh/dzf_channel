var contextPath = DZF.contextPath;

$(function() {
	initQry();
	initLineChart();
	initBarChart();
	initListen();

});

/**
 * 初始化
 */
function initQry(){
	initPeriod("#bperiod");
	initPeriod("#eperiod");
}

/**
 * 监听事件初始化
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
		}
	});
}

/**
 * 线状图初始化
 */
function initLineChart(){
	// 基于准备好的dom，初始化echarts实例
	var myChart = echarts.init(document.getElementById('main'));
	var option = {
		color: ['#5b9bd5', '#ed7d31'],
	    title: {
	        text: '业绩环比(%)'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data: ['扣款金额增长率', '合同金额增长率'],
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
	    xAxis: {
	        type: 'category',
	        boundaryGap: false,
	        data: ['2018-01', '2018-02', '2018-03', '2018-04', '2018-05', '2018-06']
	    },
	    yAxis: {
	        type: 'value'
	    },
	    series: [{
	        name: '扣款金额增长率',
	        type: 'line',
	        stack: '总量',
	        data: [120, 132, 101, 134, 90, 230]
	    },
	    {
	        name: '合同金额增长率',
	        type: 'line',
	        stack: '总量',
	        data: [220, 182, 191, 234, 290, 330]
	    }

	    ]
	};

	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}

/**
 * 柱状图初始化
 */
function initBarChart(){
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
	        data: ['2018-01', '2018-02', '2018-03', '2018-04', '2018-05', '2018-06']
	    }],
	    yAxis: [{
	        type: 'value'
	    }],
	    series: [{
	        name: '往期增长率',
	        type: 'bar',
	        data: [320, 332, 301, 334, 390, 330]
	    },
	    {
	        name: '本期增长率',
	        type: 'bar',
	        stack: '广告',
	        data: [120, 132, 101, 134, 90, 230]
	    }]
	};
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}