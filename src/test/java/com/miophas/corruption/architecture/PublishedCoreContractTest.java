package com.miophas.corruption.architecture;

import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Existing core types exposed by the API are compatibility contracts, regardless of their package. */
class PublishedCoreContractTest {
    private static final String PREFIX="com.miophas.corruption.core.";
    private static void types(Type type,Set<Class<?>> found,Set<Type> visited) {
        if(type==null||!visited.add(type))return;
        if(type instanceof Class<?> cls){if(cls.isArray())types(cls.componentType(),found,visited);else if(cls.getName().startsWith(PREFIX))found.add(cls);}
        else if(type instanceof ParameterizedType generic){types(generic.getRawType(),found,visited);for(Type argument:generic.getActualTypeArguments())types(argument,found,visited);}
        else if(type instanceof WildcardType wildcard){for(Type bound:wildcard.getUpperBounds())types(bound,found,visited);for(Type bound:wildcard.getLowerBounds())types(bound,found,visited);}
        else if(type instanceof TypeVariable<?> variable)for(Type bound:variable.getBounds())types(bound,found,visited);
        else if(type instanceof GenericArrayType array)types(array.getGenericComponentType(),found,visited);
    }
    private static void surface(Class<?> cls,Set<Class<?>> core,Set<Class<?>> visited) {
        if(!visited.add(cls))return;
        Set<Type> seen=new HashSet<>();
        types(cls.getGenericSuperclass(),core,seen);for(Type implemented:cls.getGenericInterfaces())types(implemented,core,seen);
        for(Field field:cls.getDeclaredFields())if(Modifier.isPublic(field.getModifiers())||Modifier.isProtected(field.getModifiers()))types(field.getGenericType(),core,seen);
        for(Method method:cls.getDeclaredMethods())if(Modifier.isPublic(method.getModifiers())||Modifier.isProtected(method.getModifiers())){
            types(method.getGenericReturnType(),core,seen);for(Type parameter:method.getGenericParameterTypes())types(parameter,core,seen);
        }
        for(Constructor<?> constructor:cls.getDeclaredConstructors())if(Modifier.isPublic(constructor.getModifiers())||Modifier.isProtected(constructor.getModifiers()))
            for(Type parameter:constructor.getGenericParameterTypes())types(parameter,core,seen);
        for(Class<?> nested:cls.getDeclaredClasses())if(Modifier.isPublic(nested.getModifiers()))surface(nested,core,visited);
    }
    private static SortedSet<String> snapshot() throws Exception {
        Set<Class<?>> core=new HashSet<>(),visited=new HashSet<>();var root=LayerDependenciesTest.root();
        Path folder=root.resolve("src/main/java/com/miophas/corruption/api");
        try(var paths=Files.walk(folder)) {
            for(Path path:paths.filter(p->p.toString().endsWith(".java")&&!p.getFileName().toString().equals("package-info.java")).toList()) {
                String name="com.miophas.corruption.api."+folder.relativize(path).toString().replace('\\','.').replace(".java","");
                Class<?> cls=Class.forName(name,false,PublishedCoreContractTest.class.getClassLoader());if(Modifier.isPublic(cls.getModifiers()))surface(cls,core,visited);
            }
        }
        int size=-1;while(size!=core.size()){size=core.size();for(Class<?> cls:List.copyOf(core))surface(cls,core,visited);}
        SortedSet<String> result=new TreeSet<>();
        for(Class<?> cls:core) {
            result.add("type "+cls.getName());
            if(cls.isRecord())result.add("record "+cls.getName()+Arrays.toString(Arrays.stream(cls.getRecordComponents()).map(c->c.getName()+":"+c.getGenericType().getTypeName()).toArray()));
            if(cls.isEnum())for(Object value:cls.getEnumConstants())result.add("enum "+cls.getName()+"."+((Enum<?>)value).name());
            for(Constructor<?> constructor:cls.getDeclaredConstructors())if(Modifier.isPublic(constructor.getModifiers()))result.add(constructor.toGenericString());
            for(Method method:cls.getDeclaredMethods())if(Modifier.isPublic(method.getModifiers())&&!method.isSynthetic()&&!method.isBridge())result.add(method.toGenericString());
            for(Field field:cls.getDeclaredFields())if(Modifier.isPublic(field.getModifiers())&&!field.isSynthetic())result.add(field.toGenericString());
        }
        return result;
    }
    @Test void existingPublishedSignaturesRemainAvailableAndNewCoreTypesNeedAnExplicitContract() throws Exception {
        var actual=snapshot();var path=LayerDependenciesTest.root().resolve("src/test/resources/architecture/published-core-contracts.txt");
        if("1".equals(System.getenv("CORRUPTION_WRITE_API_BASELINE"))){Files.createDirectories(path.getParent());Files.write(path,actual);}
        assertTrue(Files.exists(path),"Missing published contract baseline");
        Set<String> expected=new HashSet<>(Files.readAllLines(path));
        for(String signature:expected)assertTrue(actual.contains(signature),"Published contract removed or changed: "+signature);
        for(String signature:actual)if(signature.startsWith("type "))assertTrue(expected.contains(signature),"New core type exposed through API; choose its public contract deliberately: "+signature);
    }
}
