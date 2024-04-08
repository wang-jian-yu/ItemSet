<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="head">
			<div class="top">
				<div class="container">
					<div class="pull-right">|
						<a href="#">服务中心</a>
						<a href="#">网站地图</a>
						</div>
					
					<div class="pull-right">
						<c:choose>
							<c:when test="${empty landing}">
								<div class="top-right">
									HI~
									<a href="jsp/book/reg.jsp?type=login">[登录]</a>
									<a href="jsp/book/reg.jsp?type=reg">[免费注册]</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="btn-group adminName top-right">
									<a href="javascript:void(0)">
									    ${landing.name} <span class="caret"></span>
									</a>
									<ul class="dropdown-menu dropdown-menu-right">
									    <li><a href="OrderServlet?action=list" >我的订单</a></li>
									    <li><a href="#">我的资料</a></li>
									    <li><a style="border-top:1px #ccc solid" href="UserServlet?action=off" onClick="return confirm('确定要退出登陆了么？')">退 出 登 录</a></li>
									</ul>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
			<div class="mid container">
				<div class="row">
					<a class="logo col-md-5" href="jsp/book/index.jsp" title="shine网上书城">
						<img alt="" src="images/logo.png">
						<span>shine网上书城</span>
					</a>
					<div class="search col-md-4">
						<div class="input-group">
	     	 				<input type="text" class="form-control" placeholder="输入要搜索的图书...">
	      					<span class="input-group-btn">
	       						<button class="btn btn-default" type="button">Go!</button>
	      					</span>
   						</div>
					</div>
					<div class="shopcart col-md-2 col-md-offset-1">
						<a id="cart" href="jsp/book/cart.jsp">
							<span class="badge num">
								<c:choose>
									<c:when test="${!empty shopCart}">
										${shopCart.getTotQuan()}
									</c:when>
									<c:otherwise>
										0
									</c:otherwise>
								</c:choose>
							</span>
							<span class="glyphicon glyphicon-shopping-cart" aria-hidden="true"></span>
							<span>购物车</span>
							<span class="glyphicon glyphicon-menu-right" aria-hidden="true"></span>
						</a>
					</div>
				</div>
				<div class="navbar">
					<ul class="nav navbar-nav">
				        <li class="active"><a href="jsp/book/index.jsp">首 页 <span class="sr-only">(current)</span></a></li>
						<li><a href="#">分 类</a></li>
						<li><a href="#">新 品</a></li>
						<li><a href="#">特 惠</a></li>
						<li><a href="#">热销榜</a></li>
			      	</ul>
				</div>
			</div>
		</div>