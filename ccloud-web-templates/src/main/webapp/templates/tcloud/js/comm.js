// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

// 今天的日期
$.today = function () {
	return new Date().Format("yyyy-MM-dd");
}
  
// 今天的日期和星期
$.week = function () {
	return "星期" + "日一二三四五六".charAt(new Date().getDay());
}

Utils = {
	context: '',	
	devMode: false,
	pageSize: 10,
	ajax: function(url, param, callback) {
		$.ajax({
            type: "post",
            url: url,
            data: param,
            dataType: 'json',
            success: function(res) {
            	if (callback)
            		callback(res);
            },
            error: function(res) {
            	toastr.error(res.message);
            }
        });
	},
	get: function(url) {
		$.get(url, function(res) {
            if (res.errorCode > 0) {
                toastr.warn(res.message);
            } else {
                toastr.success(res.message);
                location.reload();
            }
        });
	},
	confirm: function(title, text, url) {
		$.confirm({
            title: title,
            text: text,
            onOK: function() {
                $.get(url, function(res) {
		            if (res.errorCode > 0) {
		                toastr.warn(res.message);
		            } else {
		                toastr.success(res.message);
		                location.reload();
		            }
		        });
            },
            onCancel: function() {}
        });
	},
	infinite: function($root, $loadmore, data, loading, callback) {
		$root = $root || $(window.body);
		$root.infinite().on("infinite", function() {
			console.log('infinite start');
			if ($loadmore) $loadmore.show();
			
			if (loading) return;
			loading = true;
			$.ajax({
	            type: "post",
	            url: '${CPATH}/api',
	            data: data,
	            dataType: 'json',
	            success: function(res) {
	            	if (callback)
	            		callback();
	            },
	            error: function(res) {
	                toast.error(res.message);
	            }
	        });
		});
	}
}

//菜单
var open = false;
var finished = true;
var $currentInput = null;
var $menu = $(".hidden-menu ul");
var $menuBtn = $("#button");
var $layer = $(".layer");
var $combinSearch = $('#combin-filter');
//打开菜单
function openMenu() {
	open = true;
	finished = false;
	$(".hidden-menu ul").show().addClass("animated fadeInUp");
	$("#button").addClass("close-button");
	$(".layer").addClass("layer-show");
	setTimeout(function() {
		$(".hidden-menu ul").removeClass("animated fadeInUp");
		finished = true;
	}, 300);
}
//关闭菜单
function closeMenu() {
	finished = false;
	$(".hidden-menu ul").addClass("animated fadeOutDown");
	$("#button").removeClass("close-button");
	setTimeout(function() {
		$(".hidden-menu ul").hide().removeClass("animated fadeOutDown");
		$(".layer").removeClass("layer-show");
		open = false;
		finished = true;
	}, 300);
}
//弹出输入框
function openPop() {
	open = true;
	$("body")
			.append(
					'<div class="pop-input">'
							+ '<input type="number" value="0">'
							+ '<div class="pop-button">'
							+ '<a class="white-button-no-border width-50" id="cancel-input">取消</a>'
							+ '<a class="red-button width-50" id="confirm-input">确定</a>'
							+ '</div>' + '</div>');
	$(".pop-input").addClass("animated fadeIn");
	$(".layer").addClass("layer-pop-show");
	$(".pop-input input").val($currentInput.val());
	$(".pop-input input").focus();
}
//关闭输入框	
function closePop() {
	open = false;
	$(".pop-input").addClass("animated fadeOut");
	setTimeout(function() {
		$(".pop-input").remove();
		$(".layer").removeClass("layer-pop-show");
	}, 300);
}
//确认输入
function confirmInput() {
	$currentInput.val($(".pop-input input").val());
	closePop();
}

//阻止滚动后触发touchend事件
function stopTouchendPropagationAfterScroll() {
  var locked = false;

  window.addEventListener('touchmove', function (ev) {
    locked || (locked = true, window.addEventListener('touchend', stopTouchendPropagation, true));
  }, true);

  function stopTouchendPropagation(ev) {
    ev.stopPropagation();
    window.removeEventListener('touchend', stopTouchendPropagation, true);
    locked = false;
  }
}


//打开组合搜索
function openCombinSearch() {
  $combinSearch.show().addClass("animated bounceInRight");
  $layer.addClass("layer-show");
  setTimeout(function () {
    $combinSearch.removeClass("animated bounceInRight");
  }, 300);
}

//关闭组合搜索
function closeCombinSearch() {
  $combinSearch.addClass("animated bounceOutRight");
  setTimeout(function () {
    $layer.removeClass("layer-show");
    $combinSearch.hide().removeClass("animated bounceOutRight");
  }, 300);
}

//设定storage
function setItemBykey(key, value){
	if(window.localStorage){
		var storage = window.localStorage;
		storage.setItem(key, JSON.stringify(value));
	} else {
		alert("浏览器不支持localstorage");
	}
}
//获取storage中的数据
function getItemBykey(key){
	if(window.localStorage){
		var storage = window.localStorage;
		var json = storage.getItem(key);
        return JSON.parse(json);
	} else {
		alert("浏览器不支持localstorage");
	}
}
//移除storage中的item
function removeItemBykey(key){
	if(window.localStorage){
		var storage = window.localStorage;
		storage.removeItem(key);
	} else {
		alert("浏览器不支持localstorage");
	}
}
//清空storage
function clearStorage(){
	if(window.localStorage){
		var storage = window.localStorage;
		storage.clear();
	} else {
		alert("浏览器不支持localstorage");
	}
}

function wxLocation() {
    var df = $.Deferred();
    wx.ready(function() {
        wx.checkJsApi({
            jsApiList: [
            'checkJsApi',
            'getLocation'
            ],
            success: function (res) {
                if (res.checkResult.getLocation == false) {
                    alert('你的微信版本太低，不支持微信JS接口，请升级到最新的微信版本！');
                    return;
                } else {
                    wx.getLocation({
                                type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                                success: function (res) {
                                    var latitude = res.latitude;                      // 纬度，浮点数，范围为90 ~ -90
                                    var longitude = res.longitude;                    // 经度，浮点数，范围为180 ~ -180。
                                    var speed = res.speed;                            // 速度，以米/每秒计
                                    var accuracy = res.accuracy;                      // 位置精度

                                    var point = new BMap.Point(longitude, latitude);  // 将经纬度转化为百度经纬度
                                    var geoc = new BMap.Geocoder();                   // 获取百度地址解析器  

                                translateCallback = function (point) {            // 回调函数
                                    geoc.getLocation(point, function(rs) {
                                        var addComp = rs.addressComponents;
                                            //var Address = addComp.province + addComp.city + addComp.district + addComp.street + addComp.streetNumber;

                                            $.cookie(Utils.longitudeCache, rs.point.lng, { expires: 7 });
                                            $.cookie(Utils.latitudeCache, rs.point.lat, { expires: 7 });

                                            $.cookie(Utils.provCacheName, addComp.province, { expires: 7 });
                                            $.cookie(Utils.cityCacheName, addComp.city, { expires: 7 });
                                            $.cookie(Utils.countryCacheName, addComp.district, { expires: 7 });

                                            df.resolve();

                                        });
                                };
                            },
                            fail: function(rs){

                                $.cookie(Utils.longitudeCache, 114.362938, { expires: 7 });
                                $.cookie(Utils.latitudeCache, 30.533494, { expires: 7 });

                                $.cookie(Utils.provCacheName, '湖北省', { expires: 7 });
                                $.cookie(Utils.cityCacheName, '武汉市', { expires: 7 });
                                $.cookie(Utils.countryCacheName, '武昌区', { expires: 7 });

                                alert("定位失败！请检查是否开启定位，暂时將为您显示武汉信息");
                            }
                        });
                }
            }
        });
    });
    wx.error(function(res) {
        console.log(JSON.stringify(res));
    });

    return df.promise();;
}

$(function() {
	FastClick.attach(document.body);
	$(document).on("touchstart", "#button", function() {
		if (finished) {
			if (!open) {
				openMenu();
			} else {
				closeMenu();
			}
			;
		}
		;
	}).on("touchmove", ".layer", function() {
		event.preventDefault();
	}).on("touchend", "input[type=number]:not([readonly])", function() {
		if (!open) {
			$currentInput = $(this);
			openPop();
		}
	}).on("touchstart", "#cancel-input", function() {
		closePop();
	}).on("touchstart", "#confirm-input", function() {
		confirmInput();
	}).on("touchstart", ".operate:first-child", function() {//减少商品数量
		var $input = $(this).next();
		Number($input.val())-1 >= 0 ? $input.val(Number($input.val()) - 1) : "";
	}).on("touchstart", ".operate:last-child", function() {//增加商品数量
		var $input = $(this).prev();
		$input.val(Number($input.val()) + 1);
	}).on("change", "input[name=add-gift]", function() {//点击遮罩关闭菜单
		$(this).parent().next().slideToggle("fast");
	}).on("touchend", ".layer", function () { //点击遮罩关闭菜单、组合筛选
	    closeMenu();
	    closeCombinSearch();
	}).on('touchend', '#combin-filter-btn', function () { //打开组合筛选
	    if ($combinSearch.length > 0) {
	      openCombinSearch();
	    }
	}).on('touchend', '#combin-filter .cancel-search-btn, #combin-filter .confirm-search-btn', function () { //关闭组合筛选
	    closeCombinSearch();
	}).on('touchend', '#combin-filter span', function () {
		$(this).addClass('red-button').siblings().removeClass('red-button');
	}).on('touchend', '.goback', function() {
		historyUtils.back();
	});
})