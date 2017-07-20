$(function() {
	$(".jp-onmouse").mouseover(function() {
		$(this).find(".row-actions").show();
	}).mouseout(function() {
		$(".row-actions").hide()
	})
});

function checkAll(checkbox) {
	var items = document.getElementsByName("dataItem");
	for (var i = 0; i < items.length; i++) {
		items[i].checked = checkbox.checked;
	}
}


jQuery.mm = { 
	alert : function(){
		window.alert("test");
	},
	
	submit : function (formId, resultFunc){
		formId = formId || "form";
		resultFunc = resultFunc || function() {
			toastr.success(data.message, '操作成功');
		}
		
		$(formId).ajaxSubmit({
			type : "post", 
			dataType : "json", 
			success : function(data) { 
				resultFunc(data);
			},
			error : function() {
				toastr.error('信息提交错误','错误');
			}
		});
	},
	
	ajax : function(){
		
	},
	
	initEditTable : function(tableId, url, queryParams, fields, editableSaveFunc, clickCellFunc) {
		
		tableId = tableId || "_table";
		
		$(tableId).bootstrapTable({
			url: url,
			method: 'get',
			editable: true,//开启编辑模式
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			classes: 'table-no-bordered',
			cache: false,					// 是否使用缓存
			pagination: true,				// 是否显示分页
			queryParams: queryParams,	// 传递参数
			sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
			paginationLoop: false,
			paginationPreText: '上一页',
			paginationNextText: '下一页',
			pageNumber: 1,
			pageSize: 10,
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			onEditableSave: editableSaveFunc,
			onClickCell: clickCellFunc || function () {}
		});
		
	},
	
	initDatetimepicker: function(formId, fieldId, fieldName, format, minView, maxView, startView) {
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
	   	}).on('hide', function(e) {
	   		// updateStatus方法的作用是更新给定字段的校验状态， validateField方法的作用是触发给定字段的校验
	   		if (formId != null && formId != undefined) {
		   		$(formId).data('bootstrapValidator')
		   			.updateStatus(fieldName, 'NOT_VALIDATED', null)
		   			.validateField(fieldName);
	   		}
	   	});  
	},
	
	initInputAutoComplete: function(fieldId, url, displayTextFunc, afterSelectFunc) {
		$(fieldId).typeahead({
			/** query为当前文本输入框中的字符串*/
			source: function(query, process) {
				var param = {name : query};
				$.post(url, param, function(data) {
					process(data);
				});
			},
			items: 10,
			minLength: 2,
			fitToElement: true,
			displayText: displayTextFunc,
			afterSelect: afterSelectFunc,
			// 指定延时毫米数后，才真正向后台请求数据，默认：500
			delay: 500 
		});
	}
}

