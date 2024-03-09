package com.sky.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    // 营业统计
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 存放begin end 每天日期 应该是date
        List<LocalDate> datalist = new ArrayList<>();
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : datalist) {
            // 已完成订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumbyMap(map);
            turnoverList.add(turnover == null ? 0.0 : turnover);

        }
        return TurnoverReportVO.builder().dateList(StringUtils.join(datalist, ","))
                .turnoverList(StringUtils.join(turnoverList, ",")).build();
    }

    // 用户统计
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datalist = new ArrayList<>();
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(datalist, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    // 统计指定时间区间订单数据
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datalist = new ArrayList<>();
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }
        List<Integer> ordercCountList = new ArrayList<>();
        List<Integer> validorderCount = new ArrayList<>();
        for (LocalDate date : datalist) {
            // 查询每天订单总数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            // 查询么天的有效订单数
            Integer vaildCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            ordercCountList.add(orderCount);
            validorderCount.add(vaildCount);
        }
        Integer totalordercount = ordercCountList.stream().reduce(Integer::sum).get();
        Integer vaildorderSum = validorderCount.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if (totalordercount != 0)
            orderCompletionRate = vaildorderSum.doubleValue() / totalordercount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(datalist, ","))
                .orderCountList(StringUtils.join(ordercCountList, ","))
                .validOrderCountList(StringUtils.join(validorderCount, ","))
                .validOrderCount(vaildorderSum)
                .totalOrderCount(totalordercount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);

    }

    // 销量前10
    public SalesTop10ReportVO getSalestop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOsList = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> namecollect = goodsSalesDTOsList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(namecollect, ",");
        List<Integer> numbercollect = goodsSalesDTOsList.stream().map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList());
        String numberList = StringUtils.join(numbercollect, ",");
        return SalesTop10ReportVO.builder()
                .numberList(numberList)
                .nameList(nameList)
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end =  LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // 查询数据库
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime,endTime);
        // 通过DOt将数据库放入excel文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板");
        ServletOutputStream out = null;
        XSSFWorkbook excel = null;
        try {
            excel = new XSSFWorkbook(in);
            // 填充数据
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间："+beginTime+"至"+endTime);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());


            // 明细数据
            for(int i = 0; i < 30 ;i++){
                LocalDate date = begin.plusDays(1);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date,LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            }





            out = response.getOutputStream();

            excel.write(out);
            in.close();
            out.close();
            excel.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 通过输出excel文件下载
       
    }
}
