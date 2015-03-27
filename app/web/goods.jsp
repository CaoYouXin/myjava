<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
    <title>商品管理 - 樂之道服装仓库系统</title>
    <link href="css/main.css" rel="stylesheet" type="text/css"/>
    <!--[if IE 8]>
    <link href="css/ie8.css" rel="stylesheet" type="text/css"/><![endif]-->
    <!--[if IE 9]>
    <link href="css/ie9.css" rel="stylesheet" type="text/css"/><![endif]-->

    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.9.2.min.js"></script>

    <script type="text/javascript" src="js/plugins/ui/jquery.easytabs.min.js"></script>
    <script type="text/javascript" src="js/plugins/ui/jquery.collapsible.min.js"></script>
    <script type="text/javascript" src="js/plugins/ui/jquery.fancybox.js"></script>
    <script type="text/javascript" src="js/plugins/ui/jquery.bootbox.min.js"></script>

    <script type="text/javascript" src="js/plugins/forms/jquery.uniform.min.js"></script>

    <script type="text/javascript" src="js/plugins/jquery.cookie.js"></script>

    <script type="text/javascript" src="js/user/user.util.js"></script>

    <script type="text/javascript" src="js/util/form.util.js"></script>
    <script type="text/javascript" src="js/util/ajax.util.js"></script>
    <script type="text/javascript" src="js/util/table.util.js"></script>

    <script type="text/javascript" src="js/files/bootstrap.min.js"></script>
    <script type="text/javascript" src="js/files/functions_blank.js"></script>

    <script type="text/javascript">
        $(function () {

            var username = getUserName();
            alert("当前用户：" + username);

            function successHandler(data) {
                if (data.suc === 'True') {
                    $("#data-table").find("tbody").empty();
                    $.each(data.data, function (i, goods) {
                        $("#data-table").find("tbody").append(goodsRow(goods));
                    });
                    $(".styled").uniform({radioClass: 'choice'});
                }
            }

            setGlobalHandler(successHandler);

            $("#search_btn").click(function () {
                var url = '/api/v1/entity/goods/filterSelect';
                var formData = getFormJson($("#form"));
                setLastCmd(url, formData);
                formData.un = getUserName();
                $.ajax(url, {
                    type: "POST",
                    data: formData,
                    dataType: "json",
                    success: successHandler,
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert("有问题啦！！！" + jqXHR + "！！！" + textStatus + "！！！" + errorThrown);
                        alert("问题详情（返回状态）！！！" + jqXHR.status);
                        alert("问题详情（返回内容）！！！" + jqXHR.responseText);
                    }
                });
            });
        });
    </script>

</head>

<body>

<%@include file="header.jsp" %>

<!-- Content container -->
<div id="container">

    <%@include file="sidebar.jsp" %>

    <!-- Content -->
    <div id="content">

        <!-- Content wrapper -->
        <div class="wrapper">

            <!-- Breadcrumbs line -->
            <div class="crumbs">
                <ul id="breadcrumbs" class="breadcrumb">
                    <li><a href="/index.jsp">仪表盘</a></li>
                    <li class="active"><a href="/goods.jsp">商品管理</a></li>
                </ul>
            </div>
            <!-- /breadcrumbs line -->

            <br/>

            <!-- Search widget -->
            <form class="search widget" action="#">
                <div class="autocomplete-append">
                    <ul class="search-options">
                        <li><a href="#" title="Go to search page" class="go-option tip"></a></li>
                        <li><a href="#" title="Advanced search" class="advanced-option tip"></a></li>
                        <li><a href="#" title="Settings" class="settings-option tip"></a></li>
                    </ul>
                    <input type="text" placeholder="search website..." id="autocomplete"/>
                    <input type="submit" class="btn btn-info" value="Search"/>
                </div>
            </form>
            <!-- /search widget -->

            <!-- Ajax Form -->
            <form method="post" class="form-horizontal" id="form">
                <fieldset>
                    <div class="widget row-fluid">
                        <div class="well">
                            <div class="alert margin">
                                <button type="button" class="close" data-dismiss="alert">×</button>
                                在这里可以查找或者添加新条目
                            </div>

                            <div class="control-group">
                                <label class="control-label">输入：</label>

                                <div class="controls">
                                    <div class="row-fluid">
                                        <div class="span3">
                                            <input type="text" name="code" class="span12"/><span
                                                class="help-block align-center">代码</span>
                                        </div>
                                        <div class="span3">
                                            <input type="text" name="name" class="span12"/><span
                                                class="help-block align-center">名称</span>
                                        </div>
                                        <div class="span3">
                                            <input type="text" name="color" class="span12"/><span
                                                class="help-block align-center">颜色</span>
                                        </div>
                                        <div class="span3">
                                            <input type="text" name="size" class="span12"/><span
                                                class="help-block align-center">尺寸</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-actions align-right">
                                <%--<span class="help-inline">Right aligned buttons</span>--%>
                                <button id="search_btn" type="button" class="btn btn-primary">查找</button>
                                <button id="add_btn" type="button" class="btn btn-danger">添加</button>
                                <button type="reset" class="btn">重置</button>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </form>
            <!-- /ajax form -->

            <div class="widget">
                <div class="navbar">
                    <div class="navbar-inner"><h6>数据</h6></div>
                </div>
                <div class="table-overflow">
                    <table class="table table-striped table-bordered table-checks" id="data-table">
                        <thead>
                        <tr>
                            <th><input type="checkbox" id="styled"/></th>
                            <th>代码</th>
                            <th>名称</th>
                            <th>颜色</th>
                            <th>尺寸</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
            <!-- /default datatable -->
        </div>
        <!-- /content wrapper -->

    </div>
    <!-- /content -->

</div>
<!-- /content container -->

<%@include file="footer.jsp" %>

</body>
</html>
