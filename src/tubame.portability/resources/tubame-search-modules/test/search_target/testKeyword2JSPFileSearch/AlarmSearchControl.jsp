<%--

てすと
てすと

--%>



<%@ page contentType="text/html; charset=EUC-JP" pageEncoding="EUC-JP" %>

<!-- test -->

<%@ page import="java.util.*" %>

<!-- test -->

<%@ page import="java.text.*" %>

<%@ page import="jp.co.nttdocomo.knlgsys.web.web.util.*" %>

<!-- test -->

<%@ include file="../../common/common_implicit.jsf" %>





<%




String strCountKind = ( String ) request.getParameter( "count_kind" );


strCountKind = ( strCountKind == null ) ? "" : strCountKind;



String strLoad = "";

%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Frameset//EN">

<html>



<head>

<%@ include file="../../common/common_header.jsf" %>

<title>test</title>

<!-- test -->

<%

String strResult = ( String )request.getAttribute( KnlgWebDefine.ACTION_RESULT );

strResult = ( strResult == null ) ? "0" : strResult;

if ( strResult.equals( "1" ) ) {

    strLoad = "load()";

    %>



		

        <script type="text/javascript">


        <!--

        function load() {

                parent.parent.condition.listEnabled();
                
                

                location.href = "/jsp/common/ErrorDlg.jsp";

        }

    //-->

    </script>




        <%

} else {

    if ( request.getParameter( KnlgWebDefine.PID ).equals( "R01F03P02" ) ) {

        strLoad = "load()";

        String LimitCount = ( String )mapOpeInfo.get( "_alarm_limitcnt" );

        String RecCount   = ( String )mapOpeInfo.get( "_alarm_count" );

        if ( "kind_default".equals( strCountKind ) ||

                Integer.parseInt( RecCount ) <= Integer.parseInt( LimitCount ) ) {

            %>



                <script type="text/javascript">

                <!--

                function load() {

                    parent.parent.condition.NewWindow('<%= strStartupId %>');

                }

            //-->

            </script>




                <%
                
                

        } else {

            DecimalFormat df  = new DecimalFormat("###,###,##0");
            
            

            LimitCount = df.format( Long.parseLong( LimitCount ) );

            %>



                <script type="text/javascript">

                <!--

                function load() {

                    var check;

                    check = confirm( "検索結果が表示件数を超えています。\n" +

                            "表示件数(<%= LimitCount %>件)までを一覧表示しますか？" );

                    if ( check == true ) {

                        parent.parent.condition.NewWindow('<%= strStartupId %>');

                    } else {

                        parent.parent.condition.listEnabled();

                    }

                }

            //-->

            </script>



                <%

        }

    }

}

%>



</head>

<body onload="<%= strLoad %>">

コントロール部です。

</body>
<!--  listEnabled -->


</html>

<%-- vim:se sw=4 ts=4 sts=4 et nu: --%>

