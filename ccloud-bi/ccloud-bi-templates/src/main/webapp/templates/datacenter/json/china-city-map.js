var cityMap = {
    "北京": "BeiJing",
    "上海": "ShangHai",
    "天津": "TianJin",
    "重庆": "ChongQing",
    "香港": "XiangGang",
    "澳门": "Aomen",
    "安徽": "AnHui",
    "安庆市": "AnQing",
    "亳州市": "BoZhou",
    "蚌埠市": "BangBu",
    "池州市": "ChiZhou",
    "滁州市": "ChuZhou",
    "阜阳市": "FuYang",
    "合肥市": "HeFei",
    "淮南市": "HuaiNan",
    "淮北市": "HuaiBei",
    "黄山市": "HuangShan",
    "六安市": "LiuAn",
    "马鞍山市": "MaAnShan",
    "宿州市": "SuZhou",
    "铜陵市": "TongLing",
    "芜湖市": "WuHu",
    "宣城市": "XuanCheng",
    "福建": "FuJian",
    "福州市": "FuZhou",
    "龙岩市": "LongYan",
    "南平市": "NanPing",
    "宁德市": "NingDe",
    "莆田市": "PuTian",
    "泉州市": "QuanZhou",
    "三明市": "SanXing",
    "厦门市": "XiaMen",
    "漳州市": "ZhangZhou",
    "广东": "GuangDong",
    "潮州市": "ChaoZhou",
    "东莞市": "DongGuan",
    "佛山市": "FoShan",
    "广州市": "GuangZhou",
    "河源市": "HeYuan",
    "惠州市": "HuiZhou",
    "江门市": "JiangMen",
    "揭阳市": "JieYang",
    "梅州市": "MeiZhou",
    "茂名市": "MaoMing",
    "清远市": "QingYuan",
    "深圳市": "ShenZhen",
    "汕头市": "ShanTou",
    "韶关市": "ShaoGuan",
    "汕尾市": "ShanWei",
    "云浮市": "YunFu",
    "阳江市": "YangJiang",
    "珠海市": "ZhuHai",
    "中山市": "ZhongShan",
    "肇庆市": "ZhaoQing",
    "湛江市": "ZhanXiang",
    "广西": "GuangXi",
    "北海市": "BeiHai",
    "百色市": "BaiSe",
    "崇左市": "ChongZuo",
    "防城港市": "FangChengGang",
    "桂林市": "GuiLi",
    "贵港市": "GuiGang",
    "贺州市": "HeZhou",
    "河池市": "HeChi",
    "柳州市": "LiuZhou",
    "来宾市": "LaiBin",
    "南宁市": "NanNing",
    "钦州市": "QinZhou",
    "梧州市": "WuZhou",
    "玉林市": "YuLin",
    "贵州": "GuiZhou",
    "安顺市": "AnShun",
    "贵阳市": "GuiYang",
    "六盘水市": "LiuPanShui",
    "遵义市": "ZunYi",
    "毕节市": "BiJie",
    "黔东南苗族侗族自治州": "QianDongNanMiaoZuTongZuZiZhiZhou",
    "黔南布依族苗族自治州": "QianNanBuYiZuMiaoZuZiZhiZhou",
    "黔西南布依族苗族自治州": "QianXiNanBuYiZuZiZhiZhou",
    "铜仁市": "TongRen",
    "甘肃": "GanSu",
    "白银市": "BaiYin",
    "金昌市": "JinChang",
    "嘉峪关市": "JiaYuGuan",
    "酒泉市": "JiuQuan",
    "兰州市": "LanZhou",
    "庆阳市": "QingYang",
    "天水市": "TianShui",
    "武威市": "WuWei",
    "张掖市": "ZhangYe",
    "定西市": "DingXi",
    "甘南藏族自治州": "GanNanZangZuZiZhiZhou",
    "临夏回族自治州": "LinXiaHuiZuZiZhiZhou",
    "陇南市": "LongNan",
    "平凉市": "PingLiang",
    "海南": "HaiNan",
    "海口市": "HaiKou",
    "三亚市": "SanYa",
    "白沙黎族自治县": "BaiShaLiZuZiZhiXian",
    "保亭黎族苗族自治县": "BaoTingLiZuMiaoZuZiZhiXian",
    "昌江黎族自治县": "ChangJiangLiZuZiZhiXian",
    "澄迈县": "ChengMai",
    "儋州市": "DanZhou",
    "定安县": "DingAn",
    "东方市": "DongFang",
    "乐东黎族自治县": "LeDongLiZuZiZhiXian",
    "临高县": "LinGao",
    "陵水黎族自治县": "LingShuiLiZuZiZhiXian",
    "琼海市": "QiongHai",
    "琼中黎族苗族自治县": "QiongZhongLiZhuMiaoZuZiZhiXian",
    "三沙市": "SanSha",
    "屯昌县": "TunChang",
    "万宁市": "WangNing",
    "文昌市": "WenChang",
    "五指山市": "WuZhiShan",
    "河北": "HeBei",
    "保定市": "BaoDing",
    "承德市": "ChengDe",
    "沧州市": "CangZhou",
    "邯郸市": "HanDan",
    "衡水市": "HengShui",
    "廊坊市": "LangFang",
    "秦皇岛市": "QinHuangDao",
    "石家庄市": "ShiJiaZhuang",
    "唐山市": "TangShan",
    "邢台市": "XingTai",
    "张家口市": "ZhangJiaKou",
    "河南": "HeNan",
    "安阳市": "AnYang",
    "焦作市": "JiaoZuo",
    "开封市": "KaiFeng",
    "洛阳市": "LuoYang",
    "鹤壁市": "HeBi",
    "漯河市": "LuoHe",
    "南阳市": "NanYang",
    "平顶山市": "PingDingShan",
    "濮阳市": "PuYang",
    "三门峡市": "SanMenXia",
    "商丘市": "ShangQiu",
    "新乡市": "XinXiang",
    "许昌市": "XuChang",
    "信阳市": "XinYang",
    "郑州市": "ZhengZhou",
    "周口市": "ZhouKou",
    "驻马店市": "ZhuMaDian",
    "济源市":"JiYuan",
    "黑龙江": "HeiLongJiang",
    "大庆市": "DaQing",
    "哈尔滨市": "HaErBin",
    "鹤岗市": "HeGang",
    "黑河市": "HeiHe",
    "鸡西市": "JiXi",
    "佳木斯市": "JiaMuSi",
    "牡丹江市": "MuDanJiang",
    "齐齐哈尔市": "QiQiHaEr",
    "七台河市": "QiTaiHe",
    "绥化市": "SuiHua",
    "双鸭山市": "ShuangYaShan",
    "伊春市": "YiChun",
    "大兴安岭地区": "DaXingAnLingDiQu",
    "湖北": "HuBei",
    "鄂州市": "EZhou",
    "黄石市": "HuangShi",
    "黄冈市": "HuangGang",
    "荆州市": "JingZhou",
    "荆门市": "JingMen",
    "十堰市": "ShiYan",
    "武汉市": "WuHan",
    "襄阳市": "XiangYang",
    "孝感市": "XiaoGan",
    "咸宁市": "XianNing",
    "宜昌市": "YiChang",
    "恩施土家族苗族自治州": "EnShiTuJiaZuMiaoZuZiZhiZhou",
    "潜江市": "QianJiang",
    "神农架林区": "ShenNongJiaLinQu",
    "随州市": "SuiZhou",
    "天门市": "TianMen",
    "仙桃市": "XianTao",
    "湖南": "HuNan",
    "长沙市": "ChangSha",
    "常德市": "ChangDe",
    "郴州市": "ChenZhou",
    "衡阳市": "HengYang",
    "怀化市": "HuaiHua",
    "娄底市": "LouDi",
    "邵阳市": "ShaoYang",
    "湘潭市": "xiangTan",
    "岳阳市": "YueYang",
    "益阳市": "YiYang",
    "永州市": "YongZhou",
    "株洲市": "ZhuZhou",
    "张家界市": "ZhangJiaJie",
    "湘西土家族苗族自治州": "XiangXiTuJiaZuMiaoZuZiZhiZhou",
    "吉林": "JiLin",
    "白山市": "BaiShan",
    "白城市": "BaiCheng",
    "长春市": "ChangChun",
    "吉林市": "JiLinShi",
    "辽源市": "LiaoYuan",
    "四平市": "SiPing",
    "松原市": "SongYuan",
    "通化市": "TongHua",
    "延边朝鲜族自治州": "YanBianChanXianZuZiZhiZhou",
    "江苏": "JiangSu",
    "常州市": "ChangZhou",
    "淮安市": "HuaiAn",
    "连云港市": "LianYunGang",
    "南京市": "NanJing",
    "南通市": "NanTong",
    "苏州市": "SuZhou",
    "宿迁市": "SuQian",
    "泰州市": "TaiZhou",
    "无锡市": "WuXi",
    "徐州市": "XuZhou",
    "盐城市": "YanCheng",
    "扬州市": "YangZhou",
    "镇江市": "ZhenJiang",
    "江西": "JiangXi",
    "抚州市": "FuZhou",
    "赣州市": "GanZhou",
    "景德镇市": "JingDeZhen",
    "九江市": "JiuJiang",
    "吉安市": "JiAn",
    "南昌市": "NanChang",
    "萍乡市": "PingXiang",
    "上饶市": "ShangRao",
    "新余市": "XinYu",
    "鹰潭市": "YingTan",
    "宜春市": "YiChun",
    "辽宁": "LiaoNing",
    "鞍山市": "AnShan",
    "本溪市": "BenXi",
    "朝阳市": "ChaoYang",
    "大连市": "DaLian",
    "丹东市": "DanDong",
    "抚顺市": "FuShun",
    "阜新市": "FuXin",
    "葫芦岛市": "HuLuDao",
    "锦州市": "JinZhou",
    "辽阳市": "LiaoYang",
    "盘锦市": "PanJin",
    "沈阳市": "ShenYang",
    "铁岭市": "TieLing",
    "营口市": "YingKou",
    "内蒙古": "NeiMengGu",
    "包头市": "BaoTou",
    "赤峰市": "ChiFeng",
    "鄂尔多斯市": "EErDuoSi",
    "呼和浩特市": "HuHeHaoTe",
    "通辽市": "TongLiao",
    "乌海市": "WuHai",
    "阿拉商盟": "ALaShanMeng",
    "巴彦淖尔市": "BaYanNaoEr",
    "呼伦贝尔市": "HuLunBeiEr",
    "乌兰察布市": "WuLanChaBu",
    "锡林郭勒盟": "XiLinGuoLeMeng",
    "兴安盟": "XingAnMeng",
    "宁夏": "NingXia",
    "固原市": "GuYuan",
    "吴忠市": "WuZhong",
    "银川市": "YingChuan",
    "石嘴山市": "ShiZuiShan",
    "中卫市": "ZhongWei",
    "青海": "QingHai",
    "西宁市": "XiNing",
    "果洛藏族自治州": "GuoLuoZangZuZiZhiZhou",
    "海北藏族自治州": "HaiBeiZangZuZiZhiZhou",
    "海东市": "HaiDong",
    "海南藏族自治州": "HaiNanZangZuZiZhiZhou",
    "海西蒙古族藏族自治州": "HaiXiMengGuZuZangZuZiZhiZhou",
    "黄南藏族自治州": "HuanNanZangZuZiZhiZhou",
    "玉树藏族自治州": "YuShuZangZuZiZhiZhou",
    "陕西": "ShanXi",
    "安康市": "AnKang",
    "宝鸡市": "BaoJi",
    "汉中市": "HanZhong",
    "商洛市": "ShangLuo",
    "铜川市": "TongChuan",
    "渭南市": "WeiNan",
    "西安市": "XiAn",
    "延安市": "YaNan",
    "咸阳市": "XianYang",
    "榆林市": "YuLin",
    "山西": "ShanXiYi",
    "长治市": "ChangZhi",
    "大同市": "DaTong",
    "晋城市": "JinCheng",
    "临汾市": "LinFen",
    "朔州市": "ShuoZhou",
    "太原市": "TaiYuan",
    "忻州市": "XinZhou",
    "阳泉市": "YangQuan",
    "运城市": "YunCheng",
    "晋中市": "JinZhong",
    "吕梁市": "LvLiang",
    "山东": "ShanDong",
    "滨州市": "BinZhou",
    "东营市": "DongYing",
    "德州市": "DeZhou",
    "菏泽市": "HeZe",
    "济南市": "JiNan",
    "济宁市": "JiNing",
    "莱芜市": "LaiWu",
    "临沂市": "LinYi",
    "聊城市": "LiaoCheng",
    "青岛市": "QingDao",
    "日照市": "RiZhao",
    "泰安市": "TaiAn",
    "潍坊市": "WeiFang",
    "威海市": "WeiHai",
    "烟台市": "YanTai",
    "淄博市": "ZiBo",
    "枣庄市": "ZaoZhuang",
    "四川": "SiChuan",
    "巴中市": "BaZhong",
    "成都市": "ChengDu",
    "德阳市": "DeYang",
    "达州市": "DaZhou",
    "广元市": "GuangYuan",
    "广安市": "GuangAn",
    "泸州市": "LuZhou",
    "乐山市": "LeShan",
    "绵阳市": "MianYang",
    "眉山市": "MeiShan",
    "内江市": "NeiJiang",
    "南充市": "NanChong",
    "攀枝花市": "PanZhiHua",
    "遂宁市": "SuiNing",
    "雅安市": "YaAn",
    "宜宾市": "YiBin",
    "自贡市": "ZiGong",
    "资阳市": "Ziyang",
    "阿坝藏族羌族自治州": "ABaZangZuQiangZuZiZhiZhou",
    "甘孜藏族自治州": "GanZiZangZuZiZhiZhou",
    "凉山彝族自治州": "LiangShanYiZuZiZhiZhou",
    "台湾": "TaiWan",
    "西藏": "XiZang",
    "拉萨市": "LaSa",
    "阿里地区": "ALiDiQu",
    "昌都市": "ChangDu",
    "林芝市": "LinZhi",
    "那曲地区": "NaQuDiQu",
    "日喀则市": "RiKaZe",
    "山南市": "ShanNan",
    "新疆": "XinJiang",
    "克拉玛依市": "KeLaMaYi",
    "乌鲁木齐市": "WuLuMuQi",
    "阿克苏地区": "AkeSuDiQu",
    "阿拉尔地区": "ALaErDiQu",
    "阿勒泰地区": "ALeTaiDiQu",
    "巴音郭楞蒙古自治州": "BaYinGuoLengMengGuZiZhiZhou",
    "北屯市": "BeiTun",
    "博尔塔拉蒙古自治州": "BoErTaLaMengGuZiZhiZhou",
    "昌吉回族自治州": "ChangJiHuiZuZiZhiZhou",
    "哈密市": "HaMi",
    "和田地区": "HeTianDiQu",
    "喀什地区": "KaShiDiQu",
    "可克达拉市": "KeKeDaLa",
    "克孜勒苏柯尔克孜自治州": "KeZiLeSuKeErKeZiZiZhiZhou",
    "昆玉市": "KunYu",
    "石河子市": "ShiHeZi",
    "双河市": "ShuangHe",
    "塔城地区": "TaChengDiQu",
    "铁门关市": "TieMenGuan",
    "图木舒克市": "TuMuShuKe",
    "吐鲁番市": "TuLuFan",
    "五家渠市": "WuJiaQu",
    "伊犁哈萨克自治州": "YiLiHaSaKeZiZhiZhou",
    "云南": "YunNan",
    "保山市": "BaoShan",
    "昆明市": "KunMing",
    "玉溪市": "YuXi",
    "昭通市": "ZhaoTong",
    "楚雄彝族自治州": "ChuXiongYiZuZiZhiZhou",
    "大理白族自治州": "DaLiBaiZuZiZhiZhou",
    "德宏傣族景颇族自治州": "DeHongDaiZuJingPoZuZiZhiZhou",
    "迪庆藏族自治州": "DiQingZangZuZiZhiZhou",
    "红河哈尼族彝族自治州": "HongHeHaNiZuYiZuZiZhiZhou",
    "丽江市": "LiJiang",
    "临沧市": "LinCang",
    "怒江傈傈族自治州": "NuJiangLiLiZuZiZhiZhou",
    "普洱市": "PuEr",
    "曲靖市": "QuJing",
    "文山壮族苗族自治州": "WenShanZhuangZuMiaoZuZiZhiZhou",
    "西双版纳傣族自治州": "XiShuangBanNaDaiZuZiZhiZhou",
    "浙江": "ZheJiang",
    "杭州市": "HangZhou",
    "湖州市": "HuZhou",
    "嘉兴市": "JiaXing",
    "金华市": "JinHua",
    "丽水市": "LiShui",
    "宁波市": "NingBo",
    "衢州市": "QuZhou",
    "绍兴市": "ShaoXing",
    "台州市": "TaiZhou",
    "温州市": "WenZhou",
    "舟山市": "ZhouShan"
}