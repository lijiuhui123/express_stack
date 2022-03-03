package com.ljh.wx.controller;

import com.ljh.bean.BootStrapTableExpress;
import com.ljh.bean.Express;
import com.ljh.bean.Message;
import com.ljh.bean.User;
import com.ljh.mvc.ResponseBody;
import com.ljh.mvc.ResponseView;
import com.ljh.service.ExpressService;
import com.ljh.util.DateFormatUtil;
import com.ljh.util.JSONUtil;
import com.ljh.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class QRCodeController {

    @ResponseView("/wx/createQRCode.do")
    public String createQrcode(HttpServletRequest request, HttpServletResponse response){
        String code = request.getParameter("code");
        //express | user
        String type = request.getParameter("type");
        String userPhone = null;
        String qRCodeContent = null;
        if("express".equals(type)){
            //快递二维码:被扫后,展示单个快递的信息
            //code
            qRCodeContent = "express_"+code;
        }else{
            //用户二维码:被扫后,快递员(柜子)端展示用户所有快递
            //userPhone
            User wxUser = UserUtil.getWxUser(request.getSession());
            userPhone = wxUser.getUserPhone();
            qRCodeContent = "userPhone_"+userPhone;
        }
        HttpSession session = request.getSession();
        session.setAttribute("qrcode",qRCodeContent);
        return "/personQRcode.html";
    }

    @ResponseBody("/wx/qrcode.do")
    public String getQRCode(HttpServletRequest request,HttpServletResponse response){
        HttpSession session = request.getSession();
        String qrcode = (String) session.getAttribute("qrcode");
        Message msg = new Message();
        if(qrcode == null){
            msg.setStatus(-1);
            msg.setResult("取件码获取出错,请用户重新操作");
        }else{
            msg.setStatus(0);
            msg.setResult(qrcode);
        }

        return JSONUtil.toJSON(msg);
    }

    @ResponseBody("/wx/updateStatus.do")
    public String updateExpressStatus(HttpServletRequest request,HttpServletResponse response){
        String code = request.getParameter("code");
        boolean flag = ExpressService.updateStatus(code);
        Message msg = new Message();
        if(flag){
            msg.setStatus(0);
            msg.setResult("取件成功");
        }else{
            msg.setStatus(-1);
            msg.setResult("取件码不存在,请用户更新二维码");
        }
        String json = JSONUtil.toJSON(msg);
        return json;
    }
    @ResponseBody("/wx/findExpressByCode.do")
    public String findExpressByCode(HttpServletRequest request,HttpServletResponse response){
        String code = request.getParameter("code");
        Express e = ExpressService.findByCode(code);
        BootStrapTableExpress e2 =null;
        if(e!=null){
            e2 = new BootStrapTableExpress(e.getId(), e.getNumber(), e.getUsername()
                , e.getUserPhone(), e.getCompany(), e.getCode(),
                DateFormatUtil.format(e.getInTime()), e.getOutTime()==null?"未出库":DateFormatUtil.format(e.getOutTime()),e.getStatus()==0?"待取件":"已取件", e.getSysPhone());
        }
        Message msg = new Message();
        if(e == null){
            msg.setStatus(-1);
            msg.setResult("取件码不存在");
        }else{
            msg.setStatus(0);
            msg.setResult("查询成功");
            msg.setData(e2);
        }
        return JSONUtil.toJSON(msg);
    }

}
