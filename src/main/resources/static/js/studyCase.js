$(function() {
    $(".warning").click(function() {
    var classId = $("#classId").text();
    var studentId = $("#studentId").text();
	$.post(
	    "/studyAnalysis/warning",
	    {"classId":classId,"studentId":studentId},
	    function(data){
	        data = $.parseJSON(data);
	        //在提示框中显示返回的消息
	        if(data.code==0){
	            alert(data.msg);
	            $(".warning").attr("value","已通知");
	        }else{
	            alert("通知失败");
	        }
	    }
	);
    })
})