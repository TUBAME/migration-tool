<%--

/* **************************************************************

 * �ʥ�å������ƥ� WEB�����Х��֥����ƥ�

 * Copyrightc 2003 NEC Corporation

 * �ե�����̾   : AlarmSearchControl.jsp

 * �С������   : $Id: AlarmSearchControl.jsp,v 1.2 2005/06/22 09:47:37 knlg04 Exp $

 * ����         : �������򸡺�

 * ����         :

 * ��������     : 2003/06/13 �ͳ�		��������

 *                2003/09/17 �ͳ�		������ץȤ�javascript���ѹ�

 *                2003/09/25 �ͳ�		null�к�����

 *                2003/10/03 �ͳ�		public��no-cache���ѹ�

 *				  2004/02/06 �渶		window.open�б�

 *				  2004/03/22 �渶		�۾ｪλ�ν����˥��졼�����Ȳ�����ɲ�

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

<title>�������򸡺� - �ʥ�å������ƥ�</title>



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

                    check = confirm( "������̤�ɽ�������Ķ���Ƥ��ޤ���\n" +

                            "ɽ�����(<%= LimitCount %>��)�ޤǤ����ɽ�����ޤ�����" );

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

����ȥ������Ǥ���

</body>
<!--  listEnabled -->


</html>

<%-- vim:se sw=4 ts=4 sts=4 et nu: --%>

