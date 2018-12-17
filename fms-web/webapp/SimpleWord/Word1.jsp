<%@ page language="java"
	import="java.util.*,com.zhuozhengsoft.pageoffice.*"
	pageEncoding="utf-8"%>
<%
// 获取文件磁盘路径
String filePath = request.getSession().getServletContext().getRealPath("SimpleWord/doc/test.doc");

PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
//设置服务器页面
poCtrl.setServerPage(request.getContextPath()+"/poserver.zz");
//添加自定义按钮
poCtrl.addCustomToolButton("保存","Save",1);
//设置保存页面
poCtrl.setSaveFilePage("SaveFile.jsp");
//打开Word文档
poCtrl.webOpen(filePath,OpenModeType.docNormalEdit,"张佚名");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<meta charset="utf-8">
<title>XX文档系统</title>  

</head>
<body>
    <script type="text/javascript">
        function Save() {
            document.getElementById("PageOfficeCtrl1").WebSave();
        }
    </script>
 


    <div style=" width:auto; height:700px;">
       <%=poCtrl.getHtmlCode("PageOfficeCtrl1")%>
    </div>

 </section>
 <br /><br />
 <div style=" text-align:center; height:80px; border-top: solid 1px #666; line-height:70px;">Copyright &copy 2015 北京卓正志远软件有限公司</div>
</body>
</html>

