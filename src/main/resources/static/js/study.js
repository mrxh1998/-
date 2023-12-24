$(function() {
    var hour = 0;
    var min = 0;
    var sen = 0;
    var classId = $("#classId").text();
    function showTime() {
        sen++;
        if (sen >= 60) {
            min++;
            sen = 0;
        }
        if (min >= 60) {
            hour++;
            min = 0;
        }
        if (hour >= 12) {
            hour = 0;
        }
        var dataStr = "已学习时间：" + (hour < 10 ? ("0" + hour) : hour) + ":" + (min < 10 ? ("0" + min) : min) + ":" + (sen < 10 ? ("0" + sen) : sen);
        $("#show").text(dataStr);
    }
    $('body').everyTime('1s', function() {
        showTime(); //展示已经学习时间

    });
    $('body').everyTime('60s', function() {
        $.post(
        	    "/studyAnalysis/study",
        	    {"classId":classId},
        	    function(data){
        	        data = $.parseJSON(data);
        	        //在提示框中显示返回的消息
        	    }
        	);
    });
})