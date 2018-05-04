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
	if(cityName.length != 0) {
		renderCityMap({"name": cityName}, curProvName);
	} else if(provName.length != 0){
		renderProvMap({"name": curProvName}, true);
	} else {
		renderChinaMap(true);
	}
	initDatetimepicker('#start-date', 'yyyy-MM-dd', 2, 4, 2);
	initDatetimepicker('#end-date', 'yyyy-MM-dd', 2, 4, 2);
	$("[data-id='" + brandId + "']").addClass('red-button').siblings().removeClass('red-button');
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

$("#searchInput").on("input", function() {
	if ($("#searchInput").val() == '') {
		$(".weui-cell").show();
	}

	$(".weui-cell").hide().filter(":contains("+ $("#searchInput").val().trim() +")").show();
});

$("#searchClear").on("click", function() {
	$(".weui-cell").show();
});

$("#searchCancel").on("click", function() {
	$(".weui-cell").show();
});

function selectDealer(selectDataArea,obj) {
	Utils.loading();
	dataArea = selectDataArea;
	$.cookie(Utils.dataAreaCache, dataArea, { expires: 1 });
	$("#dealerName").text($(obj).find(".weui-cell__hd").eq(0).text() + '汇总信息');
	$.ajax({
		url:CPATH + '/pc/selectDealer',
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

var prov_data = [];
var city_data = [];
var country_data = [];

//回到中国地图界面
function goBackChinaMap() {

	renderChinaMap(true);

	provName = '';
	cityName = '';
	countryName = '';
	$.cookie(Utils.provCacheName, provName, { expires: 7 });
	$.cookie(Utils.cityCacheName, cityName, { expires: 7 });
	$.cookie(Utils.countryCacheName, countryName, { expires: 7 });

}

//回到省界面
function goBackProvMap() {

	cityName = '';
	countryName = '';
	$.cookie(Utils.provCacheName, provName, { expires: 7 });
	$.cookie(Utils.cityCacheName, cityName, { expires: 7 });
	$.cookie(Utils.countryCacheName, countryName, { expires: 7 });

	curProvName = strKit.convertProvName(provName);
	renderProvMap({"name": curProvName}, true);

}

//点击地图上的省
function renderProvMap (result, isRender) {
	$('#city').css('visibility','hidden');
	cityName = '';
	countryName = '';
	var maxData = 0;

	if(result.name == '北京' || result.name == '上海' || result.name == '天津' || result.name == '台湾' || result.name == '重庆'){
		provName = result.name + '市';
	} else {
		provName = result.name + '省';
	}
	curProvName = strKit.convertProvName(provName);

	$.cookie(Utils.provCacheName, provName, { expires: 7 });
	$.cookie(Utils.cityCacheName, cityName, { expires: 7 });
	$.cookie(Utils.countryCacheName, countryName, { expires: 7 });

	$.ajax({
		url:CPATH + "/biSales/queryMapData",
		type:"post",
		data:{"provName": provName, "cityName": '', countryName: '', startDate: startDate, endDate: endDate, dataArea: dataArea, brandId: brandId},
		dataType:"json"
	}).done(function(data) {
		city_data = [];
		for(var i = 0; i < data.length; i++) {
			city_data.push({"name": data[i].city_name, "value": data[i].realAmount});
			if(data[i].realAmount > maxData) maxData = data[i].realAmount;
		}

		//setTimeout(function () {
		$('#prov_view').text(result.name);
		$('#prov').css('visibility', 'visible');

		if (isRender) {
			$('#china_map').css('display','none');
			$('#prov_map').css('display','block');
			$('#city_map').css('display','none');
		}
		//}, 500);

		var _provMap = document.getElementById('prov_map');
		var provChart = echarts.init(_provMap);
		$(_provMap).width(1000);
		// 选择省的单击事件
		var selectProv = result.name.toString();

		provChart.showLoading({
			text: '加载中...',
			effect: 'whirling'
		});

		// 从json获取地图数据
		$.get(CTPATH + '/json/' + cityMap[selectProv] + '/'+cityMap[selectProv] + '.json', function (data) {
			echarts.registerMap(selectProv, data);

			provChart.hideLoading();
			provChart.resize();

			provChart.setOption({
				tooltip: {
					trigger: 'item',
					textStyle: {fontSize: 9},
					formatter: function loadData(result){
						if(isNaN(result.value)) return result.name + '<br />总金额: 0';
						else return result.name+'<br />总金额:'+result.value + '万元';
					}
				},
				visualMap:{
					min: 0,
					max: maxData+1,
					splitNumber: 0,
					text: ['高','低'],
					realtime: false,
					selectedMode: false,
					realtime: false,
					show: true,
					itemWidth: 20,
					itemHeight: 120
				},
				series: [{
					type: 'map',
					map: selectProv,                  // 要和echarts.registerMap（）中第一个参数一致
					roam: true,
					scaleLimit: { min: 1, max: 4 },   // 缩放
					mapLocation:{
						y:60
					},
					itemSytle:{
						emphasis:{label:{show:false}}
					},
					label: {
						normal: {
							show: true
						},
						emphasis: {
							show: true
						}
					},
					data : city_data.reverse()
				}]
			});

			provChart.on('click', function(rel){
				renderCityMap(rel, selectProv);
			});
		});
	});
	initData();
}

//点击地图上的市
function renderCityMap(rel, selectProv) {
	var maxData = 0;
	cityName = rel.name;

	$.cookie(Utils.provCacheName, provName, { expires: 7 });
	$.cookie(Utils.cityCacheName, cityName, { expires: 7 });
	$.cookie(Utils.countryCacheName, countryName, { expires: 7 });

	$.ajax({
		url:CPATH + "/biSales/queryMapData",
		type:"post",
		data:{"provName": provName, "cityName": cityName, "countryName": '', startDate: startDate, endDate: endDate, dataArea: dataArea, brandId: brandId},
		dataType:"json"
	}).done(function(data) {
		country_data = [];
		for(var i = 0; i < data.length; i++)
		{
			country_data.push({"name": data[i].country_name, "value": data[i].realAmount});
			if (data[i].realAmount > maxData) maxData = data[i].realAmount;
		}

		setTimeout(function () {
			function contains(arr, obj) {
				var i = arr.length;
				while (i--) {
					if (arr[i] === obj) {
						return true;
					}
				}
				return false;
			}

			var arr = new Array('北京','上海','天津','台湾','重庆');

			if(contains(arr, selectProv) == false) {

				$('#prov_view').text(selectProv);
				$('#prov').css('visibility','visible');
				$('#city_view').text(rel.name);
				$('#city').css('visibility','visible');

				$('#china_map').css('display','none');
				$('#prov_map').css('display','none');
				$('#city_map').css('display','block');

			} else {
				$('#china_map').css('display','none');
				$('#prov_map').css('display','block');
				$('#city_map').css('display','none');
			}
		}, 500);

		// 选择市的单击事件
		var selectCity = rel.name;

		// 调取后台数据
		countryMapRender(selectProv, selectCity, maxData);
	});
	initData();
}

// 城市地图(市)
function countryMapRender(selectProv, selectCity, maxData) {
	var _cityMap = document.getElementById('city_map');
	var cityChart = echarts.init(_cityMap);
	$(_cityMap).width(1000);

	cityChart.showLoading({
		text: '加载中...',
		effect: 'whirling'
	});

	$.get(CTPATH + '/json/'+cityMap[selectProv]+'/'+cityMap[selectCity]+'.json', function (data) {

		echarts.registerMap(selectCity, data);
		cityChart.hideLoading();
		cityChart.resize();

		cityChart.setOption({
			tooltip: {
				trigger: 'item',
				textStyle: {fontSize: 9},
				formatter: function loadData(result) {
					if(isNaN(result.value)) return result.name + '<br />总金额: 0';
					else return result.name+'<br />总金额:'+result.value + '万元';
				}
			},
			visualMap:{
				min: 0,
				max: maxData+1,
				splitNumber: 0,
				text: ['高','低'],
				realtime: false,
				selectedMode: false,
				realtime: false,
				show: true,
				itemWidth: 20,
				itemHeight: 120
			},
			series: [{
				type: 'map',
				map: selectCity ,//要和echarts.registerMap（）中第一个参数一致
				roam: true,
				scaleLimit: { min: 1, max: 4 },//缩放
				mapLocation: {
					y:60
				},
				itemSytle: {
					emphasis: {label: {show: false} }
				},
				label: {
					normal: {
						show: true
					},
					emphasis: {
						show: true
					}
				},
				data: country_data.reverse()
			}]
		});

		/*cityChart.on('click', function(rel) {
			setTimeout(function () {
			}, 500);
		});*/
	});
}

//中国地图渲染
function renderChinaMap(isRender) {
	$('#prov').css('visibility','hidden');
	$('#city').css('visibility','hidden');
	var maxData = 0;

	$.ajax({
		url: CPATH + "/biSales/queryMapData",
		type: "post",
		data: {startDate: startDate, endDate: endDate, dataArea: dataArea, brandId: brandId},
		dataType:"json"
	}).done(function(data) {
		prov_data = [];
		for(var i = 0; i < data.length; i++) {
			prov_data.push({
				"name": data[i].prov_name.substring(0, data[i].prov_name.length -1),
				"value": data[i].realAmount
			});

			if (data[i].realAmount > maxData)
				maxData = data[i].realAmount;
		}

		if (isRender) {
			//setTimeout(function () {
			$('#china_map').css('display','block');
			$('#city_map').css('display','none');
			$('#prov_map').css('display','none');
			//}, 100);
		}

		var _chinaMap = document.getElementById('china_map');
		$(_chinaMap).width(1000);
		var chart = echarts.init(_chinaMap);                     // 在id为china_map的dom元素中显示地图
		chart.showLoading({
			text: '加载中...',
			effect: 'whirling'
		});

		$.get(CTPATH + '/json/china.json', function (mapJson) {

			echarts.registerMap('china', mapJson);
			chart.hideLoading();
			chart.resize();

			chart.setOption({
				tooltip: {
					trigger: 'item',
					textStyle: {fontSize: 9},
					formatter: function (result){
						if(isNaN(result.value))
							return result.name + '<br />总金额: 0';
						else
							return result.name + '<br />总金额:' + result.value + '万元';
					}
				},
				visualMap: {
					min: 0,
					max: maxData + 1,
					splitNumber: 0,
					text: ['高','低'],
					realtime: false,
					selectedMode: false,
					realtime: false,
					show: true,
					itemWidth: 20,
					itemHeight: 120
				},
				series: [{
					type: 'map',
					map: 'china',                   // 要和echarts.registerMap（）中第一个参数一致
					roam: true,
					scaleLimit: { min: 1, max: 4},  // 缩放
					mapLocation:{
						y:60
					},
					itemSytle: {
						emphasis: {label: {show: false}}
					},
					label: {
						normal: {
							show: true,
							textStyle: {fontSize: 9},
						},
						emphasis: {
							show: true,
							textStyle: {fontSize: 9},
						}
					},
					data: prov_data.reverse()
				}]
			});

			chart.on('click', function(result) {
				renderProvMap(result, true);
			});
		});
		initData();
	});
}