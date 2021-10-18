$(".toggle").on("click", function () {
  $(".container").stop().addClass("active");
});

$(".close").on("click", function () {
  $(".container").stop().removeClass("active");
});

$("#btn").click(function () {
  $.post("user/main",$("#loginForm").serialize(),function(res){
    if (res.flag){
      location.href = `chat/${res.message}`;
    } else {
      $("#err_msg").html(res.message);
    }
  },"json");
});
