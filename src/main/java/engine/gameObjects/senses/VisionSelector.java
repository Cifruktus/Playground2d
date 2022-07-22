package engine.gameObjects.senses;

public interface VisionSelector<K> {
    double evaluate(K input);
}
