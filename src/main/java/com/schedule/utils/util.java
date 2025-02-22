package com.schedule.utils;

import com.schedule.supervisory.entity.Task;

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

    public static void main(String[] args) {
        String old = "remove,add";
        System.out.println("======== join: " + util.joinString(old, "add"));
        System.out.println("======== remove: " + util.removeString(old, "remove"));
    }
}
