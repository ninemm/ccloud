
Utils = {
	context: '',	
	devMode: false,	
	provCacheName: 'PROV_CACHE_NAME',
	cityCacheName: 'CITY_CACHE_NAME',
	countryCacheName: 'COUNTRY_CACHE_NAME',
	longitudeCache: 'LONGITUDE_CACHE',
	latitudeCache: 'LATITUDE_CACHE',
	dateTypeCache: 'DATE_TYPE',
	rootName: '大冶市',
	cp: [114.974842,30.098804],
	loading: function() {
		layer.open({
			type: 2,
			content: '加载中...'
		});
	},
	close: function() {
		layer.closeAll();
	},
	planePath: 'path://M1705.06,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705.06,1318.313z'
}

function Map() {
	var struct = function(key, value) {
		this.key = key;
		this.value = value;
	}

	var put = function(key, value) {
		for (var i = 0; i < this.arr.length; i++) {
			if (this.arr[i].key === key) {
				this.arr[i].value = value;
				return;
			}
		}
		this.arr[this.arr.length] = new struct(key, value);
	}

	var get = function(key) {
		for (var i = 0; i < this.arr.length; i++) {
			if (this.arr[i].key === key) {
				return this.arr[i].value;
			}
		}
		return null;
	}

	var remove = function(key) {
		var v;
		for (var i = 0; i < this.arr.length; i++) {
			v = this.arr.pop();
			if (v.key === key) {
				continue;
			}
			this.arr.unshift(v);
		}
	}

	var size = function() {
		return this.arr.length;
	}

	var isEmpty = function() {
		return this.arr.length <= 0;
	}
	this.arr = new Array();
	this.get = get;
	this.put = put;
	this.remove = remove;
	this.size = size;
	this.isEmpty = isEmpty;
}

function initWxConfig(appId, timestamp, nonceStr, signature) {
    wx.config({
        debug: false,
        appId: appId,
        timestamp: timestamp,
        nonceStr: nonceStr,
        signature: signature,
        jsApiList: [
            'checkJsApi',
            'getLocation',
            'chooseImage',
            'uploadImage',
            'downloadImage'
        ]
    });
}

function wxReadyGetLocation(callback) {
    wx.ready(function() {
        wxGetLocation(callback);
    });
}

function wxGetLocation(callback) {
    wx.getLocation({
        type: 'wgs84',
        success: function (res) {
            var latitude = res.latitude;                      // 纬度，浮点数，范围为90 ~ -90
            var longitude = res.longitude;                    // 经度，浮点数，范围为180 ~ -180。
            var speed = res.speed;                            // 速度，以米/每秒计
            var accuracy = res.accuracy;                      // 位置精度

            var point = new BMap.Point(longitude, latitude);  // 将经纬度转化为百度经纬度
            var geoc = new BMap.Geocoder();                   // 获取百度地址解析器

            var translateCallback = function (point) {            // 回调函数
                var lng = parseFloat(point.lng);
                var lat = parseFloat(point.lat);
                geoc.getLocation(point, function(rs) {
                    var addComp = rs.addressComponents;
                    var address = addComp.province + addComp.city + addComp.district + addComp.street + addComp.streetNumber;

	                $.cookie(Utils.longitudeCache, rs.point.lng, { expires: 7 });
	                $.cookie(Utils.latitudeCache, rs.point.lat, { expires: 7 });

	                $.cookie(Utils.provCacheName, addComp.province, { expires: 7 });
	                $.cookie(Utils.cityCacheName, addComp.city, { expires: 7 });
	                $.cookie(Utils.countryCacheName, addComp.district, { expires: 7 });

                    callback(lng, lat, addComp, address);

                });
            };
            setTimeout(function() {
                BMap.Convertor.translate(point, 0, translateCallback);//真实经纬度转成百度坐标
            }, 100);
        },
        cancel: function (res) {
            console.log('用户拒绝授权获取地理位置');
        },
        fail: function (res) {
            console.log(JSON.stringify(res));
        }
    });
}

function BaiduMap() {
	
	var getLocation = function(loadOrderData) {
		
		var geolocation = new BMap.Geolocation();
		geolocation.getCurrentPosition(function(r) {
			if(this.getStatus() == BMAP_STATUS_SUCCESS) {
				var rp = new BMap.Point(r.point.lng,r.point.lat);
				var gc = new BMap.Geocoder();
				gc.getLocation(rp, function(rs) {
					var addComp = rs.addressComponents;
					provName = addComp.province;
					cityName = addComp.city;
					countryName = addComp.district;
					
					localStorage.provName = provName;
					localStorage.cityName = cityName;
					localStorage.countryName = countryName;
					
					//console.log(rs.point.lng, rs.point.lat)
					
					$.cookie(Utils.longitudeCache, rs.point.lng, { expires: 7 });
					$.cookie(Utils.latitudeCache, rs.point.lat, { expires: 7 });
					
					$.cookie(Utils.provCacheName, provName, { expires: 7 });
					$.cookie(Utils.cityCacheName, cityName, { expires: 7 });
					$.cookie(Utils.countryCacheName, countryName, { expires: 7 });
					
					curProvName = provName.substring(0, provName.length - 1);
					//alert(countryName);
					//console.log(provName, cityName + '-' + countryName);
					
					/*if(countryName.length != 0) {
						//clickCity({"name": cityName}, provName);
						//provName = provName + "省";
					} else if(cityName.length != 0){
						//clickProv({"name": provName}, true);
					} else {
						mapRender(true);
					}*/
					if (loadOrderData) {
						mapDataUrl = Utils.context + '/json/'+cityMap[curProvName]+'/'+cityMap[cityName]+'.json';
						loadOrderData();
						$city.text(cityName);
					}
			  });
			}
		},{enableHighAccuracy: true})
		
	}
	
	this.getLocation = getLocation;
}

function StrKit() {
	var convertProvName = function(provName) {
		
		if (provName.indexOf('省') != -1) {
			return provName.substring(0, provName.length - 1);
		} else if (provName.indexOf('自治区') != -1) {
			if (provName.indexOf('新疆') > -1)
				return '新疆';
			else if (provName.indexOf('宁夏') > -1)
				return '宁夏';
			else if (provName.indexOf('广西') > -1)
				return '广西';
			else if (provName.indexOf('西藏') > -1)
				return '西藏';
			else if (provName.indexOf('内蒙古') > -1)
				return '内蒙古';
		}
		return provName;
	}
	
	this.convertProvName = convertProvName;
}

var MapSet = {
	GetLocation:function(){
		var geolocation = new BMap.Geolocation();
		geolocation.getCurrentPosition(function(r) {
			if(this.getStatus() == BMAP_STATUS_SUCCESS) {
				var rp = new BMap.Point(r.point.lng,r.point.lat);
				var gc = new BMap.Geocoder();
				gc.getLocation(rp,function(rs) {
					var addComp = rs.addressComponents;
					provName = addComp.province;
					cityName = addComp.city;
					countryName = addComp.district;
					provName = provName.substring(0, provName.length - 1);

					if(countryName.length != 0) {
				  //clickCity({"name": cityName}, provName);
						provName = provName + "省";
					} else if(cityName.length !=0){
				  //clickProv({"name": provName}, true);
					} else {
						mapRender(true);
					}
			  });
			}
		},{enableHighAccuracy: true})
	}
};

$(".trOpen").click(function() {
	$(this).hide().siblings().show();
	$(this).parents('table').find('tbody tr:gt(5)').show();
});

$(".trClose").click(function() {
	$(this).hide().siblings().show();
	$(this).parents('table').find('tbody tr:gt(5)').hide();
});