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
    private static final Map<String, List<String>> EXACT = new HashMap<>();

    static {
        add("ni", "你", "呢", "尼", "泥");
        add("hao", "好", "号", "浩", "毫");
        add("nihao", "你好");
        add("shi", "是", "时", "事", "市", "十");
        add("jie", "界", "解", "接", "节", "街");
        add("shijie", "世界");
        add("wo", "我", "握", "窝");
        add("women", "我们");
        add("nimen", "你们");
        add("ta", "他", "她", "它", "塔");
        add("tamen", "他们", "她们");
        add("zhong", "中", "种", "重", "众");
        add("guo", "国", "过", "果");
        add("zhongguo", "中国");
        add("ren", "人", "仁", "认", "任");
        add("renshi", "认识");
        add("de", "的", "得", "德");
        add("ma", "吗", "妈", "马", "嘛");
        add("hen", "很", "狠", "痕");
        add("gaoxing", "高兴");
        add("xiexie", "谢谢");
        add("zaijian", "再见");
        add("qing", "请", "情", "青", "清");
        add("wen", "问", "文", "闻", "稳");
        add("qingwen", "请问");
        add("dui", "对", "队");
        add("bu", "不", "部", "布");
        add("duibuqi", "对不起");
        add("mei", "没", "美", "每", "妹");
        add("guanxi", "关系");
        add("meiguanxi", "没关系");
        add("zai", "在", "再", "载");
        add("jian", "见", "件", "间");
        add("ai", "爱", "矮", "哎");
        add("chi", "吃", "持", "迟", "池");
        add("fan", "饭", "反", "凡");
        add("chifan", "吃饭");
        add("he", "和", "喝", "河", "合");
        add("shui", "水", "谁", "睡");
        add("heshui", "喝水");
        add("jintian", "今天");
        add("mingtian", "明天");
        add("zuotian", "昨天");
        add("xianzai", "现在");
        add("shenme", "什么");
        add("zenme", "怎么");
        add("weishenme", "为什么");
        add("keyi", "可以");
        add("yidian", "一点");
        add("yige", "一个");
        add("meiyou", "没有");
        add("you", "有", "又", "右");
        add("qu", "去", "取", "区");
        add("lai", "来", "莱", "赖");
        add("huilai", "回来");
        add("gongzuo", "工作");
        add("xuexi", "学习");
        add("shengri", "生日");
        add("kuaile", "快乐");
        add("shengrikuaile", "生日快乐");
        add("zaoshanghao", "早上好");
        add("wanan", "晚安");
        add("laoshi", "老师");
        add("xuesheng", "学生");
        add("pengyou", "朋友");
        add("jia", "家", "加", "假");
        add("huijia", "回家");
        add("shangban", "上班");
        add("xiaban", "下班");
    }

    private static void add(String key, String... values) {
        EXACT.put(key, Arrays.asList(values));
    }

    public List<String> getCandidates(String rawInput) {
        String input = normalize(rawInput);
        if (input.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> out = new LinkedHashSet<>();
        List<String> exact = EXACT.get(input);
        if (exact != null) {
            out.addAll(exact);
        }
        for (Map.Entry<String, List<String>> entry : EXACT.entrySet()) {
            if (entry.getKey().startsWith(input) && !entry.getKey().equals(input)) {
                out.addAll(entry.getValue());
            }
        }
        if (input.length() > 1) {
            String tail = longestKnownSuffix(input);
            if (!tail.isEmpty()) {
                List<String> suffix = EXACT.get(tail);
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

    private String longestKnownSuffix(String input) {
        for (int i = 0; i < input.length(); i++) {
            String suffix = input.substring(i);
            if (EXACT.containsKey(suffix)) {
                return suffix;
            }
        }
        return "";
    }
}
