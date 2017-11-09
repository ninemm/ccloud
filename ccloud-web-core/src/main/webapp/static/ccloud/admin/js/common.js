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
	
	confirm: function(msg, size, callback) {
		
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
	
	ajax: function(url, data, dataType, callback) {

		$.ajax({
			type : "post",
			url : url,
			data : data,
			dataType : dataType || 'json',
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			async: false,
			cache: false,
			success:function(data){
				//result = response;
				toastr.success(data.message, '操作成功');
				//扩展回调函数
				if( callback != null ){
					callback();
				}
			},
			error : function(data) {
				toastr.error(data.message,'错误');
			}
		});
	},
	
	ajaxSubmit: function(formId) {
		$(formId).ajaxSubmit({
			type: "post",
			dataType: "json",
			success: function(result) { 
				if (result.errorCode > 0) {
					toastr.error(result.message, '操作失败');
				} else {
					location.reload();
				}
			},
			error: function() {
				toastr.error("信息提交错误", '操作失败');
			}
		});
		
	},
	
	batchAction: function(formId, message) {
		var count = $('input[name="dataItem"]:checked').length;
		if (count == 0) {
			toastr.warning(message);
		} else {
			layer.confirm('确认批量删除吗?', {
				btn: ['确认', '取消']
			}, function() {
				layer.close();
				jQuery.mm.ajaxSubmit(formId);
			}, function() {
				layer.close();
			});
		}
	},
	
	del: function(url) {
		layer.confirm('确定删除吗？', 
			{btn : ['确定', '取消']},
			function() {
				layer.closeAll();
				$.get(url, function(result){
					if (result.errorCode > 0) {
						toastr.error(result.message, '操作失败');
					} else {
						location.reload();
					}
				});
			},
			function() {
				layer.close();
			}
		);
	},
	
	update: function(message, url, data, dataType) {
		layer.confirm(message, 
			{btn : ['确定', '取消']},
			function() {
				layer.close();
				$.ajax({
					type : "post",
					url : url,
					data : data,
					dataType : dataType || "json",
					contentType: "application/x-www-form-urlencoded; charset=UTF-8",
					async: false,
					cache: false,
					success: function(data) {
						if (data.errorCode > 0) {
							toast.error(data.message, '操作失败');
						} else {
							location.reload();
						}
					},
					error: function(data) {
						toastr.error(data.message, '错误');
					}
				});
			},
			function() {
				layer.close();
			}
		);
	},
	
	initTreeView: function(treeId, data, nodeSelectedFunc, nodeUnselectedFunc) {
		treeId = treeId || '#tree';
		
		$(treeId).treeview({
			data : data,
			levels : 2,
			showBorder : false,
			showCheckbox : true,
			multiSelect : false,
			//showTags: true,
			onNodeSelected: nodeSelectedFunc || function(event, data) {
				$(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
			},
			onNodeUnselected: nodeUnselectedFunc || function(event, data) {
				$(treeId).treeview('uncheckNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('unselectNode', [data.nodeId, { silent: true }]);
			},
			onNodeChecked: nodeSelectedFunc || function(event, data) {
				$(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
			},
			onNodeUnchecked: nodeUnselectedFunc || function(event, data) {
				$(treeId).treeview('uncheckNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('unselectNode', [data.nodeId, { silent: true }]);
			}
		});
	},
	
	initMultiTreeView: function(treeId, data, nodeSelectedFunc, nodeUnselectedFunc) {
		treeId = treeId || '#tree';
		
		$(treeId).treeview({
			data : data,
			levels : 2,
			showBorder : false,
			showCheckbox : true,
			multiSelect : true,
			//showTags: true,
			onNodeSelected: nodeSelectedFunc || function(event, data) {
				$(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			},
			onNodeUnselected: nodeUnselectedFunc || function(event, data) {
				$(treeId).treeview('uncheckNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('unselectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			},
			onNodeChecked: nodeSelectedFunc || function(event, data) {
				$(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			},
			onNodeUnchecked: nodeUnselectedFunc || function(event, data) {
				$(treeId).treeview('uncheckNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('unselectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			}
		});
	},	
	
	initMultiTreeView: function(treeId, data, nodeSelectedFunc, nodeUnselectedFunc) {
		treeId = treeId || '#tree';
		
		$(treeId).treeview({
			data : data,
			levels : 2,
			showBorder : false,
			showCheckbox : true,
			multiSelect : true,
			//showTags: true,
			onNodeSelected: nodeSelectedFunc || function(event, data) {
				$(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			},
			onNodeUnselected: nodeUnselectedFunc || function(event, data) {
				$(treeId).treeview('uncheckNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('unselectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			},
			onNodeChecked: nodeSelectedFunc || function(event, data) {
				$(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			},
			onNodeUnchecked: nodeUnselectedFunc || function(event, data) {
				$(treeId).treeview('uncheckNode', [data.nodeId, { silent: true }]);
				$(treeId).treeview('unselectNode', [data.nodeId, { silent: true }]);
				if(data.nodes != null) {
					var arrayInfo = data.nodes;
					for (var i = 0; i < arrayInfo.length; i++) {
						$(treeId).treeview('toggleNodeChecked', [ arrayInfo[i].nodeId, { silent: true } ]);
						$(treeId).treeview('toggleNodeSelected', [ arrayInfo[i].nodeId, { silent: true } ]);
					}
				}
			}
		});
	},	
	
	initValidator: function(formId, fields) {
		formId = formId || '#form';
		$(formId).bootstrapValidator({
			excluded: [':disabled'],
	    	group: '.validata-box',
	        message: '请输入正确的值',
	        feedbackIcons: {
	            valid: 'glyphicon glyphicon-ok',
	            invalid: 'glyphicon glyphicon-remove',
	            validating: 'glyphicon glyphicon-refresh'
	        },
	        fields: fields
		});
	},
	
	initParentTable: function(tableId, url, queryParams, fields, subUrl) {
		tableId = tableId || "_table";
		
		$(tableId).bootstrapTable({
			url: url,
			method: 'get',
			editable: false,//开启编辑模式
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			detailView: true,//父子表
			classes: 'table-no-bordered',
			cache: false,					// 是否使用缓存
			pagination: true,				// 是否显示分页
			queryParams: queryParams,		// 传递参数
			sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
			paginationLoop: false,
			paginationPreText: '上一页',
			paginationNextText: '下一页',
			pageNumber: 1,
			pageSize: 10,
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			onExpandRow: function (index, row, $detail) {
                jQuery.mm.initSubTable(index, row, $detail, fields, url);
            }
		});
	},
	
	initSubTable: function(index, row, $detail, fields, url) {
		var parentId = row.id;
		var sub_table = $detail.html('<table></table>').find('table');
		$(sub_table).bootstrapTable({
			url: url,
			method: 'get',
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			detailView: true,//父子表
			//classes: 'table-no-bordered',
			cache: false,					// 是否使用缓存
			queryParams: {parentId: parentId},		// 传递参数
			sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
			undefinedText: '',
			columns: fields,
			onExpandRow: function (index, row, $detail) {
                jQuery.mm.initSubTable(index, row, $detail, fields, url);
            }
		});
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
			queryParams: queryParams,		// 传递参数
			sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
			paginationLoop: false,
			paginationPreText: '上一页',
			paginationNextText: '下一页',
			pageNumber: 1,
			pageSize: 10,
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			onEditableSave: editableSaveFunc || function () {},
			onClickCell: clickCellFunc || function () {}
		});
		
	},

    initUnPageEditTable : function(tableId, url, queryParams, fields, editableSaveFunc, clickCellFunc) {

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
            pagination: false,				// 是否显示分页
            queryParams: queryParams,		// 传递参数
            sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
            paginationLoop: false,
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

