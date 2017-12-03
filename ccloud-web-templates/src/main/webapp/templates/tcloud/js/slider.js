/**
 * 开发时间：2016/5/24
 * 开发人员：boxUnll
 * 开发项目：移动端滑动验证代码
 */
var oBtn = document.getElementById('btn');
var oW, oLeft;
var oSlider = document.getElementById('slider');
var oTrack = document.getElementById('track');
var oIcon = document.getElementById('icon');
var oSpinner = document.getElementById('spinner');
var flag = 1;

oBtn.addEventListener('touchstart', function (e) {
  if (flag == 1) {
    var touches = e.touches[0];
    oW = touches.clientX - oBtn.offsetLeft;
    console.log(oW);
    oBtn.className = "button";
    oTrack.className = "track";
  }

}, false);

oBtn.addEventListener("touchmove", function (e) {
  if (flag == 1) {
    var touches = e.touches[0];
    oLeft = touches.clientX - oW;
    console.log(oLeft);
    if (oLeft < 0) {
      oLeft = 0;
    } else if (oLeft > oSlider.clientWidth - oBtn.offsetWidth) {
      oLeft = (oSlider.clientWidth - oBtn.offsetWidth);
    }
    oBtn.style.left = oLeft + "px";
    oTrack.style.width = oLeft + 'px';
  }

}, false);

oBtn.addEventListener("touchend", function () {
  if (oLeft >= (oSlider.clientWidth - oBtn.clientWidth)) {
    oBtn.style.left = (oSlider.clientWidth - oBtn.offsetWidth);
    oTrack.style.width = (oSlider.clientWidth - oBtn.offsetWidth);
    oIcon.style.display = 'none';
    oSpinner.style.display = 'block';
    flag = 0;
    pass();
  } else {
    oBtn.style.left = 0;
    oTrack.style.width = 0;
  }
  oBtn.className = "button-on";
  oTrack.className = "track-on";
}, false);


function pass() {
  setTimeout(function() {
    $(".stage").hide();
    oBtn.style.left = 0;
    oTrack.style.width = 0;
    oSpinner.style.display = 'none';
    oIcon.style.display = 'block';
    oLeft = 0;
    flag = 1;
    $("#code").focus();
  }, 1000)
  var time = 60;
  var interval = setInterval(function () {
    time--;
    $("#get-code").text(time + "s");
    if (time == 0) {
      $("#get-code").text("获取验证码")
      if (/^1[34578]\d{9}$/.test($("#phone").val())) {
        $("#get-code").addClass("can-get");
      }
      clearInterval(interval);
    }
  }, 1000)
}