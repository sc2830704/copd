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
  <!-- Custom styles for this template-->
  <link href="css/sb-admin.css" rel="stylesheet">

  <!-- FLOT CHART -->
  <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>
  <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/excanvas.min.js"></script><![endif]-->
  <script type="text/javascript" src="js/jquery.flot.min.js"></script>
  <script type="text/javascript" src="js/jquery.flot.time.js"></script>    
  <script type="text/javascript" src="js/jquery.flot.symbol.js"></script>
  <script type="text/javascript" src="js/jquery.flot.axislabels.js"></script>
  <script type="text/javascript" src="js/hashtable.js"></script>    
  <script type="text/javascript" src="js/jquery.numberformatter.js"></script> 

  <link rel="stylesheet" href="css\myStyle.css">
  <link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
  <script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>

  <script type="text/JavaScript">
  
  ///////////////////////////////////////// FLOT CHART /////////////////////////////////////////// -->
    //資料1
    var data1 = [
        [gd(2012, 0, 1), 1652.21], [gd(2012, 1, 1), 1742.14], [gd(2012, 2, 1), 1673.77], [gd(2012, 3, 1), 1649.69],
        [gd(2012, 4, 1), 1591.19], [gd(2012, 5, 1), 1598.76], [gd(2012, 6, 1), 1589.90], [gd(2012, 7, 1), 1630.31],
        [gd(2012, 8, 1), 1744.81], [gd(2012, 9, 1), 1746.58], [gd(2012, 10, 1), 1721.64], [gd(2012, 11, 2), 1684.76]
    ];
    //資料2
    var data2 = [
        [gd(2012, 0, 1), 0.63], [gd(2012, 1, 1), 5.44], [gd(2012, 2, 1), -3.92], [gd(2012, 3, 1), -1.44],
        [gd(2012, 4, 1), -3.55], [gd(2012, 5, 1), 0.48], [gd(2012, 6, 1), -0.55], [gd(2012, 7, 1), 2.54],
        [gd(2012, 8, 1), 7.02], [gd(2012, 9, 1), 0.10], [gd(2012, 10, 1), -1.43], [gd(2012, 11, 2), -2.14]
    ];
    //圖表基本設定
    var dataset = [
        { label: "Gold Price", data: data1, points: { symbol: "triangle"} }, //point的圖示為三角形
        { label: "Change", data: data2, yaxis: 2 } //以兩種y呈現
    ];
    
    var options = {
        series: {
            lines: {
                show: true
            },
            points: {
                radius: 3,
                fill: true,
                show: true
            }
        },
        xaxis: {
            //下方x軸
            mode: "time",
            tickSize: [1, "month"],
            tickLength: 0,
            axisLabel: "2012",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10
        },
        yaxes: [{
            //左邊y軸
            axisLabel: "Gold Price(USD)",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,
            tickFormatter: function (v, axis) {
                return $.formatNumber(v, { format: "#,###", locale: "us" });
            }
        }, {
            //右邊y軸
            position: "right",
            axisLabel: "Change(%)",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3
        }
      ],
        legend: {
            //線標外框
            noColumns: 0,
            labelBoxBorderColor: "#000000",
            position: "nw"
        },
        grid: {
            //圖表外框
            hoverable: true,
            borderWidth: 2,
            borderColor: "#633200",
            //圖表背景顏色 [上,下] 漸層下來
            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
        },
        //左線顏色,右線顏色
        colors: ["#FF0000", "#0022FF"]
    };
    $(document).ready(function(){
      var activityDataTable = $('#activityTable').DataTable();
      var personalDataTable = $('#personal_Activity').DataTable();
      getActivityData();

      $("#personal_datatable_cancel").click(function(){
          $("#ActivityRecord").hide();
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
          //顯示Flot Chart
          $.plot($("#flot-placeholder1"), dataset, options);
          $("#flot-placeholder1").UseTooltip();
          //顯示個人資料表
          $("#ActivityRecord").show();
          //印出彈出表單之DataTable
          LoadPersonalDataToTable(row,data);
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
          document.getElementById('before_dbp').value = '前測 舒張壓：' + bp_data.before.sbp + 'hmmg';
          document.getElementById('before_sbp').value = ' 收縮壓：' + bp_data.before.dbp + 'hmmg';
          document.getElementById('after_dbp').value = '後測 舒張壓：' + bp_data.after.sbp + 'hmmg';
          document.getElementById('after_sbp').value = ' 收縮壓：' + bp_data.after.dbp + 'hmmg';
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

    function LoadPersonalDataToTable(row,data){
      var personalDataTable = $('#personal_Activity').DataTable();
      personalDataTable.clear().draw(false);
      var Parse_data = JSON.parse(data.data);
      for (var i in Parse_data) {
        //change millisecond to date
        var millisecond_to_date = new Date(parseInt(Parse_data[i].datetime));
        Parse_data[i].datetime = millisecond_to_date;
        //load data to datatable
        personalDataTable.row.add([
          Parse_data[i].spo2,
          Parse_data[i].hr,
          Parse_data[i].datetime
        ]).draw(false);
      }
      personalDataTable.columns.adjust().draw();
    }

    function gd(year, month, day) {
        return new Date(year, month, day).getTime();
    }

    var previousPoint = null, previousLabel = null;
    var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

    $.fn.UseTooltip = function () {
        $(this).bind("plothover", function (event, pos, item) {
            if (item) {
                if ((previousLabel != item.series.label) || (previousPoint != item.dataIndex)) {
                    previousPoint = item.dataIndex;
                    previousLabel = item.series.label;
                    $("#tooltip").remove();

                    var x = item.datapoint[0];
                    var y = item.datapoint[1];

                    var color = item.series.color;
                    var month = new Date(x).getMonth();

                    //console.log(item);

                    if (item.seriesIndex == 0) {
                        showTooltip(item.pageX,
                        item.pageY,
                        color,
                        "<strong>" + item.series.label + "</strong><br>" + monthNames[month] + " : <strong>" + y + "</strong>(USD)");
                    } else {
                        showTooltip(item.pageX,
                        item.pageY,
                        color,
                        "<strong>" + item.series.label + "</strong><br>" + monthNames[month] + " : <strong>" + y + "</strong>(%)");
                    }
                }
            } else {
                $("#tooltip").remove();
                previousPoint = null;
            }
        });
    };

    function showTooltip(x, y, color, contents) {
        $('<div id="tooltip">' + contents + '</div>').css({
            position: 'absolute',
            display: 'none',
            top: y - 40,
            left: x - 120,
            border: '2px solid ' + color,
            padding: '3px',
            'font-size': '9px',
            'border-radius': '5px',
            'background-color': '#fff',
            'font-family': 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
            opacity: 0.9
        }).appendTo("body").fadeIn(200);
    }
  </script>
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
            <i class="fa fa-fw fa-dashboard"></i>
            <span class="nav-link-text">COPD首頁</span>
          </a>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Patient">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="PatientPage.php" data-parent="#exampleAccordion">
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
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="EnvironmentPage.php" data-parent="#exampleAccordion">
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
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="DailyPage.php" data-parent="#exampleAccordion">
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
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="ActivityPage.php" data-parent="#exampleAccordion">
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

      <div style="width:100%;height:300px;text-align:center;">        
        <div id="flot-placeholder1" style="width:100%;height:100%;"></div>        
      </div>

      <div id="datatable_personal_visible">
        <table id="personal_Activity" class="display" cellspacing="0" width="100%">
          <thead>
            <tr>
              <th>SPO2</th>
              <th>心跳</th>
              <th>時間</th>
            </tr>
          </thead>
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
          <option value="getbytime/week">近一週</option>
          <option value="getbytime/month">本月</option>
          <option value="getall">全部</option>
        </select>
        </div>
        <div class="column" align="right" style="width: 70%; ">
          <button onclick="window.location.href='Download_Activity_PDF.php'" style="display: none;">PDF Download</button>
          <button onclick="window.location.href='Download_Activity_Excel.php'">EXCEL Download</button>
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
      <!--<script src="vendor/jquery/jquery.min.js"></script>
      <script src="vendor/popper/popper.min.js"></script>
      <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
      <!-- Core plugin JavaScript-->
      <!--<script src="vendor/jquery-easing/jquery.easing.min.js"></script>
      <!-- Page level plugin JavaScript-->
      <!--<script src="vendor/datatables/jquery.dataTables.js"></script>
      <!-- <script src="vendor/datatables/dataTables.bootstrap4.js"></script> -->
      <!-- Custom scripts for all pages-->
      <!--<script src="js/sb-admin.min.js"></script>
      <!-- Custom scripts for this page-->
      <!--<script src="js/sb-admin-datatables.min.js"></script>-->
    </div>
</body>

</html>
