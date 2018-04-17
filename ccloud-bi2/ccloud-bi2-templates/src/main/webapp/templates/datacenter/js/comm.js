//页面初始化
var strKit = new StrKit();

var provName = $.cookie(Utils.provCacheName);
var cityName = $.cookie(Utils.cityCacheName);
var countryName = $.cookie(Utils.countryCacheName);
var curProvName = strKit.convertProvName(provName);

var dateType = $.cookie(Utils.dateTypeCache) == null ? 0 : $.cookie(Utils.dateTypeCache);
$("#date-div").find(".date").eq(dateType).addClass("active-date");

$(function () {
	initTopHoverTree("gotop", 100, 10, 50);
});

// 时间切换选择, 如近一天，近一周等
$(".date").click(function (e) {
	var $a = $(e.currentTarget);
	$a.parent().find(".date").removeClass("active-date");
	$a.addClass("active-date");
});


//共通展开收起
$(".trOpen").click(function () {
	$(this).hide().siblings().show();
	$(this).parents('table').find('tbody tr:gt(5)').show();
});

$(".trClose").click(function () {
	$(this).hide().siblings().show();
	$(this).parents('table').find('tbody tr:gt(5)').hide();
});

var $layer = $(".layer");
var $combinSearch;

$(document).on('click', '#combin-filter-btn, #dealer-select', function () { //打开组合筛选
	$combinSearch = $("#" + $(this).data('target'));
	openCombinSearch();
}).on('click', '#combin-filter .cancel-search-btn', function () { //关闭组合筛选
	closeCombinSearch();
}).on("click", ".layer", function () { //点击遮罩关闭菜单、组合筛选
	closeCombinSearch();
}).on('click', '#combin-filter .filter-item span', function () {
	var obj = $(this);
	if (obj.attr('class') == 'red-button') {
		obj.removeClass('red-button');
	} else {
		obj.addClass('red-button');
	}
});

var ModalHelper = (function (bodyCls) {
	var scrollTop;
	return {
		afterOpen: function () {
			scrollTop = document.scrollingElement.scrollTop;
			document.body.classList.add(bodyCls);
			document.body.style.top = -scrollTop + "px";
		},
		beforeClose: function () {
			document.body.classList.remove(bodyCls);
			document.scrollingElement.scrollTop = scrollTop;
		}
	};
})("modal-open");

//打开组合搜索
function openCombinSearch() {
	ModalHelper.afterOpen();
	$layer.addClass("layer-show");
	$combinSearch.show().removeClass("slideOutRight").addClass("slideInRight");
}

//关闭组合搜索
function closeCombinSearch() {
	ModalHelper.beforeClose();
	$combinSearch.removeClass("slideInRight").addClass("slideOutRight");
	setTimeout(function () {
		$layer.removeClass("layer-show");
	}, 300);
}

$("#start-date").calendar({
	maxDate: function() {
		return $('#end-date').val()
	}
});
$("#start-date").val($.today());
$("#end-date").calendar({
	minDate: function() {
		var date = new Date($('#start-date').val().replace(/-/g,"/"));
		date.setDate(date.getDate() - 1);
		return date.Format("yyyy-MM-dd")
	}
});
$("#end-date").val($.today());