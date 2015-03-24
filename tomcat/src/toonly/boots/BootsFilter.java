package toonly.boots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.appobj.InvokeAppError;
import toonly.appobj.UnPermissioned;
import toonly.configer.PropsConfiger;
import toonly.configer.watcher.WatchServiceWrapper;
import toonly.dbmanager.lowlevel.DB;
import toonly.debugger.BugReporter;
import toonly.debugger.Debugger;
import toonly.debugger.Feature;
import toonly.mapper.FlagMapper;
import toonly.mapper.ret.RB;
import toonly.repos.ReposManager;
import toonly.wrapper.StringWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static toonly.boots.ServletUser._login;
import static toonly.mapper.ret.RB.RB_KEY_PROBLEM;
import static toonly.mapper.ret.RB.RB_KEY_SUC;


/**
 * Created by caoyouxin on 15-2-19.
 */
@WebFilter(filterName = "boots&shutdown", urlPatterns = "/*")
public class BootsFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootsFilter.class);
    private static final String CONFIG_FILE_NAME = "redirect.prop";
    private static final String LOGIN_PAGE = "login_page";
    private static final String DEFAULT_LOGIN_PAGE = "/login.html";
    private static final String LOGIN_FAIL_PAGE = "login_fail";
    private static final String DEFAULT_LOGIN_FAIL_PAGE = "/login.html#fail";
    private static final String INIT_PAGE = "init_page";
    private static final String DEFAULT_INIT_PAGE = "/init.html";
    private static final String HOME_PAGE = "home_page";
    private static final String DEFAULT_HOME_PAGE = "/index.jsp";
    private static final String ADMIN_HOME_PAGE = "admin_home";
    private static final String DEFAULT_ADMIN_HOME_PAGE = "/index.jsp#stuff";
    private static final String API_V1 = "/api/v1";
    private Properties configer;
    private List<String> matchers;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("boot ...");
        Feature.set(0);
        this.configer = new PropsConfiger().cache(CONFIG_FILE_NAME);
        this.matchers = Arrays.asList(this.configer.getProperty("blocks", "/blocks").split("\\|\\|"));
        Debugger.debugRun(this, () -> {
            LOGGER.info("blocks : {}", this.matchers);
            _login("test", "S");
        });
        LOGGER.info("boot done");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        StringWrapper stringWrapper = this.getUrl(servletRequest, servletResponse);

        /**
         * 创建临时用户状态对象
         */
        ServletUser user = new ServletUser(servletRequest);

        if (this.block(servletResponse, stringWrapper, user)) {
            return;
        }

        if (this.updateDB((HttpServletResponse) servletResponse, stringWrapper, user)) {
            return;
        }

        if (normal(servletRequest, servletResponse, filterChain, stringWrapper, user)) {
            return;
        }

        if (logOutOrIn(servletResponse, stringWrapper, user)) {
            return;
        }

        /**
         * 是否登录状态
         */
        if (user.isLogin()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            redirect(servletResponse, LOGIN_PAGE, DEFAULT_LOGIN_PAGE);
        }
    }

    private boolean logOutOrIn(ServletResponse servletResponse, StringWrapper stringWrapper, ServletUser user) throws IOException {
        /**
         * 注销操作
         */
        if (stringWrapper.matchFrom0("/logout.do") && user.logout()) {
            redirect(servletResponse, LOGIN_PAGE, DEFAULT_LOGIN_PAGE);
            return true;
        }

        /**
         * 登录操作
         */
        if (stringWrapper.matchFrom0("/login.do")) {
            if (user.login()) {
                if (user.isAdmin()) {
                    redirect(servletResponse, ADMIN_HOME_PAGE, DEFAULT_ADMIN_HOME_PAGE);
                } else {
                    redirect(servletResponse, HOME_PAGE, DEFAULT_HOME_PAGE);
                }
            } else {
                if (user.isNeedInit()) {
                    redirect(servletResponse, INIT_PAGE, DEFAULT_INIT_PAGE);
                } else {
                    redirect(servletResponse, LOGIN_FAIL_PAGE, DEFAULT_LOGIN_FAIL_PAGE);
                }
            }
            return true;
        }
        return false;
    }

    private boolean normal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain, StringWrapper stringWrapper, ServletUser user) throws IOException, ServletException {
        /**
         * 将用户名传给jsp
         */
        if (stringWrapper.val().endsWith(".jsp")
                || stringWrapper.matchFrom0(API_V1)) {
            servletRequest.setAttribute("un", user.getUserName());
        }

        /**
         * 普通请求
         */
        if (user.isNormalRequest()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return true;
        }
        return false;
    }

    private boolean updateDB(HttpServletResponse servletResponse, StringWrapper stringWrapper, ServletUser user) throws IOException {
        /**
         * 升级数据库操作
         */
        if (stringWrapper.matchFrom0("/init.do")) {
            boolean b = false;
            try {
                b = ReposManager.INSTANCE.makeUpToDate(user.getUserName().toString());
            } catch (Exception e) {
                this.updateDBFail(e, servletResponse);
                return true;
            }
            RB ret = new RB().put("suc", b);
            FlagMapper.sendResponse(servletResponse, ret);
            if (b) {
                user.logout();
            }
            return true;
        }
        return false;
    }

    private void updateDBFail(Exception e, HttpServletResponse servletResponse) throws IOException {
        RB ret = new RB().put(RB_KEY_SUC, false);
        if (e instanceof UnPermissioned) {
            FlagMapper.sendResponse(servletResponse, ret.put(RB_KEY_PROBLEM, "unpermissioned"));
        } else if (e instanceof InvokeAppError) {
            servletResponse.sendError(500);
        }
    }

    private boolean block(ServletResponse servletResponse, StringWrapper stringWrapper, ServletUser user) throws IOException {
        HttpServletResponse response = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

        /**
         * 保护《某些》目录
         */
        if (stringWrapper.matchFrom0(this.matchers)) {
            response.sendError(503);
            return true;
        }

        /**
         * 系统调试阶段，只有管理员可以进入
         */
        if (SysStatus.isDebugging() && !user.isAdmin()) {
            response.sendError(503);
            return true;
        }

        return false;
    }

    private StringWrapper getUrl(ServletRequest servletRequest, ServletResponse servletResponse) throws UnsupportedEncodingException {
        /**
         * 设置编码
         */
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");

        /**
         * 这么强制转型，是因为它是Tomcat
         */
        HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest) servletRequest);
        return new StringWrapper(request.getRequestURL().toString()).toRootPath();
    }

    private void redirect(ServletResponse servletResponse, String page, String defaultPage) throws IOException {
        HttpServletResponse response = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        response.sendRedirect(configer.getProperty(page, defaultPage));
    }

    @Override
    public void destroy() {
        LOGGER.info("shutdown ...");
        BugReporter.closeClient();
        DB.instance().close();
        WatchServiceWrapper.INSTANCE.stopWatching();
        LOGGER.info("shutdown done");
    }
}
