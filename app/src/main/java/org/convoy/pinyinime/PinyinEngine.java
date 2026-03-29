package org.convoy.pinyinime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PinyinEngine {
    public enum ScriptMode {
        SIMPLIFIED,
        TRADITIONAL
    }

    private static final Map<String, List<String>> SIMPLIFIED = new HashMap<>();
    private static final Map<String, List<String>> TRADITIONAL = new HashMap<>();

    static {
        add("ni", list("你", "呢", "尼", "泥"), list("你", "呢", "尼", "泥"));
        add("hao", list("好", "号", "浩", "毫"), list("好", "號", "浩", "毫"));
        add("nihao", list("你好"), list("你好"));
        add("shi", list("是", "时", "事", "市", "十"), list("是", "時", "事", "市", "十"));
        add("jie", list("界", "解", "接", "节", "街"), list("界", "解", "接", "節", "街"));
        add("shijie", list("世界"), list("世界"));
        add("wo", list("我", "握", "窝"), list("我", "握", "窩"));
        add("women", list("我们"), list("我們"));
        add("nimen", list("你们"), list("你們"));
        add("ta", list("他", "她", "它", "塔"), list("他", "她", "它", "塔"));
        add("tamen", list("他们", "她们"), list("他們", "她們"));
        add("zhong", list("中", "种", "重", "众"), list("中", "種", "重", "眾"));
        add("guo", list("国", "过", "果"), list("國", "過", "果"));
        add("zhongguo", list("中国"), list("中國"));
        add("ren", list("人", "仁", "认", "任"), list("人", "仁", "認", "任"));
        add("renshi", list("认识"), list("認識"));
        add("de", list("的", "得", "德"), list("的", "得", "德"));
        add("ma", list("吗", "妈", "马", "嘛"), list("嗎", "媽", "馬", "嘛"));
        add("hen", list("很", "狠", "痕"), list("很", "狠", "痕"));
        add("gaoxing", list("高兴"), list("高興"));
        add("xiexie", list("谢谢"), list("謝謝"));
        add("zaijian", list("再见"), list("再見"));
        add("qing", list("请", "情", "青", "清"), list("請", "情", "青", "清"));
        add("wen", list("问", "文", "闻", "稳"), list("問", "文", "聞", "穩"));
        add("qingwen", list("请问"), list("請問"));
        add("dui", list("对", "队"), list("對", "隊"));
        add("bu", list("不", "部", "布"), list("不", "部", "布"));
        add("duibuqi", list("对不起"), list("對不起"));
        add("mei", list("没", "美", "每", "妹"), list("沒", "美", "每", "妹"));
        add("guanxi", list("关系"), list("關係"));
        add("meiguanxi", list("没关系"), list("沒關係"));
        add("zai", list("在", "再", "载"), list("在", "再", "載"));
        add("jian", list("见", "件", "间"), list("見", "件", "間"));
        add("ai", list("爱", "矮", "哎"), list("愛", "矮", "哎"));
        add("chi", list("吃", "持", "迟", "池"), list("吃", "持", "遲", "池"));
        add("fan", list("饭", "反", "凡"), list("飯", "反", "凡"));
        add("chifan", list("吃饭"), list("吃飯"));
        add("he", list("和", "喝", "河", "合"), list("和", "喝", "河", "合"));
        add("shui", list("水", "谁", "睡"), list("水", "誰", "睡"));
        add("heshui", list("喝水"), list("喝水"));
        add("jintian", list("今天"), list("今天"));
        add("mingtian", list("明天"), list("明天"));
        add("zuotian", list("昨天"), list("昨天"));
        add("xianzai", list("现在"), list("現在"));
        add("shenme", list("什么"), list("什麼"));
        add("zenme", list("怎么"), list("怎麼"));
        add("weishenme", list("为什么"), list("為什麼"));
        add("keyi", list("可以"), list("可以"));
        add("yidian", list("一点"), list("一點"));
        add("yige", list("一个"), list("一個"));
        add("meiyou", list("没有"), list("沒有"));
        add("you", list("有", "又", "右"), list("有", "又", "右"));
        add("qu", list("去", "取", "区"), list("去", "取", "區"));
        add("lai", list("来", "莱", "赖"), list("來", "萊", "賴"));
        add("huilai", list("回来"), list("回來"));
        add("gongzuo", list("工作"), list("工作"));
        add("xuexi", list("学习"), list("學習"));
        add("shengri", list("生日"), list("生日"));
        add("kuaile", list("快乐"), list("快樂"));
        add("shengrikuaile", list("生日快乐"), list("生日快樂"));
        add("zaoshanghao", list("早上好"), list("早上好"));
        add("wanan", list("晚安"), list("晚安"));
        add("laoshi", list("老师"), list("老師"));
        add("xuesheng", list("学生"), list("學生"));
        add("pengyou", list("朋友"), list("朋友"));
        add("jia", list("家", "加", "假"), list("家", "加", "假"));
        add("huijia", list("回家"), list("回家"));
        add("shangban", list("上班"), list("上班"));
        add("xiaban", list("下班"), list("下班"));
        add("men", list("们", "门"), list("們", "門"));
        add("kai", list("开", "凯"), list("開", "凱"));
        add("guan", list("关", "观", "管"), list("關", "觀", "管"));
        add("kaimen", list("开门"), list("開門"));
        add("guanmen", list("关门"), list("關門"));
        add("xiang", list("想", "向", "像"), list("想", "向", "像"));
        add("yao", list("要", "药", "摇"), list("要", "藥", "搖"));
        add("hui", list("会", "回", "灰"), list("會", "回", "灰"));
        add("buhui", list("不会"), list("不會"));
        add("zhidao", list("知道"), list("知道"));
        add("juede", list("觉得"), list("覺得"));
        add("zhen", list("真", "针"), list("真", "針"));
        add("zhende", list("真的"), list("真的"));
        add("keyima", list("可以吗"), list("可以嗎"));
        add("haochi", list("好吃"), list("好吃"));
        add("haokan", list("好看"), list("好看"));
        add("haoting", list("好听"), list("好聽"));
        add("ting", list("听", "停"), list("聽", "停"));
        add("kan", list("看", "刊"), list("看", "刊"));
        add("shuo", list("说", "说"), list("說", "說"));
        add("shuohua", list("说话"), list("說話"));
        add("hua", list("话", "花"), list("話", "花"));
        add("zuo", list("做", "坐", "左"), list("做", "坐", "左"));
        add("qingzuo", list("请坐"), list("請坐"));
        add("qingjin", list("请进"), list("請進"));
        add("jin", list("进", "今", "近"), list("進", "今", "近"));
        add("huanying", list("欢迎"), list("歡迎"));
        add("deng", list("等", "灯"), list("等", "燈"));
        add("yixia", list("一下"), list("一下"));
        add("qingdengyixia", list("请等一下"), list("請等一下"));
        add("buyao", list("不要"), list("不要"));
        add("buxing", list("不行"), list("不行"));
        add("xing", list("行", "性", "星"), list("行", "性", "星"));
        add("xingma", list("行吗"), list("行嗎"));
        add("yishi", list("意思"), list("意思"));
        add("dong", list("懂", "东"), list("懂", "東"));
        add("budong", list("不懂"), list("不懂"));
        add("tingbudong", list("听不懂"), list("聽不懂"));
        add("kebukeyi", list("可不可以"), list("可不可以"));
        add("keshi", list("可是"), list("可是"));
        add("danshi", list("但是"), list("但是"));
        add("yinwei", list("因为"), list("因為"));
        add("suoyi", list("所以"), list("所以"));
        add("ruguo", list("如果"), list("如果"));
        add("yinweisuoyi", list("因为所以"), list("因為所以"));
        add("yi", list("一", "已", "衣"), list("一", "已", "衣"));
        add("er", list("二", "而", "儿"), list("二", "而", "兒"));
        add("san", list("三"), list("三"));
        add("si", list("四", "是", "思"), list("四", "是", "思"));
        add("wu", list("五", "无", "物"), list("五", "無", "物"));
        add("liu", list("六", "流"), list("六", "流"));
        add("qi", list("七", "起", "气"), list("七", "起", "氣"));
        add("ba", list("八", "吧", "把"), list("八", "吧", "把"));
        add("jiu", list("九", "久", "就"), list("九", "久", "就"));
        add("shihao", list("十号"), list("十號"));
        add("yue", list("月", "越", "约"), list("月", "越", "約"));
        add("ri", list("日"), list("日"));
        add("xingqi", list("星期"), list("星期"));
        add("zhou", list("周", "州"), list("周", "州"));
        add("zhoumo", list("周末"), list("週末"));
        add("nian", list("年"), list("年"));
        add("jinian", list("几年"), list("幾年"));
        add("ji", list("几", "机", "给"), list("幾", "機", "給"));
        add("duoshao", list("多少"), list("多少"));
        add("qian", list("前", "钱", "千"), list("前", "錢", "千"));
        add("hou", list("后", "候"), list("後", "候"));
        add("qianmian", list("前面"), list("前面"));
        add("houmian", list("后面"), list("後面"));
        add("shangmian", list("上面"), list("上面"));
        add("xiamian", list("下面"), list("下面"));
        add("limian", list("里面"), list("裡面"));
        add("waimian", list("外面"), list("外面"));
        add("dongxi", list("东西"), list("東西"));
        add("xibian", list("西边"), list("西邊"));
        add("dongbian", list("东边"), list("東邊"));
        add("beibian", list("北边"), list("北邊"));
        add("nanbian", list("南边"), list("南邊"));
        add("zuobian", list("左边"), list("左邊"));
        add("youbian", list("右边"), list("右邊"));
        add("shangxue", list("上学"), list("上學"));
        add("fangxue", list("放学"), list("放學"));
        add("xihuan", list("喜欢"), list("喜歡"));
        add("buxihuan", list("不喜欢"), list("不喜歡"));
        add("juzi", list("句子"), list("句子"));
        add("hanzi", list("汉字"), list("漢字"));
        add("zhongwen", list("中文"), list("中文"));
        add("hanyu", list("汉语"), list("漢語"));
        add("putonghua", list("普通话"), list("普通話"));
        add("yingwen", list("英文"), list("英文"));
        add("yingyu", list("英语"), list("英語"));
        add("dianhua", list("电话"), list("電話"));
        add("shouji", list("手机"), list("手機"));
        add("diannao", list("电脑"), list("電腦"));
        add("wangluo", list("网络"), list("網路"));
        add("gongsi", list("公司"), list("公司"));
        add("yiyuan", list("医院"), list("醫院"));
        add("xuexiao", list("学校"), list("學校"));
        add("fandian", list("饭店"), list("飯店"));
        add("canyin", list("餐饮"), list("餐飲"));
        add("che", list("车", "撤"), list("車", "撤"));
        add("gonggongqiche", list("公共汽车"), list("公共汽車"));
        add("huoche", list("火车"), list("火車"));
        add("feiji", list("飞机"), list("飛機"));
        add("ditie", list("地铁"), list("地鐵"));
        add("chuzuche", list("出租车"), list("出租車"));
        add("wojiaozhangsan", list("我叫张三"), list("我叫張三"));
        add("hengaoxingrenshini", list("很高兴认识你"), list("很高興認識你"));
        add("jintiantianqihenhao", list("今天天气很好"), list("今天天氣很好"));
        add("niyaoqunar", list("你要去哪儿"), list("你要去哪兒"));
        add("womenyiqiquba", list("我们一起去吧"), list("我們一起去吧"));
        add("xianzaikaishiba", list("现在开始吧"), list("現在開始吧"));
        add("qingzaishuyibian", list("请再说一遍"), list("請再說一遍"));
        add("womeitingdong", list("我没听懂"), list("我沒聽懂"));
        add("wokeyibangni", list("我可以帮你"), list("我可以幫你"));
        add("qingbangwoyixia", list("请帮我一下"), list("請幫我一下"));
        add("nihaoma", list("你好吗"), list("你好嗎"));
        add("wohenhao", list("我很好"), list("我很好"));
        add("xiexienidebangzhu", list("谢谢你的帮助"), list("謝謝你的幫助"));
        add("bukeqi", list("不客气"), list("不客氣"));
        add("mingtianjian", list("明天见"), list("明天見"));
        add("huitoujian", list("回头见"), list("回頭見"));
        add("qingwenzhenmepinyin", list("请问怎么拼音"), list("請問怎麼拼音"));
        add("wozaixuezhongwen", list("我在学中文"), list("我在學中文"));
        add("tashiwodelaoshi", list("他是我的老师"), list("他是我的老師"));
        add("tajintiangongzuohenmang", list("他今天工作很忙"), list("他今天工作很忙"));
        add("womenmingtianzaichifan", list("我们明天再吃饭"), list("我們明天再吃飯"));
        add("qinggeiwoyidian", list("请给我一点"), list("請給我一點"));
        add("woxiangheshui", list("我想喝水"), list("我想喝水"));
        add("woxiangchifan", list("我想吃饭"), list("我想吃飯"));
        add("nixianzaizainar", list("你现在在哪儿"), list("你現在在哪兒"));
        add("wozaijia", list("我在家"), list("我在家"));
        add("wozaigongsi", list("我在公司"), list("我在公司"));
        add("wozaixuexiao", list("我在学校"), list("我在學校"));
        add("wushangbanle", list("我上班了"), list("我上班了"));
        add("woxiabanle", list("我下班了"), list("我下班了"));
        add("qingwenxizainali", list("请问洗手间在哪里"), list("請問洗手間在哪裡"));
        add("cesuozainali", list("厕所在哪里"), list("廁所在哪裡"));
        add("wokeyiyongma", list("我可以用吗"), list("我可以用嗎"));
        add("nikeyibangwoma", list("你可以帮我吗"), list("你可以幫我嗎"));
        add("womenyiqixuexizhongwen", list("我们一起学习中文"), list("我們一起學習中文"));
        add("qingmanyidian", list("请慢一点"), list("請慢一點"));
        add("qingdashengyidian", list("请大声一点"), list("請大聲一點"));
        add("wotingbuqingchu", list("我听不清楚"), list("我聽不清楚"));
        add("qingbazhegexiexialai", list("请把这个写下来"), list("請把這個寫下來"));
        add("woxiangmaizhege", list("我想买这个"), list("我想買這個"));
        add("zhegeshaoqian", list("这个多少钱"), list("這個多少錢"));
        add("taiguile", list("太贵了"), list("太貴了"));
        add("pianyiyidian", list("便宜一点"), list("便宜一點"));
        add("wokeyishishima", list("我可以试试吗"), list("我可以試試嗎"));
        add("qinggeiwozhangdan", list("请给我账单"), list("請給我賬單"));
        add("womenbaochilianxi", list("我们保持联系"), list("我們保持聯繫"));
        add("zhuyini anquan", list("注意你安全"), list("注意你安全"));
        add("zhuyianquan", list("注意安全"), list("注意安全"));
        add("yilushunfeng", list("一路顺风"), list("一路順風"));
        add("zhunihaoyun", list("祝你好运"), list("祝你好運"));
        add("xinniankuaile", list("新年快乐"), list("新年快樂"));
        add("jierikuaile", list("节日快乐"), list("節日快樂"));
    }

    private static List<String> list(String... values) {
        return Arrays.asList(values);
    }

    private static void add(String key, List<String> simplified, List<String> traditional) {
        SIMPLIFIED.put(key, simplified);
        TRADITIONAL.put(key, traditional);
    }

    public List<String> getCandidates(String rawInput, ScriptMode scriptMode) {
        String input = normalize(rawInput);
        if (input.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, List<String>> dictionary = scriptMode == ScriptMode.TRADITIONAL ? TRADITIONAL : SIMPLIFIED;
        Set<String> out = new LinkedHashSet<>();
        List<String> exact = dictionary.get(input);
        if (exact != null) {
            out.addAll(exact);
        }
        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            if (entry.getKey().startsWith(input) && !entry.getKey().equals(input)) {
                out.addAll(entry.getValue());
            }
        }
        if (input.length() > 1) {
            String tail = longestKnownSuffix(input, dictionary);
            if (!tail.isEmpty()) {
                List<String> suffix = dictionary.get(tail);
                if (suffix != null) {
                    out.addAll(suffix);
                }
            }
        }
        return new ArrayList<>(out);
    }

    public boolean isComposingChar(char ch) {
        return Character.isLetter(ch) || ch == '\'';
    }

    public String normalize(String rawInput) {
        return rawInput == null ? "" : rawInput.toLowerCase(Locale.US).replaceAll("[^a-z']", "");
    }

    private String longestKnownSuffix(String input, Map<String, List<String>> dictionary) {
        for (int i = 0; i < input.length(); i++) {
            String suffix = input.substring(i);
            if (dictionary.containsKey(suffix)) {
                return suffix;
            }
        }
        return "";
    }
}
