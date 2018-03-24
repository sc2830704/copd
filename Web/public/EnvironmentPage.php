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
  <script src="vendor/datatables/jquery.dataTables.js"></script>
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
    initial_date();
    
	  var envDataTable = $('#evnTable').DataTable({
      "order": [[ 6, "desc" ]],
      rowReorder: {
        selector: 'td:nth-child(2)'
      },
      responsive: true
    });
	  getEnvData();

    $("#time_select").change(function(){
      getEnvData();
    })
    $("#env_date").change(function(){
      document.getElementById('start_time').value = $("#env_date").val() + " 00:00:00";
      document.getElementById('end_time').value = $("#env_date").val() + " 23:59:59";
      getEnvData();
    })
    $("#Download").click(function(){
      $("#Download_Table").toggle();
    })
    $("#Env_PDF_Download").click(function(){
      if ($("#download_start_time").val()==''||$("#download_end_time").val()==''){
        alert("日期不能為空!");
      }
      else if($("#download_start_time").val() > $("#download_end_time").val()){
        alert("開始時間不能大於結束時間!");
      }
      else{
        document.location.href="Download_Env_zip.php?start_time=" + $("#download_start_time").val() + "&end_time=" + $("#download_end_time").val() + "&type=PDF";
      }
    })
    $("#Env_Excel_Download").click(function(){
      if ($("#download_start_time").val()==''||$("#download_end_time").val()==''){
        alert("日期不能為空!");
      }
      else if($("#download_start_time").val() > $("#download_end_time").val()){
        alert("開始時間不能大於結束時間!");
      }
      else{
        document.location.href="Download_Env_zip.php?start_time=" + $("#download_start_time").val() + "&end_time=" + $("#download_end_time").val() + "&type=Excel";
      }
    })
	});

	function LoadEnvDataToTable(envData){
	  var envDataTable = $('#evnTable').DataTable();
		envDataTable.clear().draw(false);
		for (var i in envData) {
      envDataTable.row.add([
        envData[i].id,
        envData[i].deviceid,
        envData[i].temperature,
        envData[i].humidity,
        envData[i].pm25,
        envData[i].uv,
        envData[i].datetime
      ]).draw(false);
    }
		envDataTable.columns.adjust().draw();
	}

  function getEnvData() {
    $.ajax({
      type : 'GET',
      url  : '../apiv1/env/getbytime',
      dataType: 'json',
      data: {
        start_time: $("#start_time").val(),
        end_time: $("#end_time").val(),
      },
      cache: false,
      success :  function(result){
        $("#error_msg").hide();
        console.log(result);
        $("#datatable_env_visible").show();
        LoadEnvDataToTable(result);
      },
      error: function(jqXHR) {
        //alert("選取的日期（" + $("#env_date").val() + "）沒有資料");
        $("#datatable_env_visible").hide();
        $("#error_msg").show();
        $("#error_msg").html($("#env_date").val() + " 沒有資料  ");
      }
    });
  }
	 
  function initial_date(){
    var today = new Date();
    var today_date;
    if ((today.getMonth()+1)<10){
      if(today.getDate()<10){
        today_date = today.getFullYear() + "-0" + (today.getMonth()+1) + "-0" + today.getDate();
        document.getElementById('env_date').value = today_date;
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
      else{
        today_date = today.getFullYear() + "-0" + (today.getMonth()+1) + "-" + today.getDate();
        document.getElementById('env_date').value = today_date;
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
    }
    else{
      if(today.getDate()<10){
        today_date = today.getFullYear() + "-" + (today.getMonth()+1) + "-0" + today.getDate();
        document.getElementById('env_date').value = today_date;
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
      else{
        today_date = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + today.getDate();
        document.getElementById('env_date').value = today_date;
        document.getElementById('download_start_time').value = today_date;
        document.getElementById('download_end_time').value = today_date;
      }
    }
    document.getElementById('start_time').value = $("#env_date").val() + " 00:00:00";
    document.getElementById('end_time').value = $("#env_date").val() + " 23:59:59";
  }

</script>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
  <!-- Navigation-->
  <?php require('module.php') ?>

  <!-- center -->
  <div class="content-wrapper">
    <div class="container-fluid">
      <div class="col-lg-12" align="right">
        <label id="error_msg" style="color: red; display: none;"></label>&nbsp;&nbsp;&nbsp;&nbsp;
        <button data-toggle="modal" data-target="#Download_Table">下載</button>
      </div>
      <div class="col-lg-12" align="right" style="padding-top: 10px">
        <!-- 時間篩選 -->
        顯示資料日期：<input type="date" id="env_date">
        <input type="text" id="start_time" style="display: none;">
        <input type="text" id="end_time" style="display: none;">
      </div>
      <div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" id="Download_Table">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">環境檔案下載</h5>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close" >
                <span aria-hidden="true">×</span>
              </button>
            </div>
            <div class="modal-body">
              開始時間：<input type="date" id="download_start_time"><br></br>
              結束時間：<input type="date" id="download_end_time"><br>
            </div>
            <div class="modal-footer">
              <button class="btn btn-primary" id="Env_PDF_Download">PDF 下載</button>
              <button class="btn btn-primary" id="Env_Excel_Download">Excel 下載</button>
            </div>
          </div>
        </div>
      </div>
    </div><br>
    <!-- /.container-fluid-->
    <!-- EnvDataTable-->
    <div id="datatable_env_visible" style="width: 98%; margin: auto;">
      <table id="evnTable" class="display " cellspacing="0" width="100%">
        <thead>
          <tr>
            <th>編號</th>
            <th>裝置編號</th>
            <th>溫度</th>
            <th>濕度</th>
            <th>PM2.5</th>
            <th>紫外線指數</th>
            <th>時間</th>
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
