<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>  

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-control" content="no-cache">
<meta http-equiv="Cache" content="no-cache">
<title></title>
<%@ include file="/WEB-INF/views/include/easyui.jsp"%>

</head>
<body>

 <div id="tb3" style="padding:5px;height:auto">
                         <div>
	       		           <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="addRowButton"  onclick="addPersonForm()"> 添加 </a>
	       		           <span class="toolbar-item dialog-tool-separator"></span>
	                       <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" id="delButton"   onclick="deletePerson()">删除</a>
	        	           <span class="toolbar-item dialog-tool-separator"></span>
	                       <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" id="editRowButton" onclick="editPersonForm()">修改</a>
	                       <span class="toolbar-item dialog-tool-separator"></span>
	                       
	                       <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-standard-arrow-refresh" plain="true" onclick="refresh()">刷新</a>
	                       <span class="toolbar-item dialog-tool-separator"></span>
	                       <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-tip" plain="true" title="用户与数据库无关."></a>
	                       
                         </div> 
                       
  </div>
  <div id="dlgg" ></div>  
 
 <table id="dg3"></table> 
<script type="text/javascript">
var dgg;
var person;
$(function(){  
	
    initData();
});

function initData(){
	dgg=$('#dg3').datagrid({     
	method: "get",
    url: '${ctx}/system/permission/i/personList', 
    fit : true,
	fitColumns : true,
	border : false,
	idField : 'id',
	striped:true,
	pagination:true,
	rownumbers:true,
	pageNumber:1,
	pageSize : 20,
	pageList : [ 10, 20, 30, 40, 50 ],
	singleSelect:false,
    columns:[[    
	  	{field:'TREESOFTPRIMARYKEY',checkbox:true}, 
	  		{field:'id',title:'操作',width:50,sortable:true,formatter: function(value,row,index){
				 return ' <img src="${ctx}/static/plugins/easyui/jquery-easyui-theme/icons/table-key.png" style="cursor: pointer;" />&nbsp;	 <a style="text-decoration:none;" class="easyui-linkbutton"   href="javascript:reset(\'' +row["id"]+  '\')">重置密码</a>';
				 
		}},
	  	{field:'username',title:'帐号',width:100 },
	  	{field:'realname',title:'姓名',sortable:true,width:100 },
	  	{field:'role',title:'角色',width:100,sortable:true,formatter: function(value,row,index){
				if (row.role=='0'){
					return '普通用户';
				} else {
					return '管理员';
				}
		}},
        {field:'status',title:'状态',width:100,sortable:true,formatter: function(value,row,index){
				if (row.status=='0'){
					return '启用';
				} else {
					return '禁用';
				}
		}},
		{field:'expiration',title:'有效期至',width:100 }
		
	  	
    ] ], 
    checkOnSelect:true,
    selectOnCheck:true,
    extEditing:false,
    toolbar:'#tb3',
    autoEditing: false,     //该属性启用双击行时自定开启该行的编辑状态
    singleEditing: false,
    onDblClickRow: function (rowIndex, rowData) {  
    	var id =rowData.id  ;
	    person = $("#dlgg").dialog({   
	    title: '查看',    
	    width: 350,    
	    height: 380,    
	    href:'${ctx}/system/permission/i/editPersonForm/'+id,
	    maximizable:true,
	    modal:true,
	    buttons:[
	    	 
	    	{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
					person.panel('close');
				}
		 }]
	  });
    }
   }); 
  }

 //打开 新增 编辑 对话框
   function addPersonForm(){
	    person = $("#dlgg").dialog({   
	    title: ' 新增',    
	    width: 350,    
	    height: 380,    
	    href:'${ctx}/system/permission/i/addPersonForm',
	    maximizable:true,
	    modal:true,
	    buttons:[ 
	    	{
			text:'保存',
			iconCls:'icon-ok',
			handler:function(){
	    		addUpdatePerson();
				//$("#mainform").submit(); 
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
					person.panel('close');
				}
		 }]
	  });
  }
   
 function editPersonForm(){
	
	var checkedItems = $('#dg3').datagrid('getChecked');
	if(checkedItems.length == 0 ){
	      parent.$.messager.show({ title : "提示",msg: "请先选择一行数据！", position: "bottomRight" });
		  return;
	 }
	    
	 if(checkedItems.length >1 ){
	      parent.$.messager.show({ title : "提示",msg: "请选择一行数据！", position: "bottomRight" });
		  return;
	 }
	 
	 var id = checkedItems[0]['id']  ;
	 
	 person= $("#dlgg").dialog({   
	    title: '编辑',    
	    width: 350,    
	    height: 350,    
	    href:'${ctx}/system/permission/i/editPersonForm/'+id,
	    maximizable:true,
	    modal:true,
	    buttons:[ 
	    	{
			text:'保存',
			iconCls:'icon-ok',
			handler:function(){
	    		addUpdatePerson();
				// $("#mainform").submit(); 
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
					person.panel('close');
				}
		}]
	});
 }
 
 
   //重置密码  
   function reset( id ){
	  var ids = [];
      ids.push( id );
	  $.ajax({
			    type:'POST',
		       	contentType:'application/json;charset=utf-8',
                url:"${ctx}/system/permission/i/resetPersonPass",
                data: JSON.stringify( { 'ids':ids } ),
                datatype: "json", 
               //成功返回之后调用的函数             
                success:function(data){
                   var status = data.status ;
            	   if(status == 'success' ){
            	      parent.$.messager.show({ title : "提示",msg: data.mess, position: "bottomRight" });
            	   }else{
            	      parent.$.messager.show({ title : "提示",msg: data.mess, position: "bottomRight" });
            	  }
            }  
       });
   }
	 
 
   //删除  
   function deletePerson(){
	 
	  var checkedItems = $('#dg3').datagrid('getChecked');
	  var length = checkedItems.length;
	  
	  var data2=$('#dg3').datagrid('getData');
	  var totalLength = data2.total;
	  
	  // alert('总数据量:'+data.total)
	  
	   if(totalLength - length <= 0 ){
		 parent.$.messager.show({ title : "提示",msg: "必须保留一行用户信息！", position: "bottomRight" });
		 return ;
	  }
	  
	  if(checkedItems.length == 0 ){
		 parent.$.messager.show({ title : "提示",msg: "请先选择一行数据！", position: "bottomRight" });
		 return ;
	  }
	 
	  var ids = [];
      $.each( checkedItems, function(index, item){
    	  ids.push( item["id"] );
     }); 
       
	 $.easyui.messager.confirm("操作提示", "您确定要删除"+length+"行数据吗？", function (c) {
                if (c) {
                	
                   $.ajax({
			        type:'POST',
		          	contentType:'application/json;charset=utf-8',
                    url:"${ctx}/system/permission/i/deletePerson",
                    data: JSON.stringify( { 'ids':ids } ),
                    datatype: "json", 
                   //成功返回之后调用的函数             
                    success:function(data){
                       var status = data.status ;
            	       if(status == 'success' ){
            	    	   $('#dg3').datagrid('reload');
            	    	   $("#dg3").datagrid('clearSelections').datagrid('clearChecked');
            	            parent.$.messager.show({ title : "提示",msg: data.mess, position: "bottomRight" });
            	       }else{
            	    	    parent.$.messager.show({ title : "提示",msg: data.mess, position: "bottomRight" });
            	       }
            	     }  
                   });
                }
            });
   }
 
   function refresh(){
	    $('#dg3').datagrid('reload');
	    $("#dg3").datagrid('clearSelections').datagrid('clearChecked');
   }
    
</script>

</body>
</html>