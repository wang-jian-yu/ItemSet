package com.huxiaofan.api;
//此类实现用户列表查询，用户信息更新和用户删除

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.alibaba.fastjson.*;

@WebServlet(name = "ListMembers")
public class ListMembers extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //此api用于删除和修改用户
        //封装的http请求响应头
        httpUtils.httpUtil(request, response);
        request.setCharacterEncoding("utf-8");
        //定义输出对象
        Writer o = response.getWriter();

        //如果参数中method为删除，则操作mysql删除数据
        if (request.getParameter("method").equals("delete")) {
            //新的数据工具类对象
            dbUtils db = new dbUtils();
            //创建stmt类
            Statement stmt = db.getStatement();
            String cno = request.getParameter("cno");
            //这里使用了leftjoin多表联合查询进行删除;
            String d = "delete members,mpass,record from members " +
                    "LEFT JOIN mpass ON members.cno=mpass.cno" +
                    " LEFT JOIN record on members.cno=record.cno " +
                    "where members.cno = \"" + cno + "\"";
            try {
                if (stmt.executeUpdate(d) == 0) {
                    response.setStatus(202);
                    o.write("Fail，删除失败！");
                    System.out.println("Fail，删除失败！" + d);
                } else {
                    response.setStatus(200);
                    o.write("OK，删除成功！");
                    System.out.println("OK，删除成功！" + cno);
                    System.out.println(d);
                }
            } catch (SQLException throwables) {
                response.setStatus(204);
                o.write("Fail，删除失败！");
                System.out.println("Fail，删除失败！" + d);
                throwables.printStackTrace();
            } finally {
                //使用定义的工具类一键断开con和stmt连接
                db.closeConnect();
            }
        } else if (request.getParameter("method").equals("modify")) {
            //新的数据工具类对象
            dbUtils db = new dbUtils();
            //创建stmt类
            Statement stmt = db.getStatement();
            //从参数获取前端需要修改的学生ID
            String cno = request.getParameter("row[cno]");
            String cname = request.getParameter("row[cname]");
            String caddress = request.getParameter("row[caddress]");
            //因为前端来的数据形如 cregtime="1986-01-07T16:00:00.000Z"需要对其进行处理
            String cregtime = request.getParameter("row[cregtime]").substring(0, 19);
            String csex = request.getParameter("row[csex]");
            String cphone = request.getParameter("row[cphone]");
            System.out.println("取得的参数为：" + cno + " " + cname + " " + caddress + " " + cregtime + " " + csex + " " + cphone);
            //用于sql出错分析
            // String m = "update members set cname=\"" + cname + "\", csex=\"" + csex + "\", caddress=\"" + caddress + "\", cregtime=\"" + cregtime + "\" where cno=\"" + cno + "\"";

            try {
                //先转换为时间对象是为了解决时区UTF-8同步的的问题
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = sdf.parse(cregtime);
                //这句话加8小时
                d.setTime(d.getTime() + 8 * 60 * 60 * 1000);
                //System.out.println(d);
                //时间对象转字符串
                cregtime = sdf.format(d);
                //sql语句拼接
                String m = "update members set cname=\"" + cname + "\", csex=\"" + csex + "\", caddress=\"" + caddress + "\", cregtime=\"" + cregtime + "\", cphone=\"" + cphone + "\" where cno=\"" + cno + "\"";
                System.out.println(m);
                //打印行数
                int count = stmt.executeUpdate(m);
                if (count > 0) {
                    response.setStatus(200);
                    o.write("修改成功，影响行数：" + count);
                }
                System.out.println("修改成功，影响行数：" + count);
            } catch (SQLException | ParseException throwables) {
                response.setStatus(204);
                o.write("Fail，修改失败！");
                System.out.println("Fail，修改失败！");
                throwables.printStackTrace();
            } finally {
                //使用定义的工具类一键断开con和stmt连接
                db.closeConnect();
            }
        }

        //使用定义的工具类一键断开con和stmt连接
        //db.closeConnect();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //此接口返回用户列表
        //封装的http请求响应头
        httpUtils.httpUtil(request, response);
        request.setCharacterEncoding("utf-8");
        //定义输出对象
        Writer o = response.getWriter();
        dbUtils db = new dbUtils();
        Statement stmt = db.getStatement();
        //result接口
        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM members");
            rs.beforeFirst();
            //使用alibaba的fastjson建立一个json对象
            JSONArray membersJson = new JSONArray();

            while (rs.next()) {
                //创建用户信息哈希表
                HashMap<String, String> members = new HashMap<String, String>();
                String cno = rs.getString(1);
                String cname = rs.getString(2);
                String csex = rs.getString(3);
                String caddress = rs.getString(4);
                String cregtime = rs.getString(5);
                String cmoney = rs.getString(6);
                String cphone = rs.getString(7);
                members.put("cno", cno);
                members.put("cname", cname);
                members.put("csex", csex);
                members.put("caddress", caddress);
                members.put("cregtime", cregtime);
                members.put("cmoney", cmoney);
                members.put("cphone", cphone);
                //把hashmap对象添加到json数组中
                membersJson.add(members);
            }
            //字符串输出json内容
            response.setStatus(200);
            o.write(membersJson.toJSONString());
            //断开数据库连接
            rs.close();
            //使用定义的工具类一键断开con和stmt连接
            //db.closeConnect();
            System.out.println("获取用户列表成功");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //使用定义的工具类一键断开con和stmt连接
            db.closeConnect();
        }
    }
}
