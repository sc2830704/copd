<head>
    <title></title>
    <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>  
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="js/jquery.flot.min.js"></script>
    <script type="text/javascript" src="js/jquery.flot.time.js"></script>    
    <script type="text/javascript" src="js/jquery.flot.symbol.js"></script>
    <script type="text/javascript" src="js/jquery.flot.axislabels.js"></script>
    <script type="text/javascript" src="js/hashtable.js"></script>    
    <script type="text/javascript" src="js/jquery.numberformatter.js"></script> 
     
    
 
    <script>
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
 
        $(document).ready(function () {
            $.plot($("#flot-placeholder1"), dataset, options);
            $("#flot-placeholder1").UseTooltip();
        });
 
 
 
 
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
<body>
    <div style="width:450px;height:300px;text-align:center;margin:10px">        
        <div id="flot-placeholder1" style="width:100%;height:100%;"></div>        
    </div>
</body>
</html>