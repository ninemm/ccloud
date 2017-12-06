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

$.today = function() {
	return new Date().Format("yyyy-MM-dd");
}

Utils = {
	context: '',	
	devMode: true,
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

// 菜单
var open = false;
var finished = true;
var $currentInput = null;

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

function openPop() {
	open = true;
	$("body")
			.append(
					'<div class="pop-input">'
							+ '<input type="number" value="0">'
							+ '<div class="pop-button">'
							+ '<a class="gray-button width-50" id="cancel-input">取消</a>'
							+ '<a class="blue-button width-50" id="confirm-input">确定</a>'
							+ '</div>' + '</div>');
	$(".pop-input").addClass("animated fadeIn");
	$(".layer").addClass("layer-pop-show");
	$(".pop-input input").val($currentInput.val());
	$(".pop-input input").focus();
}

function closePop() {
	open = false;
	$(".pop-input").addClass("animated fadeOut");
	setTimeout(function() {
		$(".pop-input").remove();
		$(".layer").removeClass("layer-pop-show");
	}, 300);
}

function confirmInput() {
	$currentInput.val($(".pop-input input").val());
	closePop();
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
	}).on("touchend", "input[type=number]", function() {
		if (!open) {
			$currentInput = $(this);
			openPop();
		}
	}).on("touchstart", "#cancel-input", function() {
		closePop();
	}).on("touchstart", "#confirm-input", function() {
		confirmInput();
	}).on("touchstart", ".operate:first-child", function() {
		var $input = $(this).next();
		$input.val(Number($input.val()) - 1);
	}).on("touchstart", ".operate:last-child", function() {
		var $input = $(this).prev();
		$input.val(Number($input.val()) + 1);
	}).on("change", "input[name=add-gift]", function() {
		$(this).parent().next().slideToggle("fast");
	});
})