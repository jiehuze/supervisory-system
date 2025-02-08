package com.schedule.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WordFileReplace {
    public static boolean replace(String oldText, String newText, String outputFilePath) {
        String inputFilePath = "doc/templete.doc";  // 原文档路径
//        String outputFilePath = "/Users/jiehu/works/test/replacefile/testreplace.doc";  // 替换后的文档路径

        try {
            // 打开 .doc 文件
            Resource resource = new ClassPathResource(inputFilePath);
            InputStream fis = resource.getInputStream();
            HWPFDocument document = new HWPFDocument(fis);

            // 获取文件的范围（即文档的所有文本内容）
            Range range = document.getRange();

            // 遍历文档中的每个字符块（CharacterRun）
            for (int i = 0; i < range.numCharacterRuns(); i++) {
                CharacterRun run = range.getCharacterRun(i);
                String text = run.text();
                if (text.contains(oldText)) {
                    // 替换文本并保持格式
                    text = text.replace(oldText, newText);
                    run.replaceText(run.text(), text);  // 更新文本
                }
            }

            // 保存修改后的文档
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            document.write(fos);
            fos.close();
            fis.close();

            System.out.println("文档中的文字已成功替换！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

//    public static void main(String[] args) {
//        WordFileReplace.replace("X", "CCCCC");
//    }
}
