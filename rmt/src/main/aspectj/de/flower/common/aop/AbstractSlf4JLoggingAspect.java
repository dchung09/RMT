package de.flower.common.aop;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

/**
 * @author oblume
 * @NotThreadSafe Uses unsynchronized and not thread context bound stack to
 * trace start time in #logEnter.
 */
// TODO enable again
// @NotThreadSafe
@Aspect
public abstract class AbstractSlf4JLoggingAspect extends AbstractLoggingAspect {

    private int depth = 1;

    private ArrayStack stack = new ArrayStack();

    @Override
    protected void logEnter(JoinPoint jp, boolean indent) {
        Logger log = getLogger(jp.getStaticPart());
        // public method -> debug, private -> trace
        boolean publicMethod = isPublicMethod(jp.getStaticPart());
        boolean logEnabled = (publicMethod) ? log.isDebugEnabled() : log.isTraceEnabled();
        if (logEnabled) {
            String msg;
            msg = (indent == true) ? indent(depth++, ">>") : ">> new ";
            // msg += "<" + jp.getKind();
            msg += " " + jp.getSignature().toShortString();
            String args = null;
            jp.getSignature().toShortString();
            for (Object arg : jp.getArgs()) {
                args = (args == null) ? "" : args + ", ";
                if (arg != null) {
                    args += arg.getClass().getSimpleName() + ": [" + arg.toString() + "]";
                } else {
                    args += "Object: null";
                }
            }
            // push start of execution time onto stack
            if (indent) {
                pushTime();
            }
            msg += (StringUtils.isEmpty(args)) ? "" : "(" + args + ")";
            if (publicMethod == true && indent /* no debug for constructors */) {
                log.debug(msg);
            } else {
                log.trace(msg);
            }
        }
    }

    private boolean isPublicMethod(JoinPoint.StaticPart jp) {
        return Modifier.isPublic(jp.getSignature().getModifiers());
    }


    @Override
    protected void logExit(JoinPoint.StaticPart jp) {
        Logger log = getLogger(jp);
        boolean publicMethod = isPublicMethod(jp);
        boolean logEnabled = (publicMethod) ? log.isDebugEnabled() : log.isTraceEnabled();
        if (logEnabled) {
            String msg;
            msg = indent(--depth, "<<");
            long time = System.currentTimeMillis() - popTime();
            if (time > 0) {
                msg += " [" + time + " ms]";
            }
            msg += " " + jp.toString();
            if (publicMethod == true) {
                log.debug(msg);
            } else {
                log.trace(msg);
            }
        }
    }

    private void pushTime() {
        stack.push(System.currentTimeMillis());
    }

    private long popTime() {
        Long start = (Long) stack.pop();
        return start;
    }

    /**
     * Creates and returns a logger with a name based on the target object.
     * Allows controlling trace logging through enabling loggers in logging config.
     * <p/>
     * Performance: It is assumed that the underlying log implementation uses a
     * caching mechanism (logback does).
     *
     * @param jp
     * @return
     */
    private Logger getLogger(JoinPoint.StaticPart jp) {
        Class<?> key = jp.getSignature().getDeclaringType();
        return LoggerFactory.getLogger("trace." + key.getName());
    }

    private String indent(int i, CharSequence s) {
        return MyStringBuilder.fixedLengthString(s, i);
    }

    public static class MyStringBuilder {

        public static String fixedLengthString(CharSequence s, int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(s);
            }
            return sb.toString();
        }
    }


}

