//滑动解锁插件
//2017年2月16日 作者:holy_nova
;
(function ($) {
    function Slider(elem, options) {
        this.$container = elem;
        this.default = {
            width: this.$container.width() - 2,
            height: this.$container.height() - 2,
            bgColor: '#E8E8E8',
            progressColor: '#FFE97F',
            handleColor: '#fff',
            succColor: '#78D02E',
            text: 'slide to unlock',
            succText: 'ok!',
            textColor: '#000',
            succTextColor: '#000',
            successFunc: function () {
                alert('successfully unlock!');
            }
        };
        this.options = $.extend({}, this.default, options);
        this.isSuccess = false;
    }
    Slider.prototype = {
        create: function () {
            var $container = this.$container;
            var options = this.options;
            initDOM();
            initStyle();

            function initDOM() {
                var template = '<div class="slide-to-unlock-bg"><span>' +
                    options.text +
                    '</span></div><div class="slide-to-unlock-progress"></div><div class="slide-to-unlock-handle"><i class="icon-arrow-right2"></i></div>';
                $container.html(template);
            }

            function initStyle() {
                $container.css({
                    position: 'relative',
                });
                $container.find('span').css({
                    lineHeight: options.height + 'px',
                    fontSize: options.height / 3.5,
                    color: options.textColor
                });
                $container.find('.slide-to-unlock-bg').css({
                    // width: options.width + 'px',
                    // height: options.height + 'px',
                    backgroundColor: options.bgColor,
                });
                $container.find('.slide-to-unlock-progress').css({
                    backgroundColor: options.progressColor,
                    // height: options.height - 2 + 'px'
                });
                $container.find('.slide-to-unlock-handle').css({
                    backgroundColor: options.handleColor,
                    // height: (options.height - 0) + 'px',
                    // lineHeight: (options.height - 0) + 'px',
                    // width: (Math.floor(options.width / 8)) + 'px',
                });
            }
        },
        bindDragEvent: function () {
            var that = this;
            var $container = this.$container;
            var options = this.options;
            var downX;
            $prog = $container.find('.slide-to-unlock-progress'),
                $bg = $container.find('.slide-to-unlock-bg'),
                $handle = $container.find('.slide-to-unlock-handle');
            $handle.on('touchstart', null, mousedownHandler);
            var succMoveWidth;

            function getLimitNumber(num, min, max) {
                if (num > max) {
                    num = max;
                } else if (num < min) {
                    num = min;
                }
                return num;
            }

            function mousedownHandler(event) {
                succMoveWidth = $bg.width() - $handle.width();
                downX = event.touches[0].clientX;
                $(document).on('touchmove', null, mousemoveHandler);
                $(document).on('touchend', null, mouseupHandler);
            }

            function mousemoveHandler(event) {
                var moveX = event.touches[0].clientX;
                var diffX = getLimitNumber(moveX - downX, 0, succMoveWidth);
                $prog.width(diffX);
                $handle.css({
                    left: diffX
                });
                if (diffX === succMoveWidth) {
                    success();
                }
                event.preventDefault();
            }

            function mouseupHandler(event) {
                if (!that.isSuccess) {
                    $prog.animate({
                        width: 0
                    }, 100);
                    $handle.animate({
                        left: 0
                    }, 100);
                }
                $(document).off('touchmove', null, mousemoveHandler);
                $(document).off('touchend', null, mouseupHandler);
            }

            function success() {
                $prog.css({
                    backgroundColor: options.succColor,
                });
                $container.find('span').css({
                    color: options.succTextColor
                });
                that.isSuccess = true;
                $container.find('span').html(options.succText);
                $handle.off('touchstart', null, mousedownHandler);
                $(document).off('touchmove', null, mousemoveHandler);
                setTimeout(function () {
                    options.successFunc && options.successFunc();
                }, 30);
            }
        },
        reset: function () {
            this.isSuccess = false;
            $prog.width(0);
            $handle.css({
                left: 0
            });
            $prog.css({
                backgroundColor: this.options.progressColor,
            });
            this.$container.find('span').css({
                color: this.options.textColor
            });
            this.$container.find('span').html(this.options.text);
            this.bindDragEvent();
        }
    };
    $.fn.extend({
        slideToUnlock: function (options) {
            return this.each(function () {
                var _this = $(this);
                var slider = new Slider(_this, options);
                slider.create();
                slider.bindDragEvent();
                $.fn.extend({
                    resetUnlock: function () {
                        if(_this.is($(this))) {
                            slider.reset();
                        }
                    }
                })
            });
        }
    });
})(jQuery);