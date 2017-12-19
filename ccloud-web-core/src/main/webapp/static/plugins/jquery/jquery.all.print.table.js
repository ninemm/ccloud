/**
 * jquery 表格分页打印插件
 *
 * 作者：Eric.Huang
 * 日期：2017年6月4日
 * 分页样式(需要自定义)：
 * @media print {
 *	.pageBreak { page-break-after:always; }
 * } 
 * 使用例子：
 *  $(function(){
 *		$("#tabContent").printTable({
 *		 mode          : "rowNumber",
 *		 header        : "#headerInfo",
 *		 footer        : "#footerInfo",
 *		 pageNumStyle  : "第#p页/共#P页",
 *		 pageNumClass  : ".pageNum",
 *		 pageSize      : 10
 *		});
 *   });
 *  注意事项：
 *      使用时注意表格中要使用 thead 和 tbody区分出标题头与表格内容，否则可能出现错误
 * 
 * 参数说明：
 *  options are passed as json (json example: { rowHeight : "rowHeight", header : ".tableHeader",})
 *
 *  {OPTIONS}        | [type]    | (default), values            | Explanation
 *  ---------------- | --------- | -----------------------------| -----------
 *  @mode            | [string]  | ("rowHeight"),rowNumber      | 分页模式，按行高分页或按行数分页
 *  @header          | [string]  | (".tableHeader")             | 页面开始处要添加的内同
 *  @footer          | [string]  | (".tableFooter")             | 页面结束要添加的内容
 *  @pageSize        | [number]  | (30)                         | 自动分页行数，按行高分页时改参数无效
 *  @breakClass      | [string]  | ("pageBreak")                  | 分页插入符class,需要定义分页样式
 *  @pageNumStyle    | [string]  | "#p/#P"                      | 页码显示样式，#p当前页，#P总页数
 *  @pageNumClass    | [string]  | ".pageNumClass"              | 页码class样式，用于设值(使用text方法设置) 
 *  @startPage       | [number]  | (1)                          | 第一页起始页码
 *  @pageHeight      | [number]  | (297)                        | 页面高度,单位像素
 *  @topMargin       | [number]  | (15)                         | 上边距高度，单位像素
 *  @bottomMargin    | [number]  | (15)                         | 低边距高度，单位像素
 */
(function($) {
	var modes = { rowHeight : "rowHeight", rowNumber : "rowNumber" };
	//默认参数
	var defaults = { 
		mode          : modes.rowHeight,
		header        : ".tableHeader",
		content       : ".tableContent",
		footer        : ".tableFooter",
		pageSize      : 30,
		breakClass    : "pageBreak",
		pageNumStyle  : "#p/#P",
		pageNumClass  : ".pageNumClass",
		startPage     : 1,
		pageHeight    : 1230,
		topMargin     : 50,
		bottomMargin  : 50
	};
	var settings = {};//global settings
	var rowCount = 0;//行总数
	var pageCount = 0;//页总数
	var currentPage = 0;//当前页
	var $header = new Array();//表格头
	var $content = new Array();//表格内容
	var $footer = new Array();//表格尾
	var $table = new Array();
	var $tbodyTr = new Array();
	var $allTable = null;
	$.fn.printTable = function( options ) {
		$.extend( settings, defaults, options );
		$allTable = $(this);
		$allTable.each( function(index, item) {
			$table[index] = $(this);
			$tbodyTr[index] = $(this).find("tbody tr");
		});
		
		switch ( settings.mode ) {
            case modes.rowHeight :
            	rowHeightPage();//行高分页
                break;
            case modes.rowNumber :
            	rowNumberPage();//行数分页
        }

	};
	
	//获取页总数
	$.fn.printTable.getStartPage = function(startPage) {
		return getPageStyle(startPage , pageCount);
	};
	//行高分页
	function rowHeightPage(){
		var contentHeight =	 initHeightPage();
		getContentClone();
	    beginPageByHeight(contentHeight);
	    hidenContent();
	}
	
	function rowNumberPage() {
		getContentClone();
		var length = $table.length;
		for(var i = 0; i < length; i++) {
			initNumberPage(i);
		}
		hidenContent();
	}
	
	//按行高分页
	function beginPageByHeight(contentHeight){
		var totalHeight = 0;
		var startLine = 0;
		$tbodyTr.each(function(i){
			var cHeight = $(this).outerHeight(true);
			$(this).height(cHeight);
			if((totalHeight + cHeight ) < contentHeight){
				totalHeight += cHeight;
				if(i == $tbodyTr.length -1){
					newPage(i + 1);
				}
			}else{
				newPage(i);
			}
		});
		function newPage(index){
			createPage(startLine,index);
			currentPage ++;
			startLine = index;
			totalHeight = 0;
		}
	}
	
	//初始化高度分页信息
	function initHeightPage(contentHeight){
		var contentHeight =	initContentHeight();
		currentPage = 0 + settings.startPage;
		pageCount = Math.ceil($table.find("tbody").outerHeight(true)/contentHeight) + settings.startPage - 1;//初始化总页数
		rowCount = $tbodyTr.length;//初始化总记录数
		return contentHeight;
	}
	
	
	//初始化内容高度
	function initContentHeight(){
		var headerHeight = $(settings.header).outerHeight(true);
		var footerHeight = $(settings.footer).outerHeight(true);
		var theadHeight = $table.find("thead").outerHeight(true);
		var tableHeight =  settings.pageHeight - settings.topMargin - settings.bottomMargin ;
		var tbodyHeight =  tableHeight - theadHeight- headerHeight - footerHeight;
		return tbodyHeight;
	}
	
	//初始化分页基本信息
	function initNumberPage(i){
		//rowCount = $tbodyTr.length;//初始化总记录数
		$allTable.each( function (index, item) {
			if (index == i) {
				rowCount = $(this).find("tbody tr").length;
				pageCount =  Math.ceil(rowCount/settings.pageSize) + settings.startPage - 1;//初始化总页数
				currentPage = 0 + settings.startPage;
				
				beginPageByNumber(index);
			}
		} );
	}
	
	//开始分页
	function beginPageByNumber(num) {
		var startLine = 1;//开始行号
		var offsetLine = 0;//偏移行号
		for(var i = settings.startPage; i <= pageCount; i++ ) {
			currentPage = i;
			startLine = settings.pageSize* (currentPage - settings.startPage);
			offsetLine = (startLine + settings.pageSize) > rowCount ? rowCount : (startLine + settings.pageSize);
			createPage(num, startLine, offsetLine);
		};
	}
	 //创建新的一页
	function createPage(i, startLine, offsetLine) {
		var $pageHeader = $header[i].clone();
		var $pageContent = $content[i].clone().append(getTrRecord(i, startLine, offsetLine));
		var $pageFooter = $footer[i].clone();
		$pageFooter.find(settings.pageNumClass).text(getPageStyle(i, currentPage , pageCount));//页码显示格式
		if (offsetLine == rowCount) {
			$table[i].before($pageHeader).before($pageContent).before($pageFooter);
			if (i < $header.length - 1)
				$table[i].before(addPageBreak());
		} else {
			$table[i].before($pageHeader).before($pageContent).before($pageFooter).before(addPageBreak());
		}
	}
	
	//添加分页符
	function addPageBreak(){
		return "<div class='"+settings.breakClass+"'></div>";
	}
	
	//获取分页样式
	function getPageStyle(i, currentPage , pageCount) {
		var numStr = settings.pageNumStyle;
		 numStr = numStr.replace(/#p/g, currentPage);
		 numStr = numStr.replace(/#P/g, pageCount);
		 return numStr;
	}
	
	//获取记录
	function getTrRecord(i, startLine, offsetLine) {
		return $tbodyTr[i].clone().slice(startLine, offsetLine);
	}

	//获取内容
	function getContentClone() {

		$(settings.header).each( function(index, item) {
			$header[index] = $(this).clone().removeClass("clearheader").removeAttr("style");
		});

		var len = $table.length;
		for(var i = 0; i < len; i++ ) {
			$content[i] = $table[i].clone().find("tbody")
				.remove().end().removeClass("clearcontent").removeAttr("style");
		}

		$(settings.footer).each( function(index, item) {
			$footer[index] = $(this).clone().removeClass("clearfooter").removeAttr("style");
		});
	}

	//隐藏原来的数据
	function hidenContent() {

		$(settings.header).each( function() {
			$(this).hide();
		});
		
		$(settings.content).each( function() {
			$(this).hide();
		});

		$(settings.footer).each( function() {
			$(this).hide();
		});
	}
	
})(jQuery);    