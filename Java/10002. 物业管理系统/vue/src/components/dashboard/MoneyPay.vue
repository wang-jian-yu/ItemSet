<template>
  <!--用来新增用户-->
  <el-form
    label-position="left"
    ref="form"
    :model="form"
    label-width="80px"
    style="max-width: 800px;"
    :rules="rules"
  >
    <el-form-item label="用户ID" prop="uid">
      <el-select
        filterable
        v-model="form.uid"
        placeholder="请选择用户"
        style="width: 100%"
      >
        <el-option
          v-for="item in cnoList"
          :key="item.cno"
          :label="'ID-' + item.cno + '  ' + item.cname"
          :value="item.cno"
        >
        </el-option>
      </el-select>

      <!--      <el-input v-model="form.uid" type="number" class="elinput"></el-input>-->
    </el-form-item>

    <el-form-item label="服务名称" prop="service" required>
      <el-select
        filterable
        v-model="form.service"
        placeholder="请选择对应服务"
        style="width: 100%"
      >
        <el-option
          v-for="item in serviceList"
          :key="item.sid"
          :label="'ID-' + item.sid + '  ' + item.sname"
          :value="item.sid"
        >
        </el-option>
      </el-select>
    </el-form-item>

    <!--这里要把校验规则prop写在下面的子ithem中-->
    <el-form-item label="服务时间" required>
      <el-col :span="11">
        <el-form-item prop="date1">
          <el-date-picker
            type="date"
            placeholder="选择日期"
            v-model="form.date1"
            style="width: 100%;"
          ></el-date-picker>
        </el-form-item>
      </el-col>
      <el-col
        class="line"
        :span="2"
        style="width: 5px;margin-right: 5px;margin-left: 5px"
      >
        -
      </el-col>
      <el-col :span="11">
        <el-form-item prop="date2">
          <el-time-picker
            placeholder="选择时间"
            v-model="form.date2"
            style="width: 100%;"
          ></el-time-picker>
        </el-form-item>
      </el-col>
    </el-form-item>

    <el-form-item label="服务次数" prop="money">
      <el-slider
        :max="max"
        :min="min"
        v-model="form.times"
        show-input
      ></el-slider>
    </el-form-item>

    <el-form-item label="服务员工" prop="staff">
      <el-input
        class="elinput"
        v-model="form.staff"
        placeholder="请输入员工工号"
        type="number"
      ></el-input>
    </el-form-item>

    <el-form-item>
      <el-button style="float: right" type="primary" @click="onSubmit('form')"
        >用户扣费</el-button
      >
    </el-form-item>
  </el-form>
</template>

<script>
import axios from "axios";
import qs from "qs";

export default {
  name: "MoneyPay",
  data() {
    return {
      cnoList: [],
      serviceList: [],
      max: 5,
      min: 1,
      form: {
        uid: "",
        service: "",
        date1: "",
        date2: "",
        times: 0,
        staff: ""
      },
      //表单校验规则
      rules: {
        uid: [{ required: true, message: "请选择用户 ID", trigger: "change" }],
        service: [
          { required: true, message: "请选择服务名称", trigger: "change" }
        ],
        date1: [
          {
            type: "date",
            required: true,
            message: "请选择日期",
            trigger: "change"
          }
        ],
        date2: [
          {
            type: "date",
            required: true,
            message: "请选择时间",
            trigger: "change"
          }
        ],
        staff: [
          {
            required: true,
            min: 1,
            max: 15,
            message: "请填写员工号，小于 15 个字符",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    getCnoList() {
      let that = this;
      axios
        // eslint-disable-next-line no-undef
        .get(hxf_conf.BaseUrl + "/api/listmembers")
        .then(response => {
          that.cnoList = response.data;
          console.log("获取新用户列表成功");
          // console.log(that.serviceList);
        })
        .catch(function(error) {
          try {
            if (error.response.status === 405) {
              console.log("子组件收到 405");
            } else {
              console.log("获取用户列表失败：", error);
              that.$message({
                showClose: true,
                message: "服务器内部错误或者服务异常，请检查： " + error,
                offset: 66,
                type: "warning"
              });
            }
          } catch (e) {
            console.log("获取用户列表失败：", error);
            that.$message({
              showClose: true,
              message: "连接服务器失败，请检查网络： " + error,
              offset: 66,
              type: "warning"
            });
          }
        });
    },
    getSeriveList() {
      let that = this;
      let getform = {
        want: "slist"
      };
      axios
        // eslint-disable-next-line no-undef
        .get(hxf_conf.BaseUrl + "/api/servicemanage?", { params: getform })
        .then(response => {
          that.serviceList = response.data;
          console.log("获取新服务列表成功");
          // console.log(that.serviceList);
        })
        .catch(function(error) {
          try {
            if (error.response.status === 405) {
              console.log("子组件收到 405");
            } else {
              console.log("获取服务列表失败：", error);
              that.$message({
                showClose: true,
                message: "服务器内部错误或者服务异常，请检查： " + error,
                offset: 66,
                type: "warning"
              });
            }
          } catch (e) {
            console.log("获取服务列表失败：", error);
            that.$message({
              showClose: true,
              message: "连接服务器失败，请检查网络： " + error,
              offset: 66,
              type: "warning"
            });
          }
        });
    },
    onSubmit(formName) {
      //这里是表单校验规则
      this.$refs[formName].validate(valid => {
        if (!valid) {
          console.log("error submit!!");
        } else {
          //下面是post请求部分
          let that = this;
          //这里因为后端servlet对json处理我老是调试不好就使用传统参数，需要使用qs模块反序列化为url
          let postform = {
            method: "pay",
            uid: this.form.uid,
            service: this.form.service,
            date: this.form.date2,
            times: this.form.times,
            staff: this.form.staff
          };
          axios
            .post(
              // eslint-disable-next-line no-undef
              hxf_conf.BaseUrl + "/api/moneyapi",
              qs.stringify(postform)
            )
            .then(response => {
              if (response.status == 205) {
                this.$message({
                  showClose: true,
                  message: "错误：未找到用户 ID",
                  offset: 66,
                  type: "warning"
                });
                console.log("保存失败：", response.status);
              } else if (response.status == 206) {
                this.$message({
                  showClose: true,
                  message: "错误：用户余额不足，需要充值",
                  offset: 66,
                  type: "warning"
                });
                console.log("保存失败：", response.status);
              } else if (response.status == 207) {
                this.$message({
                  showClose: true,
                  message: "内部错误：用户扣款失败",
                  offset: 66,
                  type: "error"
                });
                console.log("保存失败：", response.status);
              } else if (response.status != 200) {
                this.$message({
                  showClose: true,
                  message: "警告哦，扣缴失败，请检查服务端和数据库",
                  offset: 66,
                  type: "error"
                });
                console.log("保存失败：", response.status);
              } else {
                this.$message({
                  showClose: true,
                  message: "恭喜你，扣缴成功",
                  offset: 66,
                  type: "success"
                });
                console.log("扣缴成功：", response.status);
              }
            })
            .catch(function(error) {
              try {
                if (error.response.status === 405) {
                  console.log("子组件收到 405");
                } else{
                  console.log("扣缴失败：", error);
                  that.$message({
                    showClose: true,
                    message: "警告哦，扣缴失败，错误原因：" + error,
                    offset: 66,
                    type: "warning"
                  });
                }
              } catch (e) {
                console.log("扣缴失败：", error);
                that.$message({
                  showClose: true,
                  message: "警告哦，扣缴失败，网络错误：" + error,
                  offset: 66,
                  type: "warning"
                });
              }
            })
            .finally(function() {
              //清空表单
              that.form = {
                uid: "",
                service: "",
                date1: "",
                date2: "",
                times: 0,
                staff: ""
              };
              that.resetForm("form");
            });
          console.log("submit!");
          // console.log(postform);
        }
      });
    }
  },
  created() {
    this.form.staff = this.isadmin = JSON.parse(this.$store.state.Authorization).eid;
    this.getCnoList();
    setTimeout(() => {
      // 延迟加载
      this.getSeriveList();
    }, 600);
  }
};
</script>

<style scoped></style>
<style>
.elinput input {
  /*height: 40px!important;*/
  /*padding: 4px 0px!important;*/
  line-height: 1 !important;
}
</style>
