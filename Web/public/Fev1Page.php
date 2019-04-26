<?php
if($_SERVER["HTTPS"] != "on")
{
    header("Location: https://" . $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"]);
    exit();
}
session_start();
if(empty($_SESSION['account'])){
    header("Location: index.php");
}
else{
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>COPD Manage System</title>
    <!-- Bootstrap core CSS-->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="css/sb-admin.css" rel="stylesheet">

    <!-- Bootstrap core JavaScript-->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/popper/popper.min.js"></script>
    <!-- Page level plugin JavaScript-->
    <script src="vendor/chart.js/Chart.min.js"></script>
    <!-- Custom scripts for this page-->
    <script src="js/sb-admin-datatables.min.js"></script>

    <script src="js/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" href="css\myStyle.css">
    <link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
    <script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>

    <!-- DataTable for Mobile -->
    <script src="js/rowReorder.min.js"></script>
    <script src="js/responsive.min.js"></script>
    <link rel="stylesheet" href="css\responsive.dataTables.min.css">
    <link rel="stylesheet" href="css\rowReorder.dataTables.min.css">
</head>

<script type="text/JavaScript">

    $(document).ready(function(){
        getFev1Data();
    });

    function getFev1Data() {
        $.ajax({
            type : 'GET',
            url  : '../apiv1/fev1/getall',
            dataType: 'json',
            cache: false,
            success :  function(result){
                $("#error_msg").hide();
                $("#fev1Table").show();
                LoadFev1DataToTable(result);
            },
            error: function(jqXHR) {
                $("#fev1Table").hide();
                $("#error_msg").show();
                $("#error_msg").html("查無資料");
            }
        });
    }

    function LoadFev1DataToTable(fev1Data) {
        var fev1DataTable = $('#fev1Table').DataTable();
        fev1DataTable.clear().draw(false);
        for (var i in fev1Data){
            fev1DataTable.row.add([
                fev1Data[i].index,
                fev1Data[i].id,
                fev1Data[i].fname,
                fev1Data[i].lname,
                fev1Data[i].fev1,
                fev1Data[i].testing_time
            ]).draw(false);
        }
        fev1DataTable.columns.adjust().draw();
    }
</script>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
<!-- Navigation-->
<?php require('module.php') ?>

<!-- center -->
<div class="content-wrapper">
    <div class="container-fluid">
        <div id="datatable_env_visible" style="width: 98%; margin: auto;">
            <table id="fev1Table" class="display" cellspacing="0" width="100%">
                <thead>
                <tr>
                    <th>編號</th>
                    <th>帳號</th>
                    <th>姓</th>
                    <th>名</th>
                    <th>Fev1</th>
                    <th>測量時間</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
    <!-- Logout Button + Footer -->
    <?php require('footer_and_logout.php'); ?>
</div>
<!-- 左邊縮排需要的.js -->
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<script src="vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="js/sb-admin.min.js"></script>
</body>

</html>
