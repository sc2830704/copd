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
  <!--   <link href="vendor/datatables/dataTables.bootstrap4.css" rel="stylesheet">   -->
  <!-- Custom styles for this template-->
  <link href="css/sb-admin.css" rel="stylesheet">
    
  <!-- Bootstrap core JavaScript-->
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/popper/popper.min.js"></script>
  <!-- Page level plugin JavaScript-->
  <script src="vendor/chart.js/Chart.min.js"></script>
  <!--<script src="vendor/datatables/jquery.dataTables.js"></script> -->
  <!-- <script src="vendor/datatables/dataTables.bootstrap4.js"></script> -->
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
    //initial_date();

    var evaluateDataTable = $('#myTable').DataTable({
      "order": [[ 10, "desc" ]],
      rowReorder: {
        selector: 'td:nth-child(2)'
      },
      responsive: true
    });
    getevaluateData();
/*
    $("#time_select").change(function(){
      getevaluateData();
    })

    $("#env_date").change(function(){
      document.getElementById('start_time').value = $("#env_date").val() + " 00:00:00";
      document.getElementById('end_time').value = $("#env_date").val() + " 23:59:59";
      getEnvData();
    })
    $("#Daily_PDF_Download").click(function(){
      if ($("#download_start_time").val()==''||$("#download_end_time").val()==''){
        alert("日期不能為空!");
      }
      else if($("#download_start_time").val() > $("#download_end_time").val()){
        alert("開始時間不能大於結束時間!");
      }
      else{
        document.location.href="Download_Daily_zip.php?start_time=" + $("#download_start_time").val() + "&end_time=" + $("#download_end_time").val() + "&type=PDF";
      }
    })
    $("#Daily_Excel_Download").click(function(){
      if ($("#download_start_time").val()==''||$("#download_end_time").val()==''){
        alert("日期不能為空!");
      }
      else if($("#download_start_time").val() > $("#download_end_time").val()){
        alert("開始時間不能大於結束時間!");
      }
      else{
        document.location.href="Download_Daily_zip.php?start_time=" + $("#download_start_time").val() + "&end_time=" + $("#download_end_time").val() + "&type=Excel";
      }
    })
    */
  });

  function getevaluateData() {
    $.ajax({
      type : 'GET',
      url  : '../apiv1/evaluate/getall',// + $("#time_select").val(),
      dataType: 'json',
      cache: false,
      success :  function(result){
        $("#error_msg").hide();
        $("#myTable").show();
        LoadevaluateDataToTable(result);
      },
      error: function(jqXHR) {
        $("#myTable").hide();
        $("#error_msg").show();
        $("#error_msg").html("查無資料");
      }
    });
  }

  function LoadevaluateDataToTable(evaluateData) {
    var evaluateDataTable = $('#myTable').DataTable();
    evaluateDataTable.clear().draw(false);
    for (var i in evaluateData){
      evaluateDataTable.row.add([
        evaluateData[i].uid,
        evaluateData[i].mmrc,
        evaluateData[i].cat1,
        evaluateData[i].cat2,
        evaluateData[i].cat3,
        evaluateData[i].cat4,
        evaluateData[i].cat5,
        evaluateData[i].cat6,
        evaluateData[i].cat7,
        evaluateData[i].cat8,
        evaluateData[i].datetime
      ]).draw(false);
    }
    evaluateDataTable.columns.adjust().draw();
  }
/*
  function initial_date(){
    var today = new Date();
    var today_date;
    if ((today.getMonth()+1)<10){
      if(today.getDate()<10){
        today_date = today.getFullYear() + "-0" + (today.getMonth()+1) + "-0" + today.getDate();
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
      else{
        today_date = today.getFullYear() + "-0" + (today.getMonth()+1) + "-" + today.getDate();
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
    }
    else{
      if(today.getDate()<10){
        today_date = today.getFullYear() + "-" + (today.getMonth()+1) + "-0" + today.getDate();
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
      else{
        today_date = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + today.getDate();
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
    }
  }
  */

</script>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
  <!-- Navigation-->
  <?php require('module.php') ?>

  <!-- center -->
  <div class="content-wrapper">
    <div class="container-fluid">
      <div align="right">
        <label id="error_msg" style="color: red; display: none;"></label>&nbsp;&nbsp;&nbsp;
        <!--
        資料顯示：<select id="time_select">
        <option value="getbytime/week">近一週</option>
        <option value="getall">全部</option>
        <option value="getbytime/month">本月</option>
        </select>&nbsp;&nbsp;
        <button data-toggle="modal" data-target="#Download_Table">下載</button>
        <input type="text" id="start_time" style="display: none;">
        <input type="text" id="end_time" style="display: none;">
      </div>
      <div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" id="Download_Table">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">每日統計檔案下載</h5>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close" >
                <span aria-hidden="true">×</span>
              </button>
            </div>
            <div class="modal-body">
              開始時間：<input type="date" id="download_start_time"><br></br>
              結束時間：<input type="date" id="download_end_time"><br>
            </div>
            <div class="modal-footer">
              <button class="btn btn-primary" id="Daily_PDF_Download">PDF 下載</button>
              <button class="btn btn-primary" id="Daily_Excel_Download">Excel 下載</button>
            </div>
          </div>
          -->
      </div>
    </div><br>
    <!-- /.container-fluid-->
    <!-- Download Page -->
    <!-- Daily DataTable-->
    <div id="datatable_env_visible" style="width: 98%; margin: auto;">
      <table id="myTable" class="display" cellspacing="0" width="100%">
        <thead>
          <tr>
            <th>uid</th>
            <th>mmrc</th>
            <th>cat1</th>
            <th>cat2</th>
            <th>cat3</th>
            <th>cat4</th>
            <th>cat5</th>
            <th>cat6</th>
            <th>cat7</th>
            <th>cat8</th>
            <th>datetime</th>
          </tr>
        </thead>
      </table>
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
