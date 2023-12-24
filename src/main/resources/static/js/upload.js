$(function() {
    $(".add").click(function() {
	var classId = $("#classId").text();
	$.post(
	    "/studyAnalysis/joinClass",
	    {"classId":classId},
	    function(data){
	        data = $.parseJSON(data);
	        //在提示框中显示返回的消息
	        if(data.code==0){
	            alert(data.msg);
	            $(".add").attr("value","已加入");
	        }else{
	            alert("加入失败");
	        }
	    }
	);
    })
    $(".btn").click(function() {
        var names = $("#cname").val();



        $.ajax({
            url: "localhost:8080/add2", //要请求的服务器url
            data: {
                courseinfo: names
            }, //第一个name对应的是后端request.getParameter("name")的name、第二个name对应的是此js中的var name = $("#name").val();的name
            async: true, //是否是异步请求
            cache: false, //是否缓存结果
            type: "GET", //请求方式
            dataType: "json", //服务器返回什么类型数据 text xml javascript json(javascript对象)
            success: function(result) { //函数会在服务器执行成功后执行，result就是服务器返回结果
                console.log(result);
                alert("请求成功");
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("请求失败");
            }
        });
    })

})
window.onload = function() {


}