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
