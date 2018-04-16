	function findIndex(source, target) {
		for (var i = target.length - 1; i >= 0; i--) {
			if (target[i].sellProductId == source) {
				return i;
			}
		}
		return -1;	
	}
	
	function OrderAgain(url, url2) {
	     $.ajax({
            type: "post",
            url: url,
            dataType: "json",
            success:function(data) {
            	var order = data.order;
            	var productList = [];
            	var compositionList = [];
				var customer = {
					"customerName": order.customer_name,
					"sellerCustomerId": order.customer_id,
					"contact": order.ccontact,
					"mobile":  order.cmobile,
					"address": order.caddress
				}
				var lastItem = '';
				var parentId = '';
				var lastProductIds = [];
				var totalCount = 0;
	            $.each(data.orderDetail, function(idx, item) {
	            	if (item.is_composite == '0') {
	            		var index = $.inArray(item.sell_product_id, lastProductIds); 
	            		lastProductIds.push(item.sell_product_id);
	            		if (index < 0) {
			            	var product = getProduct(item);
			            	productList.push(product);								            		
	            		} else {
	            			var desc = findIndex(item.sell_product_id, productList);
	            			var product = productList[desc];
			            	var sub_product = product.subProduct[0];
			            	setSubItem(sub_product, item);         		
	            		}
	            	} else {
	            		if (parentId != item.composite_id) {
	            			var $newProductDetail = $(".composition:last");
	            			if (parentId != '') {
		            			var composition = getComposition(lastItem, $newProductDetail, totalCount);
		            			compositionList.push(composition);	            				
	            			}
	            			lastItem = item;
	            			parentId = item.composite_id;
	            			totalCount = 0;
							var bigCount = (item.comCount/item.convert_relate).toFixed(0);
							var smallCount = (item.comCount%item.convert_relate).toFixed(0);
							totalCount = totalCount + (item.comCount/item.convert_relate).toFixed(2) * 1;
							var bigCountHtml = "";
							var smallCountHtml = "";			
							if (bigCount != 0) {
								 bigCountHtml = "" + bigCount + item.big_unit + "";
							}
							if (smallCount != 0) {
								smallCountHtml = "" + smallCount + item.small_unit + "";
							}
							$newProductDetail.find("#compositionValueName").html('<div>'+item.custom_name + ' ' + bigCountHtml + smallCountHtml+'</div>');
	            		} else {
	            			lastItem = item;
							var sub_bigCount = (item.comCount/item.convert_relate).toFixed(0);
							var sub_smallCount = (item.comCount%item.convert_relate).toFixed(0);
							totalCount = totalCount + (item.comCount/item.convert_relate).toFixed(2) * 1;
							var sub_bigCountHtml = "";
							var sub_smallCountHtml = "";
							var $newProductDetail = $(".composition:last");
							if (sub_bigCount != 0) {
								sub_bigCountHtml = "" + sub_bigCount + item.big_unit + "";
							}
							
							if (sub_smallCount != 0) {
								sub_smallCountHtml = "" + sub_smallCount + item.small_unit + "";
							}
							var html = $newProductDetail.find("#subProduct").html().trim();
							if (item.is_gift == 0) { 
								$newProductDetail.find("#compositionValueName").append('<div>'+item.custom_name + ' ' + sub_bigCountHtml + sub_smallCountHtml+'</div>');
							} else {
								if (html) {
									$newProductDetail.find("#subsubProduct").append("<p>"+item.custom_name + ' ' + sub_bigCountHtml + sub_smallCountHtml +"</p>");
								} else {
									$newProductDetail.find("#subProduct").append(""+item.custom_name + ' ' + sub_bigCountHtml + sub_smallCountHtml +"");
								}
							}
	            		}
	            		
	            		if (idx == data.orderDetail.length-1) {
	            			var $newProductDetail = $(".composition:last");
	            			var composition = getComposition(item, $newProductDetail, totalCount);
	            			compositionList.push(composition);	 	            		
	            		}
	            	}
	            });
	            setItemBykey("productList", productList);
	            setItemBykey("compositionList", compositionList);
		        setItemBykey("customerInfo", customer);
		        window.location.href = url2;			       	
            }
        });		
	}
	
	function setSubItem(product, item) {
		product.productName = item.custom_name;
		product.valueName =  item.valueName;
		product.bigPriceSpan = '￥' + item.price;
		product.smallPriceSpan = '￥' + (item.price/item.convert_relate).toFixed(2);
		product.bigUnitSpan = '￥' + (item.price/item.convert_relate).toFixed(2);
		product.smallUnitSpan = '/' + item.small_unit;
				
		product.bigPrice = item.price;
		product.smallPrice = (item.price/item.convert_relate).toFixed(2);
		product.bigUnit = item.big_unit;
		product.smallUnit = item.small_unit;
		product.sellProductId =  item.sell_product_id;
		product.productId = item.productId;
		product.convert = item.convert_relate;
		
		product.bigNum = Math.floor(item.product_count/item.convert_relate);
		product.smallNum = (item.product_count%item.convert_relate);
		product.isGift = 1;			
	}
	
	function getComposition($el, $html, totalCount){
		return composition = {
			compositionName : $el.comName,
			
			compositionPriceSpan : '￥' + $el.comPrice,
			
			compositionPrice : $el.comPrice,
			compositionId : $el.composite_id,
	
			compositionNum : ($el.product_count/$el.comCount).toFixed(0),
			compositionCount : totalCount,
			compositionValueName : $html.find("#compositionValueName").html(),
			
			compositionSub : $html.find("#gift").html()
		}
	}
	
	function getProduct($el){
		return product = {
			productName : $el.custom_name,
			valueName : $el.valueName,
			categoryId : $el.categoryId,
			
			bigPriceSpan : '￥' + $el.price,
			smallPriceSpan : '￥' + ($el.price/$el.convert_relate).toFixed(2),
			bigUnitSpan : '/' + $el.big_unit,
			smallUnitSpan :'/' + $el.small_unit,
	
			bigCost : $el.cost,
			smallCost : ($el.cost/$el.convert_relate).toFixed(2),
			bigAccountPrice : $el.account_price,
			smallAccountPrice : ($el.account_price/$el.convert_relate).toFixed(2),
	
			bigPrice : $el.price,
			smallPrice :($el.price/$el.convert_relate).toFixed(2),
			bigUnit : $el.big_unit,
			smallUnit : $el.small_unit,
			sellProductId : $el.sell_product_id,
			productId : $el.productId,
			convert : $el.convert_relate,
	
			bigNum : Math.floor($el.product_count/$el.convert_relate),
			smallNum : ($el.product_count%$el.convert_relate),
			isGift : $el.is_gift,
			
			subProduct : [{
				productName : "",
				valueName : "",
						
				bigPriceSpan : "",
				smallPriceSpan : "",
				bigUnitSpan : "",
				smallUnitSpan : "",
				stock : "",
				
				bigPrice : "",
				smallPrice : "",
				bigUnit : "",
				smallUnit : "",
				sellProductId : "",
				productId : "",
				convert : "",
		
				bigNum : "",
				smallNum : "",
				isGift : 1
			}]
		}
	}