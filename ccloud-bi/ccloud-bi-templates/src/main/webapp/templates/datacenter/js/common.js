
Utils = {
	devMode: 'dev',	
	rootName: '大冶市',
	cp: [114.974842,30.098804]
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
	
	var getLocation = function() {
		
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
					
					provName = provName.substring(0, provName.length - 1);
					
					//console.log(provName, cityName + '-' + countryName);
					
					if(countryName.length != 0) {
						//clickCity({"name": cityName}, provName);
						provName = provName + "省";
					} else if(cityName.length != 0){
						//clickProv({"name": provName}, true);
					} else {
						mapRender(true);
					}
              });
			}
		},{enableHighAccuracy: true})
		
	}
	
	this.getLocation = getLocation;
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
