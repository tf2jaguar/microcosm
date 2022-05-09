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
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import io.github.tf2jaguar.micro.core.input.IgnoreRequestInput;
import io.github.tf2jaguar.micro.core.input.InputMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
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
 * 拦截参数解析，生成swagger与InputMessgae格式一致
 *
 * @author hobo
 * @since 2018/6/9 下午4:32
 */
@Aspect
public class ParamsResolverAspect {

    private static final Logger logger = LoggerFactory.getLogger(ParamsResolverAspect.class);

    private static final String SPRING4_DISCOVERER = "org.springframework.core.DefaultParameterNameDiscoverer";
    private final ParameterNameDiscoverer parameterNameDiscover = parameterNameDiscoverer();
    @Autowired
    private TypeResolver typeResolver;
    private Map<Class, List<ResolvedMethod>> methodsResolvedForHostClasses = new HashMap<Class, List<ResolvedMethod>>();

    @Pointcut("(execution(public * springfox.documentation.spring.web.readers.operation.HandlerMethodResolver.methodParameters(..) ))")
    public void methodParameters() {

    }

    /**
     * 处理参数
     *
     * @param pjp pjp
     * @return object 执行结果
     * @throws Throwable throwable 类型异常
     */
    @Around("methodParameters()")
    public Object doAfterReturning(ProceedingJoinPoint pjp) throws Throwable {
        List<ResolvedMethodParameter> result = new ArrayList<>();
        Object[] objs = pjp.getArgs();
        Object object = pjp.proceed();
        HandlerMethod methodToResolve = (HandlerMethod) objs[0];
        object = methodParameters(methodToResolve);
        return object;
    }

    public ResolvedType methodReturnType(HandlerMethod handlerMethod) {
        return resolvedMethod(handlerMethod).transform(toReturnType(typeResolver)).or(typeResolver.resolve(Void.TYPE));
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

    public List<ResolvedMethodParameter> methodParameters(final HandlerMethod methodToResolve) {
        return resolvedMethod(methodToResolve)
                .transform(toParameters(methodToResolve))
                .or(Lists.<ResolvedMethodParameter>newArrayList());
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
        return input -> Optional.fromNullable(input.getReturnType()).or(resolver.resolve(Void.TYPE));
    }

    private Function<ResolvedMethod, List<ResolvedMethodParameter>> toParameters(final HandlerMethod methodToResolve) {
        return input -> {
            List<ResolvedMethodParameter> parameters = newArrayList();
            MethodParameter[] methodParameters = methodToResolve.getMethodParameters();
            for (int i = 0; i < input.getArgumentCount(); i++) {
                MethodParameter methodParameter = methodParameters[i];
                ResolvedType parameterType = input.getArgumentType(i);

                if (needInputMessageFormat(methodParameter)) {
                    parameterType = typeResolver.resolve(InputMessage.class, parameterType);
                }

                parameters.add(new ResolvedMethodParameter(
                        discoveredName(methodParameters[i]).or(String.format("param%s", i)),
                        methodParameter,
                        parameterType));
            }
            return parameters;
        };
    }

    private boolean needInputMessageFormat(MethodParameter methodParameter) {
        RequestMapping requestMapping = methodParameter.getMethodAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return false;
        }
        RequestMethod[] requestMethods = requestMapping.method();
        boolean post = Arrays.stream(requestMethods).anyMatch(e -> e == RequestMethod.POST);
        if (!post) {
            return false;
        }
        if (methodParameter.getParameterType() == InputMessage.class) {
            return false;
        }
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreRequestInput.class)) {
            return false;
        }
        return methodParameter.hasParameterAnnotation(RequestBody.class)
                || methodParameter.hasParameterAnnotation(RequestPart.class);
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
}
