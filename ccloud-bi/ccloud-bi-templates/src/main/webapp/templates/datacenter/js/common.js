
Utils = {
	context: '',	
	devMode: false,
	provCacheName: 'PROV_CACHE_NAME',
	cityCacheName: 'CITY_CACHE_NAME',
	countryCacheName: 'COUNTRY_CACHE_NAME',
	rootName: '大冶市',
	cp: [114.974842,30.098804],
	loading: function() {
		layer.open({
	        type: 2,
	        content: '加载中...'
	    });
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
