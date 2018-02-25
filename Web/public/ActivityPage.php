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
  <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
  <!-- Core plugin JavaScript-->
  <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
  <!-- Page level plugin JavaScript-->
  <script src="vendor/datatables/jquery.dataTables.js"></script>
  <script src="vendor/datatables/dataTables.bootstrap4.js"></script>
  <!-- Custom scripts for all pages-->
  <script src="js/sb-admin.min.js"></script>
  <!-- Custom scripts for this page-->
  <script src="js/sb-admin-datatables.min.js"></script>
  <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>

  <script src="js/highcharts.js"></script>
  <script src="js/boost.js"></script>
  <script src="js/exporting.js"></script>

  <link rel="stylesheet" href="css\myStyle.css">
  <link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
  <script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>
</head>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
  <!-- Navigation-->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" id="mainNav">
    <a class="navbar-brand" href="homepage.php">Welcome COPD Manage System</a>
    <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarResponsive">
      <ul class="navbar-nav navbar-sidenav" id="exampleAccordion">
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="HomePage">
          <a class="nav-link" href="homepage.php">
            <i class="fa fa-fw fa-windows"></i>
            <span class="nav-link-text">COPD首頁</span>
          </a>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Patient">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseComponents" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-child"></i>
            <span class="nav-link-text" id="test">病患資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseComponents">
            <li>
              <a href="PatientPage.php">Patient</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Environment">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseExamplePages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-bank"></i>
            <span class="nav-link-text" id="test">環境資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseExamplePages">
            <li>
              <a href="EnvironmentPage.php">Environment</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Daily">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseDailyPages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">每日統計</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseDailyPages">
            <li>
              <a href="DailyPage.php">Daily</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Activity">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseActivityPages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-bar-chart-o"></i>
            <span class="nav-link-text" id="test">活動紀錄</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseActivityPages">
            <li>
              <a href="ActivityPage.php">Activity</a>
            </li>
          </ul>
        </li>
      </ul>
      <ul class="navbar-nav sidenav-toggler">
        <li class="nav-item">
          <a class="nav-link text-center" id="sidenavToggler">
            <i class="fa fa-fw fa-angle-left"></i>
          </a>
        </li>
      </ul>
      <ul class="navbar-nav ml-auto">
       <text align="text-center" style="margin: auto; color:yellow; padding-right: 10px" id="login_msg">Hello! <?php echo $_SESSION['account'] ?></text>
        <li class="nav-item">
          <a class="nav-link" data-toggle="modal" data-target="#exampleModal">
            <i class="fa fa-fw fa-sign-out"></i>Logout</a>
        </li>
      </ul>
    </div>
  </nav>


  <!-- center   -->
  <div class="content-wrapper" style="padding-left: 5px">
    <div class="row" id="PersonalDataAndFlot" style="display: none;">
      <div class="column" style="width: 65%; padding-right: 5px;">  	
  		<div id="flot" style="height: 400px; max-width: 800px; margin: 0 auto;"></div>
      </div>
      <div class="column" style="width: 35%;">
        <!-- click activity record -->
        <div class="edit_table" id="ActivityRecord" style="display: none; border: 1px solid;">
          <h2>活動紀錄</h2>
          <br>
          <!-- 個人姓名 -->
          <input id="personal_name" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
          <!-- 個人年齡 -->
          <input id="age" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
          <!-- 前測 DBP SBP -->
          <input id="before_dbp" style="width:200px; text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input>
          <input id="before_sbp" style="width:200px; text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
          <!-- 後測 DBP SBP -->
          <input id="after_dbp" style="width:200px; text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input>
          <input id="after_sbp" style="width:200px; text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
          <!-- 運動時間 -->
          <input id="exercise_time" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
          <!-- 高強度運動時間 -->
          <input id="h_i_time" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
          <!-- 個人資訊之DataTable -->

          <div align="right">
            <button id="personal_datatable_cancel">關閉</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 全部資料之DataTable -->
    <br>
    <div class="container-fluid">
      <div class="row">
        <div class="column" align="right" style="width: 100%; padding-left: 15px">
        <!-- 時間篩選 -->
          <select id="time_select">
            <option value="getall">全部</option>
            <option value="getbytime/week">近一週</option>
            <option value="getbytime/month">本月</option>
          </select>&nbsp;&nbsp;
          <button onclick="window.location.href='Download_Activity_PDF.php'" style="display: none;">PDF 下載</button>
          <button onclick="window.location.href='Download_Activity_Excel.php'">EXCEL 下載</button>
        </div>
      </div>
    </div>
    <br>
    <!-- container-fluid-->
    <div class="container-fluid">
	    <!-- Activity DataTable-->
        <div id="datatable_activity_visible">
          <table id="activityTable" class="display" cellspacing="0" width="100%">
              <thead>
                  <tr>
                      <th>編號</th>
                      <th>帳號</th>
                      <th>步數</th>
                      <th>開始時間</th>
                      <th>結束時間</th>
                      <th>距離(公尺)</th>
                      <th>高強度運動(分)</th>
                  </tr>
              </thead>
          </table>
        </div>
	    <!-- /.content-wrapper-->
	    <footer class="sticky-footer">
	      <div class="container">
	        <div class="text-center">
	          <small>COPD Walk © 2018</small>
	        </div>
	      </div>
	    </footer>
	    <!-- Scroll to Top Button-->
	    <a class="scroll-to-top rounded" href="#page-top">
	      <i class="fa fa-angle-up"></i>
	    </a>
	    <!-- Logout Modal-->
	    <div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	      <div class="modal-dialog" role="document">
	        <div class="modal-content">
	          <div class="modal-header">
	            <h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
	            <button class="close" type="button" data-dismiss="modal" aria-label="Close" >
	              <span aria-hidden="true">×</span>
	            </button>
	          </div>
	          <div class="modal-body">Select "Logout" below if you are ready to end your current session.</div>
	          <div class="modal-footer">
	            <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
	            <a class="btn btn-primary" href="index.php">Logout</a>
	          </div>
	        </div>
	      </div>
	    </div>
    </div>
</body>

<script type="text/JavaScript">
$(document).ready(function(){
  var activityDataTable = $('#activityTable').DataTable({
    "order": [[ 3, "desc" ]]
  });
  getActivityData();

  $("#personal_datatable_cancel").click(function(){
      $("#ActivityRecord").hide();
      $("#flot-placeholder").hide();
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

function click_row(row){
  $.ajax({
    type: "GET",
    url: "../apiv1/activity/getbyid/" + row,
    dataType: "json",
    data: {
    },
    success: function(data) {
      //顯示個人資料表
      $("#ActivityRecord").show();
      $("#flot-placeholder").show();
      $("#PersonalDataAndFlot").show();
      //繪製圖表
      LoadActivityFlotChart(row,data);
      //將bp的資料做分解
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
      document.getElementById('before_dbp').value = '前測 舒張壓：' + bp_data.before.dbp.toFixed(2) + 'mmhg';
      document.getElementById('before_sbp').value = '收縮壓：' + bp_data.before.sbp.toFixed(2) + 'mmhg';
      document.getElementById('after_dbp').value = '後測 舒張壓：' + bp_data.after.dbp.toFixed(2) + 'mmhg';
      document.getElementById('after_sbp').value = '收縮壓：' + bp_data.after.sbp.toFixed(2) + 'mmhg';
      document.getElementById('exercise_time').value = '運動時間：' + minute_int + '分' + second_int + '秒';
      document.getElementById('h_i_time').value = '高強度運動時間：' + data.h_i_time + '分';
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
      $("#ActivityRecord").hide();
      $("#flot-placeholder").hide();
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
      activityData[i].start_time,
      activityData[i].end_time,
      activityData[i].distance,
      activityData[i].h_i_time
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
    console.log(year,mon,day,h,m,s);

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

	    subtitle: {
	        text: '心率和SPO2'
	    },

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
