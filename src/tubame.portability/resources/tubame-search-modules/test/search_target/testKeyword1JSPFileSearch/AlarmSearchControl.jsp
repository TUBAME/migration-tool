<%--

/* **************************************************************

 * ナレッジシステム WEBサーバサブシステム

 * Copyrightc 2003 NEC Corporation

 * ファイル名   : AlarmSearchControl.jsp

 * バージョン   : $Id: AlarmSearchControl.jsp,v 1.2 2005/06/22 09:47:37 knlg04 Exp $

 * 概要         : 警報履歴検索

 * 備考         :

 * 修正履歴     : 2003/06/13 四海		新規作成

 *                2003/09/17 四海		スクリプトをjavascriptに変更

 *                2003/09/25 四海		null対策強化

 *                2003/10/03 四海		publicをno-cacheに変更

 *				  2004/02/06 中原		window.open対応

 *				  2004/03/22 中原		異常終了の処理にグレーアウト解除を追加

 ****************************************************************/

--%>

<%@ page contentType="text/html; charset=EUC-JP" pageEncoding="EUC-JP" %>



<%@ page import="java.util.*" %>

<%@ page import="java.text.*" %>

<%@ page import="jp.co.nttdocomo.knlgsys.web.web.util.*" %>



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

<title>警報履歴検索 - ナレッジシステム</title>



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

