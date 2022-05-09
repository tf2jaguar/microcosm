package io.github.tf2jaguar.micro.distribute.id;

/**
 * 分布式ID生成器：
 * 1. 一台机器上使用SnowFlake单例（将不能为不同表更换不同的dataCenter）
 * 使用IdWorker，将只会实例化一次SnowFlake，之后每次都会使用该实例来生成ID，保证全局唯一。
 * 也就是说，SnowFlake中的dataCenterId 和 machineId为第一次创建IdWorker时传入的参数，之后传入的参数将无效。
 * <p>
 * 2. 每次创建一个SnowFlake，可以使用不同的dataCenterId 和 machineId来区分（需保证不会有第二个使用相同的dataCenterId 和 machineId）
 * 使用SnowFlake，每次自行创建一个SnowFlake对象进行生成ID，需保证全局无相同的dataCenterId 和 machineId 的SnowFlake。
 * （可以构建一个数据库表，存放所有的dataCenterId 和 machineId，以及创建它的相关类，既可以保证全局唯一，也可以在宕机重启时重新构建）
 *
 * @author zhangguodong
 */
public class IdWorker {

    private static Long machineId;

    private static Long dataCenterId;

    private enum SnowFlakeSingleton {
        //
        Singleton;
        private SnowFlake snowFlake;

        SnowFlakeSingleton() {
            snowFlake = new SnowFlake(machineId, dataCenterId);
        }

        public SnowFlake getInstance() {
            return snowFlake;
        }
    }

    public IdWorker() {
        this(null, null);
    }

    /**
     * IdWorker有参构造方法。
     * 仅当第一次创建IdWorker实例时，参数会被传入SnowFlake生成器中。
     *
     * @param machineId    机器ID
     * @param dataCenterId 数据中心ID
     */
    public IdWorker(Long machineId, Long dataCenterId) {
        if (machineId == null) {
            machineId = 1L;
        }
        if (dataCenterId == null) {
            dataCenterId = 1L;
        }
        IdWorker.machineId = machineId;
        IdWorker.dataCenterId = dataCenterId;
    }

    /**
     * 获取下一个分布式ID
     *
     * @return 分布式ID
     */
    public long nextId() {
        return SnowFlakeSingleton.Singleton.getInstance().nextId();
    }

}
