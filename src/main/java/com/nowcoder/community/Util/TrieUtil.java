package com.nowcoder.community.Util;

import com.sun.org.apache.xml.internal.utils.Trie;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-08-06 21:42
 */
@Component
public class TrieUtil {
    private TrieNode root = new TrieNode();

    public String filterSensitiveWord(String str) {
        if (StringUtils.isBlank(str)) return str;
        StringBuilder stringBuilder = new StringBuilder();
        int end = 0;
        TrieNode tmpNode = root;
        int start = 0;
        while (start < str.length()) {
            if (end <= str.length() - 1) {
                char c = str.charAt(end);
                if (isSymbol(c)) {
                    if (tmpNode == root) {
                        stringBuilder.append(c);
                        start++;
                    }
                    end++;
                    continue;
                }
                tmpNode = tmpNode.map.get(c);
                if (tmpNode == null) {
                    stringBuilder.append(str.charAt(start++));
                    end = start;
                    tmpNode = root;
                } else if (tmpNode.endNum > 0) {
                    for (int i = start; i <= end; i++)
                        stringBuilder.append('*');
                    start = ++end;
                    tmpNode = root;
                } else {
                    end++;
                }
            }else{
                stringBuilder.append(str.charAt(start));
                end=++start;tmpNode=root;
            }
        }
        return stringBuilder.toString();
    }

    private boolean isSymbol(Character c) {
        return ((c > 0x007F && c < 0x2E80) || c > 0x9FFF);
    }

    private boolean search(String str) {
        char[] chars = str.toCharArray();
        TrieNode node = root;
        for (char aChar : chars) {
            if (node.map.containsKey(aChar))
                node = node.map.get(aChar);
            else
                return false;
        }
        return node.endNum > 0;
    }

    @PostConstruct
    private void makeTrie() {
        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            String word = new String();
            while ((word = bufferedReader.readLine()) != null) {
                insertToTrie(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertToTrie(String word) {
        if (StringUtils.isBlank(word)) return;
        char[] chars = word.toCharArray();
        TrieNode node = root;
        for (int i = 0; i < chars.length; i++) {
            node.startNum++;
            if (!node.map.containsKey(chars[i])) node.map.put(chars[i], new TrieNode());
            node = node.map.get(chars[i]);
        }
        node.startNum++;
        node.endNum++;
    }
}

class TrieNode {
    public int startNum = 0;
    public int endNum = 0;
    public HashMap<Character, TrieNode> map = new HashMap<>();
}
