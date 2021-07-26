package toonly.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Function;

/**
 * Created by caoyouxin on 15-2-21.
 */
public class StringWrapper extends SW<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringWrapper.class);
    public static final String HTTPS = "https://"; //hack！"http://"的情况下，第一个字符也不可能是'/'

    public StringWrapper(String str) {
        super(str);
    }

    public StringWrapper() {
    }

    public StringWrapper wrapJoin(List<? extends Object> list, String wrap, String sep) {
        if (list.isEmpty()) {
            return (StringWrapper) this.val("");
        }
        StringBuilder sb = new StringBuilder();
        list.forEach(o -> sb.append(sep).append(String.format("%s%s%s", wrap, o.toString(), wrap)));
        return (StringWrapper) this.val(sb.substring(sep.length()).toString());
    }

    public StringWrapper replaceJoin(List<? extends Object> list, String rep, String sep) {
        if (list.isEmpty()) {
            return (StringWrapper) this.val("");
        }
        StringBuilder sb = new StringBuilder();
        list.forEach(o -> sb.append(sep).append(rep));
        return (StringWrapper) this.val(sb.substring(sep.length()).toString());
    }

    public StringWrapper unwrap() {
        return this.unwrap(1);
    }

    public StringWrapper unwrap(int len) {
        String val = this.val();
        int length = val.length();
        if (len * 2 > length) {
            throw new SourceCorruptedException("source is too short.");
        }
        this.val(val.substring(len, length - len));
        return this;
    }

    public StringWrapper wrap(String pre, String post) {
        this.val(pre + this.val() + post);
        return this;
    }

    public StringWrapper md5Len32() {
        return this.md5(sb -> sb.toString());
    }

    public StringWrapper md5Len16() {
        return this.md5(sb -> sb.toString().substring(8, 24));
    }

    private StringWrapper md5(Function<StringBuilder, String> fn) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(this.val().getBytes());
            byte[] b = md.digest();

            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            this.val(fn.apply(sb));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("There is no MD5 algorithm.");
        }
        return this;
    }

    public StringWrapper toUpPath() {
        String property = System.getProperty("file.separator");
        String[] split = this.val().split("\\.");
        StringBuilder sb = new StringBuilder();
        int i = split.length;
        while (i-- > 0) {
            sb.append("..").append(property);
        }
        return (StringWrapper) this.val(sb.toString());
    }

    public StringWrapper toRootPath() {
        this.val(this.val().substring(this.val().indexOf('/', HTTPS.length())));
        return this;
    }

    public StringWrapper toRootPath(String find) {
        return this.toRootPath(find, str -> str.startsWith("/") ? str : '/' + str);
    }

    public StringWrapper toRootPathQuickly(String find) {
        return this.toRootPath(find, str -> str);
    }

    private StringWrapper toRootPath(String find, Function<String, String> fn) {
        String substring = this.val().substring(find.length() + this.val().indexOf(find, HTTPS.length()));
        this.val(fn.apply(substring));
        return this;
    }

    public boolean matchFrom0(List<String> matchers) {
        for (String matcher : matchers) {
            if (this.val().startsWith(matcher)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchFrom0(String matcher) {
        if (this.val().startsWith(matcher)) {
            return true;
        }
        return false;
    }

    public boolean endWith(List<String> matchers) {
        for (String matcher : matchers) {
            if (this.val().endsWith(matcher)) {
                return true;
            }
        }
        return false;
    }
}
