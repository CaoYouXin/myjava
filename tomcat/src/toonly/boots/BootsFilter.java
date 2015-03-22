package toonly.boots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.configer.watcher.ChangeWatcherSupport;
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static toonly.boots.ServletUser._login;


/**
 * Created by caoyouxin on 15-2-19.
 */
@WebFilter(filterName = "boots&shutdown", urlPatterns = "/*")
public class BootsFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(BootsFilter.class);
    private Properties _configer;
    private List<String> _matchers;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("boot ...");
        Feature.set(0);
        this._configer = this.getConfger();
        this._matchers = Arrays.asList(this._configer.getProperty("blocks", "/blocks").split("\\|\\|"));
        Debugger.debugRun(this, () -> {
            log.info("blocks : {}", Arrays.toString(this._matchers.toArray()));
            _login("test", "S");
        });
        log.info("boot done");
    }

    private Properties getConfger() {
        PropsConfiger propsConfiger = new PropsConfiger();
        try {
            return propsConfiger.cache("redirect.prop");
        } catch (UncachedException e) {
            return propsConfiger.config("redirect.prop");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /**
         * 设置编码
         */
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");

        /**
         * 这么强制转型，是因为它是Tomcat
         */
        HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest) servletRequest);
        StringWrapper stringWrapper = new StringWrapper(request.getRequestURL().toString()).toRootPath();

        /**
         * 保护《某些》目录
         */
        if (stringWrapper.matchFrom0(this._matchers)) {
            redirect(servletResponse, "block_page", "/503.html");
            return;
        }

        /**
         * 创建临时用户状态对象
         */
        ServletUser user = new ServletUser(servletRequest);

        /**
         * 升级数据库操作
         */
        if (stringWrapper.matchFrom0("/init.do")) {
            boolean b = ReposManager.getInstance().makeUpToDate();
            RB ret = new RB().put("suc", b);
            FlagMapper.sendResponse((HttpServletResponse) servletResponse, ret);
            if (b) {
                user.logout();
            }
            return;
        }

        /**
         * 将用户名传给jsp
         */
        if (stringWrapper.val().endsWith(".jsp")
                || stringWrapper.matchFrom0("/api/v1")) {
            servletRequest.setAttribute("un", user.getUserName());
        }

        /**
         * 普通请求
         */
        if (user.isNormalRequest()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        /**
         * 系统调试阶段，只有管理员可以进入
         */
        if (SysStatus.isDebugging() && !user.isAdmin()) {
            redirect(servletResponse, "block_page", "/503.html");
            return;
        }

        /**
         * 注销操作
         */
        if (stringWrapper.matchFrom0("/logout.do") && user.logout()) {
            redirect(servletResponse, "login_page", "/login.html");
            return;
        }

        /**
         * 登录操作
         */
        if (stringWrapper.matchFrom0("/login.do")) {
            if (user.login()) {
                if (user.isAdmin()) {
                    redirect(servletResponse, "admin_home", "/index.jsp#stuff");
                } else {
                    redirect(servletResponse, "home_page", "/index.jsp");
                }
            } else {
                if (user.isNeedInit()) {
                    redirect(servletResponse, "init_page", "/init.html");
                } else {
                    redirect(servletResponse, "login_fail", "/login.html#fail");
                }
            }
            return;
        }

        /**
         * 是否登录状态
         */
        if (user.isLogin())
            filterChain.doFilter(servletRequest, servletResponse);
        else
            redirect(servletResponse, "login_page", "/login.html");
    }

    private void redirect(ServletResponse servletResponse, String page, String defaultPage) throws IOException {
        HttpServletResponse response = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        response.sendRedirect(_configer.getProperty(page, defaultPage));
    }

    @Override
    public void destroy() {
        log.info("shutdown ...");
        BugReporter.closeClient();
        DB.instance().close();
        ChangeWatcherSupport.stopWatching();
        log.info("shutdown done");
    }
}
