package toonly.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.appobj.AppFactory;
import toonly.appobj.InvokeAppError;
import toonly.appobj.UnPermissioned;
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
import java.util.HashSet;
import java.util.Set;

import static toonly.mapper.ret.RB.*;

/**
 * Created by caoyouxin on 15-2-25.
 */
@WebServlet(name = "flag_mapper", urlPatterns = {"/api/v1/*"})
public class FlagMapper extends HttpServlet {

    public static final String CHARSET_NAME = "UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagMapper.class);

    private final ThreadLocal<String> line1 = new ThreadLocal<>();

    public static void sendResponse(HttpServletResponse resp, RB ret) throws IOException {
        resp.addHeader("Content-Type", "application/json;charset=" + CHARSET_NAME);
        ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.write(ret.toJson().getBytes(CHARSET_NAME));
        outputStream.flush();
        outputStream.close();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.checkDBStatus(resp)) {
            return;
        }

        /**
         * 读取数据
         */
        this.readData(req);

        /**
         * 解析path
         */
        String[] info = req.getPathInfo().split("\\/");
        Debugger.debugRun(this, () -> LOGGER.info("info : {}", Arrays.toString(info)));

        /**
         * 实例化对象
         */
        Object app = AppFactory.INSTANCE.getAppObject(info[2]);

        /**
         * 注入值
         */
        this.injectValues(req, app);

        /**
         * 执行调用
         */
        Object invokeRet = AppFactory.INSTANCE.invokeMethod(req.getAttribute("un").toString(), app, info[3]);
        if (null != invokeRet) {
            this.ret(resp, info[1], invokeRet);
            return;
        }
        sendResponse(resp, new RB().put(RB_KEY_EXP, RB_RET_NULL));
    }

    private void readData(HttpServletRequest req) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        line1.set(bufferedReader.readLine());
        bufferedReader.close();

        /**
         * 打印调试信息
         */
        Debugger.debugExRun(this, () -> this.printRequest(req));
    }

    private void injectValues(HttpServletRequest req, Object app) {
        boolean constructed = false;
        if (app instanceof Jsonable) {
            Jsonable jsonable = (Jsonable) app;
            constructed = jsonable.fromJson(null == line1.get() ? "" : line1.get());
        }
        if (!constructed && app instanceof ParamConstructable) {
            ParamConstructable paramConstructable = (ParamConstructable) app;
            paramConstructable.construct(req.getParameterMap());
        }
    }

    private boolean checkDBStatus(HttpServletResponse resp) throws IOException {
        try {
            boolean upToDate = ReposManager.INSTANCE.isUpToDate();
            Debugger.debugRun(this, () -> LOGGER.info("Q:is db up to date? A:{}", upToDate));
            if (!upToDate) {
                sendResponse(resp, new RB().put(RB_KEY_EXP, "dbuninit"));
                return true;
            }
        } catch (Exception e) {
            if (e instanceof UnPermissioned) {
                sendResponse(resp, new RB().put(RB_KEY_EXP, RB_BLOCK));
            } else {
                sendResponse(resp, new RB().put(RB_KEY_EXP, RB_ERROR));
            }
            return true;
        }
        return false;
    }

    private void ret(HttpServletResponse resp, String info, Object invokeRet) throws IOException {
        if (invokeRet instanceof UnPermissioned) {
            sendResponse(resp, new RB().put(RB_KEY_EXP, RB_BLOCK));
        } else if (invokeRet instanceof InvokeAppError) {
            sendResponse(resp, new RB().put(RB_KEY_EXP, RB_ERROR));
        }

        Set<Boolean> booleanSet = new HashSet<>();
        switch (info) {
            case "entity":
                booleanSet.add(this.handleBoolean(resp, invokeRet));
                booleanSet.add(this.handleRS(resp, invokeRet));
                break;
            case "func":
                booleanSet.add(this.handleBoolean(resp, invokeRet));
                break;
            default:
                booleanSet.add(false);
        }
        if (!booleanSet.contains(true)) {
            sendResponse(resp, new RB().put(RB_KEY_EXP, RB_NONSENSE));
        }
    }

    private boolean handleBoolean(HttpServletResponse resp, Object invokeRet) throws IOException {
        if (invokeRet instanceof Boolean) {
            sendResponse(resp, this.buildRB((boolean) invokeRet, null, null));
            return true;
        }
        return false;
    }

    private boolean handleRS(HttpServletResponse resp, Object invokeRet) throws IOException {
        if (invokeRet instanceof RS) {
            RS rs = (RS) invokeRet;

            if (rs.isEmpty()) {
                sendResponse(resp, new RB().put(RB_KEY_EXP, RB_EMPTY));
                return true;
            }

            RB ret = new RB();
            RBArray array = new RBArray();
            while (rs.next()) {
                RB rb = new RB();
                rs.forEach((key, value) -> rb.put(key, value.toString()));
                array.add(rb);
            }
            sendResponse(resp, ret.put(RB_KEY_SUC, Bool.TRUE.toString()).put("data", array));
            return true;
        }
        return false;
    }

    private RB buildRB(boolean suc, String problem, Exception e) {
        RB ret = new RB();

        if (suc) {
            return ret.put(RB_KEY_SUC, Bool.TRUE.toString());
        }
        if (null != problem) {
            if (null != e) {
                return ret.put(RB_KEY_SUC, Bool.FALSE.toString()).put(RB_KEY_PROBLEM, String.format("%s %s", problem, e.getMessage()));
            } else {
                return ret.put(RB_KEY_SUC, Bool.FALSE.toString()).put(RB_KEY_PROBLEM, problem);
            }
        } else {
            return ret.put(RB_KEY_SUC, Bool.FALSE.toString());
        }
    }

    private void printRequest(HttpServletRequest req) throws IOException {
        req.setCharacterEncoding(CHARSET_NAME);

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
