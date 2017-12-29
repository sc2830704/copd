<?php
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
</head>

<script src="js/jquery-1.11.3.min.js"></script>
<link rel="stylesheet" href="css\myStyle.css">
<link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
<script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>

<script type="text/JavaScript">
$(document).ready(function(){
  $("#activityTable").show();
  $("#personal_datatable_cancel").click(function(){
      $("#ActivityRecord").hide();
  });

  $.ajax({
    type : 'GET',
    url : '../apiv1/activity/' + $("#time_select").val(),
    dataType: 'json',
    result :{
    },
    success :  function(result){
        var data_array = [];
        for (i = 0; i < result.length; i++) {
          data = [[  result[i].id,result[i].uid,result[i].step,result[i].start_time,result[i].end_time,result[i].distance,result[i].h_i_time  ]];
          var data_array = data_array.concat(data);
        } 
        //pass data to datatable
        if(result=='No data avaliable.'){
          alert('No data avaliable.');
          $("#activityTable").hide();
        }
        else{
          console.log(data_array);
          $("#activityTable").show();
          $('#activityTable').DataTable({
            "aaData": data_array, //here we get the array data from the ajax call.
          });
          $('#activityTable').DataTable().draw();
        }
    },
    error: function(jqXHR) {
      alert("發生錯誤: " + jqXHR.status);
    }
  });

  $("#time_select").change(function(){
    $('#activityTable').DataTable().destroy();
    $.ajax({
      type : 'GET',
      url : '../apiv1/activity/' + $("#time_select").val(),
      dataType: 'json',
      result :{
      },
      success :  function(result){
        var data_array = [];
        console.log(result.length);
        for (i = 0; i < result.length; i++) {
          data = [[  result[i].id,result[i].uid,result[i].step,result[i].start_time,result[i].end_time,result[i].distance,result[i].h_i_time  ]];
          var data_array = data_array.concat(data);
        } 
        //pass data to datatable
        if(result=='No data avaliable.'){
          alert('No data avaliable.');
          $("#activityTable").hide();
        }
        else{
          console.log(data_array);
          $("#activityTable").show();
          $('#activityTable').DataTable({
            "aaData": data_array, //here we get the array data from the ajax call.
          });
          $('#activityTable').DataTable().draw();
        }
      },
      error: function(jqXHR) {
        alert("發生錯誤: " + jqXHR.status);
      }
    });
  });

  $(document).on("click", "tr[class='odd'],tr[class='even']", function(){
        $("#ActivityRecord").show();
        $getid = $(this).children('td:first-child').text();
        $.ajax({
            type: "GET",
            url: "../apiv1/activity/getbyid/" + $getid,
            dataType: "json",
            data: {
            },
            success: function(data) {
              //印出彈出表單之DataTable
              print_personal_table();
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
              document.getElementById('age').value = '年齡：';
              document.getElementById('before_dbp').value = '前測 舒張壓：' + bp_data.before.sbp + 'hmmg';
              document.getElementById('before_sbp').value = ' 收縮壓：' + bp_data.before.dbp + 'hmmg';
              document.getElementById('after_dbp').value = '後測 舒張壓：' + bp_data.after.sbp + 'hmmg';
              document.getElementById('after_sbp').value = ' 收縮壓：' + bp_data.after.dbp + 'hmmg';
              document.getElementById('exercise_time').value = '運動時間：' + minute_int + '分' + second_int + '秒';
              document.getElementById('h_i_time').value = '高強度運動時間：' + data.h_i_time + '秒';
            },

            error: function(jqXHR) {
                alert("發生錯誤: " + jqXHR.status);
            }
        })    
    });

    function print_personal_table (){
      $('#personal_Activity').DataTable().destroy();
      $.ajax({
        type : 'GET',
        url  : '../apiv1/activity/getbyiddata/' + $getid,
        dataType: 'json',
        cache: false,
        success :  function(result)
        {
          //pass data to datatable
          $("#personal_Activity").show();
          console.log(result); // just to see I'm getting the correct data.
          $('#personal_Activity').DataTable({
             "aaData": result, //here we get the array data from the ajax call.
          });
          $('#personal_Activity').DataTable().draw();
        },
        error: function(jqXHR) {
          alert("發生錯誤: " + jqXHR.status);
        }
      });
    }
});

</script>

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
            <i class="fa fa-fw fa-dashboard"></i>
            <span class="nav-link-text">COPD首頁</span>
          </a>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Patient">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseComponents" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">病患資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseComponents">
            <li>
              <a href="PatientPage.php">Table</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Environment">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseExamplePages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">環境資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseExamplePages">
            <li>
              <a href="EnvironmentPage.php">Table</a>
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
              <a href="DailyPage.php">Table</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Activity">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseActivityPages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">活動紀錄</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseActivityPages">
            <li>
              <a href="ActivityPage.php">Table</a>
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
    <!-- click activity record -->
    <div class="edit_table" id="ActivityRecord" style="display: none;">
      <h2>活動紀錄</h2>
      <br>
      <!-- 個人姓名 -->
      <input id="personal_name" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
      <!-- 個人年齡 -->
      <input id="age" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
      <!-- 前測 DBP SBP -->
      <input id="before_dbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input>
      <input id="before_sbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
      <!-- 後測 DBP SBP -->
      <input id="after_dbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input>
      <input id="after_sbp" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
      <!-- 運動時間 -->
      <input id="exercise_time" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br>
      <!-- 高強度運動時間 -->
      <input id="h_i_time" style="text-align: left; padding-right: 5px; border: 0px; background: #ffffff;" size="number" disabled></input><br><br>
      <!-- 個人資訊之DataTable -->
      <div id="datatable_personal_visible">
          <table id="personal_Activity" class="display" cellspacing="0" width="100%" style="display: none;">
              <thead>
                  <tr>
                      <th>SPO2</th>
                      <th>心跳</th>
                      <th>時間</th>
                  </tr>
              </thead>
              <tfoot>
                  <tr>
                      <th>SPO2</th>
                      <th>心跳</th>
                      <th>時間</th>
                  </tr>
              </tfoot>
          </table>
        </div>
        <br>
        <div align="right">
          <button id="personal_datatable_cancel">取消</button>
        </div>
    </div>
    <!-- 全部資料之DataTable -->
    <br>
    <div class="container-fluid">
      <div class="row">
        <div class="column" align="left" style="width: 30%; padding-left: 15px">
        <!-- 時間篩選 -->
        <select id="time_select">
          <option value="getallweek">近一週</option>
          <option value="getallmonth">本月</option>
          <option value="getall">全部</option>
        </select>
        <!-- 使用者篩選 -->
        <select id="human">
          <option value="1"></option>
          <option value="2"></option>
        </select>
        </div>
        <div class="column" align="right" style="width: 70%; ">
          <button onclick="window.location.href='Download_Activity_PDF.php'" >PDF Download</button>
          <button onclick="window.location.href='Download_Activity_Excel.php'">EXCEL Download</button>
        </div>
      </div>
    </div>
    <br>
    <!-- container-fluid-->
    <div class="container-fluid">
	    <!-- Activity DataTable-->
        <div id="datatable_activity_visible">
          <table id="activityTable" class="display" cellspacing="0" width="100%" style="display: none;">
              <thead>
                  <tr>
                      <th>編號</th>
                      <th>帳號</th>
                      <th>步數</th>
                      <th>開始時間</th>
                      <th>結束時間</th>
                      <th>距離(公尺)</th>
                      <th>高強度運動(秒)</th>
                  </tr>
              </thead>
              <tfoot>
                  <tr>
                      <th>編號</th>
                      <th>帳號</th>
                      <th>步數</th>
                      <th>開始時間</th>
                      <th>結束時間</th>
                      <th>距離(公尺)</th>
                      <th>高強度運動(秒)</th>
                  </tr>
              </tfoot>
              
          </table>
        </div>
	    <!-- /.content-wrapper-->
	    <footer class="sticky-footer">
	      <div class="container">
	        <div class="text-center">
	          <small>Copyright © Your Website 2017</small>
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
	    <!-- Bootstrap core JavaScript-->
	    <script src="vendor/jquery/jquery.min.js"></script>
	    <script src="vendor/popper/popper.min.js"></script>
	    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
	    <!-- Core plugin JavaScript-->
	    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
	    <!-- Page level plugin JavaScript-->
	    <script src="vendor/chart.js/Chart.min.js"></script>
	    <script src="vendor/datatables/jquery.dataTables.js"></script>
	    <!-- <script src="vendor/datatables/dataTables.bootstrap4.js"></script> -->
	    <!-- Custom scripts for all pages-->
	    <script src="js/sb-admin.min.js"></script>
	    <!-- Custom scripts for this page-->
	    <script src="js/sb-admin-datatables.min.js"></script>
	    <script src="js/sb-admin-charts.min.js"></script>
    </div>
</body>

</html>
