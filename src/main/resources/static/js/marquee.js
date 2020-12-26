String.prototype.format = function (args) {
    var result = this;
    if (arguments.length < 1) {
        return result;
    }
    var data = arguments;
    if (arguments.length == 1 && typeof (args) == "object") {
        data = args;
    }
    for (var key in data) {
        var value = data[key];
        if (undefined != value) {
            result = result.replace("{" + key + "}", value);
        }
    }
    return result;
}
var barrager_code =
    'var item={\n' +
    "   img:'{img}', //图片 \n" +
    "   info:'{info}', //文字 \n" +
    "   href:'{href}', //链接 \n" +
    "   close:{close}, //显示关闭按钮 \n" +
    "   speed:{speed}, //延迟,单位秒,默认6 \n" +
    "   bottom:{bottom}, //距离底部高度,单位px,默认随机 \n" +
    "   color:'{color}', //颜色,默认白色 \n" +
    "   old_ie_color:'{old_ie_color}', //ie低版兼容色,不能与网页背景相同,默认黑色 \n" +
    " }\n" +
    "$('body').barrager(item);"
    ;

$(function () {
    // SyntaxHighlighter.all();

    //每条弹幕发送间隔
    var looper_time = 3 * 1000;
    //是否首次执行
    var run_once = true;
    // do_barrager();

    function do_barrager() {
        if (run_once) {
            //如果是首次执行,则设置一个定时器,并且把首次执行置为false
            looper = setInterval(do_barrager, looper_time);
            run_once = false;
        }
        //获取
        $.getJSON('server.php?mode=1', function (data) {
            //是否有数据
            if (data.info) {

                $('body').barrager(data);
            }

        });
    }

});

function marquee(img, info) {
    // setInterval, 呼叫api取得彈幕，如果有還沒被顯示過的，每秒一次，else每5秒一次
    // 要顯示的東西都交給後端判斷
    // 沒顯示出來過的優先
    // 都跑完了才跑已經顯示過的內容
    // 字數限制

    // var  info=$('input[name=info]').val();
    (info == '') ? info = '請填寫彈幕文字' : info = info;

    var speed = Math.floor(Math.random() * Math.floor(16)) + 10; // 取得10～25之間的值
    var window_height = $(window).height() - 150;
    var bottom = Math.floor(Math.random() * window_height + 40); // 隨機高度
    var code = barrager_code;

    code = code.replace("   bottom:{bottom}, //距离底部高度,单位px,默认随机 \n", '');

    var randomColor = getRandomColor();
    var item = {
        'img': img,
        'info': info,
        'close': false,
        'speed': speed,
        'bottom': bottom,
        'color': randomColor,
        'old_ie_color': randomColor
    };

    code = code.format(item);
    $('#barrager-code').val(code);
    eval(code);
}

function getRandomColor() { // get HSL color
    return "hsl(" + 360 * Math.random() + ", 100%, 50%)";
}