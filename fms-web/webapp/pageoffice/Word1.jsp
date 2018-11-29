<%@ page language="java"
	import="java.util.*,com.zhuozhengsoft.pageoffice.*"
	pageEncoding="utf-8"%>
<%
// 获取文件磁盘路径
String filePath = request.getSession().getServletContext().getRealPath("SimpleWord/doc/test.doc");
 //   "http://47.93.40.219:80/group1/M00/00/15/rBD8oFvs_aCAWLHZAAH-rEHAK9M29.docx"
PageOfficeCtrl poCtrl=new PageOfficeCtrl(request);
//设置服务器页面
poCtrl.setServerPage(request.getContextPath()+"/poserver.zz");
//添加打开文件后触发的事件
poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
//添加自定义按钮
poCtrl.addCustomToolButton("保存","Save",1);
poCtrl.addCustomToolButton("加入白名单","AddWhiteList",2);
poCtrl.addCustomToolButton("加入黑名单","AddBlackList",3);
//设置保存页面
poCtrl.setSaveFilePage("SaveFile.jsp");
//String fileUrl =  "D:/01工作/03工程目录/git/fms/fms-web/webapp/SimpleWord/doc/test.doc";
//打开Word文档
poCtrl.webOpen(filePath,OpenModeType.docNormalEdit,"张佚名");
//poCtrl.webOpen(fileUrl,OpenModeType.docNormalEdit,"张佚名");
//poCtrl.webOpen("http://47.93.40.219:80/group1/M00/00/15/rBD8oFvs_aCAWLHZAAH-rEHAK9M29.docx",OpenModeType.docNormalEdit,"张佚名")
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<meta charset="utf-8">
<title>fms</title>
<script type="text/javascript" src="../jquery.min.js"></script>
<script type="text/javascript" src="../pageoffice.js" id="po_js_main"></script>
<script>
        // $(window).load(function(){
        //     var fileUrl = window.external.UserParams;
        //     //打开Word文档
        //     poCtrl.webOpen(fileUrl,OpenModeType.docNormalEdit,"张佚名");
        // });
    </script>
</head>
<body>
    <script type="text/javascript">
        function  getSelectionText(){
            if (document.getElementById("PageOfficeCtrl1").Document.Application.Selection.Range.Text != "") {
                return document.getElementById("PageOfficeCtrl1").Document.Application.Selection.Range.Text;
            } else {
               return null;
            }
        }
        function Save() {
            document.getElementById("PageOfficeCtrl1").WebSave();
        }
        function AddWhiteList() {
            alert(getSelectionText())
        }
        function AddBlackList() {
            alert(getSelectionText())
        }

    </script>
    <div style=" width:auto; height:700px;"><%=filePath%>
        <%=request%>
       <%=poCtrl.getHtmlCode("PageOfficeCtrl1")%>
    </div>
</body>
</html>

