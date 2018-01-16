 <head>
     <style type="text/css">
        #flot-placeholder{width:80%;height:300px;}        
    </style>
    <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>  
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="js/jquery.flot.min.js"></script>
    <script type="text/javascript" src="js/jquery.flot.axislabels.js"></script>

    <script type="text/javascript">
        //每筆資料會有一個start_time & end_time 利用這兩個時間做為首尾
        var data1 = [[7.52, 119], [7.53, 112], [7.54, 106], [7.55, 89], [7.56, 92], [7.57, 81], [7.58, 79], [7.59, 85], [8.00, 98], [8.01, 102], [8.02, 105], [8.03, 114]];
        //資料2
        var data2 = [[7.52, 99], [7.53, 100], [7.54, 101], [7.55, 100], [7.56, 98], [7.57, 96], [7.58, 99], [7.59, 101], [8.00, 100], [8.01, 102], [8.02, 105], [8.03, 103]];
        //圖表基本設定
        var dataset = [
            { label: "Heart Rate", data: data1}, //point的圖示為三角形
            { label: "SPO2", data: data2, yaxis: 2 } //以兩種y呈現
        ];
        
        var options = {
            series: {
                lines: {
                    show: true
                }
            },
            xaxis: {
                //下方x軸
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 10
            },
            yaxes: [{
                //左邊y軸
                axisLabel: "Heart Rate",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 3
            }, {
                //右邊y軸
                position: "right",
                axisLabel: "SPO2",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 3
            }
          ],
            legend: {
                //線標外框
                noColumns: 0,
                labelBoxBorderColor: "#000000"
                //position: "nw"
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


        $(document).ready(function () {
            $.plot($("#flot-placeholder"), dataset, options);
        });
    </script>
</head>
<body>
    <div id="flot-placeholder" style="margin: auto;"></div>
</body>
</html>