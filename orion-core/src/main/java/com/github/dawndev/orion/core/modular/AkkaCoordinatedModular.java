package com.github.dawndev.orion.core.modular;

import akka.Done;
import akka.actor.ActorSystem;
import akka.actor.CoordinatedShutdown;
import ch.qos.logback.classic.LoggerContext;
import com.github.dawndev.orion.core.annotation.Modular;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.LifecycleProcessor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Modular(initMethod = "init")
public class AkkaCoordinatedModular extends AbstractModular {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ActorSystem actorSystem;

    public void init() {

    }

    @Override
    public void start() {
        logger.info("AkkaCoordinatedModular start");
        CoordinatedShutdown coordinatedShutdown = CoordinatedShutdown.get(actorSystem);
        coordinatedShutdown.addTask(
                // SS：服务解除绑定前
                CoordinatedShutdown.PhaseBeforeServiceUnbind(),
                "enter-stopping-state",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseBeforeServiceUnbind 阶段，服务器状态切换为STOPPING");
                })
        );
        coordinatedShutdown.addTask(
                // 服务解除绑定
                // 停止接收新进入的连接。
                // 这里是个合适的时机来阻止新连接的进入，考虑在这里终止netty？
                CoordinatedShutdown.PhaseServiceUnbind(),
                "service-unbind",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseServiceUnbind 阶段，服务解除绑定，停止接收新连接");
                })
        );
        coordinatedShutdown.addTask(
                // 等待服务请求完成
                // 因为新连接已经停止了，这里适合等待现有的连接的任务的完成。
                CoordinatedShutdown.PhaseServiceRequestsDone(),
                "service-requests-done",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseServiceRequestsDone 阶段，等待服务请求完成");
                })
        );
        coordinatedShutdown.addTask(
                CoordinatedShutdown.PhaseServiceStop(),
                "service-stop",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseServiceStop 阶段，服务停止");
                })
        );
        coordinatedShutdown.addTask(
                // SS：集群关闭前
                CoordinatedShutdown.PhaseBeforeClusterShutdown(),
                "check-before-cluster-shutdown",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseBeforeClusterShutdown 阶段");
                })
        );
        coordinatedShutdown.addTask(
                // 集群分片的关闭
                // 这里不建议添加自定义的任务
                CoordinatedShutdown.PhaseClusterShardingShutdownRegion(),
                "check-cluster-sharding-shutdown-region",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseClusterShardingShutdownRegion 阶段");
                })
        );

        //            // 在所有角色的节点同时开始停服时，作为proxy的ShardRegion如果在承载Shard的ShardRegion执行handoff时停止，
        //            // 会导致此次handoff必然超时。这里将每个承载Shard节点错开延迟开始执行GracefulShutdown规避此问题。
        //            if (includingShards.isNotEmpty()) {
        //                val delaySeconds = (GameWorldShard.values().indexOf(includingShards.first()) + 1) * 5L
        //
        //                addTask(
        //                    // SS：集群shard关闭前？(看配置这个是自定义阶段)
        //                    "before-cluster-sharding-shutdown-region",
        //                    "delay-by-shard-name",
        //                    taskSupplier {
        //                        globalLog.lzInfo { "Coordinated 关闭 - before-cluster-sharding-shutdown-region 阶段，等待 $delaySeconds 秒（暂时屏蔽）" }
        //
        //                        //
        //                        if (!processMgr.tryShutdownMoreFast) {
        //                            TimeUnit.SECONDS.sleep(delaySeconds)
        //                        } else {
        //                            globalLog.lzInfo { "屏蔽进程关闭延迟，尝试更加快速的关闭" }
        //                        }
        //                    }
        //                )
        //            }

        coordinatedShutdown.addTask(
                // 集群离开
                // 这里不建议添加自定义的任务
                CoordinatedShutdown.PhaseClusterLeave(),
                "check-cluster-leave",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseClusterLeave 阶段");
                })
        );
        coordinatedShutdown.addTask(
                // 集群退出阶段
                // 这会等待Region都关闭了，然后再关闭shard coordinator。
                // 这里也会关闭集群单例
                // 这里不建议添加自定义的任务
                CoordinatedShutdown.PhaseClusterExiting(),
                "write-graceful-shutdown-region-flag-files",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseClusterExiting 阶段");
                })
        );
        coordinatedShutdown.addTask(
                // 集群退出结束
                // 这会等待exiting完成。
                // 这里不建议添加自定义的任务
                CoordinatedShutdown.PhaseClusterExitingDone(),
                "wait-cluster-exiting-done",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseClusterExitingDone 阶段");
                })
        );
        coordinatedShutdown.addTask(
                // 集群关闭
                // 这里不建议添加自定义的任务
                CoordinatedShutdown.PhaseClusterShutdown(),
                "wait-cluster-shutdown",
                taskSupplier(()->{
                    logger.info("Coordinated 关闭 - PhaseClusterShutdown 阶段");
                })
        );
        coordinatedShutdown.addTask(
                // 在系统终止前
                CoordinatedShutdown.PhaseBeforeActorSystemTerminate(),
                "stop-global-components",
                taskSupplier(()->{
                    LifecycleProcessor processor = context.getBean(LifecycleProcessor.class);
                    processor.stop();

                    // 手动触发停机
                    SpringApplication.exit(context,
                            () -> {
                                logger.info("关闭SpringApplication");
                                return 0;
                            }
                    );

                    // 停止日志
                    ILoggerFactory factory = LoggerFactory.getILoggerFactory();
                    if (factory instanceof LoggerContext) {
                        ((LoggerContext) factory).stop();
                    }
                    logger.info("Coordinated 关闭 - PhaseBeforeActorSystemTerminate 阶段，停止全局组件；服务器状态切换为STOPPED");
                })
        );
    }

    private Supplier<CompletionStage<Done>> taskSupplier(Runnable task) {
        return () -> CompletableFuture.supplyAsync(()-> {
            task.run();
            return  Done.getInstance();
        });
    }

    @Override
    public int getPhase() {
        return super.getPhase();
    }
}