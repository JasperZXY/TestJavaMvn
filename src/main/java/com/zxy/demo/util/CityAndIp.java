package com.zxy.demo.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class CityAndIp {
	
	public static void main(String[] args) {
		if (checkCityUniqueness()) {
			System.out.println("yes");
		} else {
			System.out.println("no");
		}
		
//		Multimap<String, Range<Long>> cityIps = ArrayListMultimap.create();
//		Multimap<String, Range<Long>> provinceIps = ArrayListMultimap.create();
		Map<String, RangeSet<Long>> cityIps = new HashMap<String, RangeSet<Long>>();
		Map<String, RangeSet<Long>> provinceIps = new HashMap<String, RangeSet<Long>>();
		Scanner scanner = null;
        try {
        	String filePath = CityAndIp.class.getClassLoader().getResource("networkips_city.txt").getFile();
        	scanner = new Scanner(new File(filePath), "utf-8");
        	int count = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] words = StringUtils.splitPreserveAllTokens(line, ",");
                try {
                	long ip1 = ip2Long(words[0]);
                	long ip2 = ip2Long(words[1]);
                	Range<Long> range = Range.closed(ip1, ip2);
                	//2：省份 ；3：市
                	if(StringUtils.isNotBlank(words[2])) {
                	    RangeSet<Long> rangeSet = provinceIps.get(words[2]);
                	    if(rangeSet == null) {
                	        rangeSet = TreeRangeSet.create();
                	        provinceIps.put(words[2], rangeSet);
                	    }
                		rangeSet.add(range);
                	}
                	if(StringUtils.isNotBlank(words[3])) {
                	    RangeSet<Long> rangeSet = cityIps.get(words[3]);
                        if(rangeSet == null) {
                            rangeSet = TreeRangeSet.create();
                            cityIps.put(words[3], rangeSet);
                        }
                        rangeSet.add(range);
                	}
                } catch(Exception e) {
                	System.out.println(line);
                }
                count++;
            }
            System.out.println("Total ip set is:" + count);
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        System.out.println(cityIps.get("广州").contains(ip2Long("183.60.177.228")));
        System.out.println(cityIps.get("广州").contains(ip2Long("223.247.64.12")));
        System.out.println(cityIps.get("亳州").contains(ip2Long("223.247.64.12")));
        System.out.println(provinceIps.get("安徽").contains(ip2Long("223.247.64.12")));
	}
	
	@SuppressWarnings("resource")
    public static boolean checkCityUniqueness() {
		Map<String, String> cityMap = new HashMap<String, String>();
		Scanner scanner = null;
		boolean flag = true;
        try {
        	String filePath = CityAndIp.class.getClassLoader().getResource("networkips_city.txt").getFile();
        	scanner = new Scanner(new File(filePath), "utf-8");
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] words = StringUtils.splitPreserveAllTokens(line, ",");
                try {
                	//2：省份 ；3：市
                	if (StringUtils.isNotBlank(words[3])) {
                		if (cityMap.get(words[3]) != null && 
                				! words[2].equals(cityMap.get(words[3]))) {
                			System.out.println(line);
                			System.out.println(words[3].getBytes().length + "=" + words[3] + "=" + words[2] + "=" + cityMap.get(words[3]));
                			flag = false;
                		}
                		cityMap.put(words[3], words[2]);
                	}
                } catch(Exception e) {
                	System.out.println(line);
                }
            }
        } catch(Exception e) {
        	e.printStackTrace();
        }
      	return flag;
	}
	
	public static long ip2Long(String ipStr) {
        String[] tmp = ipStr.split("[.]");
        if (tmp.length != 4) {
        	System.out.println("ip=" + ipStr);
        	return 0;
        }
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
