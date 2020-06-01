<%--
  Created by IntelliJ IDEA.
  User: Wayen
  Date: 20/5/28
  Time: 下午 05:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>秒杀页面</title>
    <script src="jquery/jquery-2.1.1.min.js" type="text/javascript"></script>
</head>
<body>
    <form action="sk/doSecondKill" method="post">
        <input type="hidden" name="id" value="1001">
        <a href="#">点击参与秒杀活动</a>
    </form>

    <script type="text/javascript">

        $("a").click(function () {

            $.ajax({
                type:"post",
                url:$("form").prop("action"),
                data:$("form").serialize(),
                success:function (result) {
                    if (result == "ok"){
                        alert("秒杀成功")
                    }else if (result != "ok"){
                        alert(result)
                        $("a").prop("disable",true);
                    }
                }



            });




            return false
        })

    </script>

</body>
</html>
