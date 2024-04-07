package com.huxiaofan.api;

import com.alibaba.fastjson.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@WebServlet(name = "MoneyApi")
public class MoneyApi extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //封装的http请求响应头
        httpUtils.httpUtil(request, response);
        request.setCharacterEncoding("utf-8");
        //定义输出对象
        Writer o = response.getWriter();

        //需要用到事务查询，就不使用之前的工具类了
        Connection con = null;
        Statement stmt = null;
        try {
            con = MySql.getConnection();
            System.out.println("Statement建立成功");
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //因为涉及“钱”的操作
        //事务默认就是自动提交的。 需要开启事务，关闭自动提交。
        try {
            con.setAutoCommit(false);
            System.out.println("MoneyAPI关闭自动提交成功");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String method = request.getParameter("method");

        if (method.equals("income")) {
            //用来充值物业费
            String id = request.getParameter("id");
            String money = request.getParameter("money");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String d = dateFormat.format(date);
            System.out.println(id + " " + money + " " + d);
            String income = "update members set cmoney=cmoney+\"" + money + "\" where cno=\"" + id + "\"";
            System.out.println(income);
            String record = "INSERT INTO record" +
                    "(method,cno,date,times,money)" +
                    "VALUES" +
                    "(\'" + method + "\',\'" + id + "\',\'" + d + "\',\'" + "1" + "\',\'"  + money + "\')";
            System.out.println(record);
            try {
                if (stmt.executeUpdate(income) == 0 || stmt.executeUpdate(record) == 0) {
                    response.setStatus(202);
                    o.write("Fail，充值失败！");
                    System.out.println("Fail，充值失败！");
                } else {
                    con.commit();
                    response.setStatus(200);
                    o.write("OK，充值成功！");
                    System.out.println("OK，充值成功！");
                }
            } catch (SQLException throwables) {
                try {
                    con.rollback();
                    System.out.println("遇到异常，回滚数据库成功");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                response.setStatus(204);
                o.write("Fail，充值失败！");
                System.out.println("Fail，充值失败！");
                throwables.printStackTrace();
            } finally {
                //使用定义的工具类一键断开con和stmt连接
                try {
                    stmt.close();
                    con.close();
                    System.out.println("断开事务的mysql连接成功");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } else if (method.equals("delete")) {
            //用来删除物业费记录
            String id = request.getParameter("id");
            String rm = "delete from record where id = \"" + id + "\"";
            try {
                if (stmt.executeUpdate(rm) == 0) {
                    response.setStatus(202);
                    o.write("Fail，删除失败！");
                    System.out.println("Fail，删除失败！" + rm);
                } else {
                    con.commit();
                    response.setStatus(200);
                    o.write("OK，删除成功！");
                    System.out.println("OK，删除成功！" + rm);
                    System.out.println(rm);
                }
            } catch (SQLException throwables) {
                try {
                    con.rollback();
                    System.out.println("遇到异常，回滚数据库成功");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                response.setStatus(204);
                o.write("Fail，删除失败！");
                System.out.println("Fail，删除失败！" + rm);
                throwables.printStackTrace();
            } finally {
                //使用定义的工具类一键断开con和stmt连接
                try {
                    stmt.close();
                    con.close();
                    System.out.println("断开事务的mysql连接成功");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } else if (method.equals("pay")) {
            //用来支付物业费
            String cno = request.getParameter("uid");
            String sid = request.getParameter("service");
            String date = request.getParameter("date").substring(0, 19);
            String times = request.getParameter("times");
            String staff = request.getParameter("staff");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date d = null;
            try {
                d = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //这句话加8小时
            d.setTime(d.getTime() + 8 * 60 * 60 * 1000);
            //System.out.println(d);
            //时间对象转字符串
            date = sdf.format(d);
            System.out.println(method + " " + cno + " " + sid + " " + date + " " + times + " " + staff);

            //增加物业服务次数记录
            //使用sql语句内置的运算可以避免把整个表遍历再update的性能损耗
            //f = find
            String f = "select cno from members where cno = " + cno + " limit 1";
            //howmuch h1查询用户余额
            //查询服务价格h2
            String h1 = "select cmoney from members where cno = " + cno + " limit 1";
            String h2 = "select sprice from service where sid = " + sid + " limit 1";

            //m = modify
            String m = "update service set stime=stime+1 where sid = " + sid;

            //result接口
            ResultSet rs;
            try {
                String rid = null;
                Double umoney = Double.valueOf(0);
                Double smoney = Double.valueOf(0);

                rs = stmt.executeQuery(f);
                while (rs.next()) {
                    rid = rs.getString(1);
                }
                rs.close();
                rs = stmt.executeQuery(h1);
                while (rs.next()) {
                    umoney = Double.parseDouble(rs.getString(1));
                }
                rs.close();
                rs = stmt.executeQuery(h2);
                while (rs.next()) {
                    smoney = Double.parseDouble(rs.getString(1));
                }
                rs.close();
                System.out.println("用户余额：" + umoney + " 服务金额：" + smoney);

                //计算服务总金额 = 金额*次数
                smoney = Double.parseDouble(times) * smoney;
                System.out.println("smoney：" + smoney);

                //p = pay
                Double cmoney = umoney - smoney;
                String p = "update members set cmoney=" + cmoney + " where cno = " + cno;

                //添加物业费记录
                //a = add
                String a = "INSERT INTO record" +
                        "(method,cno,sid,date,times,staff,money)" +
                        "VALUES" +
                        "(\'" + method + "\',\'" + cno + "\',\'" + sid + "\',\'" + date + "\',\'" + times + "\',\'" + staff + "\',\'" + smoney + "\')";

                if (rid == null) {
                    response.setStatus(205);
                    o.write("Fail，用户ID不存在！");
                    System.out.println("Fail，用户ID不存在！" + f);
                } else if (umoney < smoney) {
                    response.setStatus(206);
                    o.write("Fail，用户余额不足！");
                    System.out.println("Fail，用户余额不足！");
                } else if (stmt.executeUpdate(p) == 0) {
                    response.setStatus(207);
                    o.write("Fail，用户扣款失败！");
                    System.out.println("Fail，用户扣款失败！" + p);
                } else if (stmt.executeUpdate(a) == 0) {
                    response.setStatus(202);
                    o.write("Fail，修改服务次数失败！");
                    System.out.println("Fail，修改服务次数失败！" + a);
                } else if (stmt.executeUpdate(m) == 0) {
                    response.setStatus(202);
                    o.write("Fail，插入失败！");
                    System.out.println("Fail，插入用户名密码失败！" + m);
                } else {

                    //增加员工绩效
                    //绩效增加失败不影响整体执行
                    Double jx = Double.valueOf(times)*5;
                    String s = "update staff set escore=escore+"+ jx +" where eid = " + staff;
                    try{
                        stmt.executeUpdate(s);
                        System.out.println("员工绩效增加成功！" + s);
                    }catch (Exception e){
                        System.out.println(e);
                    }

                    //没问题的话才提交数据库查询
                    con.commit();
                    response.setStatus(200);
                    o.write("OK，新增物业费记录成功！");
                    System.out.println("OK，新增物业费记录成功！" + sid);
                    System.out.println(a);
                }

            } catch (SQLException throwables) {
                try {
                    con.rollback();
                    System.out.println("遇到异常，回滚数据库成功");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                response.setStatus(204);
                o.write("Fail，插入失败！");
                System.out.println("Fail，插入物业费记录失败！");
                throwables.printStackTrace();
            } finally {
                //使用定义的工具类一键断开con和stmt连接
                try {
                    stmt.close();
                    con.close();
                    System.out.println("断开事务的mysql连接成功");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //此接口返回物业费记录列表
        //封装的http请求响应头
        httpUtils.httpUtil(request, response);
        request.setCharacterEncoding("utf-8");
        //定义输出对象
        Writer o = response.getWriter();
        //result接口
        ResultSet rs;

        dbUtils db = new dbUtils();
        try {
            Statement stmt = db.getStatement();
            rs = stmt.executeQuery("SELECT * FROM record");

            rs.beforeFirst();
            //使用alibaba的fastjson建立一个json对象
            JSONArray recordsJson = new JSONArray();

            while (rs.next()) {
                //创建用户信息哈希表
                HashMap<String, String> records = new HashMap<String, String>();
                String id = rs.getString(1);
                String date = rs.getString(2);
                String cno = rs.getString(3);
                String sid = rs.getString(4);
                String method = rs.getString(5);
                String times = rs.getString(6);
                String staff = rs.getString(7);
                String money = rs.getString(8);
                records.put("id", id);
                records.put("date", date);
                records.put("cno", cno);
                if (sid == null){
                    records.put("sid", "-");
                }else {
                    records.put("sid", sid);
                }
                if (method.equals("pay")) {
                    records.put("method", "服务消费");
                } else {
                    records.put("method", "物业费充值");
                }
                records.put("times", times);
                if (staff == null){
                    records.put("staff", "-");
                }else {
                    records.put("staff", staff);
                }
                records.put("money", money);
                //把hashmap对象添加到json数组中
                recordsJson.add(records);
            }
            //字符串输出json内容
            response.setStatus(200);
            o.write(recordsJson.toJSONString());
            //断开数据库连接
            rs.close();
            //使用定义的工具类一键断开con和stmt连接
            //db.closeConnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //使用定义的工具类一键断开con和stmt连接
            db.closeConnect();
        }
    }
}
