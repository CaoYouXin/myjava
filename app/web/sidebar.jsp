<%--
  Created by IntelliJ IDEA.
  User: cls
  Date: 15-3-22
  Time: 下午1:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String url = request.getRequestURL().toString();
    boolean b2 = url.endsWith("goods.jsp");
    boolean b3 = url.endsWith("client.jsp");
    boolean b4 = b2 || b3;
    boolean b5 = url.endsWith("order.jsp");
    boolean b6 = url.endsWith("user.jsp");
    boolean b1 = !(b2 || b3 || b4 || b5 || b6);
%>
<!-- Sidebar -->
<div id="sidebar">

    <div class="sidebar-tabs">
        <ul class="tabs-nav two-items">
            <li><a href="#general" title=""><i class="icon-reorder"></i></a></li>
            <li><a href="#stuff" title=""><i class="icon-cogs"></i></a></li>
        </ul>

        <div id="general">

            <div class="general-stats widget">
                <ul class="head">
                    <li><span>系统用户</span></li>
                    <li><span>入库单数</span></li>
                    <li><span>出库单数</span></li>
                </ul>
                <ul class="body">
                    <li><strong>116k+</strong></li>
                    <li><strong>1290</strong></li>
                    <li><strong>554</strong></li>
                </ul>
            </div>

            <!-- Main navigation -->
            <ul class="navigation widget">
                <li<%=b1 ? " class=\"active\"" : ""%>><a href="/index.jsp" title=""><i class="icon-home"></i>仪表盘</a></li>
                <li<%=b4 ? " class=\"active\"" : ""%>>
                    <a href="#" title="" class="expand"<%=b4 ? " id=\"current\"" : ""%>><i class="icon-reorder"></i>小册子</a>
                    <ul>
                        <li><a href="/goods.jsp" title=""<%=b2 ? " class=\"current\"" : ""%>>商品管理</a></li>
                        <li><a href="/client.jsp" title=""<%=b3 ? " class=\"current\"" : ""%>>客户管理</a></li>
                    </ul>
                </li>
                <li<%=b5 ? " class=\"active\"" : ""%>><a href="/order.jsp" title=""><i class="icon-tasks"></i>单据</a></li>
            </ul>
            <!-- /main navigation -->

        </div>

        <div id="stuff">

            <!-- Admin Main navigation -->
            <ul class="navigation widget">
                <li<%=b1 ? " class=\"active\"" : ""%>><a href="/index.jsp#stuff" title=""><i class="icon-home"></i>仪表盘</a></li>
                <li<%=b6 ? " class=\"active\"" : ""%>><a href="/user.jsp#stuff" title=""><i class="icon-user"></i>用户管理</a></li>
            </ul>
            <!-- /Admin main navigation -->

        </div>

    </div>

</div>
<!-- /sidebar -->
