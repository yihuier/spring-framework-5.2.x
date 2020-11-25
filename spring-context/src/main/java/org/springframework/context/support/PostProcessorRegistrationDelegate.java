/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	/**
	 * 调用BeanFactoryPostProcessor，内部还是有分批次顺序来调用的
	 * 分为BeanDefinitionRegistryPostProcessor，和其他的BeanFactoryPostProcessor两类
	 * 然后每一类中还按照下面的顺序进行调用：
	 * 1、是否实现了PriorityOrdered接口，如果有优先调用
	 * 2、是否实现了Ordered接口，如果有优先调用
	 * 3、最后调用其他的没有实现上面两个接口的BeanFactoryPostProcessor
	 */
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		/**
		 * 可能需要结合后面的再来理解这个变量的作用
		 * 这个集合可以作为后面去重使用，因为我们知道一个BeanDefinitionRegistryPostProcessor
		 * 它同时也是一个BeanFactoryPostProcessor。所以，如果我们前面通过BeanDefinitionRegistryPostProcessor.class
		 * 来获取，和后面再通过BeanFactoryPostProcessor.class，那些BeanDefinitionRegistryPostProcessor
		 * 就会被获取两遍，所有借助这个变量可以用来去掉重复的，其实这里只的是已经调用了相应方法的。
		 */
		Set<String> processedBeans = new HashSet<>();

		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// 用来保存常规的BeanFactoryPostProcessor
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			// 用来保存BeanDefinitionRegisterPostProcessor
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			// 一般来说如果我们没有手动添加，beanFactoryPostProcessors为空，这里的循环是不会执行的
			// 用来将beanFactoryPostProcessors中的后置处理器分别加到对应的列表中
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					/**
					 * 这里直接就对我们自定义的手动注册的BeanDefinitionRegistryPostProcessor的回调进行调用
					 * 而后面才获取Spring内部定义的BeanDefinitionRegistryPostProcessor
					 * 所以，我们自定义的手动注册的BeanDefinitionRegistryPostProcessor会先于Spring内部定义的
					 * 执行postProcessBeanDefinitionRegistry回调方法
					 */
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					regularPostProcessors.add(postProcessor);
				}
			}

			/**
			 * 执行完前面的代码，到这里，已经把我们通过调用addBeanFactoryPostProcessor()添加的BeanFactoryPostProcessor
			 * 根据类型分别添加到regularPostProcessors和registryProcessors中
			 */

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			/**
			 * 用来保存当前要注册的BeanDefinitionRegistryPostProcessor，什么叫做当前要注册的呢？
			 * 相对于前面的regularPostProcessors和registryProcessors来说，现在要添加的就是当前要注册的
			 */
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			/**
			 * 这个通过类型获取目前BeanDefinition中的所有BeanDefinitionRegistryPostProcessor的名称，
			 * （
			 * 		注意此时我们自己定义的可以被扫描到的BeanDefinitionRegistryPostProcessor，
			 * 		还不会被获取，因此那些类还没有被扫描，即还没有被注册成BeanDefinition
			 * ）
			 * 将会获取到一个很重要的BeanDefinitionRegistryPostProcessor的名称，即ConfigurationClassPostProcessor的名称
			 * "org.springframework.context.annotation.internalConfigurationAnnotationProcessor"
			 */
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					/**
					 * 上面获取到名称之后，这里通过BeanFactory#getBean方法将该post-processors实例化，
					 * 然后加到currentRegistryProcessors列表总
					 */
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			/**
			 * 这里把现在刚刚注册的BeanDefinitionRegistryPostProcessor加入到registryProcessors
			 * 所以现在registryProcessors中有我们手动调用addBeanFactoryPostProcessor()添加的以及
			 * 上面类型获取BeanDefinition，然后实例化而添加的
			 */
			registryProcessors.addAll(currentRegistryProcessors);

			/**
			 * =====================！！！！！！！！！！！===================================
			 * 注意这里调用上面获取到的BeanDefinitionRegistryPostProcessor的对应方法postProcessBeanDefinitionRegistry，
			 * 这里面有一个ConfigurationClassPostProcessor，该后置处理器是用来进行组件扫描的，
			 * 也就是说执行完下面这行代码之后，我们希望被Spring扫描到的类都会被扫描到，并且转换成对应的BeanDefinition
			 *
			 * 所以，正如前面注释，前面也有调用过getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class,...)
			 * 但是还没有办法获取我们自己的BeanDefinitionRegistryPostProcessor，因为它们还不是BeanDefinition，
			 * 不过当执行完这行代码，我们自定义的BeanDefinitionRegistryPostProcessor（前提是可以被扫描到，即使用@Component之类的注解）
			 * 在后面调用getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class,...)的时候，就可以被获取到了
			 * 然后就可以被注册，被调用对应的回调方法
			 *
			 * 所以，到此，Spring内部定义的实现了PriorityOrdered接口的BeanDefinitionRegistryPostProcessor
			 * 将会被调用，而我们自定义的BeanDefinitionRegistryPostProcessor将会放在后面实例化并且调用
			 * ===================！！！！！！！！！！！！=====================================
			 */
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 每次调用方法完就会清空该列表，避免后面重复调用方法
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			/**
			 * 这里再重新通过类型获取目前所有BeanDefinitionRegistryPostProcessor的名称，因为在通过
			 * 前面调用ConfigurationClassPostProcessor这个处理器之后，可能会扫描到更多的我们自定义的
			 * 一些BeanDefinitionRegistryPostProcessor
			 */
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				/**
				 * 这里有个疑问，如果我们的自定义BeanDefinitionRegistryPostProcessor实现了PriorityOrdered接口
				 * 那么当我们的自定义BeanDefinitionRegistryPostProcessor中同时存在实现了PriorityOrdered接口的
				 * 和实现了Ordered接口的，都会被到加入到下面的列表中，此时，是不是有可能出现顺序问题？（已解决，详见后面注释）
				 */
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					/**
					 * 同样，这里也是获取BeanDefinitionRegistryPostProcessor实例对象，如果没有则实例化一个返回
					 * 借助于beanFactory#getBean
					 */
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			/**
			 * 按照前面所说，这里可能会存在分别实现了PriorityOrdered和Ordered的对象，然后导致调用的顺序问题？
			 * 经过验证（偷懒没去看实现）：
			 * 	sortPostProcessors能够正确对PriorityOrdered和Ordered排序的，所以上面那么处理不会出现顺序问题
			 */
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			/**
			 * 到此，我们自定义的以及Spring内部的实现了Ordered接口的BeanDefinitionRegistryPostProcessor
			 * 都会被实例化并且调用
			 */
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				/**
				 * 到此，我们自定义的以及Spring内部的其他BeanDefinitionRegistryPostProcessor都会被实例化并且调用
				 */
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			/**
			 * 由于一个BeanDefinitionRegistryPostProcessor同时也是一个BeanFactoryPostProcessor，
			 * 所以在上面调用了BeanDefinitionRegistryPostProcessor接口中定义的postProcessBeanDefinitionRegistry方法之后
			 * 这里开始调用BeanFactoryPostProcessor接口中定义的postProcessBeanFactory方法
			 */
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);

			/**
			 * ================================================================================
			 * 到这里，所有自定义的被扫描到的BeanDefinitionRegistryPostProcessor，以及Spring内部定义的
			 * BeanDefinitionRegistryPostProcessor，都已经被实例化并且调用了相应的方法，
			 * 即BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry
			 * 和BeanFactoryPostProcessor#postProcessBeanFactory
			 * ================================================================================
			 */
		}

		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		/**
		 * 这里通过类型获取目前所有BeanFactoryPostProcessor的名称，就会获取到前面由ConfigurationClassPostProcessor
		 * 扫描并注册的BeanFactoryPostProcessor BeanDefinition的名称
		 */
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		/**
		 * TODO
		 * 这里不太清楚为什么要priorityOrderedPostProcessors这个存放的是后置处理器实例对象
		 * 而orderedPostProcessorNames、nonOrderedPostProcessorNames这两个要保存后置处理器的名称
		 * 然后后面再通过名称去获取对象实例，像这样做是不是比较奇怪？
		 */
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			/**
			 * beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, ...)这个方法必然会获取到
			 * 前面已经处理完毕的那些BeanDefinitionRegistryPostProcessor，所有我们需要过滤掉那些，
			 * 此时processedBeans这个变量的作用之一就体现出来了：只要是前面处理过的，在这里我就不再做处理
			 */
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				// 通过beanFactory.getBean获取一个BeanFactoryPostProcessor的实例，如果还没有则去创建一个返回
				// 所以，这里就是创建实现了PriorityOrdered接口的BeanFactoryPostProcessor的实例对象，然后加到列表中
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 执行到这里的时候，已经注册成BeanDefinition的BeanFactoryPostProcessor中那些实现了PriorityOrdered接口的将会被调用相应方法
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			// 同上，通过beanFactory.getBean获取一个BeanFactoryPostProcessor的实例，如果还没有则去创建一个返回
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 执行到这里，已经注册成BeanDefinition的BeanFactoryPostProcessor中那些实现了Ordered接口的将会被调用相应的方法
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			// 同上，通过beanFactory.getBean获取一个BeanFactoryPostProcessor的实例，如果还没有则去创建一个返回
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 执行到这里，已经注册成BeanDefinition的BeanFactoryPostProcessor中那些没有实现上述两个接口的其他对象将会被调用相应的方法
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		/**
		 * ================================================================================
		 * 当代码执行到这里的时候，容器中目前所有被扫描到的自定义的BeanFactoryPostProcessor，
		 * 以及Spring内部添加的BeanFactoryPostProcessor都已经被实例化并且调用了相应的方法，
		 * 即BeanFactoryPostProcessor#postProcessBeanFactory
		 * ================================================================================
		 */

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	/**
	 * 注册BeanPostProcessor，也是分批次来注册的，
	 * 1、首先是实现了PriorityOrdered的
	 * 2、其次是实现了Ordered的
	 * 3、再来是实现了BeanPostProcessors
	 * 4、最后是重新注册internal BeanPostProcessors（重新注册的目的是，让他们位于列表的最后）
	 *
	 * TODO 但是这个internal BeanPostProcessors并不清楚
	 */
	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		/**
		 * 此时那些已经被扫描到并且注册成BeanDefinition的BeanPostProcessor的名称都会被获取到
		 * 当然如果我们自己手动在refresh调用之前注册的BeanPostProcessor也会被获取到
		 */
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		/**
		 * 这里的beanFactory.getBeanPostProcessorCount()中的那几个BeanPostProcessor，有的是在
		 * AbstractApplicationContext#prepareBeanFactory中注册的，比如
		 * 1、ApplicationContextAwareProcessor
		 * 2、ApplicationListenerDetector
		 */
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		/**
		 * 下面就和处理BeanFactoryPostProcessor的逻辑差不多了，根据优先级分组添加到列表中
		 * 不同的是，这里还会重复添加已经在列表中的BeanFactoryPostProcessor，目的是让他们
		 * 位置列表的最后。
		 */
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		// 从上面的逻辑可以知道，internalPostProcessors中的处理器可能会出现在上面三个列表的一个或者多个中，即
		// priorityOrderedPostProcessors、orderedPostProcessors、nonOrderedPostProcessors
		// 这里在把它们注册进去，显然已经重复添加了，但是不要紧，registerBeanPostProcessors方法内部
		// 会先删除已经存在的，然后再添加。不过最终导致的结果是在后置处理器列表中internalPostProcessors中的
		// 后置处理器会位于其他后置处理器的后面
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		// 这里的重复注册，和上面的internalPostProcessors目的是一样的，让他位于列表的最后
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// Nothing to sort?
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
