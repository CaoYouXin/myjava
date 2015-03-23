package toonly.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.appobj.AppFactory;
import toonly.appobj.UnPermissioned;
import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.dbmanager.base.Jsonable;
import toonly.dbmanager.lowlevel.RS;
import toonly.debugger.Debugger;
import toonly.mapper.ret.RB;
import toonly.mapper.ret.RBArray;
import toonly.repos.ReposManager;
import toonly.wrapper.Bool;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by caoyouxin on 15-2-25.
 */
@WebServlet(name = "flag_mapper", urlPatterns = { "/api/v1/*" })
public class FlagMapper extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagMapper.class);
    private static final String CONFIG_FILE_NAME = "redirect.prop";
    private ThreadLocal<String> line1 = new ThreadLocal<>();
    private Properties redirectConfiger = new PropsConfiger().cache(CONFIG_FILE_NAME);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ReposManager.getInstance().isUpToDate()) {
            resp.sendRedirect(this.redirectConfiger.getProperty("init_page", "/init.html"));
            return;
        }

        /**
         * 读取数据
         */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        line1.set(bufferedReader.readLine());
        bufferedReader.close();

        /**
         * 打印调试信息
         */
        Debugger.debugExRun(this, () -> this.printRequest(req));

        /**
         * 解析path
         */
        String[] info = req.getPathInfo().split("\\/");
        Debugger.debugRun(this, () -> LOGGER.info("info : {}", Arrays.toString(info)));

        /**
         * 实例化对象
         */
        Object app = AppFactory.instance.getAppObject(info[2]);

        /**
         * 注入值
         */
        boolean constructed = false;
        if (app instanceof Jsonable) {
            Jsonable jsonable = (Jsonable) app;
            constructed = jsonable.fromJson(null == line1.get() ? "" : line1.get());
        }
        if (!constructed && app instanceof ParamConstructable) {
            ParamConstructable paramConstructable = (ParamConstructable) app;
            constructed = paramConstructable.construct(req.getParameterMap());
        }

        /**
         * 执行调用
         */
        Object invokeRet = AppFactory.instance.invokeMethod(req.getAttribute("un").toString(), app, info[3]);
        if (null != invokeRet) {
            this.ret(resp, info[1], invokeRet);
            return;
        }
        sendResponse(resp, this.buildRB(false, "app 执行调用返回null", null));
    }

    private void ret(HttpServletResponse resp, String info, Object invokeRet) throws IOException {
        if (null == invokeRet) {
            sendResponse(resp, this.buildRB(false, "app object produced null ret", null));
        } else if (invokeRet instanceof UnPermissioned) {
            sendResponse(resp, this.buildRB(false, "unpermissioned", null));
        }

        switch (info) {
            case "entity":
                this.handleEntity(resp, invokeRet);
                break;
            case "func":
                this.handleFunction(resp, invokeRet);
                break;
            default:
                sendResponse(resp, this.buildRB(false, "nonsense", null));
        }
    }

    private void handleFunction(HttpServletResponse resp, Object invokeRet) throws IOException {
        if (invokeRet instanceof Boolean) {
            sendResponse(resp, this.buildRB((boolean) invokeRet, null, null));
        } else {
            sendResponse(resp, this.buildRB(false, "nonsense", null));
        }
    }

    private void handleEntity(HttpServletResponse resp, Object invokeRet) throws IOException {
        if (invokeRet instanceof Boolean) {
            sendResponse(resp, this.buildRB((boolean) invokeRet, null, null));
        } else if (invokeRet instanceof RS) {
            sendResponse(resp, this.buildRB((RS) invokeRet));
        } else {
            sendResponse(resp, this.buildRB(false, "nonsense", null));
        }
    }

    private RB buildRB(RS invokeRet) {
        RB ret = new RB();

        if (invokeRet.isEmpty())
            return ret.put("suc", Bool.FALSE.toString());

        RBArray array = new RBArray();
        while (invokeRet.next()) {
            RB rb = new RB();
            invokeRet.forEach((key, value) -> rb.put(key, value.toString()));
            array.add(rb);
        }
        return ret.put("suc", Bool.TRUE.toString()).put("data", array);
    }

    private RB buildRB(boolean suc, String problem, Exception e) {
        RB ret = new RB();

        if (suc) {
            return ret.put("suc", Bool.TRUE.toString());
        } else if (null != e) {
            return ret.put("suc", Bool.FALSE.toString()).put("problem", String.format("%s %s", problem, e.getMessage()));
        } else {
            return ret.put("suc", Bool.FALSE.toString()).put("problem", problem);
        }
    }

    public static void sendResponse(HttpServletResponse resp, RB ret) throws IOException {
        resp.addHeader("Content-Type", "application/json;charset=UTF-8");
        ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.write(ret.toJson().getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private void printRequest(HttpServletRequest req) throws IOException {
        req.setCharacterEncoding("UTF-8");

        LOGGER.info("servlet path : {}", req.getServletPath());
        LOGGER.info("servlet content type : {}", req.getContentType());
        LOGGER.info("query string : {}", req.getQueryString());
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String arg = headerNames.nextElement();
            LOGGER.info("header[{} : {}]", arg, req.getHeader(arg));
        }

        Cookie[] cookies = req.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                LOGGER.info("cookie name : {}", cookie.getName());
                LOGGER.info("cookie path : {}", cookie.getPath());
                LOGGER.info("cookie domain : {}", cookie.getDomain());
                LOGGER.info("cookie value : {}", cookie.getValue());
                LOGGER.info("cookie comment : {}", cookie.getComment());
            }
        }

        Enumeration<String> attributeNames = req.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String arg = attributeNames.nextElement();
            LOGGER.info("attr[{} : {}]", arg, req.getAttribute(arg));
        }

        req.getParameterMap().forEach((parameterName, parameters) ->
            LOGGER.info("param[{} :({})]", parameterName, Arrays.toString(parameters))
        );

        LOGGER.info("line({}) : {}", 1, line1.get());
        LOGGER.info("==========华丽的分割线==========");
    }

}
