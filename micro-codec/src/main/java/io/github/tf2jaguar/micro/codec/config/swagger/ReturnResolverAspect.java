package io.github.tf2jaguar.micro.codec.config.swagger;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import io.github.tf2jaguar.micro.core.output.IgnoreResponseResult;
import io.github.tf2jaguar.micro.core.output.OutputMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.service.ResolvedMethodParameter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

/**
 * swagger的请求响应体封装
 *
 * @author hobo
 * @since 2018/6/15 下午1:28
 */
@Aspect
public class ReturnResolverAspect {

    private static final String SPRING4_DISCOVERER = "org.springframework.core.DefaultParameterNameDiscoverer";
    private final ParameterNameDiscoverer parameterNameDiscover = parameterNameDiscoverer();
    @Autowired
    private TypeResolver typeResolver;
    private Map<Class, List<ResolvedMethod>> methodsResolvedForHostClasses = new HashMap<Class, List<ResolvedMethod>>();

    @Pointcut("(execution(public * springfox.documentation.spring.web.readers.operation.HandlerMethodResolver.methodReturnType(..) ))")
    public void methodReturnType() {

    }

    /**
     * 处理参数
     *
     * @param pjp pjp
     * @return object 对象
     * @throws Throwable throwable 类型异常
     */
    @Around("methodReturnType()")
    public Object doAfterReturning(ProceedingJoinPoint pjp) throws Throwable {
        List<ResolvedMethodParameter> result = new ArrayList<>();
        Object[] objs = pjp.getArgs();
        Object object = pjp.proceed();
        HandlerMethod methodToResolve = (HandlerMethod) objs[0];
        object = methodReturnType(methodToResolve);
        return object;
    }

    public ResolvedType methodReturnType(HandlerMethod handlerMethod) {
        ResolvedType resolvedType = resolvedMethod(handlerMethod).transform(toReturnType(typeResolver)).or(typeResolver.resolve(Void.TYPE));
        if (needResponseFormat(handlerMethod, resolvedType)) {
            resolvedType = typeResolver.resolve(OutputMessage.class, resolvedType);
        }
        return resolvedType;
    }

    public static Optional<Class> useType(Class beanType) {
        if (Proxy.class.isAssignableFrom(beanType)) {
            return Optional.absent();
        }
        if (Class.class.getName().equals(beanType.getName())) {
            return Optional.absent();
        }
        return Optional.fromNullable(beanType);
    }


    boolean contravariant(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
        return isSubClass(candidateMethodReturnValue, returnValueOnMethod)
                || isGenericTypeSubclass(candidateMethodReturnValue, returnValueOnMethod);
    }


    @VisibleForTesting
    static Ordering<ResolvedMethod> byArgumentCount() {
        return Ordering.from(new Comparator<ResolvedMethod>() {
            @Override
            public int compare(ResolvedMethod first, ResolvedMethod second) {
                return Ints.compare(first.getArgumentCount(), second.getArgumentCount());
            }
        });
    }

    @VisibleForTesting
    boolean bothAreVoids(ResolvedType candidateMethodReturnValue, Type returnType) {
        return (Void.class == candidateMethodReturnValue.getErasedType()
                || Void.TYPE == candidateMethodReturnValue.getErasedType())
                && (Void.TYPE == returnType
                || Void.class == returnType);
    }

    @VisibleForTesting
    boolean isGenericTypeSubclass(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
        return returnValueOnMethod instanceof ParameterizedType &&
                candidateMethodReturnValue.getErasedType()
                        .isAssignableFrom((Class<?>) ((ParameterizedType) returnValueOnMethod).getRawType());
    }

    @VisibleForTesting
    boolean isSubClass(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
        return returnValueOnMethod instanceof Class
                && candidateMethodReturnValue.getErasedType().isAssignableFrom((Class<?>) returnValueOnMethod);
    }

    @VisibleForTesting
    boolean covariant(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
        return isSuperClass(candidateMethodArgument, argumentOnMethod)
                || isGenericTypeSuperClass(candidateMethodArgument, argumentOnMethod);
    }

    @VisibleForTesting
    boolean isGenericTypeSuperClass(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
        return argumentOnMethod instanceof ParameterizedType &&
                ((Class<?>) ((ParameterizedType) argumentOnMethod).getRawType())
                        .isAssignableFrom(candidateMethodArgument.getErasedType());
    }

    @VisibleForTesting
    boolean isSuperClass(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
        return argumentOnMethod instanceof Class
                && ((Class<?>) argumentOnMethod).isAssignableFrom(candidateMethodArgument.getErasedType());
    }

    private Optional<ResolvedMethod> resolvedMethod(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return Optional.absent();
        }
        Class hostClass = useType(handlerMethod.getBeanType())
                .or(handlerMethod.getMethod().getDeclaringClass());
        Iterable<ResolvedMethod> filtered = filter(getMemberMethods(hostClass),
                methodNamesAreSame(handlerMethod.getMethod()));
        return resolveToMethodWithMaxResolvedTypes(filtered, handlerMethod.getMethod());
    }

    private List<ResolvedMethod> getMemberMethods(
            Class hostClass) {
        if (!methodsResolvedForHostClasses.containsKey(hostClass)) {
            ResolvedType beanType = typeResolver.resolve(hostClass);
            MemberResolver resolver = new MemberResolver(typeResolver);
            resolver.setIncludeLangObject(false);
            ResolvedTypeWithMembers typeWithMembers = resolver.resolve(beanType, null, null);
            methodsResolvedForHostClasses.put(hostClass, newArrayList(typeWithMembers.getMemberMethods()));
        }
        return methodsResolvedForHostClasses.get(hostClass);
    }

    private static Function<ResolvedMethod, ResolvedType> toReturnType(final TypeResolver resolver) {
        return new Function<ResolvedMethod, ResolvedType>() {
            @Override
            public ResolvedType apply(ResolvedMethod input) {
                return Optional.fromNullable(input.getReturnType()).or(resolver.resolve(Void.TYPE));
            }
        };
    }

    private static Iterable<ResolvedMethod> methodsWithSameNumberOfParams(
            Iterable<ResolvedMethod> filtered,
            final Method methodToResolve) {

        return filter(filtered, new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                return input.getArgumentCount() == methodToResolve.getParameterTypes().length;
            }
        });
    }

    private static Predicate<ResolvedMethod> methodNamesAreSame(final Method methodToResolve) {
        return new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                return input.getRawMember().getName().equals(methodToResolve.getName());
            }
        };
    }

    private Optional<ResolvedMethod> resolveToMethodWithMaxResolvedTypes(
            Iterable<ResolvedMethod> filtered,
            Method methodToResolve) {
        if (Iterables.size(filtered) > 1) {
            Iterable<ResolvedMethod> covariantMethods = covariantMethods(filtered, methodToResolve);
            if (Iterables.size(covariantMethods) == 0) {
                return FluentIterable.from(filtered)
                        .firstMatch(sameMethod(methodToResolve));
            } else if (Iterables.size(covariantMethods) == 1) {
                return FluentIterable.from(covariantMethods).first();
            } else {
                return Optional.of(byArgumentCount().max(covariantMethods));
            }
        }
        return FluentIterable.from(filtered).first();
    }

    private Predicate<ResolvedMethod> sameMethod(final Method methodToResolve) {
        return new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                return methodToResolve.equals(input.getRawMember());
            }
        };
    }

    private Iterable<ResolvedMethod> covariantMethods(
            Iterable<ResolvedMethod> filtered,
            final Method methodToResolve) {

        return filter(methodsWithSameNumberOfParams(filtered, methodToResolve), onlyCovariantMethods(methodToResolve));
    }

    private Predicate<ResolvedMethod> onlyCovariantMethods(final Method methodToResolve) {
        return new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                for (int index = 0; index < input.getArgumentCount(); index++) {
                    if (!covariant(input.getArgumentType(index), methodToResolve.getGenericParameterTypes()[index])) {
                        return false;
                    }
                }
                ResolvedType candidateMethodReturnValue = returnTypeOrVoid(input);
                return bothAreVoids(candidateMethodReturnValue, methodToResolve.getGenericReturnType())
                        || contravariant(candidateMethodReturnValue, methodToResolve.getGenericReturnType());
            }
        };
    }

    private ResolvedType returnTypeOrVoid(ResolvedMethod input) {
        ResolvedType returnType = input.getReturnType();
        if (returnType == null) {
            returnType = typeResolver.resolve(Void.class);
        }
        return returnType;
    }


    private Optional<String> discoveredName(MethodParameter methodParameter) {
        String[] discoveredNames = parameterNameDiscover.getParameterNames(methodParameter.getMethod());
        int discoveredNameCount = Optional.fromNullable(discoveredNames).or(new String[0]).length;
        return methodParameter.getParameterIndex() < discoveredNameCount
                ? Optional.fromNullable(emptyToNull(discoveredNames[methodParameter.getParameterIndex()]))
                : Optional.fromNullable(methodParameter.getParameterName());
    }


    private ParameterNameDiscoverer parameterNameDiscoverer() {
        ParameterNameDiscoverer discoverer;
        try {
            discoverer = (ParameterNameDiscoverer) Class.forName(SPRING4_DISCOVERER).newInstance();
        } catch (Exception e) {
            discoverer = new LocalVariableTableParameterNameDiscoverer();
        }
        return discoverer;
    }

    private boolean needResponseFormat(HandlerMethod handlerMethod, ResolvedType resolvedType) {
        RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return false;
        }
        RequestMethod[] requestMethods = requestMapping.method();
        boolean post = Arrays.stream(requestMethods).anyMatch(e -> {
            return e == RequestMethod.POST;
        });
        if (!post) {
            return false;
        }
        Class clazz = resolvedType.getErasedType();
        if (OutputMessage.class.isAssignableFrom(clazz)) {
            return false;
        }
        return !handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(IgnoreResponseResult.class);
    }
}
