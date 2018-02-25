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

<body onload="JavaScript:AutoRefresh(60000);">
  <!-- center   -->
  <div class="content-wrapper" style="padding-left: 5px">
    <div class="row" id="PersonalDataAndFlot" ">
      <div class="column" style="width: 90%; padding-right: 5px;" align="center">  	
  		  <div id="flot" style="height: 400px; max-width: 800px; margin: 0 auto;"></div><br><br><br>
        <div id="flot2" style="height: 400px; max-width: 800px; margin: 0 auto;"></div>
      </div>
    </div>
  </div>
</body>

<script type="text/JavaScript">
$(document).ready(function(){
  print();
});
function print(){
  $.ajax({
    type: "GET",
    url: "../apiv1/env/getbyid/A002",
    dataType: "json",
    data: {
    },
    success: function(data) {
      LoadActivityFlotChart(data);
    }
  })
}
function LoadActivityFlotChart(Data) {
  console.log(Data);
  var arr2_temperature = [];
  var arr2_humidity = [];
  var arr3_temperature = [];
  var arr3_humidity = [];
  for (var i in Data) {
    if(Data[i].deviceid=='A002'){
      arr2_temperature.push([
        Data[i].datetime,
        parseFloat(Data[i].temperature)
      ]);
      arr2_humidity.push([
        Data[i].datetime,
        parseFloat(Data[i].humidity)
      ]);
    }
    else {
      arr3_temperature.push([
        Data[i].datetime,
        parseFloat(Data[i].temperature)
      ]);
      arr3_humidity.push([
        Data[i].datetime,
        parseFloat(Data[i].humidity)
      ]);
    }
  }
  getData2(arr2_temperature,arr2_humidity);
  getData3(arr3_temperature,arr3_humidity);
}

function getData2(arr_temperature,arr_humidity) {
    console.time('line');
  Highcharts.chart('flot', {

      chart: {
          zoomType: 'x'
      },

      boost: {
          useGPUTranslations: true
      },

      title: {
          text: 'A002 環境觀測'
      },

      subtitle: {
          text: '溫度與濕度'
      },

      tooltip: {
          valueDecimals: 2
      },

      xAxis: {
        type: 'datetime'
      },

      series: [{
        name: '溫度',
          data: arr_temperature,
          lineWidth: 0.5
      },
      {
        name: '濕度',
        data: arr_humidity,
        lineWidth: 0.5
      }]

  });
  console.timeEnd('line');
} 

function getData3(arr_temperature,arr_humidity) {
    console.time('line');
  Highcharts.chart('flot2', {

      chart: {
          zoomType: 'x'
      },

      boost: {
          useGPUTranslations: true
      },

      title: {
          text: 'A003 環境觀測'
      },

      subtitle: {
          text: '溫度與濕度'
      },

      tooltip: {
          valueDecimals: 2
      },

      xAxis: {
        type: 'datetime'
      },

      series: [{
        name: '溫度',
          data: arr_temperature,
          lineWidth: 0.5
      },
      {
        name: '濕度',
        data: arr_humidity,
        lineWidth: 0.5
      }]

  });
  console.timeEnd('line');
} 

function AutoRefresh( t ) {
  setTimeout("location.reload(true);", t);
}
</script>

</html>
