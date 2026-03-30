package cn.nukkit.level.generator.holder;

import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomizedObjectHolder extends ObjectHolder {

    protected RandomSourceProvider randomSourceProvider;

}
