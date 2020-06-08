package cn.itrip.trade.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.common.DtoUtil;
import cn.itrip.trade.config.MyWXPayConfig;
import cn.itrip.trade.service.OrderService;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wxpay")
public class WxPayController {
    @Resource(name = "wxPayConfig")
    private MyWXPayConfig config;
    @Resource
    private OrderService orderService;

    @GetMapping("/createqccode/{orderNo}")
    public Dto createQcCode(@PathVariable String orderNo) throws Exception {
        //验证参数
        if (orderNo == null) {
            return DtoUtil.returnFail("订单编号不能为空", "400001");
        }

        //config封装了部分参数
        WXPay wxPay = new WXPay(config);
        //封装统一下单参数
        Map<String, String> data = new HashMap<>();
        data.put("body", "酒店爱旅行");
        data.put("out_trade_no", orderNo);
        data.put("fee_type", "CNY");
        data.put("total_fee", "1"); //1分钱
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", config.getNotifyUrl());
        data.put("trade_type", "NATIVE");
        data.put("product_id", "12");

        Map<String, String> resp = wxPay.unifiedOrder(data);
        if ("SUCCESS".equals(resp.get("return_code")) && "OK".equals(resp.get("return_msg"))) {
            if ("SUCCESS".equals(resp.get("result_code"))) {
                //统一下单请求支付链接成功
                String code_url = resp.get("code_url");
                //封装Dto返回前端用于生产二维码
                Map<String, String> result = new HashMap<>();
                result.put("codeUrl", code_url);
                return DtoUtil.returnDataSuccess(result);
            }
        }

        return DtoUtil.returnFail("获取二维码失败", "400002");
    }

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public void getnotify(HttpServletRequest request, HttpServletResponse response) throws Exception {


        ServletInputStream is = request.getInputStream();
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        is.close();
        WXPay wxPay = new WXPay(config);
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(sb.toString());

        if (wxPay.isResponseSignatureValid(notifyMap)) {
            //签名成功
            //进行处理
            ItripHotelOrder order = new ItripHotelOrder();
            order.setOrderNo(notifyMap.get("out_trade_no"));
            order.setTradeNo(notifyMap.get("transaction_id"));
            order.setPayType(2);
            order.setOrderStatus(2);
            order.setModifyDate(new Date());
            orderService.processupdateOrderStatus(order);
            //特殊情况：订单已退款，但收到支付结果成功的通知
        }

        //返回数据给微信 告诉微信系统我已成功接收到通知了 你不需要告诉我了
        Map<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");

        String xmlData = WXPayUtil.mapToXml(result);
        response.getWriter().write(xmlData);

    }


}
