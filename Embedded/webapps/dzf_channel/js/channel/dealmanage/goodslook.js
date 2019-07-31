
var vueApp = new Vue({
	el: '#goods',
	data: function data() {
		return {
			toViewIndex:0,   //当前查看的是第几个商品
			height:0, 
			allGoodsInfo: [],  //全部商品的数组对象
			commodityDetails: {},  //当前详情的商品信息集合
			getShowImageUrl: "",    //当前大图的img 路径
			count: 1,
            mnameUnit: '',
            fullMNmae: undefined,
            provinceList: [],
            cityList: [],
            areaList: []
		};
	},
	methods: {
		/**
		* 获取首页商品展示图
		*/
		getImage: function getImage(idx) {
			var self = this;
			var homeImgPath = self.allGoodsInfo[idx].fpath ? self.allGoodsInfo[idx].fpath : 'N' ;
//			self.getShowImage(homeImgPath);
			if (self.allGoodsInfo[idx].fpath) {
				self.$set(self.allGoodsInfo[idx], 'imgpath', DZF.contextPath + "/dealmanage/goodslook!getImage.action?imgpath=" + self.allGoodsInfo[idx].fpath);
			} else {
				self.$set(self.allGoodsInfo[idx], 'imgpath', '../../images/placeholderCart.png');
			}
		},
		/** 
		* 获取详情页商品展示图
		*/	
		getDetailImage: function getDetailImage() {
			var self = this;
			if (!self.commodityDetails.children){
				self.commodityDetails.children = [{}]
			}
			self.commodityDetails.children.forEach(function (item) {
//				self.getShowImage(item.fpath);
				if (item.fpath) {
					self.$set(item, 'imgpath', DZF.contextPath + "/dealmanage/goodslook!getImage.action?imgpath=" + item.fpath);
				} else {
					self.$set(item, 'imgpath', '../../images/placeholderCart.png');
				}
				
			});
		},
		/** 
		* 获取图片
		* @param  params {String} 图片路径
		*/	
		getShowImage: function getShowImage(params) {
				axios.post(DZF.contextPath + "/dealmanage/goodslook!getImage.action", $.param({ "imgpath": params })).then(function (response) {
					if (!response.data.success) {
						return;
					}
				}).catch(function (error) {
					console.log(error);
				})
			
		},
		/** 
		* 获取当前展示的大图的url
		* @param  index {Number} 下标
		*/	
		getSelectImg: function getSelectImg(index) {
			this.getShowImageUrl = this.commodityDetails.children[index].imgpath + '&_=' + new Date().valueOf();
		},
		/**
		* 商品规格进行切换时 触发
		* @param  index {Number} 下标
		*/	
		selectSpecifications: function selectSpecifications(index){
			this.toViewIndex = index
			var item = this.commodityDetails.bodys[index]
            if(this.commodityDetails.bodys[index].specid !== this.commodityDetails.specid){
                this.commodityDetails.count = 1
			}
			this.mnameUnit = this.commodityDetails.bodys[index].mname
            if (item.num) {
                this.fullMNmae = (item.spec ? item.spec + '*' : '') + (item.type ? item.type + '*' : '') + item.num + '/' + item.mname
            } else {
                this.fullMNmae = undefined
            }
			this.$set(this.commodityDetails, "specid", this.commodityDetails.bodys[index].specid);
			this.$set(this.commodityDetails, "spec",this.commodityDetails.bodys[index].spec);
            this.$set(this.commodityDetails, "num", this.commodityDetails.bodys[index].num);
			this.$set(this.commodityDetails, "type", this.commodityDetails.bodys[index].type);
			this.$set(this.commodityDetails, "stocknum", this.commodityDetails.bodys[index].stocknum);
			this.$set(this.commodityDetails, "priceType", this.commodityDetails.bodys[index].price);
			this.$set(this.commodityDetails, "updatets", this.commodityDetails.bodys[index].updatets);
            this.commodityDetails.bodys[index].pid && this.$set(this.commodityDetails, "pid", this.commodityDetails.bodys[index].pid);
		},

		/** 
		* 打开详情页操作
		*/	
		openDetails: function openDetails(index) {
			var that = this;
			$('#deDialog').dialog('open').dialog('center').dialog('setTitle', '商品详情');
			axios.post(DZF.contextPath + "/dealmanage/goodslook!queryByID.action", $.param({ "gid": this.allGoodsInfo[index].gid })).then(function (response) {
				if (!response.data.success) {
					Public.tips({
						content: response.data.msg,
						type: 1
					});
					return;
				}
				//console.log(response)
				if (response.status == -1) {
					Public.tips({
						content: response.msg,
						type: 2
					});
				}
				if (!response.data.rows.bodys) {
                    that.commodityDetails = {children: [{imgpath: ''}]}
                    that.getSelectImg(0);
					return;
				}
				var item = response.data.rows.bodys[0]
				that.commodityDetails = response.data.rows;
                that.mnameUnit = response.data.rows.bodys[0].mname
				if (item.num) {
                    that.fullMNmae = (item.spec ? item.spec + '*' : '')+ (item.type ? item.type + '*' : '') + item.num + '/' + item.mname
				} else {
                    that.fullMNmae = undefined
				}
				that.toViewIndex = 0;
				that.$set(that.commodityDetails, "count", that.count);
                that.$set(that.commodityDetails, "count", that.count);
				that.$set(that.commodityDetails, "commodIdx", index);
				that.$set(that.commodityDetails, "specid", that.commodityDetails.bodys[0].specid);
                that.$set(that.commodityDetails, "num", that.commodityDetails.bodys[0].num);
				that.$set(that.commodityDetails, "spec",that.commodityDetails.bodys[0].spec);
				that.$set(that.commodityDetails, "type", that.commodityDetails.bodys[0].type);
				that.$set(that.commodityDetails, "stocknum", that.commodityDetails.bodys[0].stocknum);
				that.$set(that.commodityDetails, "priceType", that.commodityDetails.bodys[0].price);
				that.$set(that.commodityDetails, "updatets", that.commodityDetails.bodys[0].updatets);
                that.commodityDetails.bodys[0].pid && that.$set(that.commodityDetails, "pid", that.commodityDetails.bodys[0].pid);
				that.getDetailImage();
				that.getSelectImg(0);
			}).catch(function (error) {
				console.log(error);
			});
		},
		/** 
		* 鼠标划入 展示放大图
		*/	
		handOver: function handOver() {

			$(".bigBox").show();
			$(".glass").show();
		},
		/** 
		* 鼠标滑动 放大图
		*/	
		handMove: function handMove(event) {
			var e = event || window.event;
			var glass = $(".glass");
			var left = e.clientX - $(".large_box").offset().left - 67.5;
			var top = e.clientY - $(".large_box").offset().top - 67.5 + $(window).scrollTop();
			if (left < 0) {
				left = 0;
			}
			if (top < 0) {
				top = 0;
			}
			if (left > 135) {
				left = 135;
			}
			if (top > 135) {
				top = 135;
			}
			glass.css({ "top": top, "left": left });
			var bigleft = -left * $(".bigImage").width() / $(".large_box").width();
			var bigtop = -top * $(".bigImage").height() / $(".large_box").height();
			$(".bigImage").css({ "top": bigtop, "left": bigleft });
		},
		/** 
		* 鼠标划出 隐藏放大图
		*/	
		handOut: function handOut() {
			$(".bigBox").hide();
			$(".glass").hide();
		},
		/** 
		* 获取全部商品
		*/	
		loadGoodlist: function loadGoodlist() {
			var self = this;
			axios.post(DZF.contextPath + '/dealmanage/goodslook!queryGoods.action', { "page": 1, "rows": 12 }).then(function (response) {
				if (!response.data.success) {
					Public.tips({
						content: response.data.msg,
						type: 1
					});
					return;
				}
				//console.log(response)
				if (response.status == -1) {
					Public.tips({
						content: response.msg,
						type: 2
					});
				} else {
					Public.tips({
						content: response.data.msg,
						type: 3
					});
					self.allGoodsInfo = response.data.rows;
					self.allGoodsInfo.forEach(function (value, idx) {
						self.$set(value, 'count', self.count);
						self.getImage(idx);
					});
				}
			}).catch(function (error) {
				console.log(error);
			});
		}

	},
	created: function created() {
		//获取商品列表
		var self = this;
		self.height = window.innerHeight;
		self.loadGoodlist();
	}
});
function leftToImg() {
	$(".controls-list").stop();
	var step = $(".small_list").width();
	var x = $(".controls-list").position().left + step;
	var minLeft = $(".controls-list").width() - step;
	if (x > 0) {
		x = 0;
	}
	$(".controls-list").animate({ left: x }, 500);
}
function rightToImg(children) {
	console.log(children);
	if (children.length < 5) {
		return;
	}
	$(".controls-list").stop();
	var step = $(".small_list").width();
	var x = $(".controls-list").position().left - step;
	var minLeft = $(".controls-list").width() - step;
	if (Math.abs(x) > minLeft) {
		x = -minLeft;
	}
	$(".controls-list").animate({ left: x }, 500);
}

(function () {

	document.onselectstart=new Function("return false");
	var toggle = false
	$(".openCart").click(function(){
		if(toggle){
			$(".shopCart").animate({right: '0px',top:'0px'}, 500, function(){
				toggle = false
			});
		}else{
			$(".shopCart").animate({right: '315px',top:'0px'}, 500,function(){
				toggle = true
			});
		}
		
	})
	$(".closeCart").click(function(){
		$(".shopCart").animate({right: '0px',top:'0px'}, 500, function(){
			toggle = false
		});
	})
	
})();


$(window).resize(function () {

});

//重写提示位置方法
Public.tsinit.prototype = {
		_init : function(){
			var self = this,opts = this.options,time;
			Public.tsinit.removeAll();
			this.createStyle();

			this.closeBtn.bind('click',function(){
				self.remove();
			});
	    
			if(opts.autoClose){
				time = opts.time || opts.type == 1 ? 5000 : 3000;
				self.timeOutNum = time;
				self.timeOutRmFn = window.setTimeout(function(){
					self.remove();
				},time);
			}
		},
		createStyle : function(){
		  var self = this;
			var opts = this.options;
			this.obj = $('<div class="tips"><i></i><span class="close"></span></div>').append(opts.content);
			this.obj.on("mouseover",function(){
	      clearTimeout(self.timeOutRmFn)
	    });
	    this.obj.on("mouseout",function(){
	      self.timeOutRmFn = window.setTimeout(function(){
	        self.remove();
	      },self.timeOutNum);
	    });
			this.closeBtn = this.obj.find('.close');
			
			switch(opts.type){
				case 0 : 		//绿色，成功
					this.obj.addClass('tips-success');
					break ;
				case 1 : 		//红色，错误提示
					this.obj.addClass('tips-error');
					break ;
				case 2 : 		//黄色，警告
					this.obj.addClass('tips-warning');
					break ;
				default :
					this.obj.addClass('tips-success');
					break ;
			}
			
			this.obj.appendTo('body').hide();
			this.setDw();
		},

		setDw : function(){
			var self = this, opts = this.options;
			if(opts.width){
				this.obj.css('width',opts.width);
			}
			var scrollTop = $(window).scrollTop();
			var top = Public.isIE6 ? parseInt(opts.top) + scrollTop : parseInt(opts.top);
			this.obj.css({
				position : Public.isIE6 ? 'absolute' : 'fixed',
				left : '50%',
				top : top,
				zIndex : '999999',
				marginLeft : -self.obj.outerWidth()/2	
			});

			window.setTimeout(function(){
				self.obj.show().css({
					marginLeft : -self.obj.outerWidth()/2
				});
			},150);

			if(Public.isIE6){
				$(window).bind('resize scroll',function(){
					var top = $(window).scrollTop() + parseInt(opts.top);
					self.obj.css('top',top);
				});
			}
		},

		remove : function(){
			var opts = this.options;
			this.obj.fadeOut(200,function(){
				$(this).remove();
			});
		}
	};