package com.qianzhui.enode.domain.impl;

import com.qianzhui.enode.common.container.GenericTypeLiteral;
import com.qianzhui.enode.common.container.ObjectContainer;
import com.qianzhui.enode.domain.IAggregateRepository;
import com.qianzhui.enode.domain.IAggregateRepositoryProvider;
import com.qianzhui.enode.domain.IAggregateRepositoryProxy;
import com.qianzhui.enode.domain.IAggregateRoot;
import com.qianzhui.enode.infrastructure.IAssemblyInitializer;
import com.qianzhui.enode.infrastructure.TypeUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by junbo_xu on 2016/4/1.
 */
public class DefaultAggregateRepositoryProvider implements IAggregateRepositoryProvider, IAssemblyInitializer {
    private final Map<Class, IAggregateRepositoryProxy> _repositoryDict = new HashMap<>();

    public IAggregateRepositoryProxy getRepository(Class<? extends IAggregateRoot> aggregateRootType) {
        return _repositoryDict.get(aggregateRootType);
    }

    public void initialize(Set<Class<?>> componentTypes) {
        componentTypes.stream().filter(this::isAggregateRepositoryType).forEach(this::registerAggregateRepository);
    }

    private void registerAggregateRepository(Class aggregateRepositoryType) {
        Type superGenericInterface = TypeUtils.getSuperGenericInterface(aggregateRepositoryType, IAggregateRepository.class);

        if (superGenericInterface instanceof Class)
            return;

        ParameterizedType superGenericInterfaceType = (ParameterizedType) superGenericInterface;

        AggregateRepositoryProxy<IAggregateRoot> aggregateRepositoryProxy = new AggregateRepositoryProxy<>(ObjectContainer.resolve(new GenericTypeLiteral<IAggregateRepository<IAggregateRoot>>() {
        }));

        _repositoryDict.put((Class) superGenericInterfaceType.getActualTypeArguments()[0], aggregateRepositoryProxy);
    }

    private boolean isAggregateRepositoryType(Class type) {
        return type != null && !Modifier.isAbstract(type.getModifiers()) && IAggregateRepository.class.isAssignableFrom(type);
    }
}