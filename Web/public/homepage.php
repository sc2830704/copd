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
  <!-- Custom fonts for this template-->
  <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
  <!-- Page level plugin CSS-->
    
  <!-- Bootstrap core JavaScript-->
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/popper/popper.min.js"></script>
  <!-- Custom scripts for all pages-->

  <!--   <link href="vendor/datatables/dataTables.bootstrap4.css" rel="stylesheet">   -->
  <!-- Custom styles for this template-->
  <link href="css/sb-admin.css" rel="stylesheet">
  <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>  
</head>

<link rel="stylesheet" href="css\myStyle.css">

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
  <!-- Navigation-->
  <?php require('module.php'); ?>

  <!-- content -->
  <div class="content-wrapper">
    <div class="container" align="center">
      <br><h1><strong>COPD walk 善步佳</strong></h1><br>
      <h4>開發慢性病患智慧健康自我管理系統與裝置 —— 以COPD為例</h4><br>
      <h4>The development of a smart health self-management system and device for patients with chronic illnesses: Chronic obstruction pulmonary disease</h4><br>
      <img class="img-fluid" src="pic/main background for management.jpg" height="1440" width="1920" style="border-radius: 5px;">
    </div>
    <!-- /.content-wrapper-->

    <!-- Logout Button + Footer -->
    <?php require('footer_and_logout.php'); ?>
  </div>
  <!-- 左邊縮排需要的.js -->
  <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
  <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="js/sb-admin.min.js"></script>
</body>

</html>
