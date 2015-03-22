<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
    <title>首页 - 樂之道服装仓库系统</title>
    <link href="css/main.css" rel="stylesheet" type="text/css" />
    <!--[if IE 8]><link href="css/ie8.css" rel="stylesheet" type="text/css" /><![endif]-->
    <!--[if IE 9]><link href="css/ie9.css" rel="stylesheet" type="text/css" /><![endif]-->

    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.9.2.min.js"></script>

    <script type="text/javascript" src="js/plugins/ui/jquery.easytabs.min.js"></script>
    <script type="text/javascript" src="js/plugins/ui/jquery.collapsible.min.js"></script>
    <script type="text/javascript" src="js/plugins/ui/jquery.fancybox.js"></script>

    <script type="text/javascript" src="js/files/bootstrap.min.js"></script>

    <script type="text/javascript" src="js/files/functions_blank.js"></script>

    <script type="text/javascript">
        $(function(){
        });
    </script>

</head>

<body>

<%@include file="header.jsp"%>

<!-- Content container -->
<div id="container">

    <%@include file="sidebar.jsp"%>

    <!-- Content -->
    <div id="content">

        <!-- Content wrapper -->
        <div class="wrapper">

            <!-- Breadcrumbs line -->
            <div class="crumbs">
                <ul id="breadcrumbs" class="breadcrumb">
                    <li><a href="/index.jsp">仪表盘</a></li>
                    <li class="active"><a href="/client.jsp">客户管理</a></li>
                </ul>
            </div>
            <!-- /breadcrumbs line -->

            <br />

            <!-- Search widget -->
            <form class="search widget" action="#">
                <div class="autocomplete-append">
                    <ul class="search-options">
                        <li><a href="#" title="Go to search page" class="go-option tip"></a></li>
                        <li><a href="#" title="Advanced search" class="advanced-option tip"></a></li>
                        <li><a href="#" title="Settings" class="settings-option tip"></a></li>
                    </ul>
                    <input type="text" placeholder="search website..." id="autocomplete" />
                    <input type="submit" class="btn btn-info" value="Search" />
                </div>
            </form>
            <!-- /search widget -->

        </div>
        <!-- /content wrapper -->

    </div>
    <!-- /content -->

</div>
<!-- /content container -->

<%@include file="footer.jsp"%>

</body>
</html>
