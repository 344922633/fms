package com.fms.utils;

import com.fms.domain.filemanage.File;
import com.fms.domain.filemanage.FileParser;
import com.fms.domain.filemanage.FileParserExt;
import com.handu.apollo.utils.CollectionUtil;
import com.handu.apollo.utils.StringPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ParserUtil {
    public static Map<String, String>  parser(FileParser fp, File file, java.io.File local, Map<String, Object> exParams) {
        Class[] classes = null;
        Object[] params = null;

        List<FileParserExt> parserExtList = (ArrayList)exParams.get("parserExtList");
//        //特殊解析器 txt (多参数)
//        if ("com.caeit.parser.txt.TxtParser".equals(fp.getClassName())) {
//            classes = new Class[] { java.io.File.class, boolean.class, char.class };
//            params = new Object[] { local, true, ',' };
//        } else {

            if(CollectionUtil.isNotEmpty(parserExtList))
            {
                int length = parserExtList.size();

                classes = new Class[length+1] ;
                params = new Object[length+1] ;
                classes[0]=java.io.File.class;
                params[0] = local ;

                for (int i = 0 ;i < parserExtList.size(); i ++)
                {
                    FileParserExt parserExt = parserExtList.get(i);

                    if ("int".equals(parserExt.getParameterName()))
                    {
                        classes[i+1]=int.class;
                        params[i+1] = Integer.valueOf(parserExtList.get(i).getParameterValue()) ;
                    }
                    else if ("boolean".equals(parserExt.getParameterName()))
                    {
                        classes[i+1]=boolean.class;
                        //params[i+1] = Integer.valueOf(parserExtList.get(i).getParameterValue()) ;
                        if("true".equals(parserExtList.get(i).getParameterValue()))
                        {
                            params[i+1] = true ;
                        }
                        else
                        {
                            params[i+1] = false ;
                        }
                    }
                    else if ("String".equals(parserExt.getParameterName()))
                    {
                        classes[i+1]=java.lang.String.class;
                        params[i+1] = parserExtList.get(i).getParameterValue() ;
                    }
                    else if ("char".equals(parserExt.getParameterName()))
                    {
                        classes[i+1] = char.class;
                        params[i+1] = parserExtList.get(i).getParameterValue().charAt(0) ;
                    }
                    else if ("File".equals(parserExt.getParameterName()))
                    {
                        classes[i+1]=java.io.File.class;
                        params[i+1] =  new java.io.File(parserExtList.get(i).getParameterValue()) ;
                    }
                    else if (parserExt.getParameterName().indexOf("List") >-1)
                    {
                        classes[i+1]=java.util.List.class;

                        String value =  parserExtList.get(i).getParameterValue();

                        String[] lists = value.split(",");

                        params[i+1] =  Arrays.asList(lists);;
                    }
                }

            }
            else
            {
                classes = new Class[] { java.io.File.class };
                params = new Object[1] ;
                params[0] = local ;
            }



//            if (fp.getClassName().indexOf("BrowserParser") > -1)
//            {
//                classes = new Class[] { java.io.File.class,java.io.File.class,int.class };
//                params = new Object[3] ;
//                params[0] = local ;
//                params[1] = new java.io.File("/root/cookie") ;
//                params[2] = Integer.valueOf(parserExtList.get(1).getParameterValue()) ;
//            }
//            else if(fp.getClassName().indexOf("CsvParser") > -1 || fp.getClassName().indexOf("StructuredTxtParser") > -1)
//            {
//                classes = new Class[] { java.io.File.class,boolean.class,boolean.class,char.class };
//                params = new Object[4] ;
//                params[0] = local ;
//
//                if("true".equals(parserExtList.get(0).getParameterValue()))
//                {
//                    params[1] = true ;
//                }
//                else
//                {
//                    params[1] = false ;
//                }
//                if("true".equals(parserExtList.get(1).getParameterValue()))
//                {
//                    params[2] = true ;
//                }
//                else
//                {
//                    params[2] = false ;
//                }
//
//                params[3] = parserExtList.get(2).getParameterValue();
//            }
//            else if(fp.getClassName().indexOf("ExcelParser") > -1 || fp.getClassName().indexOf("TxtParser") > -1  || fp.getClassName().indexOf("PdfParser") > -1 || fp.getClassName().indexOf("WordParser") > -1)
//            {
//                classes = new Class[] { java.io.File.class,java.util.List.class,java.util.List.class,java.util.List.class,int.class };
//                params = new Object[5] ;
//                params[0] = local ;
//                params[1] = new ArrayList<String>();
//                params[2] = new ArrayList<String>();
//                params[3] = new ArrayList<String>();
//                params[4] = Integer.valueOf(parserExtList.get(3).getParameterValue());
//            }
//            else
//            {
//
//            }

//            //获取解析器参数列表，把参数按顺序放进去，如果有黑白名单参数，取map中的黑白名单数据放进去
//            if (CollectionUtil.isNotEmpty(parserExtList))
//           {
//                for (FileParserExt parserExt:parserExtList)
//                {
//
//
////                    if ( parserExt.getParameterDesc().indexOf("黑名单") > -1)
////                    {
////                        params[i] = exParams.get("blackContent");
////                    }
////                    else if (parserExt.getParameterDesc().indexOf("白名单") > -1)
////                    {
////                        params[i] = exParams.get("whiteContent");
////                    }
////                    else
////                    {
////                        params[i] = parserExt.getParameterValue();
////                    }
//
//                    i ++;
//                }
//            }
        //}
        return (Map<String, String>) ParserUtil.parser(fp, classes, params);
    }

    public static Object parser(FileParser fp, Class[] clazzes, Object[] params) {
        return JarLoadUtil.execute(fp.getSource(), fp.getClassName(), fp.getMethodName(), clazzes, params);
    }
}
