package com.schedule.utils;

import com.schedule.supervisory.entity.Task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class util {
    public static String joinString(String old, String add) {
        List<String> list = null;
        if (add == null) {
            return old;
        }

        if (old == null || old.length() <= 0) {
            list = new ArrayList<>();
        } else {
            String[] splitOld = old.split(",");
            list = new ArrayList<>(Arrays.asList(splitOld));
        }

        if (add != null && list.contains(add) == false) {
            list.add(add);
        }
        return String.join(",", list);
    }

    public static String removeString(String old, String remove) {
        List<String> list = null;
        if (remove == null) {
            return old;
        }

        if (old == null || old.length() <= 0) {
            list = new ArrayList<>();
        } else {
            String[] splitOld = old.split(",");
            list = new ArrayList<>(Arrays.asList(splitOld));
        }

        if (list.contains(remove) == true) {
            list.remove(remove);
        }
        return String.join(",", list);
    }

    /**
     * 计算截止日期与当前日期之间的天数差异。
     * @return 天数差异，如果截止日期在今天之前，则返回值为负。
     */
    public static long daysDifference(LocalDate deadline) {
        LocalDate today = LocalDate.now(); // 得到当前日期
        return ChronoUnit.DAYS.between(deadline, today);
    }

    public static void main(String[] args) {
        String old = "remove,add";
        System.out.println("======== join: " + util.joinString(old, "add"));
        System.out.println("======== remove: " + util.removeString(old, "remove"));
        LocalDate deadlineExample = LocalDate.of(2025, 2, 25);
        System.out.println("----------- diff : "+ util.daysDifference(deadlineExample));
    }
}
