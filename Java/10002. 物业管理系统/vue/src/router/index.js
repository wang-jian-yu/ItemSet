import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    // 首页重定向到登录
    // eslint-disable-next-line no-unused-vars
    redirect: to =>{
      return "/login"
    },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login")
  },
  {
    path: "/user",
    name: "User",
    meta: {
      needLogin: true
    },
    component: () => import("../views/User")
  },
  {
    //实现动态路由，根据参数进入不同的控制面板
    path: "/dashboard/:path",
    name: "DashBoard",
    meta: {
      needLogin: true
    },
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/DashBoard.vue")
  }
];

const router = new VueRouter({
  routes
});

// 导航守卫
// 使用 router.beforeEach 注册一个全局前置守卫，判断用户是否登陆
router.beforeEach((to, from, next) => {
  //所有设置meta的，needlogin为true的都守卫
  if (to.meta.needLogin == true) {
    let auth = localStorage.getItem("Authorization");
    let token = null;
    if (auth == null) {
      next("/login");
    } else {
      token = JSON.parse(auth).token;
      console.log("router取得的token为：" + token);
    }

    if (token == null || token == "") {
      next("/login");
    } else {
      next();
    }
  } else {
    next();
  }
  // if (to.path === "/login") {
  //   if (localStorage.getItem("Authorization")) {
  //     next();
  //     // next("/dashboard/show");
  //   } else {
  //     next();
  //   }
  // }
});

export default router;
