//页面初始化
var strKit = new StrKit();

var provName = $.cookie(Utils.provCacheName);
var cityName = $.cookie(Utils.cityCacheName);
var countryName = $.cookie(Utils.countryCacheName);
var curProvName = strKit.convertProvName(provName);

var dateType = $.cookie(Utils.dateTypeCache) == null ? 0 : $.cookie(Utils.dateTypeCache);
$("#date-div").find(".date").eq(dateType).addClass("active-date");

$(function() {
	initTopHoverTree("gotop", 100, 10, 50);
});


// 时间切换选择, 如近一天，近一周等
$(".date").click(function(e) {
	var $a = $(e.currentTarget);
	$a.parent().find(".date").removeClass("active-date");
	$a.addClass("active-date");
});


//共通展开收起
$(".trOpen").click(function() {
	$(this).hide().siblings().show();
	$(this).parents('table').find('tbody tr:gt(5)').show();
});

$(".trClose").click(function() {
	$(this).hide().siblings().show();
	$(this).parents('table').find('tbody tr:gt(5)').hide();
});