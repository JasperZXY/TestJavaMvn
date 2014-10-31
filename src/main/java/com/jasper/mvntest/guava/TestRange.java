package com.jasper.mvntest.guava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class TestRange {
    public static void main(String[] args) {
        RangeSet<Long> rangeSet = TreeRangeSet.create();

        Multimap<String, Range<Long>> multimap = ArrayListMultimap.create();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(System.getProperty("user.dir") + "/src/main/resources/networkips_city.txt"), "utf-8");

            int count = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] words = StringUtils.splitPreserveAllTokens(line, ",");
                long ip1 = ip2Long(words[0]);
                long ip2 = ip2Long(words[1]);
                Range<Long> range = Range.closed(ip1, ip2);
                multimap.put(words[2] + "，" + words[3], range);
                count++;
            }
            System.out.println("Total GuangDong ip set is:" + count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        boolean flag = false;
        Long ip = ip2Long("183.60.177.228");
        System.out.println(ip);
        Iterator<Range<Long>> iterator = multimap.get("广东，广州").iterator();
        while(iterator.hasNext()) {
            Range<Long> range = iterator.next();
            System.out.println(range);
            if(range.contains(ip)) {
                flag = true;
                break;
            }
        }
        System.out.println(flag);

    }

    public static long ip2Long(String ipStr) {
        String[] tmp = ipStr.split("[.]");
        long ip = 0L | Long.valueOf(tmp[0]) << 24;
        ip = ip | (Long.valueOf(tmp[1]) << 16);
        ip = ip | (Long.valueOf(tmp[2]) << 8);
        ip = ip | Long.valueOf(tmp[3]);
        if (ip < 0) {
            System.out.println("Ip2Long Error!!!! ip=" + ip);
        }
        return ip;
    }

}