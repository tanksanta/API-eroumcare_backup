package kr.co.thkc.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static double getDateDiffOrder(String start, String end) throws Exception {

        if(start.contains(".")) start = start.replace(".","-");
        if(end.contains(".")) end = end.replace(".","-");

        LocalDate startDate = LocalDate.parse(start,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse(end,DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //월 수 계산
        LocalDate tmpStart = LocalDate.parse(start.substring(0,start.length()-2) + startDate.lengthOfMonth(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate tmpEnd = endDate.withDayOfMonth(1);

        Period period = tmpStart.until(tmpEnd);
        int monthCnt = (period.getYears() * 12) + period.getMonths();
        if(monthCnt==0) monthCnt = -1;

        //나머지 일 수 계산
        endDate = endDate.plusDays(1);
        double startDayCnt = (double)(startDate.lengthOfMonth()-startDate.getDayOfMonth()) / (double)startDate.lengthOfMonth();
        double endDayCnt = (double)endDate.getDayOfMonth() / (double)endDate.lengthOfMonth();


        return startDayCnt+monthCnt+endDayCnt;
    }


    /**
     * 월 차이 계산
     * @param start, end
     * @return
     * @throws Exception
     */
    public static int getDateDiffMonth(String start, String end) throws Exception {
        if(start.contains(".")) start = start.replace(".","-");
        if(end.contains(".")) end = end.replace(".","-");


        LocalDate startDate = LocalDate.parse(start,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse(end,DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1);

        Period period = startDate.until(endDate);

        return (period.getYears() * 12) + period.getMonths();
    }

}
