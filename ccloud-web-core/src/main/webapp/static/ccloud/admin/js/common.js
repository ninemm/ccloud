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
			layer.confirm('确认批量操作吗?', {
				btn: ['确认', '取消']
			}, function() {
				layer.closeAll();
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
	
	down: function(url) {
		layer.confirm('确定导出吗？', 
			{btn : ['确定', '取消']},
			function() {
				layer.closeAll();
				location.href = url;
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
	//单选树
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
/*                var selectNode = getSubNodesByParent(data);
                if(selectNode){
                    $(treeId).treeview('unselectNode', [selectNode, { silent: true }]);
                    $(treeId).treeview('uncheckNode', [selectNode, { silent: true }]);
                    $(treeId).treeview('selectNode', [selectNode, { silent: true }]);
                    $(treeId).treeview('checkNode', [selectNode, { silent: true }]);
				}*/
                $(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
                $(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);

/*                var checked = $(treeId).treeview('getChecked',data);
                for(var i = 0;i<checked.length;i++){
                	if(checked[i].nodeId!=data.nodeId){
                        checked[i].state.selected = true;
                        $(treeId).treeview('unselectNode', [checked[i], { silent: true }]);
                        checked[i].state.checked = true;
                        $(treeId).treeview('uncheckNode', [checked[i], { silent: true }]);
					}
                }*/

			},
			onNodeUnselected: nodeUnselectedFunc || function(event, data) {
                var selectNode = getSubNodesByParent(data);
                if(selectNode){
                    $(treeId).treeview('unselectNode', [selectNode, { silent: true }]);
                    $(treeId).treeview('uncheckNode', [selectNode, { silent: true }]);
				}

/*                $(treeId).treeview('unselectNode', [data, { silent: true }]);
                data.state.checked = true;
                $(treeId).treeview('uncheckNode', [data, { silent: true }]);*/
			},
			onNodeChecked: nodeSelectedFunc || function(event, data) {
                $(treeId).treeview('selectNode', [data.nodeId, { silent: true }]);
                $(treeId).treeview('checkNode', [data.nodeId, { silent: true }]);

/*                var checked = $(treeId).treeview('getChecked',data);
                var selected = $(treeId).treeview('getSelected',data);
				for(var i = 0;i<selected.length;i++){
					if(selected[i].nodeId!==data.nodeId){
                        selected[i].state.selected = true;
                        $(treeId).treeview('unselectNode', [selected[i], { silent: true }]);
                        selected[i].state.checked = true;
                        $(treeId).treeview('uncheckNode', [selected[i], { silent: true }]);
					}
				}
                for(var i = 0;i<checked.length;i++){
                    if(checked[i].nodeId!==data.nodeId){
                        checked[i].state.selected = true;
                        $(treeId).treeview('unselectNode', [checked[i], { silent: true }]);
                        checked[i].state.checked = true;
                        $(treeId).treeview('uncheckNode', [checked[i], { silent: true }]);
                    }
                }*/


			},
			onNodeUnchecked: nodeUnselectedFunc || function(event, data) {
/*				if(data.state.selected==false){
                    data.state.selected = true;
				}
                $(treeId).treeview('unselectNode', [data, { silent: true }]);
                data.state.checked = true;
                $(treeId).treeview('uncheckNode', [data, { silent: true }]);*/

                var selectNode = getSubNodesByParent(data);
                if(selectNode){
                    $(treeId).treeview('unselectNode', [selectNode, { silent: true }]);
                    $(treeId).treeview('uncheckNode', [selectNode, { silent: true }]);
				}

			}
		});
	},
    //多选树
    initMultiTreeView: function(treeId, data, nodeSelectedFunc, nodeUnselectedFunc) {
        treeId = treeId || '#tree';
        $(treeId).treeview({
            data : data,
            levels : 3,
            showBorder : false,
            showCheckbox : true,
            multiSelect : true,
            //showTags: true,
            onNodeSelected: nodeSelectedFunc || function(event, data) {
                var selectNode = getSubNodesByParent(data);
                if(selectNode){
                    $(treeId).treeview('checkNode', [selectNode, { silent: true }]);
                    $(treeId).treeview('selectNode', [selectNode, { silent: true }]);
                }
/*				var siblingsNode = $(treeId).treeview('getSiblings',data);
                var allSelect = true;
                for(var i =0;i<siblingsNode.length;i++){
					if(siblingsNode[i].state.selected == false){
                        allSelect = false;
                        break;
					}
				}
				if(allSelect==true){
                    $(treeId).treeview('checkNode', [data.parentId, { silent: true }]);
                    $(treeId).treeview('selectNode', [data.parentId, { silent: true }]);
				}*/
                checkParentNode(treeId,data);

				/*				data.state.checked = false;
				 data.state.selected = false;
				 $(treeId).treeview('checkNode', [data, { silent: true }]);
				 $(treeId).treeview('selectNode', [data, { silent: true }]);
				 if(data.nodes != null) {
				 checkChildNode(treeId,event,data.nodes,data);
				 }
				 selectParentNode(treeId,data);*/
            },
            onNodeUnselected: nodeUnselectedFunc || function(event, data) {
                var unSelectNode = getSubNodesByParent(data);
                if(unSelectNode){
                    $(treeId).treeview('uncheckNode', [unSelectNode, { silent: true }]);
                    $(treeId).treeview('unselectNode', [unSelectNode, { silent: true }]);
                }
                unCheckParentNode(treeId,data);
/*                $(treeId).treeview('uncheckNode', [data.parentId, { silent: true }]);
                $(treeId).treeview('unselectNode', [data.parentId, { silent: true }]);*/
				/*				data.state.checked = true;
				 data.state.selected = true;
				 $(treeId).treeview('uncheckNode', [data, { silent: true }]);
				 $(treeId).treeview('unselectNode', [data, { silent: true }]);
				 if(data.nodes != null) {
				 unSelectChildNode(treeId,data.nodes,data);
				 }
				 unCheckParentNode(treeId,data);*/
            },
            onNodeChecked: nodeSelectedFunc || function(event, data) {
                var checkNode = getSubNodesByParent(data);
                if(checkNode){
                    $(treeId).treeview('checkNode', [checkNode, { silent: true }]);
                    $(treeId).treeview('selectNode', [checkNode, { silent: true }]);
                }
                checkParentNode(treeId,data);

				/*                data.state.checked = false;
				 data.state.selected = false;
				 $(treeId).treeview('checkNode', [data, { silent: true }]);
				 $(treeId).treeview('selectNode', [data, { silent: true }]);
				 if(data.nodes != null) {
				 checkChildNode(treeId,data.nodes,data);
				 }
				 checkParentNode(treeId,data);*/
            },
            onNodeUnchecked: nodeUnselectedFunc || function(event, data) {
                var unCheckNode = getSubNodesByParent(data);
                if(unCheckNode){
                    $(treeId).treeview('uncheckNode', [unCheckNode, { silent: true }]);
                    $(treeId).treeview('unselectNode', [unCheckNode, { silent: true }]);
                }
                unCheckParentNode(treeId,data);
/*                $(treeId).treeview('uncheckNode', [data.parentId, { silent: true }]);
                $(treeId).treeview('unselectNode', [data.parentId, { silent: true }]);*/

				/*                data.state.checked=true;
				 $(treeId).treeview('uncheckNode', [data, { silent: true }]);
				 data.state.selected=true;
				 $(treeId).treeview('unselectNode', [data, { silent: true }]);
				 if(data.nodes != null) {
				 unCheckChildNode(treeId,data.nodes,data);
				 }
				 unCheckParentNode(treeId,data);*/
            }
        });
    },
	
/*	initMultiTreeView: function(treeId, data, nodeSelectedFunc, nodeUnselectedFunc) {
		treeId = treeId || '#tree';
		
		$(treeId).treeview({
			data : data,
			levels : 3,
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
	},*/
	
	initIconTreeView: function(treeId, data, checkBox, url, nodeSelectedFunc, nodeUnselectedFunc) {
		treeId = treeId || '#tree';
		$(treeId).treeview({
	          expandIcon: 'glyphicon glyphicon-chevron-right',
	          collapseIcon: 'glyphicon glyphicon-chevron-down',					
			  data: data,
			  showIcon: false,
			  showCheckbox: checkBox,
			  onNodeSelected: function(event, node) {
				$.ajax({
					url:url,
					type:"post",
					data:{"id": node.tags[0]},
					dataType:"json",
					success:function(data) {
						jQuery.mm.initIconTreeView('#treeview-checkable-custom', data, false);
					}
				});				  
			  },
			  onNodeUnchecked: function (event, node) {
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
	
	initEditTableExport : function(tableId, url, queryParams, fields, editableSaveFunc, clickCellFunc,fileNames) {
		
		tableId = tableId || "_table";
		
		$(tableId).bootstrapTable({
			url: url,
			method: 'get',
			editable: true,//开启编辑模式
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			classes: 'table-no-bordered',
			sortable: true,                  //是否启用排序
			sortOrder: "asc",                //排序方式
			cache: false,					// 是否使用缓存
			pagination: true,				// 是否显示分页
			queryParams: queryParams,		// 传递参数
			sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
			paginationLoop: false,
			paginationPreText: '上一页',
			paginationNextText: '下一页',
			pageNumber: 1,
			pageSize: 10,
			toolbar:	'#toolbar',
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			showExport: true,  //是否显示导出按钮  
			buttonsAlign:"right",  //按钮位置  
			exportTypes:['excel'],  //导出文件类型  
			exportDataType: "all",
			onEditableSave: editableSaveFunc || function () {},
			onClickCell: clickCellFunc || function () {},
			exportOptions:{  
                fileName: fileNames,
            },  
			
		});
	},
	
	initEditTableExport2 : function(tableId, url, queryParams, fields, editableSaveFunc, clickCellFunc,fileNames) {
		
		tableId = tableId || "_table";
		
		$(tableId).bootstrapTable({
			url: url,
			method: 'get',
			editable: true,//开启编辑模式
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			classes: 'table-no-bordered',
			sortable: true,                  //是否启用排序
			sortOrder: "asc",                //排序方式
			cache: false,					// 是否使用缓存
			pagination: true,				// 是否显示分页
			queryParams: queryParams,		// 传递参数
			sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
			paginationLoop: false,
			paginationPreText: '上一页',
			paginationNextText: '下一页',
			pageNumber: 1,
			pageSize: 10,
			toolbar:	'#toolbar2',
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			showExport: true,  //是否显示导出按钮  
			buttonsAlign:"right",  //按钮位置  
			exportTypes:['excel'],  //导出文件类型  
			exportDataType: "all",
			onEditableSave: editableSaveFunc || function () {},
			onClickCell: clickCellFunc || function () {},
			exportOptions:{  
                fileName: fileNames,
            },  
			
		});
	},
	
	initEditTableExport3 : function(tableId, url, queryParams, fields, editableSaveFunc, clickCellFunc,fileNames) {
		
		tableId = tableId || "_table";
		
		$(tableId).bootstrapTable({
			url: url,
			method: 'get',
			editable: true,//开启编辑模式
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			classes: 'table-no-bordered',
			sortable: true,                  //是否启用排序
			sortOrder: "asc",                //排序方式
			cache: false,					// 是否使用缓存
			queryParams: queryParams,		// 传递参数
			toolbar:	'#toolbar',
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			showExport: true,  //是否显示导出按钮  
			buttonsAlign:"right",  //按钮位置  
			exportTypes:['excel'],  //导出文件类型  
			exportDataType: "all",
			onEditableSave: editableSaveFunc || function () {},
			onClickCell: clickCellFunc || function () {},
			exportOptions:{  
                fileName: fileNames,
            },  
			
		});
	},
	
	initEditTableExport4 : function(tableId, url, queryParams, fields, editableSaveFunc, clickCellFunc,fileNames) {
		
		tableId = tableId || "_table";
		
		$(tableId).bootstrapTable({
			url: url,
			method: 'get',
			editable: true,//开启编辑模式
			clickToSelect: true,
			uniqueId: 'id',
			striped: true,
			classes: 'table-no-bordered',
			sortable: true,                  //是否启用排序
			sortOrder: "asc",                //排序方式
			cache: false,					// 是否使用缓存
			queryParams: queryParams,		// 传递参数
			toolbar:	'#toolbar2',
			smartDisplay: false,
			undefinedText: '',
			columns: fields,
			showExport: true,  //是否显示导出按钮  
			buttonsAlign:"right",  //按钮位置  
			exportTypes:['excel'],  //导出文件类型  
			exportDataType: "all",
			onEditableSave: editableSaveFunc || function () {},
			onClickCell: clickCellFunc || function () {},
			exportOptions:{  
                fileName: fileNames,
            },  
			
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
	},
	
	/** 数字金额大写转换(可以处理整数,小数,负数) */
	digitUppercase: function(n) {
		if (!/^(0|[1-9]\d*)(\.\d+)?$/.test(n))
	        return "数据非法";
	    var fraction = ['角', '分'];  
	    var digit = [  
	        '零', '壹', '贰', '叁', '肆',  
	        '伍', '陆', '柒', '捌', '玖'  
	    ];  
	    var unit = [  
	        ['元', '万', '亿'],  
	        ['', '拾', '佰', '仟']  
	    ];  
	    var head = n < 0 ? '欠' : '';  
	    n = Math.abs(n);  
	    var s = '';  
	    for (var i = 0; i < fraction.length; i++) {  
	        s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');  
	    }  
	    s = s || '整';  
	    n = Math.floor(n);  
	    for (var i = 0; i < unit[0].length && n > 0; i++) {  
	        var p = '';  
	        for (var j = 0; j < unit[1].length && n > 0; j++) {  
	            p = digit[n % 10] + unit[1][j] + p;  
	            n = Math.floor(n / 10);  
	        }  
	        s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;  
	    }  
	    return head + s.replace(/(零.)*零元/, '元')  
	        .replace(/(零.)+/g, '零')  
	        .replace(/^整$/, '零元整');  
	}
}

/*function checkChildNode(treeId,event,node,data){
    for (var i = 0; i < node.length; i++) {
        if(data.state.checked == node[i].state.checked
            &&data.state.selected ==node[i].state.selected)continue;
        node[i].state.checked = false;
        node[i].state.selected = false;
        $(treeId).treeview('checkNode', [ node[i], { silent: true } ]);
        $(treeId).treeview('selectNode', [ node[i], { silent: true } ]);
        if(node[i].nodes!=null){
            checkChildNode(treeId,event,node[i].nodes,node[i]);
		}
    }
}*/

/*function unCheckChildNode(treeId,node,data){
    for (var i = 0; i < node.length; i++) {
        node[i].state.checked = true;
        node[i].state.selected = true;
        $(treeId).treeview('uncheckNode', [ node[i], { silent: true } ]);
        $(treeId).treeview('unselectNode', [ node[i], { silent: true } ]);
        if(node[i].nodes!=null){
            unCheckChildNode(treeId,node[i].nodes,node[i]);
		}
    }
}*/
/*
function unSelectChildNode(treeId,node,data){
    for (var i = 0; i < node.length; i++) {
        node[i].state.checked = true;
        node[i].state.selected = true;
        $(treeId).treeview('uncheckNode', [ node[i], { silent: true } ]);
        $(treeId).treeview('unselectNode', [ node[i], { silent: true } ]);
        if(node[i].nodes!=null){
            unCheckChildNode(treeId,node[i].nodes,node[i]);
        }
    }
}*/

/*function selectParentNode(treeId,data){
    var treeNodeList = $(treeId).treeview('getNodes', data);
    if(data.parentId != null&&treeNodeList!=undefined) {
        var allSelect = true;
        var otherOption = $(treeId).treeview('getSiblings', data);
        var allSelectCount = data.state.selected==true?1:0;
        for(var i = 0;i<otherOption.length;i++){
            if(data.state.selected != otherOption[i].state.selected){
                allSelect = false;
                break;
            }
        }
        var parentNode = null;
        for(var i = 0;i<treeNodeList.length;i++){
            if(treeNodeList[i].nodeId == data.parentId){
                parentNode = treeNodeList[i];
                break;
            }
        }
        for(var i = 0;i<otherOption.length;i++){
            if(otherOption[i].state.selected==true)allSelectCount++;
        }
        if(allSelect==true&&parentNode!=undefined
			&&(data.state.selected==true)&&allSelectCount==otherOption.length+1){
            parentNode.state.checked = false;
            //parentNode.state.selected = false;
            $(treeId).treeview('checkNode', [parentNode, { silent: true }]);
            $(treeId).treeview('selectNode', [parentNode, { silent: true }]);
        }
    }
}*/

/*function checkParentNode(treeId,data){
    var treeNodeList = $(treeId).treeview('getNodes', data);
    if(data.parentId != null&&treeNodeList!=undefined) {
        var allSelect = true;
        var otherOption = $(treeId).treeview('getSiblings', data);
        var allSelectCount = data.state.checked==true?1:0;
        for(var i = 0;i<otherOption.length;i++){
            if(data.state.checked != otherOption[i].state.checked){
                allSelect = false;
                break;
            }
        }
        var parentNode = null;
        for(var i = 0;i<treeNodeList.length;i++){
            if(treeNodeList[i].nodeId == data.parentId){
                parentNode = treeNodeList[i];
                break;
            }
        }
        for(var i = 0;i<otherOption.length;i++){
            if(otherOption[i].state.checked==true)allSelectCount++;
        }
        if(allSelect==true&&parentNode!=undefined
            &&(data.state.checked==true)&&allSelectCount==otherOption.length+1){
            parentNode.state.checked = false;
            parentNode.state.selected = false;
            $(treeId).treeview('checkNode', [parentNode, { silent: true }]);
            $(treeId).treeview('selectNode', [parentNode, { silent: true }]);
        }
        if(parentNode.parentId!=null){
            checkParentNode(treeId,parentNode);
        }
    }
}*/

/*function unCheckParentNode(treeId,data){
    var treeNodeList = $(treeId).treeview('getNodes', data);
    if(data.parentId != null&&treeNodeList!=undefined) {
        var parentNode = null;
        for (var i = 0; i < treeNodeList.length; i++) {
            if (treeNodeList[i].nodeId == data.parentId) {
                parentNode = treeNodeList[i];
                break;
            }
        }
        if(parentNode != null){
            parentNode.state.checked = true;
            $(treeId).treeview('uncheckNode', [parentNode, {silent: true}]);
            parentNode.state.selected = true;
            $(treeId).treeview('unselectNode', [parentNode, {silent: true}]);
        }
        if(parentNode.parentId!=null)
        unCheckParentNode(treeId,parentNode);
    }

}*/

// 递归获取所有的节点Id
function getSubNodesByParent(node) {
	var ts = [];
	if (node.nodes) {
		ts.push(node.nodeId);
		for (i in node.nodes) {
			ts.push(node.nodes[i].nodeId);
			if (node.nodes[i].nodes) {
				var parentNode = getSubNodesByParent(node.nodes[i]);
				for (j in parentNode) {
					ts.push(parentNode[j]);
				}
			}
		}
	} else {
		ts.push(node.nodeId);
	}
	return ts;
}

function checkParentNode(treeId,data){
    var siblingsNode = $(treeId).treeview('getSiblings',data);
    if(data.parentId!=undefined||(siblingsNode!=undefined&&siblingsNode.length>0)){
        var allSelect = true;
        for(var i =0;i<siblingsNode.length;i++){
            if(siblingsNode[i].state.selected == false){
                allSelect = false;
                break;
            }
        }
        if(allSelect==true){
            $(treeId).treeview('checkNode', [data.parentId, { silent: true }]);
            $(treeId).treeview('selectNode', [data.parentId, { silent: true }]);
            var parentNode = $(treeId).treeview('getParent', [data.nodeId, { silent: true }]);
            if(parentNode!=undefined&&parentNode.parentId!=null){
                checkParentNode(treeId,parentNode);
            }
        }
	}
}
function unCheckParentNode(treeId,data){
	if(data.parentId!=undefined){
        $(treeId).treeview('uncheckNode', [data.parentId, { silent: true }]);
        $(treeId).treeview('unselectNode', [data.parentId, { silent: true }]);
        var parentNode = $(treeId).treeview('getParent', [data.nodeId, { silent: true }]);
        if(parentNode!=undefined&&parentNode.parentId!=null){
            unCheckParentNode(treeId,parentNode);
		}
	}
}

function formatNumber(num, precision, separator) {
    var parts;
    if (!isNaN(parseFloat(num)) && isFinite(num)) {
        num = Number(num);
        num = (typeof precision !== 'undefined' ? num.toFixed(precision) : num).toString();
        parts = num.split('.');
        parts[0] = parts[0].toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1' + (separator || ','));

        return parts.join('.');
    }
    return NaN;
}


Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
