package cn.nukkit.entity.ai.path;

/**
 * 寻路器接口，任何寻路器都应当实现此接口
 */
public interface PathFinder {
    void prepareSearch();

    boolean search();

    void setStart(Node start);

    Node getStart();

    void setDestination(Node destination);

    Node getDestination();

    void setPathThinker(PathThinker pathThinker);

    PathThinker getPathThinker();
}
