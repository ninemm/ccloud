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
	initDatetimepicker('#start-date', 'yyyy-MM-dd', 2, 4, 2);
	initDatetimepicker('#end-date', 'yyyy-MM-dd', 2, 4, 2);
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
	}

	$("#start-date").val(moment(startDate).format('YYYY-MM-DD'));
	$("#end-date").val(moment(endDate).format('YYYY-MM-DD'));
	$.cookie(Utils.dateTypeCache, type, {expires: 1});
	$.cookie(Utils.startDateCache, startDate, {expires: 1});
	$.cookie(Utils.endDateCache, endDate, {expires: 1});

	if(cityName.length != 0) {
		renderCityMap({"name": cityName}, curProvName);
	} else if(provName.length != 0){
		renderProvMap({"name": curProvName}, true);
	} else {
		renderChinaMap(true);
	}
}

$(document).on('click', '#getBrand span', function () {
	$(this).addClass('red-button').siblings().removeClass('red-button');
	brandId = $(this).data('id');
	$.cookie(Utils.brandIdCache, brandId, {expires: 1});

	if(cityName.length != 0) {
		renderCityMap({"name": cityName}, curProvName);
	} else if(provName.length != 0){
		renderProvMap({"name": curProvName}, true);
	} else {
		renderChinaMap(true);
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


$("#start-date").val(moment(startDate).format('YYYY-MM-DD'));
$("#end-date").val(moment(endDate).format('YYYY-MM-DD'));

function initDatetimepicker(fieldId, format, minView, maxView, startView) {
	$(fieldId).datetimepicker({
		format: format,
		language:  'zh-CN',
		weekStart: 1,
		autoclose: true,
		minView: minView,
		maxView: maxView,
		startView: startView,
		forceParse: false,
		todayBtn: true,
		todayHighlight: true
	}).on('changeDate', function (ev) {
		$(this).datetimepicker('hide');
		startDate = $("#start-date").val() + ' 00:00:00';
		endDate =$("#end-date").val() + ' 23:59:59';
		$.cookie(Utils.startDateCache, startDate, {expires: 1});
		$.cookie(Utils.endDateCache, endDate, {expires: 1});
		initData();
	}).on('hide', function(e) {

	});
}

function selectDealer(selectDataArea,obj,url) {
	Utils.loading();
	dataArea = selectDataArea;
	$.cookie(Utils.dataAreaCache, dataArea, { expires: 1 });
	$("#dealerName").text($(obj).find(".weui-cell__hd").eq(0).text() + '汇总信息');
	$.ajax({
		url:url,
		type:"post",
		data:{dataArea: dataArea},
		dataType:"json"
	}).done(function(data){

		provName = data.provName;
		cityName = data.cityName;
		countryName = data.countryName;
		curProvName = strKit.convertProvName(provName);

		$.cookie(Utils.provCacheName, data.provName, { expires: 7 });
		$.cookie(Utils.cityCacheName, data.cityName, { expires: 7 });
		$.cookie(Utils.countryCacheName, data.countryName, { expires: 7 });
		$.ajax({
			async: false,
			type: "GET",
			dataType: 'jsonp',
			jsonp: 'callback',
			jsonpCallback: 'callbackfunction',
			url: "http://api.map.baidu.com/geocoder/v2/?city="+ data.cityName +"&address=" + data.countryName +"&output=json&ak=su3AmHLUcBprpnOLmVHo7r8AljbW8X6t&callback=showLocation",
			contentType: "application/json;utf-8"
		}).done(function(data){
			if(data.status == 0){
				$.cookie(Utils.longitudeCache, data.result.location.lng, { expires: 7 });
				$.cookie(Utils.latitudeCache, data.result.location.lat, { expires: 7 });
			}
		});
		if(cityName.length != 0) {
			renderCityMap({"name": cityName}, curProvName);
		} else if(provName.length != 0){
			renderProvMap({"name": curProvName}, true);
		} else {
			renderChinaMap(true);
		}

	});
}