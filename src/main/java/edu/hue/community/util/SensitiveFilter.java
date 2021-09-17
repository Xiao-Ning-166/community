package edu.hue.community.util;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 47552
 * @date 2021/09/17
 * 进行 敏感词过滤
 */
@Slf4j
@Component
public class SensitiveFilter {

    // 替换符
    private static final String REPLACE_OPERATOR = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 根据敏感词配置文件初始化前缀树
     */
    @PostConstruct
    private void  init() {
        // 获取敏感词配置文件输入流
        try (
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        ) {
            String keyWord;
            while ((keyWord = bufferedReader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyWord);
            }
        } catch (IOException e) {
            log.error("敏感词文件加载失败！！！原因：" + e.getMessage());
        }

    }

    /**
     * 将一个敏感词添加到前缀树中
     * @param word 敏感词
     */
    private void addKeyword(String word) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            // 查看是否有此节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;
            // 设置结束标识
            if (i == word.length() - 1) {
                tempNode.setEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 未被过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StrUtil.isBlankIfStr(text)) {
            return null;
        }
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder result = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            // 进行敏感词过滤
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin下标的字符不是敏感词
                result.append(text.charAt(begin));
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isEnd()) {
                // 发现敏感词
                result.append(REPLACE_OPERATOR);
                begin = ++position;
                tempNode = rootNode;
            } else {
                position++;
            }
        }
        result.append(text.substring(begin));

        return result.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树的节点类
    private class TrieNode {
        // 是否是结束字符
        private boolean isEnd = false;
        // 子节点（key：下级字符，value：下级节点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        // 返回当前节点是否是结束字符
        public boolean isEnd() {
            return isEnd;
        }
        //
        public void setEnd(boolean keyWordEnd) {
            isEnd = keyWordEnd;
        }
        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }
        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
