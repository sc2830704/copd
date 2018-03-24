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
  <!-- Custom styles for this template-->
  <link href="css/sb-admin.css" rel="stylesheet">

  <!-- Bootstrap core JavaScript-->
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/popper/popper.min.js"></script>
  <!-- Page level plugin JavaScript-->
  <script src="vendor/datatables/jquery.dataTables.js"></script>
  <script src="vendor/datatables/dataTables.bootstrap4.js"></script>
  <!-- Custom scripts for this page-->
  <script src="js/sb-admin-datatables.min.js"></script>
  <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>

  <script src="js/highcharts.js"></script>
  <script src="js/boost.js"></script>
  <script src="js/exporting.js"></script>

  <link rel="stylesheet" href="css\myStyle.css">
  <link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
  <script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>

  <!-- DataTable for Mobile -->
  <script src="js/rowReorder.min.js"></script>
  <script src="js/responsive.min.js"></script>
  <link rel="stylesheet" href="css\responsive.dataTables.min.css">
  <link rel="stylesheet" href="css\rowReorder.dataTables.min.css">
</head>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
  <!-- Navigation-->
  <?php require('module.php'); ?>

  <!-- center -->
  <div class="content-wrapper">
    <div class="row" id="PersonalDataAndFlot" style="display: none; padding: 0px 20px 0px 20px">
      <div class="col-xs-12 col-md-12 col-lg-8">
        <div class="card">
          <div class="card-header">
            <i class="fa fa-heart"></i>&nbsp;&nbsp;心率及血氧濃度
          </div>
          <div class="card-body">
            <div id="flot" style="height: 390px; max-width: 800px; margin: 0 auto;"></div>
          </div>
          <div class="card-footer"></div>
        </div>
      </div>
      <div class="col-xs-12 col-md-12 col-lg-4">
        <div class="card">
          <div class="card-header">
            <i class="fa fa-pencil"></i>&nbsp;&nbsp;活動紀錄
          </div>
          <div class="card-body">
            <input id="personal_name" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
            <!-- 個人年齡 -->
            <input id="age" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
            <!-- 前測 DBP SBP SPO2 HR -->
            前測：
            <input id="before_sbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <input id="before_dbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <input id="before_spo2" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <input id="before_hr" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <!-- 後測 DBP SBP SPO2 HR -->
            後測：
            <input id="after_sbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <input id="after_dbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <input id="after_spo2" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <input id="after_hr" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <!-- 運動時間 -->
            <input id="exercise_time" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <!-- 高強度運動時間 -->
            <input id="h_i_time" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff; width: 100%;" disabled></input><br>
            <div align="right">
              <button id="personal_datatable_cancel">關閉</button>
            </div>
          </div>
          <div class="card-footer">
          </div>
        </div>
      </div>
    </div>
    <!-- 全部資料之DataTable -->
    <br>
    <div class="col-xs-12 col-md-12 col-lg-12" align="right">
      <!-- 時間篩選 -->
      <select id="time_select">
        <option value="getall">全部</option>
        <option value="getbytime/week">近一週</option>
        <option value="getbytime/month">本月</option>
      </select>&nbsp;&nbsp;
      <button onclick="window.location.href='Download_Activity_PDF.php'" style="display: none;">PDF</button>
      <button onclick="window.location.href='Download_Activity_Excel.php'">EXCEL</button>
    </div>
    <br>
    <!-- container-fluid-->
    <!-- Activity DataTable-->
    <div id="datatable_activity_visible" style="width: 98%; margin: auto;">
      <table id="activityTable" class="display" cellspacing="0" width="100%">
        <thead>
          <tr>
            <th>編號</th>
            <th>帳號</th>
            <th>步數</th>
            <th>距離(公尺)</th>
            <th>中高強度運動(分)</th>
            <th>開始時間</th>
            <th>結束時間</th>
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

<script type="text/JavaScript">
$(document).ready(function(){
  var activityDataTable = $('#activityTable').DataTable({
    "order": [[ 5, "desc" ]],
    rowReorder: {
      selector: 'td:nth-child(2)'
    },
    responsive: true
  });
  getActivityData();

  $("#personal_datatable_cancel").click(function(){
      $("#PersonalDataAndFlot").hide();
  });
  $("#time_select").change(function(){
    getActivityData();
  })
  $('#activityTable').on('click', 'tr', function(){
    var row = $(this).children('td:first-child').text();
    row==''? '':click_row(row);
  });
});

function topFunction() {
  document.body.scrollTop = 0;
  document.documentElement.scrollTop = 0;
}

function click_row(row){
  //topFunction();
  $.ajax({
    type: "GET",
    url: "../apiv1/activity/getbyid/" + row,
    dataType: "json",
    data: {
    },
    success: function(data) {
      //顯示個人資料表
      $("#PersonalDataAndFlot").show();
      //繪製圖表
      LoadActivityFlotChart(row,data);
      //將bp和data的資料做分解
      var bp_data = JSON.parse(data.bp);
      //計算運動時間 hour:3,600,000 & minute:60,000 & second:1000
      var time2 = new Date(data.end_time);
      var time1 = new Date(data.start_time);
      var hour = (time2.getTime() - time1.getTime()) / 3600000;
      var minute = (time2.getTime() - time1.getTime()) / 60000;
      var second = (time2.getTime() - time1.getTime()) / 1000;
      var minute_int = Math.floor(minute);
      var second_int = second-(minute_int*60);
      //填入各項資訊
      document.getElementById('personal_name').value = '姓名：' + data.fname + ' ' + data.lname;
      document.getElementById('age').value = '年齡：' + data.age + '歲';
      document.getElementById('before_sbp').value = '  收縮壓：' + bp_data.before.sbp.toFixed(2) + ' mmhg';
      document.getElementById('before_dbp').value = '  舒張壓：' +  bp_data.before.dbp.toFixed(2) + ' mmhg';
      document.getElementById('before_spo2').value = '  血氧：' + bp_data.before.spo2.toFixed(2) + ' %';
      document.getElementById('before_hr').value = '  心率：' + bp_data.before.hr.toFixed(2) + ' 下/分';

      document.getElementById('after_sbp').value = '  收縮壓：' + bp_data.after.sbp.toFixed(2) + ' mmhg';
      document.getElementById('after_dbp').value = '  舒張壓：' + bp_data.after.dbp.toFixed(2) + ' mmhg';
      document.getElementById('after_spo2').value = '  血氧：' + bp_data.after.spo2.toFixed(2) + ' %';
      document.getElementById('after_hr').value = '  心率：' + bp_data.after.hr.toFixed(2) + ' 下/分';
      document.getElementById('exercise_time').value = '運動時間：' + minute_int + '分' + second_int + '秒';
      document.getElementById('h_i_time').value = '中高強度運動時間：' + data.h_i_time + '分';
    }
  })
}

function getActivityData() {
  $.ajax({
    type : 'GET',
    url  : '../apiv1/activity/' + $("#time_select").val(),
    dataType: 'json',
    cache: false,
    success :  function(result){
      $("#activityTable").show();
      LoadActivityDataToTable(result);
    },
    error: function(jqXHR) {
      $("#PersonalDataAndFlot").hide();

      $("#activityTable").hide();
      alert("發生錯誤: " + jqXHR.status + ' ' + jqXHR.statusText);
    }
  });
}

function LoadActivityDataToTable(activityData) {
  var activityDataTable = $('#activityTable').DataTable();
  activityDataTable.clear().draw(false);
  for (var i in activityData) {
    activityDataTable.row.add([
      activityData[i].id,
      activityData[i].uid,
      activityData[i].step,
      activityData[i].distance,
      activityData[i].h_i_time,
      activityData[i].start_time,
      activityData[i].end_time
    ]).draw(false);
  }
  activityDataTable.columns.adjust().draw();
}

function LoadActivityFlotChart(row,data) {
  var Parse_data = JSON.parse(data.data);
  var arr_hr = [];
  var arr_spo2 = [];
  for (var i in Parse_data) {

    //若有用到，將millisecond_to_date輸出成此格式->Mon Dec 25 2017 14:47:24 GMT+0800 (台北標準時間)
    var millisecond_to_date = new Date(parseInt(Parse_data[i].datetime));
    var year = millisecond_to_date.getFullYear();
    var mon = millisecond_to_date.getMonth();
    var day = millisecond_to_date.getDate();
    var h = millisecond_to_date.getHours();
    var m = millisecond_to_date.getMinutes();
    var s = millisecond_to_date.getSeconds();
    //console.log(year,mon,day,h,m,s);

    arr_hr.push([
    	Date.UTC(year, mon, day, h, m, s),
    	Parse_data[i].hr
    ]);
    arr_spo2.push([
    	Date.UTC(year, mon, day, h, m, s),
    	Parse_data[i].spo2
    ]);
  }
  getData(arr_hr,arr_spo2);
}

function getData(hr_data,spo2_data) {
    console.time('line');
	Highcharts.chart('flot', {

	    chart: {
	        zoomType: 'x'
	    },

	    boost: {
	        useGPUTranslations: true
	    },

	    title: {
	        text: 'COPD 管理系統活動紀錄'
	    },

	    /*subtitle: {
	        text: '心率和SPO2'
	    },*/

	    xAxis: {
		    type: 'datetime'
		},

	    tooltip: {
	        valueDecimals: 2
	    },

	    series: [{
	    	name: '心率',
	        data: hr_data,
	        lineWidth: 0.5
	    },
	    {
	    	name: 'SPO2',
	    	data: spo2_data,
	    	lineWidth: 0.5
	    }]

	});
	console.timeEnd('line');
}

</script>

</html>
