// noinspection JSUnresolvedVariable

const NukkitClass = Java.type("cn.nukkit.Nukkit");

/**
 * PowerNukkitX服务端操作对象
 */
export const PowerNukkitX = {
    /**
     * 获取PowerNukkitX服务器版本
     * @return {string}
     */
    getServerVersion: () => {
        return NukkitClass.GIT_COMMIT;
    }
}