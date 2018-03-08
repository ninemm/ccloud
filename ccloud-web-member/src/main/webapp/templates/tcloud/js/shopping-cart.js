cart = {
	findIndex : function(source, target) {
		for (var i = target.length - 1; i >= 0; i--) {
			if (target[i].sellProductId == source) {
				return i;
			}
		}
		return -1;
	},
	findCompositionIndex : function(source, target) {
		for (var i = target.length - 1; i >= 0; i--) {
			if (target[i].compositionId == source) {
				return i;
			}
		}
		return -1;
	},
    changeProduct : function(product){
        var productList = getItemBykey("productList") == undefined ? [] : getItemBykey("productList");
        var index = this.findIndex(product.sellProductId, productList);
        if(index === -1 && (product.bigNum > 0 || product.smallNum > 0 )) {
        	productList.push(product);
        } else if(index != -1 && (product.bigNum == 0 && product.smallNum == 0 )) {
        	productList.splice(index,1);
        } else {
        	productList[index] = product;
        }
        setItemBykey("productList", productList);
    },
    changeComposition : function(composition){
        var compositionList = getItemBykey("compositionList") == undefined ? [] : getItemBykey("compositionList");
        var index = this.findCompositionIndex(composition.compositionId, compositionList);
        if(index === -1 && composition.compositionNum > 0) {
        	compositionList.push(composition);
        } else if(index != -1 && composition.compositionNum == 0) {
        	compositionList.splice(index,1);
        } else {
        	compositionList[index] = composition;
        }
        setItemBykey("compositionList", compositionList);
    }
};