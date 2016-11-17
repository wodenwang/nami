package com.riversoft.nami;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.nami.session.SessionManager;
import com.riversoft.util.JsonMapper;
import com.riversoft.weixin.common.exception.WxRuntimeException;

/**
 * Nami会话管理servlet
 * 
 * @author woden
 */
@SuppressWarnings("serial")
@WebServlet(description = "NAMI会话管理", urlPatterns = { "/login.nami", "/unionid.nami" })
public class SessionServlet extends HttpServlet {

	static Logger logger = LoggerFactory.getLogger(SessionServlet.class);

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, Object> result;
		String servletPath = request.getServletPath();
		switch (servletPath) {
		case "/login.nami":
			result = login(request, response);
			break;
		case "/unionid.nami":
			result = saveUnionId(request, response);
			break;
		default:
			result = new HashMap<>();
			result.put("msg", "请求路径出错");
			response.setStatus(299);
			break;
		}

		response.setContentType("application/json;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println(JsonMapper.defaultMapper().toJson(result));
		out.flush();
		out.close();
	}

	private Map<String, Object> login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> result = new HashMap<>();
		try {
			String code = request.getParameter("code");
			String namiKey = SessionManager.jscode2session(code);
			result.put("key", namiKey);
		} catch (WxRuntimeException e) {
			logger.error("调用微信登录接口出错", e);
			response.setStatus(299);
			result.put("msg", e.getMessage());
		}

		return result;
	}

	private Map<String, Object> saveUnionId(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> result = new HashMap<>();
		// TODO
		String rawData = request.getParameter("rawData");
		String signature = request.getParameter("signature");
		String namiKey = request.getParameter("namiKey");

		SessionManager.saveUnionId(namiKey, rawData, signature);

		result.put("msg", "已获取UNION_ID");
		return result;
	}

}
