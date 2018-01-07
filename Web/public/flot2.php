 <head>
     <style type="text/css">
        #flot-placeholder{width:80%;height:300px;}        
    </style>
    <script src="js/jquery-1.11.3.min.js" type='text/javascript'></script>  
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="js/jquery.flot.min.js"></script>
    <script type="text/javascript">
        var data = [[1, 119], [2, 112], [3, 106], [4, 89], [5, 92], [6, 81], [7, 79], [8, 85], [9, 98], [10, 102], [11, 105], [12, 114]];
 
        var dataset = [{label: "Heart Rate",data: data}];
 
        var options = {
            series: {
                lines: { show: true },
                points: {
                    radius: 2,
                    show: true
                }
            },
            colors: ["#00FF00"] 
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