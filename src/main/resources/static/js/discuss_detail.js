function like(btn,entityType,entityId,userIdOwnEntity,postId) {

    $.post(
        context_Path+"/like",
        {"entityType":entityType,"entityId":entityId,"userIdOwnEntity":userIdOwnEntity,"postId":postId},
        function (data) {
            data=$.parseJSON(data);
            if(data.code==0){
                $(btn).children("b").text(data.likeStatus==0?'赞':"已赞");
                $(btn).children("i").text(data.likeNum);
            }
            else{
                alert(data.msg);
            }
        }
    );

}