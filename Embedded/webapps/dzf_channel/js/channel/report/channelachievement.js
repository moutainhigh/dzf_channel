var contextPath = DZF.contextPath;

$(function() {
	initLineChart();
	initBarChart();
});

/**
 * 线状图初始化
 */
function initLineChart(){
	// 基于准备好的dom，初始化echarts实例
	var myChart = echarts.init(document.getElementById('main'));
	var option = {
	    title: {
	        text: '业绩环比'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data: ['扣款金额', '合同金额'],
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
	        name: '扣款金额',
	        type: 'line',
	        stack: '总量',
	        data: [120, 132, 101, 134, 90, 230]
	    },
	    {
	        name: '合同金额',
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
	        data: ['往期', '本期'],
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
	        name: '往期',
	        type: 'bar',
	        data: [320, 332, 301, 334, 390, 330]
	    },
	    {
	        name: '本期',
	        type: 'bar',
	        stack: '广告',
	        data: [120, 132, 101, 134, 90, 230]
	    }]
	};
	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}