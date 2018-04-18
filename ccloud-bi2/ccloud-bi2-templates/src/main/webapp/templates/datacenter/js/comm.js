//页面初始化
var strKit = new StrKit();

var provName = $.cookie(Utils.provCacheName) == null ? '' : $.cookie(Utils.provCacheName);
var cityName = $.cookie(Utils.cityCacheName) == null ? '' : $.cookie(Utils.cityCacheName);
var countryName = $.cookie(Utils.countryCacheName) == null ? '' : $.cookie(Utils.countryCacheName);
var curProvName = strKit.convertProvName(provName);

var dateType = $.cookie(Utils.dateTypeCache) == null ? 0 : $.cookie(Utils.dateTypeCache);
$("#date-div").find(".date").eq(dateType).addClass("active-date");

var startDate = $.cookie(Utils.startDateCache) == null ? moment().subtract(1, 'days').format('YYYY-MM-DD 00:00:00') : $.cookie(Utils.startDateCache);
var endDate = $.cookie(Utils.endDateCache) == null ? moment().format('YYYY-MM-DD HH:mm:ss') : $.cookie(Utils.endDateCache);

var dataArea = $.cookie(Utils.dataAreaCache);
var brandId = $.cookie(Utils.brandIdCache);


$(function () {
	initTopHoverTree("gotop", 100, 10, 50);
	$("[data-id='" + brandId + "']").click();
});

// 时间切换选择, 如近一天，近一周等
$(".date").click(function (e) {
	var $a = $(e.currentTarget);
	$a.parent().find(".date").removeClass("active-date");
	$a.addClass("active-date");
});

function setFilter(type) {
	dateType = type;
	if (dateType == 0) {
		startDate = moment().subtract(1, 'days').format('YYYY-MM-DD 00:00:00');
		endDate = moment().format('YYYY-MM-DD HH:mm:ss');
	} else if (dateType == 1) {
		startDate = moment().subtract(1, 'weeks').format('YYYY-MM-DD 00:00:00');
		endDate = moment().format('YYYY-MM-DD HH:mm:ss');
	} else if (dateType == 2) {
		startDate = moment().subtract(1, 'months').format('YYYY-MM-DD 00:00:00');
		endDate = moment().format('YYYY-MM-DD HH:mm:ss');
	} else if (dateType == 3) {
		startDate = $("#start-date").val() + ' 00:00:00';
		endDate =$("#end-date").val() + ' 23:59:59';

		brandId = $("#combin-filter .filter-item .red-button").eq(0).data('id');
		$.cookie(Utils.brandIdCache, brandId, {expires: 1});
	}

	$("#start-date").val(moment(startDate).format('YYYY-MM-DD'));
	$("#end-date").val(moment(endDate).format('YYYY-MM-DD'));
	$.cookie(Utils.dateTypeCache, type, {expires: 1});
	$.cookie(Utils.startDateCache, startDate, {expires: 1});
	$.cookie(Utils.endDateCache, endDate, {expires: 1});
}

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
}).on("click", ".confirm-search-btn", function () { //点击确定按钮
	closeCombinSearch();
}).on('click', '#combin-filter .filter-item span', function () {
	$(this).addClass('red-button').siblings().removeClass('red-button');
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
	maxDate: function () {
		return $('#end-date').val()
	}
});
$("#start-date").val(moment(startDate).format('YYYY-MM-DD'));
$("#end-date").calendar({
	minDate: function () {
		var date = new Date($('#start-date').val().replace(/-/g, "/"));
		date.setDate(date.getDate() - 1);
		return date.Format("yyyy-MM-dd")
	}
});
$("#end-date").val(moment(endDate).format('YYYY-MM-DD'));